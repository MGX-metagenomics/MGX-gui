package de.cebitec.mgx.gui.rarefaction.plot;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.groups.FileType;
import de.cebitec.mgx.api.groups.GroupI;
import de.cebitec.mgx.api.groups.ImageExporterI;
import de.cebitec.mgx.api.groups.SequenceExporterI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.misc.Point;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.assembly.AssembledSeqRunI;
import de.cebitec.mgx.gui.charts.basic.util.JFreeChartUtil;
import de.cebitec.mgx.gui.charts.basic.util.SVGChartPanel;
import de.cebitec.mgx.gui.rarefaction.LocalRarefaction;
import de.cebitec.mgx.gui.rarefaction.Rarefaction;
import de.cebitec.mgx.gui.seqexporter.SeqExporter;
import de.cebitec.mgx.gui.swingutils.DelayedPlot;
import de.cebitec.mgx.gui.swingutils.NonEDT;
import de.cebitec.mgx.gui.viewer.api.AbstractViewer;
import de.cebitec.mgx.gui.viewer.api.CustomizableI;
import de.cebitec.mgx.gui.viewer.api.ViewerI;
import java.awt.BasicStroke;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JComponent;
import javax.swing.SwingWorker;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.TickUnitSource;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.openide.util.Exceptions;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author sj
 */
@ServiceProvider(service = ViewerI.class)
public class RarefactionCurve extends AbstractViewer<DistributionI<Long>> implements CustomizableI, ImageExporterI.Provider, SequenceExporterI.Provider {

    private RarefactionCustomizer cust = null;
    private DelayedPlot cPanel = null;
    private JFreeChart chart = null;
    private List<Pair<GroupI, DistributionI<Long>>> data;

    @Override
    public JComponent getComponent() {
        return cPanel;
    }

    @Override
    public String getName() {
        return "Rarefaction";
    }

    @Override
    public void show(final List<Pair<GroupI, DistributionI<Long>>> dists) {

        data = dists;

        cPanel = new DelayedPlot();

        SwingWorker<JComponent, Void> worker = new SwingWorker<JComponent, Void>() {

            @Override
            protected JComponent doInBackground() throws Exception {
                JComponent ret;
                XYSeriesCollection dataset = createXYSeries(dists);

                if (dataset == null) {
                    return null;
                }

                String xAxisLabel = "Size";
                String yAxisLabel = "Richness";

                String title = getCustomizer().hideTitle() ? null : getTitle();
                chart = ChartFactory.createXYLineChart(title, xAxisLabel, yAxisLabel, dataset,
                        PlotOrientation.VERTICAL, !getCustomizer().hideLegend(), true, false);
                chart.setBorderPaint(Color.WHITE);
                chart.setBackgroundPaint(Color.WHITE);
                ret = new SVGChartPanel(chart);
                XYPlot plot = (XYPlot) chart.getPlot();
                plot.setBackgroundPaint(Color.WHITE);
                if (!getCustomizer().hideLegend()) {
                    plot.setFixedLegendItems(JFreeChartUtil.createLegend(dists));
                }

                // x axis
                ValueAxis valueAxis;
                final TickUnitSource tusX;

                valueAxis = (NumberAxis) plot.getDomainAxis();
                tusX = NumberAxis.createIntegerTickUnits();
                valueAxis.setStandardTickUnits(tusX);
                valueAxis.setInverted(false);
                plot.setDomainAxis(valueAxis);

                // y axis
                final NumberAxis rangeAxis;
                final TickUnitSource tus;

                rangeAxis = (NumberAxis) plot.getRangeAxis();
                tus = NumberAxis.createIntegerTickUnits();
                rangeAxis.setStandardTickUnits(tus);
                plot.setRangeAxis(rangeAxis);

                // set the colors
                int i = 0;
                XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
                for (Pair<GroupI, DistributionI<Long>> groupDistribution : dists) {
                    renderer.setSeriesStroke(i, new BasicStroke(getCustomizer().getLineThickness()));
                    renderer.setSeriesPaint(i++, groupDistribution.getFirst().getColor());
                }
                return ret;
            }

            @Override
            protected void done() {
                try {
                    DelayedPlot wp = RarefactionCurve.this.cPanel;
                    wp.setTarget(get(), JFreeChartUtil.getImageExporter(chart));
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }
                super.done();
            }

        };
        worker.execute();

    }

    private XYSeriesCollection createXYSeries(List<Pair<GroupI, DistributionI<Long>>> in) {

        final int numRepetitions = getCustomizer().getNumberRepetitions();
        final int numDataPoints = getCustomizer().getNumberOfPoints();
        final XYSeriesCollection dataset = new XYSeriesCollection();
        final AtomicBoolean error = new AtomicBoolean(false);

        final CountDownLatch allDone = new CountDownLatch(in.size());

        for (final Pair<GroupI, DistributionI<Long>> groupDistribution : in) {

            NonEDT.invoke(new Runnable() {

                @Override
                public void run() {
                    final DistributionI<Long> dist = groupDistribution.getSecond();
                    Iterator<Point> iter = null;
                    try {
                        iter = LocalRarefaction.rarefy(dist, numRepetitions, numDataPoints);
                    } catch (MGXException ex) {
                        Exceptions.printStackTrace(ex);
                        //
                        // local computational failed, retry on mgx server
                        //
                        try {
                            iter = Rarefaction.rarefy(dist);
                        } catch (MGXException ex1) {
                            Exceptions.printStackTrace(ex1);
                            error.set(true);
                        }
                    } finally {
                        if (!error.get()) {
                            XYSeries series = new XYSeries(groupDistribution.getFirst().getDisplayName());
                            while (iter != null && iter.hasNext()) {
                                Point p = iter.next();
                                series.add(p.getX(), p.getY());
                            }
                            dataset.addSeries(series);
                        }
                        allDone.countDown();
                    }
                }
            });

        }

        try {
            allDone.await();
        } catch (InterruptedException ex) {
            Exceptions.printStackTrace(ex);
        }
        return error.get() ? null : dataset;
    }

    @Override
    public RarefactionCustomizer getCustomizer() {
        if (cust == null) {
            cust = new RarefactionCustomizer();
        }
        return cust;
    }

    @Override
    public Class getInputType() {
        return DistributionI.class;
    }

    @Override
    public ImageExporterI getImageExporter() {
        return new ImageExporterI() {
            @Override
            public FileType[] getSupportedTypes() {
                return new FileType[]{FileType.PNG, FileType.JPEG, FileType.SVG};
            }

            @Override
            public ImageExporterI.Result export(FileType type, String fName) throws Exception {
                ImageExporterI exp = cPanel.getImageExporter();
                if (exp == null) {
                    return Result.ERROR;
                }
                return exp.export(type, fName);
            }
        };
    }

    @Override
    public SequenceExporterI[] getSequenceExporters() {
        List<SequenceExporterI> ret = new ArrayList<>(data.size());
        for (Pair<GroupI, DistributionI<Long>> p : data) {
            if (p.getSecond().getTotalClassifiedElements() > 0) {
                if (p.getFirst().getContentClass().equals(SeqRunI.class)) {
                    SequenceExporterI exp = new SeqExporter<>((GroupI<SeqRunI>) p.getFirst(), p.getSecond());
                    ret.add(exp);
                } else if (p.getFirst().getContentClass().equals(AssembledSeqRunI.class)) {
                    SequenceExporterI exp = new SeqExporter<>((GroupI<AssembledSeqRunI>) p.getFirst(), p.getSecond());
                    ret.add(exp);
                }
            }
        }
        return ret.toArray(new SequenceExporterI[]{});
    }

    @Override
    public boolean canHandle(AttributeTypeI valueType) {
        return valueType.getValueType() == AttributeTypeI.VALUE_DISCRETE;
    }

    @Override
    public void setAttributeType(AttributeTypeI aType) {
        super.setAttributeType(aType);
        super.setTitle("Rarefaction of " + aType.getName());
    }

}
