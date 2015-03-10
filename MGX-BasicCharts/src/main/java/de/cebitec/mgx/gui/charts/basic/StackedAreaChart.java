package de.cebitec.mgx.gui.charts.basic;

import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.common.VGroupManager;
import de.cebitec.mgx.common.visualization.ViewerI;
import de.cebitec.mgx.gui.charts.basic.util.JFreeChartUtil;
import java.awt.Color;
import java.util.List;
import java.util.Locale;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.TickUnitSource;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StackedXYAreaRenderer2;
import org.jfree.data.xy.TableXYDataset;
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
    public boolean canHandle(AttributeTypeI valueType) {
        return super.canHandle(valueType) && VGroupManager.getInstance().getActiveGroups().size() > 1;
    }

    @Override
    public void show(List<Pair<VisualizationGroupI, DistributionI<Long>>> in) {

        LegendItemCollection legend = JFreeChartUtil.createLegend(in);
        List<Pair<VisualizationGroupI, DistributionI<Double>>> data = getCustomizer().filter(in);
        TableXYDataset dataset = JFreeChartUtil.createTableXYDataset(data);

        String xAxisLabel = "";
        String yAxisLabel = useFractions() ? "Fraction" : "Count";

        chart = ChartFactory.createStackedXYAreaChart(getTitle(), xAxisLabel, yAxisLabel, dataset, PlotOrientation.VERTICAL, true, true, false);

        chart.setBorderPaint(Color.WHITE);
        chart.setBackgroundPaint(Color.WHITE);
        cPanel = new ChartPanel(chart);
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);

        plot.setFixedLegendItems(legend);

        // x axis
        ValueAxis valueAxis;
        final TickUnitSource tusX;
        if (getCustomizer().logX()) {
            valueAxis = new LogarithmicAxis("log(" + xAxisLabel + ")");
            ((LogarithmicAxis) valueAxis).setStrictValuesFlag(false);
            tusX = LogAxis.createLogTickUnits(Locale.getDefault());

        } else {
            valueAxis = (NumberAxis) plot.getDomainAxis();
            tusX = NumberAxis.createIntegerTickUnits();
        }
        valueAxis.setStandardTickUnits(tusX);
        valueAxis.setInverted(!getCustomizer().getSortAscending());
        plot.setDomainAxis(valueAxis);

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

        // set the colors
        int i = 0;
        StackedXYAreaRenderer2 renderer = (StackedXYAreaRenderer2) plot.getRenderer();
        for (Pair<VisualizationGroupI, DistributionI<Double>> groupDistribution : data) {
            renderer.setSeriesPaint(i++, groupDistribution.getFirst().getColor());
        }
    }
}
