package de.cebitec.mgx.gui.charts.basic;

import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.gui.charts.basic.util.JFreeChartUtil;
import de.cebitec.mgx.api.groups.ImageExporterI;
import de.cebitec.mgx.api.visualization.filter.ToFractionFilter;
import de.cebitec.mgx.common.VGroupManager;
import de.cebitec.mgx.common.visualization.NumericalViewerI;
import de.cebitec.mgx.common.visualization.ViewerI;
import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JComponent;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.TickUnitSource;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author sjaenick
 */
@ServiceProvider(service = ViewerI.class)
public class MAPlot extends NumericalViewerI {

    private ChartPanel cPanel = null;
    private JFreeChart chart = null;
    private final Map<XYDataItem, String> toolTips = new HashMap<>();

    @Override
    public JComponent getComponent() {
        return cPanel;
    }

    @Override
    public String getName() {
        return "M/A Plot";
    }

    @Override
    public Class getInputType() {
        return DistributionI.class;
    }

    @Override
    public void show(List<Pair<VisualizationGroupI, DistributionI>> dists) {
        List<Pair<VisualizationGroupI, DistributionI>> relevant = dists.subList(0, 2);

        ToFractionFilter tof = new ToFractionFilter();
        relevant = tof.filter(relevant);

        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries series = new XYSeries("");

        DistributionI first = relevant.get(0).getSecond();
        DistributionI second = relevant.get(1).getSecond();

        Set<AttributeI> attrs = new HashSet<>();
        attrs.addAll(first.keySet());
        attrs.addAll(second.keySet());

        for (AttributeI a : attrs) {
            XYDataItem item = null;
            double x, y;
            double firstVal = 0, secondVal = 0;

            if (first.keySet().contains(a) && second.keySet().contains(a)) {
                // attribute occurs in both distributions
                firstVal = first.get(a).doubleValue();
                secondVal = second.get(a).doubleValue();
                x = (log2(firstVal) + log2(secondVal)) / 2;
                y = log2(firstVal / secondVal);
                item = new XYDataItem(x, y);
//            } else if (first.keySet().contains(a) && !second.keySet().contains(a)) {
//                // attribute occurs in first distribution only
//                firstVal = first.get(a).doubleValue();
//                secondVal = 0;
//                x = (log2(firstVal) + log2(secondVal)) / 2;
//                y = log2(firstVal / secondVal);
//                item = new XYDataItem(x, y);
//            } else if (second.keySet().contains(a) && !first.keySet().contains(a)) {
//                // attribute occurs in second distribution only
//                firstVal = 0;
//                secondVal = second.get(a).doubleValue();
//                x = (log2(firstVal) + log2(secondVal)) / 2;
//                y = log2(firstVal / secondVal);
//                item = new XYDataItem(x, y);
            }

            if (item != null) {
                series.add(item);

                long numAssigned1 = Math.round(firstVal * first.getTotalClassifiedElements());
                long numAssigned2 = Math.round(secondVal * second.getTotalClassifiedElements());

                String toolTipText = new StringBuilder("<html>")
                        .append(a.getValue())
                        .append("<br><br>")
                        .append(relevant.get(0).getFirst().getName())
                        .append(": ").append(numAssigned1).append(" sequences").append("<br>")
                        .append(relevant.get(1).getFirst().getName())
                        .append(": ").append(numAssigned2).append(" sequences")
                        .append("</html>")
                        .toString();

                toolTips.put(item, toolTipText);
            }
        }

        dataset.addSeries(series);

        String xAxisLabel = "log2(" + relevant.get(0).getFirst().getName() + ") + "
                + "log2(" + relevant.get(1).getFirst().getName() + ") / 2";
        String yAxisLabel = "log2(" + relevant.get(0).getFirst().getName() + "/"
                + relevant.get(1).getFirst().getName() + ")";

        chart = ChartFactory.createXYLineChart(getTitle(), xAxisLabel, yAxisLabel, dataset, PlotOrientation.VERTICAL, false, true, false);
        chart.setBorderPaint(Color.WHITE);
        chart.setBackgroundPaint(Color.WHITE);
        cPanel = new ChartPanel(chart);
        cPanel.setDisplayToolTips(true);
        XYPlot plot = (XYPlot) chart.getPlot();

        plot.setBackgroundPaint(Color.WHITE);

        // x axis
        final ValueAxis valueAxis;
        final TickUnitSource tusX;
        valueAxis = (NumberAxis) plot.getDomainAxis();
        tusX = NumberAxis.createStandardTickUnits();
        valueAxis.setStandardTickUnits(tusX);
        valueAxis.setInverted(false);
        plot.setDomainAxis(valueAxis);

        // y axis
        final NumberAxis rangeAxis;
        final TickUnitSource tus;
        rangeAxis = (NumberAxis) plot.getRangeAxis();
        tus = NumberAxis.createStandardTickUnits();
        rangeAxis.setStandardTickUnits(tus);
        plot.setRangeAxis(rangeAxis);

        final XYLineAndShapeRenderer r = new XYLineAndShapeRenderer();
        r.setBaseLinesVisible(false);
        r.setSeriesShape(0, new Ellipse2D.Double(0, 0, 5, 5));

        r.setBaseToolTipGenerator(new XYToolTipGenerator() {

            @Override
            public String generateToolTip(XYDataset xyd, int series, int item) {
                XYSeriesCollection dataset = (XYSeriesCollection) xyd;
                XYDataItem dataItem = dataset.getSeries(series).getDataItem(item);
                return toolTips.get(dataItem);
            }
        });

        plot.setRenderer(r);
    }

    @Override
    public JComponent getCustomizer() {
        return null;
    }

    @Override
    public boolean canHandle(AttributeTypeI valueType) {
        return valueType.getValueType() == AttributeTypeI.VALUE_DISCRETE
                && VGroupManager.getInstance().getActiveGroups().size() == 2;
    }

    @Override
    public void setAttributeType(AttributeTypeI aType) {
        super.setAttributeType(aType);
        super.setTitle("M/A plot for " + aType.getName());
    }

    @Override
    public ImageExporterI getImageExporter() {
        return JFreeChartUtil.getImageExporter(chart);
    }

    private final static double log2 = Math.log(2);

    private double log2(double d) {
        return Math.log(d) / log2;
    }
}
