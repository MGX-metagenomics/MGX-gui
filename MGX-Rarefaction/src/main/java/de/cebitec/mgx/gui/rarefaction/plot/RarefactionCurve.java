package de.cebitec.mgx.gui.rarefaction.plot;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.groups.FileType;
import de.cebitec.mgx.api.groups.ImageExporterI;
import de.cebitec.mgx.api.groups.SequenceExporterI;
import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.misc.Point;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.common.visualization.AbstractViewer;
import de.cebitec.mgx.common.visualization.CustomizableI;
import de.cebitec.mgx.common.visualization.ViewerI;
import de.cebitec.mgx.gui.charts.basic.util.JFreeChartUtil;
import de.cebitec.mgx.gui.rarefaction.LocalRarefaction;
import de.cebitec.mgx.gui.rarefaction.Rarefaction;
import de.cebitec.mgx.gui.seqexporter.SeqExporter;
import de.cebitec.mgx.gui.swingutils.DelayedPlot;
import de.cebitec.mgx.gui.swingutils.NonEDT;
import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JComponent;
import javax.swing.SwingWorker;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
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
    private List<Pair<VisualizationGroupI, DistributionI<Long>>> data;

    @Override
    public JComponent getComponent() {
        return cPanel;
    }

    @Override
    public String getName() {
        return "Rarefaction";
    }

    @Override
    public void show(final List<Pair<VisualizationGroupI, DistributionI<Long>>> dists) {

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

                chart = ChartFactory.createXYLineChart(getTitle(), xAxisLabel, yAxisLabel, dataset, PlotOrientation.VERTICAL, true, true, false);
                chart.setBorderPaint(Color.WHITE);
                chart.setBackgroundPaint(Color.WHITE);
                ret = new ChartPanel(chart);
                XYPlot plot = (XYPlot) chart.getPlot();
                plot.setBackgroundPaint(Color.WHITE);
                plot.setFixedLegendItems(JFreeChartUtil.createLegend(dists));

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
                for (Pair<VisualizationGroupI, DistributionI<Long>> groupDistribution : dists) {
                    renderer.setSeriesPaint(i++, groupDistribution.getFirst().getColor());
                }
                return ret;
            }

            @Override
            protected void done() {
                try {
                    DelayedPlot wp = RarefactionCurve.this.cPanel;
                    wp.setTarget(get());
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }
                super.done();
            }

        };
        worker.execute();

    }

    private XYSeriesCollection createXYSeries(List<Pair<VisualizationGroupI, DistributionI<Long>>> in) {
        
        final int numRepetitions = getCustomizer().getNumberRepetitions();
        final XYSeriesCollection dataset = new XYSeriesCollection();
        final AtomicBoolean error = new AtomicBoolean(false);

        final CountDownLatch allDone = new CountDownLatch(in.size());

        for (final Pair<VisualizationGroupI, DistributionI<Long>> groupDistribution : in) {

            NonEDT.invoke(new Runnable() {

                @Override
                public void run() {
                    final DistributionI<Long> dist = groupDistribution.getSecond();
                    Iterator<Point> iter = null;
                    try {
                        iter = LocalRarefaction.rarefy(dist, numRepetitions);
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
                return new FileType[]{FileType.PNG};
            }

            @Override
            public Result export(FileType type, String fName) throws Exception {
                ChartUtilities.saveChartAsPNG(new File(fName), chart, 1280, 1024);
                return Result.SUCCESS;
            }
        };
    }

    @Override
    public SequenceExporterI[] getSequenceExporters() {
        List<SequenceExporterI> ret = new ArrayList<>(data.size());
        for (Pair<VisualizationGroupI, DistributionI<Long>> p : data) {
            if (p.getSecond().getTotalClassifiedElements() > 0) {
                SequenceExporterI exp = new SeqExporter<>(p.getFirst(), p.getSecond());
                ret.add(exp);
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
