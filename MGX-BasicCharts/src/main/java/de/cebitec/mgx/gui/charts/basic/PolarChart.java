package de.cebitec.mgx.gui.charts.basic;

import de.cebitec.mgx.gui.attributevisualization.filter.SortOrder;
import de.cebitec.mgx.gui.attributevisualization.viewer.NumericalViewerI;
import de.cebitec.mgx.gui.attributevisualization.viewer.ViewerI;
import de.cebitec.mgx.gui.charts.basic.util.JFreeChartUtil;
import de.cebitec.mgx.gui.datamodel.misc.Distribution;
import de.cebitec.mgx.gui.datamodel.misc.Pair;
import de.cebitec.mgx.gui.groups.VisualizationGroup;
import java.awt.Color;
import java.util.List;
import java.util.Locale;
import javax.swing.JComponent;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.TickUnitSource;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PolarPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeriesCollection;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author sj
 */
@ServiceProvider(service = ViewerI.class)
public class PolarChart extends NumericalViewerI {

    private ChartPanel cPanel = null;
    private JFreeChart chart = null;

    @Override
    public JComponent getComponent() {
        return cPanel;
    }

    @Override
    public String getName() {
        return "Polar Chart";
    }

    @Override
    public void show(List<Pair<VisualizationGroup, Distribution>> dists) {

        SortOrder sorter = new SortOrder(getAttributeType(), SortOrder.DESCENDING);
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
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
        for (Pair<VisualizationGroup, Distribution> groupDistribution : dists) {
            renderer.setSeriesPaint(i++, groupDistribution.getFirst().getColor());
        }

    }

    @Override
    public JComponent getCustomizer() {
        return null;
    }

    @Override
    public Class getInputType() {
        return Distribution.class;
    }
}
