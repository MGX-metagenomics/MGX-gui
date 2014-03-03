package de.cebitec.mgx.gui.charts.basic;

import de.cebitec.mgx.gui.attributevisualization.filter.ToFractionFilter;
import de.cebitec.mgx.gui.attributevisualization.viewer.NumericalViewerI;
import de.cebitec.mgx.gui.attributevisualization.viewer.ViewerI;
import de.cebitec.mgx.gui.charts.basic.util.JFreeChartUtil;
import de.cebitec.mgx.gui.datamodel.Attribute;
import de.cebitec.mgx.gui.datamodel.AttributeType;
import de.cebitec.mgx.gui.datamodel.misc.Distribution;
import de.cebitec.mgx.gui.datamodel.misc.Pair;
import de.cebitec.mgx.gui.groups.ImageExporterI;
import de.cebitec.mgx.gui.groups.VGroupManager;
import de.cebitec.mgx.gui.groups.VisualizationGroup;
import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.JComponent;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.TickUnitSource;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYDotRenderer;
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
    private Map<XYDataItem, String> toolTips = new HashMap<>();

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
        return Distribution.class;
    }

    @Override
    public void show(List<Pair<VisualizationGroup, Distribution>> dists) {
        List<Pair<VisualizationGroup, Distribution>> relevant = dists.subList(0, 2);

        ToFractionFilter tof = new ToFractionFilter();
        relevant = tof.filter(relevant);

        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries series = new XYSeries("");

        Distribution first = relevant.get(0).getSecond();
        Distribution second = relevant.get(1).getSecond();

        for (Entry<Attribute, Number> e : first.entrySet()) {
            // extract attributes occuring in both datasets
            if (second.keySet().contains(e.getKey())) {
                double firstVal = e.getValue().doubleValue();
                double secondVal = second.get(e.getKey()).doubleValue();

                double x = (Math.log(firstVal) + Math.log(secondVal)) / 2;
                double y = Math.log(firstVal / secondVal);
                XYDataItem item = new XYDataItem(x, y);
                series.add(item);

                long numAssigned1 = Math.round(firstVal * first.getTotalClassifiedElements());
                long numAssigned2 = Math.round(secondVal * second.getTotalClassifiedElements());

                String toolTipText = new StringBuilder("<html>")
                        .append(e.getKey().getValue())
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

        String xAxisLabel = "log(" + relevant.get(0).getFirst().getName() + ") + "
                + "log(" + relevant.get(1).getFirst().getName() + ") / 2";
        String yAxisLabel = "log(" + relevant.get(0).getFirst().getName() + "/"
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
    public boolean canHandle(AttributeType valueType) {
        return valueType.getValueType() == AttributeType.VALUE_DISCRETE
                && VGroupManager.getInstance().getActiveGroups().size() == 2;
    }

    @Override
    public void setAttributeType(AttributeType aType) {
        super.setAttributeType(aType);
        super.setTitle("M/A plot for " + aType.getName());
    }

    @Override
    public ImageExporterI getImageExporter() {
        return JFreeChartUtil.getImageExporter(chart);
    }
}
