package de.cebitec.mgx.gui.charts.basic;

import de.cebitec.mgx.gui.attributevisualization.viewer.CategoricalViewerI;
import de.cebitec.mgx.gui.attributevisualization.viewer.ViewerI;
import de.cebitec.mgx.gui.datamodel.Attribute;
import de.cebitec.mgx.gui.datamodel.Distribution;
import de.cebitec.mgx.gui.datamodel.Pair;
import de.cebitec.mgx.gui.groups.VisualizationGroup;
import java.awt.Color;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import javax.swing.JComponent;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.LogarithmicAxis;
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
    private BarChartCustomizer customizer = null;

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

//    private List<Pair<VisualizationGroup, Distribution>> filter(List<Pair<VisualizationGroup, Distribution>> dists) {
//
//        if (getCustomizer().useFractions()) {
//            VisFilterI fracFilter = new ToFractionFilter();
//            dists = fracFilter.filter(dists);
//        }
//
//        SortOrder sorter = new SortOrder(getAttributeType());
//        dists = sorter.filter(dists);
//
//        return dists;
//    }

    @Override
    public void show(List<Pair<VisualizationGroup, Distribution>> dists) {

        dists = getCustomizer().filter(dists);

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Pair<VisualizationGroup, Distribution> groupDistribution : dists) {
            Distribution d = groupDistribution.getSecond();
            //Set<Entry<Attribute, Number>> sorted = groupDistribution.getSecond().entrySet();
            
//            if (getCustomizer().getSortAscending()) {
//                Collections.reverse(d.keySet());
//            }

            for (Entry<Attribute, ? extends Number> entry : d.entrySet()) {
                if (entry.getValue().doubleValue() <= 0.0) {
                    System.err.println("error at "+entry.getKey().getValue());
                }
                dataset.addValue(entry.getValue(), groupDistribution.getFirst().getName(), entry.getKey().getValue());
            }
        }
        String xAxisLabel = "";
        String yAxisLabel = getCustomizer().useFractions() ? "Fraction" : "Count";

        // disable the stupid glossy effect
        ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
        BarRenderer.setDefaultShadowsVisible(
                false);
        XYBarRenderer.setDefaultShadowsVisible(
                false);

        JFreeChart chart = ChartFactory.createBarChart(getTitle(), xAxisLabel, yAxisLabel, dataset, PlotOrientation.VERTICAL, true, true, false);

        chart.setBorderPaint(Color.WHITE);

        chart.setBackgroundPaint(Color.WHITE);
        cPanel = new ChartPanel(chart);
        CategoryPlot plot = chart.getCategoryPlot();

        plot.setBackgroundPaint(Color.WHITE);
        CategoryAxis domainAxis = plot.getDomainAxis();

        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6));

        if (getCustomizer().logY()) {
            final NumberAxis rangeAxis = new LogarithmicAxis("log("+ yAxisLabel +")");
            plot.setRangeAxis(rangeAxis);
        }
        
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        //plot.setRangeAxis(rangeAxis);
        
        if (getCustomizer().useFractions()) {
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
    }

    @Override
    public Class getInputType() {
        return Distribution.class;
    }

    @Override
    public BarChartCustomizer getCustomizer() {
        if (customizer == null) {
            customizer = new BarChartCustomizer();
            customizer.setAttributeType(getAttributeType());
        }
        return customizer;
    }
}
