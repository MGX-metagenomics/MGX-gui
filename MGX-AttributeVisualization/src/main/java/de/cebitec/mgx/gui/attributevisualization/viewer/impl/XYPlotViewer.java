package de.cebitec.mgx.gui.attributevisualization.viewer.impl;

import de.cebitec.mgx.gui.attributevisualization.data.VisualizationGroup;
import de.cebitec.mgx.gui.attributevisualization.viewer.NumericalViewerI;
import de.cebitec.mgx.gui.attributevisualization.viewer.ViewerI;
import de.cebitec.mgx.gui.datamodel.Attribute;
import de.cebitec.mgx.gui.datamodel.Distribution;
import de.cebitec.mgx.gui.datamodel.Pair;
import java.awt.Color;
import java.util.List;
import javax.swing.JComponent;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author sj
 */
@ServiceProvider(service = ViewerI.class)
public class XYPlotViewer extends NumericalViewerI {

    private ChartPanel cPanel = null;

    @Override
    public JComponent getComponent() {
        return cPanel;
    }

    @Override
    public String getName() {
        return "XY Plot";
    }

    @Override
    public List<Pair<VisualizationGroup, Distribution>> filter(List<Pair<VisualizationGroup, Distribution>> dists) {
        boolean fractionMode = false;

        XYSeriesCollection dataset = new XYSeriesCollection();

        for (Pair<VisualizationGroup, Distribution> groupDistribution : dists) {
            XYSeries series = new XYSeries(groupDistribution.getFirst().getName());

            for (Pair<Attribute, ? extends Number> entry : groupDistribution.getSecond().getSorted()) {
                series.add(Double.parseDouble(entry.getFirst().getValue()), entry.getSecond());

                // if we encounter any double values here, we are in fraction mode
                fractionMode = entry.getSecond() instanceof Double || fractionMode;
            }
            dataset.addSeries(series);
        }
        
        String xAxisLabel = "";
        String yAxisLabel = fractionMode ? "Fraction" : "Count";

        JFreeChart chart = ChartFactory.createXYLineChart(getTitle(), xAxisLabel, yAxisLabel, dataset, PlotOrientation.VERTICAL, true, true, false);
        chart.setBorderPaint(Color.WHITE);
        cPanel = new ChartPanel(chart);
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        
        ValueAxis valueAxis = plot.getDomainAxis();
        valueAxis.setInverted(!sortAscending());
        //valueAxis.    //setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6));

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        if (fractionMode) {
            rangeAxis.setStandardTickUnits(NumberAxis.createStandardTickUnits());
        } else {
            rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        }
        
        // set the colors
        int i = 0;
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
        for (Pair<VisualizationGroup, Distribution> groupDistribution : dists) {
            renderer.setSeriesPaint(i++, groupDistribution.getFirst().getColor());
        }

        return null;
    }

    @Override
    public Class getInputType() {
        return Distribution.class;
    }
}
