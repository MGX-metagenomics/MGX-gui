package de.cebitec.mgx.gui.attributevisualization.viewer.impl;

import de.cebitec.mgx.gui.attributevisualization.data.VisualizationGroup;
import de.cebitec.mgx.gui.attributevisualization.viewer.CategoricalViewerI;
import de.cebitec.mgx.gui.attributevisualization.viewer.ViewerI;
import de.cebitec.mgx.gui.datamodel.Attribute;
import de.cebitec.mgx.gui.datamodel.Distribution;
import de.cebitec.mgx.gui.datamodel.Pair;
import java.awt.Color;
import java.util.Collections;
import java.util.List;
import javax.swing.JComponent;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author sjaenick
 */
@ServiceProvider(service = ViewerI.class)
public class BarChartViewer extends CategoricalViewerI {

    private ChartPanel cPanel = null;

    public BarChartViewer() {
    }

    @Override
    public String getName() {
        return "Bar Chart";
    }

    @Override
    public JComponent getComponent() {
        return cPanel;
    }

    @Override
    public List<Pair<VisualizationGroup, Distribution>> filter(List<Pair<VisualizationGroup, Distribution>> dists) {

        boolean fractionMode = false;

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (Pair<VisualizationGroup, Distribution> groupDistribution : dists) {

            List<Pair<Attribute, ? extends Number>> sorted = groupDistribution.getSecond().getSorted();
            if (sortAscending()) {
                Collections.reverse(sorted);
            }

            for (Pair<Attribute, ? extends Number> entry : sorted) {
                dataset.addValue(entry.getSecond(), groupDistribution.getFirst().getName(), entry.getFirst().getValue());

                // if we encounter any double values here, we are in fraction mode
                fractionMode = entry.getSecond() instanceof Double || fractionMode;
            }
        }

        String xAxisLabel = "";
        String yAxisLabel = fractionMode ? "Fraction" : "Count";

        // disable the stupid glossy effect
        ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
        BarRenderer.setDefaultShadowsVisible(false);
        XYBarRenderer.setDefaultShadowsVisible(false);

        JFreeChart chart = ChartFactory.createBarChart(getTitle(), xAxisLabel, yAxisLabel, dataset, PlotOrientation.VERTICAL, true, true, false);
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

    @Override
    public Class getInputType() {
        return Distribution.class;
    }
}
