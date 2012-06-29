package de.cebitec.mgx.gui.charts.basic;

import de.cebitec.mgx.gui.attributevisualization.VGroupManager;
import de.cebitec.mgx.gui.attributevisualization.filter.ToFractionFilter;
import de.cebitec.mgx.gui.attributevisualization.viewer.NumericalViewerI;
import de.cebitec.mgx.gui.attributevisualization.viewer.ViewerI;
import de.cebitec.mgx.gui.datamodel.Attribute;
import de.cebitec.mgx.gui.datamodel.AttributeType;
import de.cebitec.mgx.gui.datamodel.Distribution;
import de.cebitec.mgx.gui.datamodel.Pair;
import de.cebitec.mgx.gui.groups.VisualizationGroup;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JComponent;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.TickUnitSource;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYDotRenderer;
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
    private XYPlotCustomizer customizer = null;
    private JFreeChart chart = null;

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
        List<XYTextAnnotation> annot = new ArrayList<>();

        Distribution first = relevant.get(0).getSecond();
        Distribution second = relevant.get(1).getSecond();

        for (Attribute attr : first.keySet()) {
            // extract attributes occuring in both datasets
            if (second.keySet().contains(attr)) {
                double firstVal = first.get(attr).doubleValue();
                double secondVal = second.get(attr).doubleValue();

                double x = (Math.log(firstVal) + Math.log(secondVal)) / 2;
                double y = Math.log(firstVal / secondVal);
                series.add(x, y);

                XYTextAnnotation xyTextAnnotation = new XYTextAnnotation(attr.getValue(), x + 1, y + 1);
                annot.add(xyTextAnnotation);
            }
        }
        dataset.addSeries(series);

        String xAxisLabel = "log(" + relevant.get(0).getFirst().getName() + ") + "
                + "log(" + relevant.get(1).getFirst().getName() + ") / 2";
        String yAxisLabel = "log(" + relevant.get(0).getFirst().getName() + "/"
                + relevant.get(1).getFirst().getName() + ")";

        chart = ChartFactory.createXYLineChart(getTitle(), xAxisLabel, yAxisLabel, dataset, PlotOrientation.VERTICAL, true, true, false);
        chart.setBorderPaint(Color.WHITE);
        chart.setBackgroundPaint(Color.WHITE);
        cPanel = new ChartPanel(chart);
        cPanel.setDisplayToolTips(true);
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);

        // x axis
        ValueAxis valueAxis;
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


        XYDotRenderer dot = new XYDotRenderer();
        dot.setDotHeight(5);
        dot.setDotWidth(5);
        dot.setBaseToolTipGenerator(new StandardXYToolTipGenerator() {

            private static final long serialVersionUID = 1L;

            @Override
            public String generateToolTip(XYDataset dataset, int series, int item) {
                String toolTipStr = "asdf";
                return toolTipStr;
            }
        });

        plot.setRenderer(dot);
//        
//        for (XYTextAnnotation a : annot) {
//            dot.addAnnotation(a);
//        }
//        // set the colors
//        int i = 0;
//        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
//        for (Pair<VisualizationGroup, Distribution> groupDistribution : dists) {
//            renderer.setSeriesPaint(i++, groupDistribution.getFirst().getColor());
//        }
    }

    @Override
    public JComponent getCustomizer() {
        return customizer;
    }

    @Override
    public boolean canHandle(AttributeType valueType) {
        return valueType.getValueType() == AttributeType.VALUE_DISCRETE
                && VGroupManager.getInstance().getActiveGroups().size() == 2;
    }
}
