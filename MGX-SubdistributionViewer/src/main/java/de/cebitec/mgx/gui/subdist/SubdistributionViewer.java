/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.subdist;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.groups.GroupI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.gui.charts.basic.util.JFreeChartUtil;
import de.cebitec.mgx.gui.charts.basic.util.SVGChartPanel;
import de.cebitec.mgx.gui.charts.basic.util.ScrollableBarChart;
import de.cebitec.mgx.gui.charts.basic.util.SlidingCategoryDataset;
import de.cebitec.mgx.gui.viewer.api.CategoricalViewerI;
import de.cebitec.mgx.gui.viewer.api.CustomizableI;
import de.cebitec.mgx.gui.viewer.api.ViewerI;
import de.cebitec.mgx.gui.visgroups.VGroupManager;
import de.cebitec.mgx.gui.vizfilter.LongToDouble;
import de.cebitec.mgx.gui.vizfilter.ToFractionFilter;
import java.awt.Color;
import java.text.NumberFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import org.apache.commons.math3.util.FastMath;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
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
 * @author sj
 */
@ServiceProvider(service = ViewerI.class)
public class SubdistributionViewer extends CategoricalViewerI<Long> implements CustomizableI {
    
    private final SubDistCustomizer customizer = new SubDistCustomizer();
    private CategoryDataset dataset;
    private JFreeChart chart = null;
    private SVGChartPanel cPanel = null;
    
    public SubdistributionViewer() {
        // disable the stupid glossy effect
        ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
        BarRenderer.setDefaultShadowsVisible(false);
        XYBarRenderer.setDefaultShadowsVisible(false);
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
    public String getName() {
        return "Subdistribution Viewer";
    }
    
    @Override
    public Class<?> getInputType() {
        return DistributionI.class;
    }
    
    @Override
    public boolean canHandle(AttributeTypeI valueType) {
        List<GroupI> groups = VGroupManager.getInstance().getActiveGroups();
        
        return super.canHandle(valueType)
                && groups.size() == 1
                && groups.get(0).getContentClass() == SeqRunI.class
                && groups.get(0).getNumberOfSeqRuns() == 1;
    }
    
    @Override
    public void show(List<Pair<GroupI, DistributionI<Long>>> dists) {
        SeqRunI run = customizer.getSeqRun();
        AttributeI filterAttribute = customizer.getFilterAttribute();
        Pair<AttributeTypeI, JobI> p = customizer.getSelectedCriteria();
        
        setTitle("Distribution of " + p.getFirst().getName() + " for reads annotated as " + filterAttribute.getValue());
        
        MGXMasterI master = run.getMaster();
        DistributionI<Double> d = null;
        try {
            DistributionI<Long> dist = master.Attribute().getFilteredDistribution(filterAttribute, p.getFirst(), p.getSecond());
            
            if (customizer.useFractions()) {
                ToFractionFilter fracFilter = new ToFractionFilter();
                d = fracFilter.filterDist(dist);
            } else {
                LongToDouble ltd = new LongToDouble();
                d = ltd.filterDist(dist);
            }
        } catch (MGXException ex) {
            Logger.getLogger(SubdistributionViewer.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        
        dataset = JFreeChartUtil.createSingleCategoryDataset(customizer.getGroup(), d, false);
        
        String xAxisLabel = "";
        String yAxisLabel = customizer.useFractions() ? "Fraction" : "Count";
        
        boolean showLegend = true; //getCustomizer().showLegend();

        chart = ChartFactory.createBarChart(getTitle(), xAxisLabel, yAxisLabel, dataset, PlotOrientation.VERTICAL, showLegend, true, false);
        
        chart.setBorderPaint(Color.WHITE);
        chart.setBackgroundPaint(Color.WHITE);
        chart.setAntiAlias(true);
        //chart.setTextAntiAlias(RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        cPanel = new SVGChartPanel(chart);
        CategoryPlot plot = chart.getCategoryPlot();

        if (showLegend) {
            plot.setFixedLegendItems(JFreeChartUtil.createSingleLegend(customizer.getGroup(), d));
        }
        
        plot.setBackgroundPaint(Color.WHITE);
        
        BarRenderer br = (BarRenderer) plot.getRenderer();
        br.setDefaultToolTipGenerator(new StandardCategoryToolTipGenerator("<html>Group: {0} <br> Attribute: {1} <br> " + yAxisLabel + ": {2}</html>", NumberFormat.getInstance()));
        br.setMaximumBarWidth(.1); // set maximum width to 10% of chart

        // x axis
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(FastMath.PI / 6));

        // y axis
        final NumberAxis rangeAxis;
        final TickUnitSource tus;
//        if (getCustomizer().logY()) {
//            rangeAxis = new LogarithmicAxis("log(" + yAxisLabel + ")");
//            ((LogarithmicAxis) rangeAxis).setStrictValuesFlag(false);
//            tus = LogAxis.createLogTickUnits(Locale.getDefault());
//
//        } else {
        rangeAxis = (NumberAxis) plot.getRangeAxis();
        if (customizer.useFractions()) {
            tus = NumberAxis.createStandardTickUnits();
        } else {
            tus = NumberAxis.createIntegerTickUnits();
        }
        //}

        rangeAxis.setStandardTickUnits(tus);
        if (dataset instanceof SlidingCategoryDataset) {
            SlidingCategoryDataset scd = (SlidingCategoryDataset) dataset;
            rangeAxis.setAutoRange(false);
            rangeAxis.setRange(0, scd.getMaxY());
        }
        plot.setRangeAxis(rangeAxis);

        // color
        CategoryItemRenderer renderer = plot.getRenderer();
        renderer.setSeriesPaint(0, customizer.getGroup().getColor());
        
    }
    
    @Override
    public JComponent getCustomizer() {
        return customizer;
    }
    
    @Override
    public void setAttributeType(AttributeTypeI aType) {
        super.setAttributeType(aType);
        customizer.setAttributeType(aType);
    }
    
}
