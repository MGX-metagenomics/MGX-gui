package de.cebitec.mgx.gui.charts.basic;

import de.cebitec.mgx.api.groups.ImageExporterI;
import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.common.visualization.NumericalViewerI;
import de.cebitec.mgx.common.visualization.ViewerI;
import de.cebitec.mgx.gui.charts.basic.customizer.XYPlotCustomizer;
import de.cebitec.mgx.gui.charts.basic.util.JFreeChartUtil;
import java.awt.Color;
import java.util.List;
import java.util.Locale;
import javax.swing.JComponent;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.TickUnitSource;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.data.xy.XYSeriesCollection;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author sj
 */
@ServiceProvider(service = ViewerI.class)
public class AreaChart extends NumericalViewerI {

    protected ChartPanel cPanel = null;
    protected XYPlotCustomizer customizer = null;
    protected JFreeChart chart = null;

    @Override
    public JComponent getComponent() {
        return cPanel;
    }

    @Override
    public String getName() {
        return "Area Chart";
    }

    @Override
    public void show(List<Pair<VisualizationGroupI, DistributionI>> dists) {
        
        LegendItemCollection legend = JFreeChartUtil.createLegend(dists);

        dists = getCustomizer().filter(dists);
        XYSeriesCollection dataset = JFreeChartUtil.createXYSeries(dists, getCustomizer().logY());

        String xAxisLabel = "";
        String yAxisLabel = useFractions() ? "Fraction" : "Count";

        chart = ChartFactory.createXYAreaChart(getTitle(), xAxisLabel, yAxisLabel, dataset, PlotOrientation.VERTICAL, true, true, false);

        chart.setBorderPaint(Color.WHITE);
        chart.setBackgroundPaint(Color.WHITE);
        cPanel = new ChartPanel(chart);
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        
        plot.setFixedLegendItems(legend);

        // x axis
        ValueAxis valueAxis;
        final TickUnitSource tusX;
        if (getCustomizer().logX()) {
            valueAxis = new LogarithmicAxis("log(" + xAxisLabel + ")");
            ((LogarithmicAxis) valueAxis).setStrictValuesFlag(false);
            tusX = LogAxis.createLogTickUnits(Locale.getDefault());

        } else {
            valueAxis = (NumberAxis) plot.getDomainAxis();
            tusX = NumberAxis.createIntegerTickUnits();
        }
        valueAxis.setStandardTickUnits(tusX);
        valueAxis.setInverted(!getCustomizer().getSortAscending());
        plot.setDomainAxis(valueAxis);


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
        plot.setRangeAxis(rangeAxis);


        // set the colors
        int i = 0;
        XYAreaRenderer renderer = (XYAreaRenderer) plot.getRenderer();
        for (Pair<VisualizationGroupI, DistributionI> groupDistribution : dists) {
            renderer.setSeriesPaint(i++, groupDistribution.getFirst().getColor());
        }
    }

    @Override
    public XYPlotCustomizer getCustomizer() {
        if (customizer == null) {
            customizer = new XYPlotCustomizer();
            customizer.setAttributeType(getAttributeType());
        }
        return customizer;
    }

    @Override
    public Class getInputType() {
        return DistributionI.class;
    }

    @Override
    public ImageExporterI getImageExporter() {
        return JFreeChartUtil.getImageExporter(chart);
    }
    
    protected boolean useFractions() {
        return getCustomizer().useFractions();
    }
}
