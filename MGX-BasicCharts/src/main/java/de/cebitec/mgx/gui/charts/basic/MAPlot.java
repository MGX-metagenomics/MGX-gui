package de.cebitec.mgx.gui.charts.basic;

import de.cebitec.mgx.api.groups.ConflictingJobsException;
import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.gui.charts.basic.util.JFreeChartUtil;
import de.cebitec.mgx.api.groups.ImageExporterI;
import de.cebitec.mgx.gui.vizfilter.ToFractionFilter;
import de.cebitec.mgx.common.VGroupManager;
import de.cebitec.mgx.common.visualization.NumericalViewerI;
import de.cebitec.mgx.common.visualization.ViewerI;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JComponent;
import org.apache.commons.math3.util.FastMath;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.AxisState;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTick;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author sjaenick
 */
@ServiceProvider(service = ViewerI.class)
public class MAPlot extends NumericalViewerI<Long> {

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
    public void show(List<Pair<VisualizationGroupI, DistributionI<Long>>> dists) {

        if (dists.size() != 2) {
            // should not happen, see canHandle() implementation
            assert false;
        }

        List<Pair<VisualizationGroupI, DistributionI<Long>>> firstTwo = dists.subList(0, 2);
        DistributionI<Long> firstNonNormalized = firstTwo.get(0).getSecond();
        DistributionI<Long> secondNonNormalized = firstTwo.get(1).getSecond();

        // normalize to fractions
        ToFractionFilter tof = new ToFractionFilter();
        List<Pair<VisualizationGroupI, DistributionI<Double>>> cur = tof.filter(firstTwo);
        DistributionI<Double> first = cur.get(0).getSecond();
        DistributionI<Double> second = cur.get(1).getSecond();

        XYSeriesCollection normal = new XYSeriesCollection();
        XYSeriesCollection posInf = new XYSeriesCollection();
        XYSeriesCollection negInf = new XYSeriesCollection();
        XYSeries nor = new XYSeries("Normal");
        XYSeries pos = new XYSeries("Positiv Infinit");
        XYSeries neg = new XYSeries("Negativ Infinit");

        Set<AttributeI> attrs = new HashSet<>(first.size());
        attrs.addAll(first.keySet());
        attrs.addAll(second.keySet());
        
        // a small offset is added to all values to avoid log(0)
        double offset = 0.0001;
        double logOffset = log2(offset);

        for (AttributeI a : attrs) {
            XYDataItem item = null;
            double x, y;
            double firstVal = 0, secondVal = 0;

            if (first.keySet().contains(a) && second.keySet().contains(a)) {
                // attribute occurs in both distributions
                firstVal = offset + first.get(a);
                secondVal = offset + second.get(a);
                x = (log2(firstVal) + log2(secondVal)) / 2;
                y = log2(firstVal / secondVal);
                item = new XYDataItem(x, y);
                nor.add(item);
            } else if (first.keySet().contains(a) && !second.keySet().contains(a)) {
                // attribute occurs in first distribution only
                firstVal = offset + first.get(a);
                x = (log2(firstVal) + logOffset) / 2;
                y = 0;
                item = new XYDataItem(x, y);
                pos.add(item);
            } else if (second.keySet().contains(a) && !first.keySet().contains(a)) {
                // attribute occurs in second distribution only
                secondVal = offset + second.get(a);
                x = (logOffset + log2(secondVal)) / 2;
                y = 0;
                item = new XYDataItem(x, y);
                neg.add(item);
            }

            // create tooltip text
            long numAssigned1 = firstNonNormalized.containsKey(a) ? firstNonNormalized.get(a) : 0;
            long numAssigned2 = secondNonNormalized.containsKey(a) ? secondNonNormalized.get(a) : 0;
            String toolTipText = new StringBuilder("<html>")
                    .append(a.getValue())
                    .append("<br><br>")
                    .append(cur.get(0).getFirst().getName())
                    .append(": ").append(numAssigned1).append(" sequences").append("<br>")
                    .append(cur.get(1).getFirst().getName())
                    .append(": ").append(numAssigned2).append(" sequences")
                    .append("</html>")
                    .toString();
            toolTips.put(item, toolTipText);
        }

        normal.addSeries(nor);
        posInf.addSeries(pos);
        negInf.addSeries(neg);

        String xAxisLabel = "log2(" + cur.get(0).getFirst().getName() + ") + "
                + "log2(" + cur.get(1).getFirst().getName() + ") / 2";
        String yAxisLabel = "log2(" + cur.get(0).getFirst().getName() + "/"
                + cur.get(1).getFirst().getName() + ")";

        XYToolTipGenerator tooltipGenerator = new XYToolTipGenerator() {

            @Override
            public String generateToolTip(XYDataset xyd, int series, int item) {
                XYSeriesCollection dataset = (XYSeriesCollection) xyd;
                XYDataItem dataItem = dataset.getSeries(series).getDataItem(item);
                return toolTips.get(dataItem);
            }
        };

        chart = createCombinedChart(normal, posInf, negInf, xAxisLabel, yAxisLabel, tooltipGenerator);
        chart.removeLegend();
        cPanel = new ChartPanel(chart, true, false, true, true, true);
        cPanel.setInitialDelay(0);
        cPanel.setMaximumDrawHeight(1080);
        cPanel.setMaximumDrawWidth(1920);
        cPanel.setMouseWheelEnabled(true);
        cPanel.setMouseZoomable(true);

        chart.setBorderPaint(Color.WHITE);
        chart.setBackgroundPaint(Color.WHITE);
        cPanel.setDisplayToolTips(true);
        XYPlot plot = (XYPlot) chart.getPlot();

        plot.setBackgroundPaint(Color.WHITE);

    }

    private static JFreeChart createCombinedChart(XYSeriesCollection normal,
            XYSeriesCollection posInf, XYSeriesCollection negInf, String xAxisLabel, String yAxisLabel, XYToolTipGenerator toolTip) {

        final XYLineAndShapeRenderer r = new XYLineAndShapeRenderer();
        r.setBaseLinesVisible(false);
        r.setSeriesShape(0, new Ellipse2D.Double(0, 0, 5, 5));
        r.setBaseToolTipGenerator(toolTip);

        NumberAxis xAxis = new NumberAxis(xAxisLabel);
        xAxis.setInverted(false);
        xAxis.setAutoRange(true);
        xAxis.setTickLabelsVisible(false);

        NumberAxis yAxis = new NumberAxis(yAxisLabel);

        XYPlot midplot = new XYPlot(normal, xAxis, yAxis, r);
        midplot.setBackgroundPaint(Color.WHITE);
        midplot.setRangeAxisLocation(AxisLocation.TOP_OR_LEFT);

        // create subplot 2...
        NumberAxis rangeAxis2 = new NumberAxis() {
            @Override
            public List refreshTicks(Graphics2D g2, AxisState state, Rectangle2D dataArea, RectangleEdge edge) {
                List<NumberTick> myTicks = new ArrayList<>();
                myTicks.add(new NumberTick(0, "-Inf", TextAnchor.CENTER_RIGHT, TextAnchor.CENTER_RIGHT, 0.0));
                return myTicks;
            }
        };
        rangeAxis2.setAutoRangeIncludesZero(false);
        final XYPlot subplot2 = new XYPlot(negInf, xAxis, rangeAxis2, r);
        subplot2.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
        
        
        // create subplot 3...
        final NumberAxis rangeAxis3 = new NumberAxis() {
            @Override
            public List refreshTicks(Graphics2D g2, AxisState state, Rectangle2D dataArea, RectangleEdge edge) {
                List<NumberTick> myTicks = new ArrayList<>();
                myTicks.add(new NumberTick(0, "Inf", TextAnchor.CENTER_RIGHT, TextAnchor.CENTER_RIGHT, 0.0));
                return myTicks;
            }
        };
        rangeAxis3.setAutoRangeIncludesZero(false);
        final XYPlot subplot3 = new XYPlot(posInf, xAxis, rangeAxis3, r);
        subplot2.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
        
        // parent plot...
        CombinedDomainXYPlot plot = new CombinedDomainXYPlot(xAxis);
        plot.setGap(0);
        plot.add(subplot3, 1);
        plot.add(midplot, 15);
        plot.add(subplot2, 1);
        plot.setOrientation(PlotOrientation.VERTICAL);
        plot.setBackgroundPaint(Color.WHITE);

        return new JFreeChart(plot);

    }

    @Override
    public JComponent getCustomizer() {
        return null;
    }

    @Override
    public boolean canHandle(AttributeTypeI valueType) {
        try {
            return valueType.getValueType() == AttributeTypeI.VALUE_DISCRETE
                    && VGroupManager.getInstance().getActiveVizGroups().size() == 2
                    && VGroupManager.getInstance().getDistributions().size() == 2;
        } catch (ConflictingJobsException ex) {
            return false;
        }
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

    private final static double log2 = FastMath.log(2);

    private double log2(double d) {
        return FastMath.log(d) / log2;
    }
}
