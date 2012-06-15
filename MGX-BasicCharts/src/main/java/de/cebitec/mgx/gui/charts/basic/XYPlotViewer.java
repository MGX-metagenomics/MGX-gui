package de.cebitec.mgx.gui.charts.basic;

import de.cebitec.mgx.gui.attributevisualization.viewer.NumericalViewerI;
import de.cebitec.mgx.gui.attributevisualization.viewer.ViewerI;
import de.cebitec.mgx.gui.datamodel.Attribute;
import de.cebitec.mgx.gui.datamodel.Distribution;
import de.cebitec.mgx.gui.datamodel.Pair;
import de.cebitec.mgx.gui.groups.VisualizationGroup;
import java.awt.Color;
import java.util.List;
import java.util.Map.Entry;
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
    private XYPlotCustomizer customizer = null;

    @Override
    public JComponent getComponent() {
        return cPanel;
    }

    @Override
    public String getName() {
        return "XY Plot";
    }

    @Override
    public void show(List<Pair<VisualizationGroup, Distribution>> dists) {
        
        dists = getCustomizer().filter(dists);

        XYSeriesCollection dataset = new XYSeriesCollection();

        for (Pair<VisualizationGroup, Distribution> groupDistribution : dists) {
            XYSeries series = new XYSeries(groupDistribution.getFirst().getName());

            for (Entry<Attribute, ? extends Number> entry : groupDistribution.getSecond().entrySet()) {
                series.add(Double.parseDouble(entry.getKey().getValue()), entry.getValue());
            }
            dataset.addSeries(series);
        }

        String xAxisLabel = "";
        String yAxisLabel = getCustomizer().useFractions() ? "Fraction" : "Count";

        JFreeChart chart = ChartFactory.createXYLineChart(getTitle(), xAxisLabel, yAxisLabel, dataset, PlotOrientation.VERTICAL, true, true, false);
        chart.setBorderPaint(Color.WHITE);
        chart.setBackgroundPaint(Color.WHITE);
        cPanel = new ChartPanel(chart);
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);

        ValueAxis valueAxis = plot.getDomainAxis();
        valueAxis.setInverted(!getCustomizer().getSortAscending());
        //valueAxis.    //setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6));

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        if (getCustomizer().useFractions()) {
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

    }

    @Override
    public XYPlotCustomizer getCustomizer() {
        if (customizer == null) {
            customizer = new XYPlotCustomizer();
            customizer.setAttributeType(getAttributeType());
        }
        return customizer;
    }

    @Override
    public Class getInputType() {
        return Distribution.class;
    }
}
