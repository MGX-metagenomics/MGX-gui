package de.cebitec.mgx.gui.charts.basic;

import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.gui.charts.basic.util.JFreeChartUtil;
import de.cebitec.mgx.gui.charts.basic.util.TickedSpiderWebPlot;
import de.cebitec.mgx.api.groups.ImageExporterI;
import de.cebitec.mgx.api.groups.SequenceExporterI;
import de.cebitec.mgx.gui.charts.basic.customizer.SpiderWebChartCustomizer;
import de.cebitec.mgx.gui.charts.basic.util.SVGChartPanel;
import de.cebitec.mgx.gui.seqexporter.SeqExporter;
import de.cebitec.mgx.gui.viewer.api.CategoricalViewerI;
import de.cebitec.mgx.gui.viewer.api.CustomizableI;
import de.cebitec.mgx.gui.viewer.api.ViewerI;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
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
public class SpiderWebChart extends CategoricalViewerI<Long> implements CustomizableI, ImageExporterI.Provider, SequenceExporterI.Provider {

    private SVGChartPanel cPanel = null;
    private JFreeChart chart = null;
    List<Pair<VisualizationGroupI, DistributionI<Double>>> dists;
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
    public void show(List<Pair<VisualizationGroupI, DistributionI<Long>>> in) {

//        SortOrder<Long> sorter = new SortOrder<>(getAttributeType(), SortOrder.DESCENDING);
//        dists = sorter.filter(dists);
        dists = cust.filter(in);

        CategoryDataset dataset = JFreeChartUtil.createCategoryDataset(dists);

        ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
        TickedSpiderWebPlot plot = new TickedSpiderWebPlot(dataset);

        Font labelFont = new Font("SansSerif", Font.BOLD, 10);

        CategoryToolTipGenerator tooltipGenerator = new StandardCategoryToolTipGenerator();
        tooltipGenerator.generateToolTip(dataset, 0, 0);
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setLabelFont(labelFont);
        plot.setWebFilled(true);
        plot.setFixedLegendItems(JFreeChartUtil.createLegend(dists));

        // colors
        int i = 0;
        for (Pair<VisualizationGroupI, DistributionI<Double>> groupDistribution : dists) {
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
        cPanel = new SVGChartPanel(chart);
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

    @Override
    public SequenceExporterI[] getSequenceExporters() {
        List<SequenceExporterI> ret = new ArrayList<>(dists.size());
        for (Pair<VisualizationGroupI, DistributionI<Double>> p : dists) {
            if (p.getSecond().getTotalClassifiedElements() > 0) {
                SequenceExporterI exp = new SeqExporter<>(p.getFirst(), p.getSecond());
                ret.add(exp);
            }
        }
        return ret.toArray(new SequenceExporterI[]{});
    }
}
