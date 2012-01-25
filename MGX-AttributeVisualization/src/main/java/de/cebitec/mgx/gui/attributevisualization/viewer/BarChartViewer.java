package de.cebitec.mgx.gui.attributevisualization.viewer;

import de.cebitec.mgx.gui.attributevisualization.Pair;
import de.cebitec.mgx.gui.attributevisualization.data.Distribution;
import de.cebitec.mgx.gui.attributevisualization.data.VisualizationGroup;
import de.cebitec.mgx.gui.datamodel.Attribute;
import java.awt.Color;
import java.util.List;
import javax.swing.JComponent;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author sjaenick
 */
@ServiceProvider(service = ViewerI.class)
public class BarChartViewer extends ViewerI {

    private String chartTitle;
    private ChartPanel cPanel = null;

    public BarChartViewer() {
    }

    @Override
    public String getName() {
        return "Bar Chart";
    }
    
    @Override
    public void setTitle(String title) {
        chartTitle = title;
    }

    @Override
    public JComponent getComponent() {
        return cPanel;
    }

    @Override
    public List<Pair<VisualizationGroup, Distribution>> filter(List<Pair<VisualizationGroup, Distribution>> dists) {

        System.err.println("barChart.filter()");
        boolean fractionMode = false;

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (Pair<VisualizationGroup, Distribution> groupDistribution : dists) {
            VisualizationGroup vg = groupDistribution.getFirst();
            Distribution dist = groupDistribution.getSecond();

            for (Pair<Attribute, ? extends Number> entry : dist.get()) {
                dataset.addValue(entry.getSecond(), vg.getName(), entry.getFirst().getValue());

                // if we encounter any double values here, we are in fraction mode
                if (entry.getSecond() instanceof Double) {
                    fractionMode = fractionMode || true;
                }

            }
        }

        String xAxisLabel = "";
        String yAxisLabel = fractionMode ? "Fraction" : "Count";

        JFreeChart chart = ChartFactory.createBarChart(chartTitle, xAxisLabel, yAxisLabel, dataset, PlotOrientation.VERTICAL, true, true, false);
        chart.setBorderPaint(Color.WHITE);
        cPanel = new ChartPanel(chart);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6));

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        if (fractionMode) {
            rangeAxis.setStandardTickUnits(NumberAxis.createStandardTickUnits());
        } else {
            rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        }

        // set the colors
        int i = 0;
        CategoryItemRenderer renderer = chart.getCategoryPlot().getRenderer();
        for (Pair<VisualizationGroup, Distribution> groupDistribution : dists) {
            renderer.setSeriesPaint(i++, groupDistribution.getFirst().getColor());
        }
        return null;
    }
}
