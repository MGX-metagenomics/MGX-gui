package de.cebitec.mgx.gui.charts.basic;

import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.gui.charts.basic.util.JFreeChartUtil;
import de.cebitec.mgx.api.groups.ImageExporterI;
import de.cebitec.mgx.gui.vizfilter.SortOrder;
import de.cebitec.mgx.common.visualization.NumericalViewerI;
import de.cebitec.mgx.common.visualization.ViewerI;
import java.awt.Color;
import java.util.List;
import javax.swing.JComponent;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PolarPlot;
import org.jfree.chart.renderer.DefaultPolarItemRenderer;
import org.jfree.data.xy.XYSeriesCollection;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author sj
 */
@ServiceProvider(service = ViewerI.class)
public class PolarChart extends NumericalViewerI<Long> {

    private ChartPanel cPanel = null;
    private JFreeChart chart = null;

    @Override
    public boolean canHandle(AttributeTypeI valueType) {
        // currently broken, disable viewer
        return false;
    }

    @Override
    public JComponent getComponent() {
        return cPanel;
    }

    @Override
    public String getName() {
        return "Polar Chart";
    }

    @Override
    public void show(List<Pair<VisualizationGroupI, DistributionI<Long>>> dists) {

        SortOrder<Long> sorter = new SortOrder<>(getAttributeType(), SortOrder.DESCENDING);
        dists = sorter.filter(dists);
        XYSeriesCollection dataset = JFreeChartUtil.createXYSeries(dists);

        chart = ChartFactory.createPolarChart("Polar Chart", dataset, true, true, false);
        chart.setBorderPaint(Color.WHITE);
        chart.setBackgroundPaint(Color.WHITE);

        cPanel = new ChartPanel(chart);
        PolarPlot plot = (PolarPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);

        // set the colors
        int i = 0;
        DefaultPolarItemRenderer renderer = (DefaultPolarItemRenderer) plot.getRenderer();
        for (Pair<VisualizationGroupI, DistributionI<Long>> groupDistribution : dists) {
            renderer.setSeriesPaint(i++, groupDistribution.getFirst().getColor());
        }

    }

    @Override
    public JComponent getCustomizer() {
        return null;
    }

    @Override
    public Class getInputType() {
        return DistributionI.class;
    }

    @Override
    public ImageExporterI getImageExporter() {
        return JFreeChartUtil.getImageExporter(chart);
    }
}
