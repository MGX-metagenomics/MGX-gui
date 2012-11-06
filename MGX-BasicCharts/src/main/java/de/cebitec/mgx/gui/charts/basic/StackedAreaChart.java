package de.cebitec.mgx.gui.charts.basic;

import de.cebitec.mgx.gui.attributevisualization.VGroupManager;
import de.cebitec.mgx.gui.attributevisualization.viewer.ViewerI;
import de.cebitec.mgx.gui.charts.basic.util.JFreeChartUtil;
import de.cebitec.mgx.gui.datamodel.AttributeType;
import de.cebitec.mgx.gui.datamodel.misc.Distribution;
import de.cebitec.mgx.gui.datamodel.misc.Pair;
import de.cebitec.mgx.gui.groups.VisualizationGroup;
import java.awt.Color;
import java.util.List;
import java.util.Locale;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.TickUnitSource;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.CategoryDataset;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author sjaenick
 */
@ServiceProvider(service = ViewerI.class)
public class StackedAreaChart extends AreaChart {

    @Override
    public String getName() {
        return "Stacked Area Chart";
    }

    @Override
    public boolean canHandle(AttributeType valueType) {
        return ((super.canHandle(valueType)) && (VGroupManager.getInstance().getActiveGroups().size() > 1));
    }
    
    

    @Override
    public void show(List<Pair<VisualizationGroup, Distribution>> dists) {

        dists = getCustomizer().filter(dists);
        CategoryDataset dataset = JFreeChartUtil.createCategoryDataset(dists);

        String xAxisLabel = "";
        String yAxisLabel = getCustomizer().useFractions() ? "Fraction" : "Count";

        chart = ChartFactory.createStackedAreaChart(getTitle(), xAxisLabel, yAxisLabel, dataset, PlotOrientation.VERTICAL, true, true, false);
        chart.setBorderPaint(Color.WHITE);
        chart.setBackgroundPaint(Color.WHITE);
        cPanel = new ChartPanel(chart);
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);

        // x axis
        CategoryAxis valueAxis;
//        final TickUnitSource tusX;
//        if (getCustomizer().logX()) {
//            valueAxis = new LogarithmicAxis("log(" + xAxisLabel + ")");
//            ((LogarithmicAxis) valueAxis).setStrictValuesFlag(false);
//            tusX = LogAxis.createLogTickUnits(Locale.getDefault());
//
//        } else {
//            valueAxis = (CategoryAxis) plot.getDomainAxis();
//            tusX = NumberAxis.createIntegerTickUnits();
//        }
//        valueAxis.setStandardTickUnits(tusX);
//        valueAxis.setInverted(!getCustomizer().getSortAscending());
//        plot.setDomainAxis(valueAxis);
        plot.getDomainAxis().setCategoryMargin(0); 

        // y axis
        final NumberAxis rangeAxis;
        final TickUnitSource tus;
        if (getCustomizer().logY()) {
            rangeAxis = new LogarithmicAxis("log(" + yAxisLabel + ")");
            ((LogarithmicAxis) rangeAxis).setStrictValuesFlag(false);
            tus = LogAxis.createLogTickUnits(Locale.getDefault());

        } else {
            rangeAxis = (NumberAxis) plot.getRangeAxis();
            if (getCustomizer().useFractions()) {
                tus = NumberAxis.createStandardTickUnits();
            } else {
                tus = NumberAxis.createIntegerTickUnits();
            }
        }
        rangeAxis.setStandardTickUnits(tus);
        plot.setRangeAxis(rangeAxis);


        // colors
        int i = 0;
        CategoryItemRenderer renderer = chart.getCategoryPlot().getRenderer();
        for (Pair<VisualizationGroup, Distribution> groupDistribution : dists) {
            renderer.setSeriesPaint(i++, groupDistribution.getFirst().getColor());
        }
    }
}
