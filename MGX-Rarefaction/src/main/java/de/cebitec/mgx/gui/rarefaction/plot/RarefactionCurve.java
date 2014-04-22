package de.cebitec.mgx.gui.rarefaction.plot;

import de.cebitec.mgx.gui.attributevisualization.ui.DelayedPlot;
import de.cebitec.mgx.gui.attributevisualization.viewer.ViewerI;
import de.cebitec.mgx.gui.datamodel.AttributeType;
import de.cebitec.mgx.gui.datamodel.misc.Distribution;
import de.cebitec.mgx.gui.datamodel.misc.Pair;
import de.cebitec.mgx.gui.datamodel.misc.Point;
import de.cebitec.mgx.gui.groups.ImageExporterI;
import de.cebitec.mgx.gui.groups.VisualizationGroup;
import de.cebitec.mgx.gui.rarefaction.Rarefaction;
import de.cebitec.mgx.gui.swingutils.NonEDT;
import de.cebitec.mgx.gui.util.FileType;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
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
public class RarefactionCurve extends ViewerI<Distribution> {

    private DelayedPlot cPanel = null;
    private JFreeChart chart = null;

    @Override
    public JComponent getComponent() {
        return cPanel;
    }

    @Override
    public String getName() {
        return "Rarefaction";
    }

    @Override
    public void show(final List<Pair<VisualizationGroup, Distribution>> dists) {

        cPanel = new DelayedPlot();

        SwingWorker<JComponent, Void> worker = new SwingWorker<JComponent, Void>() {

            @Override
            protected JComponent doInBackground() throws Exception {
                JComponent ret;
                XYSeriesCollection dataset = createXYSeries(dists);

                String xAxisLabel = "Size";
                String yAxisLabel = "Richness";

                chart = ChartFactory.createXYLineChart(getTitle(), xAxisLabel, yAxisLabel, dataset, PlotOrientation.VERTICAL, true, true, false);
                chart.setBorderPaint(Color.WHITE);
                chart.setBackgroundPaint(Color.WHITE);
                ret = new ChartPanel(chart);
                XYPlot plot = (XYPlot) chart.getPlot();
                plot.setBackgroundPaint(Color.WHITE);

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
                for (Pair<VisualizationGroup, Distribution> groupDistribution : dists) {
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

    private static XYSeriesCollection createXYSeries(List<Pair<VisualizationGroup, Distribution>> in) {
        final XYSeriesCollection dataset = new XYSeriesCollection();

        for (final Pair<VisualizationGroup, Distribution> groupDistribution : in) {

            NonEDT.invokeAndWait(new Runnable() {

                @Override
                public void run() {
                    XYSeries series = new XYSeries(groupDistribution.getFirst().getName());
                    Distribution dist = groupDistribution.getSecond();
                    Iterator<Point> iter = Rarefaction.rarefy(dist);
                    if (iter == null) {
                        return;
                    }
                    while (iter.hasNext()) {
                        Point p = iter.next();
                        series.add(p.getX(), p.getY());
                    }
                    dataset.addSeries(series);
                }
            });

        }
        return dataset;
    }

    @Override
    public JComponent getCustomizer() {
        return null;
    }

    @Override
    public Class getInputType() {
        return Distribution.class;
    }

    @Override
    public ImageExporterI getImageExporter() {
        return new ImageExporterI() {

            @Override
            public FileType[] getSupportedTypes() {
                return new FileType[]{FileType.PNG};
            }

            @Override
            public boolean export(FileType type, String fName) throws Exception {
                 try {
                    ChartUtilities.saveChartAsPNG(new File(fName), chart, 1280, 1024);
                    return true;
                } catch (IOException ex) {
                    return false;
                }
            }
        };
    }

    @Override
    public boolean canHandle(AttributeType valueType) {
        return valueType.getValueType() == AttributeType.VALUE_DISCRETE;
    }

    @Override
    public void setAttributeType(AttributeType aType) {
        super.setAttributeType(aType);
        super.setTitle("Rarefaction of " + aType.getName());
    }

}
