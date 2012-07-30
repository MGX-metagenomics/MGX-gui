package de.cebitec.mgx.gui.charts.basic;

import de.cebitec.mgx.gui.attributevisualization.filter.SortOrder;
import de.cebitec.mgx.gui.attributevisualization.viewer.CategoricalViewerI;
import de.cebitec.mgx.gui.attributevisualization.viewer.ViewerI;
import de.cebitec.mgx.gui.charts.basic.util.JFreeChartUtil;
import de.cebitec.mgx.gui.charts.basic.util.TickedSpiderWebPlot;
import de.cebitec.mgx.gui.datamodel.misc.Distribution;
import de.cebitec.mgx.gui.datamodel.misc.Pair;
import de.cebitec.mgx.gui.groups.VisualizationGroup;
import java.awt.Color;
import java.awt.Font;
import java.util.List;
import javax.swing.JComponent;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.SpiderWebPlot;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.ui.RectangleEdge;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author sj
 */
@ServiceProvider(service = ViewerI.class)
public class SpiderWebChart extends CategoricalViewerI {

    private ChartPanel cPanel = null;
    private JFreeChart chart = null;

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
        return Distribution.class;
    }

    @Override
    public void show(List<Pair<VisualizationGroup, Distribution>> dists) {

        SortOrder sorter = new SortOrder(getAttributeType(), SortOrder.DESCENDING);
        dists = sorter.filter(dists);

        CategoryDataset dataset = JFreeChartUtil.createCategoryDataset(dists);

        ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
        TickedSpiderWebPlot plot = new TickedSpiderWebPlot(dataset);

        Font labelFont = new Font("Arial", Font.BOLD, 10);

        CategoryToolTipGenerator tooltipGenerator = new StandardCategoryToolTipGenerator();
        tooltipGenerator.generateToolTip(dataset, 0, 0);
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setLabelFont(labelFont);

        // colors
        int i = 0;
        for (Pair<VisualizationGroup, Distribution> groupDistribution : dists) {
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
        return null;
    }
}
