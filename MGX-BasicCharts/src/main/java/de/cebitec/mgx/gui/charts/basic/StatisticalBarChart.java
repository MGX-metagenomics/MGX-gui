package de.cebitec.mgx.gui.charts.basic;

import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.gui.charts.basic.customizer.BarChartCustomizer;
import de.cebitec.mgx.gui.charts.basic.util.JFreeChartUtil;
import de.cebitec.mgx.api.groups.ImageExporterI;
import de.cebitec.mgx.api.groups.ReplicateGroupI;
import de.cebitec.mgx.api.groups.SequenceExporterI;
import de.cebitec.mgx.api.misc.Triple;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.common.VGroupManager;
import de.cebitec.mgx.common.visualization.CategoricalViewerI;
import de.cebitec.mgx.common.visualization.CustomizableI;
import de.cebitec.mgx.common.visualization.ViewerI;
import de.cebitec.mgx.gui.charts.basic.util.LogAxis;
import de.cebitec.mgx.gui.charts.basic.util.SVGChartPanel;
import de.cebitec.mgx.gui.charts.basic.util.SlidingStatisticalCategoryDataset;
import de.cebitec.mgx.gui.seqexporter.SeqExporter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import org.apache.commons.math3.util.FastMath;
import org.jfree.chart.ChartFactory;
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
import org.jfree.chart.renderer.category.StatisticalBarRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author pblumenk
 */
@ServiceProvider(service = ViewerI.class)
public class StatisticalBarChart extends CategoricalViewerI<Long> implements AdjustmentListener, CustomizableI, ImageExporterI.Provider, SequenceExporterI.Provider {

    private SVGChartPanel cPanel = null;
    private BarChartCustomizer customizer = null;
    private JFreeChart chart = null;
    private CategoryDataset dataset;
    private JScrollBar scrollBar = null;
    List<Pair<VisualizationGroupI, DistributionI<Long>>> dists;

    public StatisticalBarChart() {
        // disable the stupid glossy effect
        ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
        BarRenderer.setDefaultShadowsVisible(false);
        XYBarRenderer.setDefaultShadowsVisible(false);
    }

    @Override
    public String getName() {
        return "Statistical Bar Chart";
    }

    @Override
    public JComponent getComponent() {
        if (dataset instanceof SlidingStatisticalCategoryDataset) {
            SlidingStatisticalCategoryDataset data = (SlidingStatisticalCategoryDataset) dataset;
            JPanel frame = new JPanel(new BorderLayout());
            frame.add(cPanel);
            JPanel dashboard = new JPanel(new BorderLayout());
            scrollBar = new JScrollBar(JScrollBar.HORIZONTAL, 0, 1, 0, data.getTotalColumnCount() - data.getColumnCount() + 1);
            scrollBar.addAdjustmentListener(this);
            dashboard.add(scrollBar);
            frame.add(dashboard, BorderLayout.SOUTH);
            return frame;
        } else {
            return cPanel;
        }
    }

    @Override
    public void show(List<Pair<VisualizationGroupI, DistributionI<Long>>> in) {

        dists = in;

        Collection<ReplicateGroupI> repGroup = VGroupManager.getInstance().getReplicateGroups();
        List<Triple<ReplicateGroupI, DistributionI<Double>, DistributionI<Double>>> replicateGroups = new ArrayList<>();
        for (ReplicateGroupI rg : repGroup) {
            replicateGroups.add(new Triple<>(rg, rg.getMeanDistribution(), rg.getStdvDistribution()));
        }
        List<Triple<ReplicateGroupI, DistributionI<Double>, DistributionI<Double>>> filteredRg = getCustomizer().filterRep(replicateGroups);

        dataset = JFreeChartUtil.createStatisticalCategoryDataset(filteredRg);

        String xAxisLabel = "";
        String yAxisLabel = getCustomizer().useFractions() ? "Fraction" : "Count";

        CategoryPlot plot = new CategoryPlot(dataset, new CategoryAxis(xAxisLabel), new NumberAxis(yAxisLabel), new StatisticalBarRenderer());
        plot.setOrientation(PlotOrientation.VERTICAL);
        plot.setFixedLegendItems(JFreeChartUtil.createReplicateLegend(filteredRg));
        plot.setBackgroundPaint(Color.WHITE);

        chart = new JFreeChart(getTitle(), plot);
        chart.setBorderPaint(Color.WHITE);
        chart.setBackgroundPaint(Color.WHITE);
        chart.setAntiAlias(true);
        cPanel = new SVGChartPanel(chart);

        StatisticalBarRenderer br = (StatisticalBarRenderer) plot.getRenderer();
        br.setItemMargin(customizer.getItemMargin());
        br.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator("<html>Group: {0} <br> Attribute: {1} <br> " + yAxisLabel + ": {2}</html>", NumberFormat.getInstance()));
        br.setMaximumBarWidth(.1); // set maximum width to 10% of chart
        br.setErrorIndicatorPaint(Color.BLACK);

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
        if (dataset instanceof SlidingStatisticalCategoryDataset) {
            SlidingStatisticalCategoryDataset scd = (SlidingStatisticalCategoryDataset) dataset;
            rangeAxis.setAutoRange(false);
            rangeAxis.setRange(0, scd.getMaxY() * 1.05);
        }
        plot.setRangeAxis(rangeAxis);

        // colors
        int i = 0;
        CategoryItemRenderer renderer = plot.getRenderer();
        for (Triple<ReplicateGroupI, DistributionI<Double>, DistributionI<Double>> group : filteredRg) {
            renderer.setSeriesPaint(i++, group.getFirst().getColor());
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
        List<SequenceExporterI> ret = new ArrayList<>(dists.size());
        for (Pair<VisualizationGroupI, DistributionI<Long>> p : dists) {
            if (p.getSecond().getTotalClassifiedElements() > 0) {
                SequenceExporterI exp = new SeqExporter<>(p.getFirst(), p.getSecond());
                ret.add(exp);
            }
        }
        return ret.toArray(new SequenceExporterI[]{});
    }

    @Override
    public void adjustmentValueChanged(AdjustmentEvent ae) {
        SlidingStatisticalCategoryDataset data = (SlidingStatisticalCategoryDataset) dataset;
        data.setOffset(scrollBar.getValue());
    }

    @Override
    public boolean canHandle(AttributeTypeI valueType) {
        long replicatesCount = 0;
        for (ReplicateGroupI rg : VGroupManager.getInstance().getReplicateGroups()) {
            replicatesCount += rg.getReplicates().size();
        }

        return super.canHandle(valueType)
                && VGroupManager.getInstance().getReplicateGroups().size() > 0
                && VGroupManager.getInstance().getAllVisualizationGroups().size() == replicatesCount;
    }

}
