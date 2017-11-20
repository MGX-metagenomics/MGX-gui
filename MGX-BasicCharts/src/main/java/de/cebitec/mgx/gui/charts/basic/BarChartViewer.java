package de.cebitec.mgx.gui.charts.basic;

import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.gui.charts.basic.customizer.BarChartCustomizer;
import de.cebitec.mgx.gui.charts.basic.util.JFreeChartUtil;
import de.cebitec.mgx.gui.charts.basic.util.ScrollableBarChart;
import de.cebitec.mgx.gui.charts.basic.util.SlidingCategoryDataset;
import de.cebitec.mgx.api.groups.ImageExporterI;
import de.cebitec.mgx.api.groups.SequenceExporterI;
import de.cebitec.mgx.common.visualization.CategoricalViewerI;
import de.cebitec.mgx.common.visualization.ViewerI;
import de.cebitec.mgx.gui.charts.basic.util.LogAxis;
import de.cebitec.mgx.gui.seqexporter.SeqExporter;
import java.awt.Color;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.JComponent;
import org.apache.commons.math3.util.FastMath;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.TickUnitSource;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author sjaenick
 */
@ServiceProvider(service = ViewerI.class)
public class BarChartViewer extends CategoricalViewerI<Long> {

    private ChartPanel cPanel = null;
    private BarChartCustomizer customizer = null;
    private JFreeChart chart = null;
    private CategoryDataset dataset;
    private List<Pair<VisualizationGroupI, DistributionI<Double>>> data;

    public BarChartViewer() {
        // disable the stupid glossy effect
        ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
        BarRenderer.setDefaultShadowsVisible(false);
        XYBarRenderer.setDefaultShadowsVisible(false);
    }

    @Override
    public String getName() {
        return "Bar Chart";
    }

    @Override
    public JComponent getComponent() {
        if (dataset instanceof SlidingCategoryDataset) {
            return new ScrollableBarChart(cPanel, (SlidingCategoryDataset) dataset);
        } else {
            return cPanel;
        }
    }

    @Override
    public void show(List<Pair<VisualizationGroupI, DistributionI<Long>>> in) {

        data = getCustomizer().filter(in);

        dataset = JFreeChartUtil.createCategoryDataset(data);

        String xAxisLabel = "";
        String yAxisLabel = getCustomizer().useFractions() ? "Fraction" : "Count";

        chart = ChartFactory.createBarChart(getTitle(), xAxisLabel, yAxisLabel, dataset, PlotOrientation.VERTICAL, true, true, false);

        chart.setBorderPaint(Color.WHITE);
        chart.setBackgroundPaint(Color.WHITE);
        chart.setAntiAlias(true);
        //chart.setTextAntiAlias(RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        cPanel = new ChartPanel(chart);
        CategoryPlot plot = chart.getCategoryPlot();

        plot.setFixedLegendItems(JFreeChartUtil.createLegend(data));
        plot.setBackgroundPaint(Color.WHITE);

        BarRenderer br = (BarRenderer) plot.getRenderer();
        br.setItemMargin(customizer.getItemMargin());
        br.setDefaultToolTipGenerator(new StandardCategoryToolTipGenerator("<html>Group: {0} <br> Attribute: {1} <br> " + yAxisLabel + ": {2}</html>", NumberFormat.getInstance()));
        br.setMaximumBarWidth(.1); // set maximum width to 10% of chart

        // x axis
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryMargin(customizer.getCategoryMargin());
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(FastMath.PI / 6));

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
        if (dataset instanceof SlidingCategoryDataset) {
            SlidingCategoryDataset scd = (SlidingCategoryDataset) dataset;
            rangeAxis.setAutoRange(false);
            rangeAxis.setRange(0, scd.getMaxY());
        }
        plot.setRangeAxis(rangeAxis);

        // colors
        int i = 0;
        CategoryItemRenderer renderer = plot.getRenderer();
        for (Pair<VisualizationGroupI, DistributionI<Double>> groupDistribution : data) {
            renderer.setSeriesPaint(i++, groupDistribution.getFirst().getColor());
        }
    }

    @Override
    public Class getInputType() {
        return DistributionI.class;
    }

    @Override
    public BarChartCustomizer getCustomizer() {
        if (customizer == null) {
            customizer = new BarChartCustomizer();
        }
        customizer.setAttributeType(getAttributeType());
        return customizer;
    }

    @Override
    public ImageExporterI getImageExporter() {
        return JFreeChartUtil.getImageExporter(chart);
    }

    @Override
    public SequenceExporterI[] getSequenceExporters() {
        List<SequenceExporterI> ret = new ArrayList<>(data.size());
        for (Pair<VisualizationGroupI, DistributionI<Double>> p : data) {
            if (p.getSecond().getTotalClassifiedElements() > 0) {
                SequenceExporterI exp = new SeqExporter<>(p.getFirst(), p.getSecond());
                ret.add(exp);
            }
        }
        return ret.toArray(new SequenceExporterI[]{});
    }

}
