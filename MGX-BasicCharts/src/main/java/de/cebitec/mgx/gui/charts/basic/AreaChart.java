package de.cebitec.mgx.gui.charts.basic;

import de.cebitec.mgx.api.groups.GroupI;
import de.cebitec.mgx.api.groups.ImageExporterI;
import de.cebitec.mgx.api.groups.SequenceExporterI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.assembly.AssembledSeqRunI;
import de.cebitec.mgx.gui.charts.basic.customizer.XYPlotCustomizer;
import de.cebitec.mgx.gui.charts.basic.util.JFreeChartUtil;
import de.cebitec.mgx.gui.charts.basic.util.LogAxis;
import de.cebitec.mgx.gui.charts.basic.util.SVGChartPanel;
import de.cebitec.mgx.gui.seqexporter.SeqExporter;
import de.cebitec.mgx.gui.viewer.api.CustomizableI;
import de.cebitec.mgx.gui.viewer.api.NumericalViewerI;
import de.cebitec.mgx.gui.viewer.api.ViewerI;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.swing.JComponent;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItemCollection;
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
public class AreaChart extends NumericalViewerI<Long> implements CustomizableI, ImageExporterI.Provider, SequenceExporterI.Provider {

    protected SVGChartPanel cPanel = null;
    protected XYPlotCustomizer customizer = null;
    protected JFreeChart chart = null;
    protected List<Pair<GroupI, DistributionI<Double>>> data;

    @Override
    public JComponent getComponent() {
        return cPanel;
    }

    @Override
    public String getName() {
        return "Area Chart";
    }

    @Override
    public void show(List<Pair<GroupI, DistributionI<Long>>> in) {

        LegendItemCollection legend = JFreeChartUtil.createLegend(in);

        data = getCustomizer().filter(in);
        XYSeriesCollection dataset = JFreeChartUtil.createXYSeries(data, getCustomizer().logY());

        String xAxisLabel = "";
        String yAxisLabel = useFractions() ? "Fraction" : "Count";

        chart = ChartFactory.createXYAreaChart(getTitle(), xAxisLabel, yAxisLabel, dataset, PlotOrientation.VERTICAL, true, true, false);

        chart.setBorderPaint(Color.WHITE);
        chart.setBackgroundPaint(Color.WHITE);
        cPanel = new SVGChartPanel(chart);
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
        for (Pair<GroupI, DistributionI<Double>> groupDistribution : data) {
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
    public Class<?> getInputType() {
        return DistributionI.class;
    }

    @Override
    public ImageExporterI getImageExporter() {
        return JFreeChartUtil.getImageExporter(chart);
    }

    @Override
    @SuppressWarnings("unchecked")
    public SequenceExporterI[] getSequenceExporters() {
        List<SequenceExporterI> ret = new ArrayList<>(data.size());
        Set<String> seenGeneNames = new HashSet<>();
        for (Pair<GroupI, DistributionI<Double>> p : data) {
            if (p.getSecond().getTotalClassifiedElements() > 0) {
                if (p.getFirst().getContentClass().equals(SeqRunI.class)) {
                    SequenceExporterI exp = new SeqExporter<>((GroupI<SeqRunI>) p.getFirst(), p.getSecond());
                    ret.add(exp);
                } else if (p.getFirst().getContentClass().equals(AssembledSeqRunI.class)) {
                    SequenceExporterI exp = new SeqExporter<>((GroupI<AssembledSeqRunI>) p.getFirst(), p.getSecond(), seenGeneNames);
                    ret.add(exp);
                }
            }
        }
        return ret.toArray(new SequenceExporterI[]{});
    }

    protected boolean useFractions() {
        return getCustomizer().useFractions();
    }
}
