package de.cebitec.mgx.gui.rarefaction.plot;

import de.cebitec.mgx.gui.attributevisualization.viewer.ViewerI;
import de.cebitec.mgx.gui.datamodel.AttributeType;
import de.cebitec.mgx.gui.datamodel.misc.Distribution;
import de.cebitec.mgx.gui.datamodel.misc.Pair;
import de.cebitec.mgx.gui.datamodel.misc.Point;
import de.cebitec.mgx.gui.groups.ImageExporterI;
import de.cebitec.mgx.gui.groups.VisualizationGroup;
import de.cebitec.mgx.gui.rarefaction.Rarefaction;
import de.cebitec.mgx.gui.swingutils.NonEDT;
import de.cebitec.mgx.gui.util.FileChooserUtils;
import de.cebitec.mgx.gui.util.FileType;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import javax.swing.JComponent;
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
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author sj
 */
@ServiceProvider(service = ViewerI.class)
public class RarefactionCurve extends ViewerI<Distribution> {

    private ChartPanel cPanel = null;
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
    public void show(List<Pair<VisualizationGroup, Distribution>> dists) {

        XYSeriesCollection dataset = createXYSeries(dists);

        String xAxisLabel = "Size";
        String yAxisLabel = "Richness";

        chart = ChartFactory.createXYLineChart(getTitle(), xAxisLabel, yAxisLabel, dataset, PlotOrientation.VERTICAL, true, true, false);
        chart.setBorderPaint(Color.WHITE);
        chart.setBackgroundPaint(Color.WHITE);
        cPanel = new ChartPanel(chart);
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
            public void export() {
                String fname = FileChooserUtils.selectNewFilename(new FileType[]{FileType.PNG});
                if (fname == null) {
                    return;
                }

                try {
                    ChartUtilities.saveChartAsPNG(new File(fname), chart, 1280, 1024);
                } catch (IOException ex) {
                    NotifyDescriptor nd = new NotifyDescriptor.Message("Error: " + ex.getMessage(), NotifyDescriptor.ERROR_MESSAGE);
                    DialogDisplayer.getDefault().notify(nd);
                    return;
                }
                NotifyDescriptor nd = new NotifyDescriptor.Message("Chart saved to " + fname, NotifyDescriptor.INFORMATION_MESSAGE);
                DialogDisplayer.getDefault().notify(nd);
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
