package de.cebitec.mgx.gui.charts.basic;

import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.gui.charts.basic.util.JFreeChartUtil;
import de.cebitec.mgx.gui.charts.basic.util.TickedSpiderWebPlot;
import de.cebitec.mgx.api.groups.ImageExporterI;
import de.cebitec.mgx.gui.vizfilter.SortOrder;
import de.cebitec.mgx.common.visualization.CategoricalViewerI;
import de.cebitec.mgx.common.visualization.ViewerI;
import de.cebitec.mgx.gui.charts.basic.customizer.SpiderWebChartCustomizer;
import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.List;
import javax.swing.JComponent;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.data.category.CategoryDataset;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author sj
 */
@ServiceProvider(service = ViewerI.class)
public class SpiderWebChart extends CategoricalViewerI<Long> {

    private ChartPanel cPanel = null;
    private JFreeChart chart = null;
    private final SpiderWebChartCustomizer cust = new SpiderWebChartCustomizer();

    @Override
    public JComponent getComponent() {
        return cPanel;
    }

    @Override
    public String getName() {
        return "Spiderweb Plot";
    }

    @Override
    public Class getInputType() {
        return DistributionI.class;
    }

    @Override
    public void show(List<Pair<VisualizationGroupI, DistributionI<Long>>> dists) {

//        SortOrder<Long> sorter = new SortOrder<>(getAttributeType(), SortOrder.DESCENDING);
//        dists = sorter.filter(dists);
        
        List<Pair<VisualizationGroupI, DistributionI<Double>>> d = cust.filter(dists);

        CategoryDataset dataset = JFreeChartUtil.createCategoryDataset(d);

        ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
        TickedSpiderWebPlot plot = new TickedSpiderWebPlot(dataset);

        Font labelFont = new Font("Arial", Font.BOLD, 10);

        CategoryToolTipGenerator tooltipGenerator = new StandardCategoryToolTipGenerator();
        tooltipGenerator.generateToolTip(dataset, 0, 0);
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setLabelFont(labelFont);
        plot.setWebFilled(true);
        plot.setFixedLegendItems(JFreeChartUtil.createLegend(d));

        // colors
        int i = 0;
        for (Pair<VisualizationGroupI, DistributionI<Double>> groupDistribution : d) {
            plot.setSeriesPaint(i++, groupDistribution.getFirst().getColor());
        }

        chart = new JFreeChart(getTitle(), JFreeChart.DEFAULT_TITLE_FONT, plot, true);
        chart.setBorderPaint(Color.WHITE);
        chart.setBackgroundPaint(Color.WHITE);
        chart.setBorderVisible(false);

//        LegendTitle legendtitle = new LegendTitle(plot);
//        legendtitle.setPosition(RectangleEdge.RIGHT);
//        legendtitle.setFrame(new BlockBorder(1.0D, 1.0D, 1.0D, 1.0D));
//        chart.addSubtitle(legendtitle);
        cPanel = new ChartPanel(chart);
    }

    @Override
    public JComponent getCustomizer() {
        cust.setAttributeType(getAttributeType());
        return cust;
    }

    @Override
    public ImageExporterI getImageExporter() {
        return JFreeChartUtil.getImageExporter(chart);
    }
}
