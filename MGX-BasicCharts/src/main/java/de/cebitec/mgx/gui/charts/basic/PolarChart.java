package de.cebitec.mgx.gui.charts.basic;

import de.cebitec.mgx.api.groups.GroupI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.gui.charts.basic.util.JFreeChartUtil;
import de.cebitec.mgx.api.groups.ImageExporterI;
import de.cebitec.mgx.api.groups.SequenceExporterI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.gui.vizfilter.SortOrder;
import de.cebitec.mgx.gui.charts.basic.util.SVGChartPanel;
import de.cebitec.mgx.gui.seqexporter.SeqExporter;
import de.cebitec.mgx.gui.viewer.api.NumericalViewerI;
import de.cebitec.mgx.gui.viewer.api.ViewerI;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import org.jfree.chart.ChartFactory;
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
public class PolarChart extends NumericalViewerI<Long> implements ImageExporterI.Provider, SequenceExporterI.Provider {

    private SVGChartPanel cPanel = null;
    private JFreeChart chart = null;
    private List<Pair<GroupI, DistributionI<Long>>> dists;

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
    public void show(List<Pair<GroupI, DistributionI<Long>>> in) {

        SortOrder<Long> sorter = new SortOrder<>(getAttributeType(), SortOrder.DESCENDING);
        dists = sorter.filter(in);
        XYSeriesCollection dataset = JFreeChartUtil.createXYSeries(dists);

        chart = ChartFactory.createPolarChart("Polar Chart", dataset, true, true, false);
        chart.setBorderPaint(Color.WHITE);
        chart.setBackgroundPaint(Color.WHITE);

        cPanel = new SVGChartPanel(chart);
        PolarPlot plot = (PolarPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);

        // set the colors
        int i = 0;
        DefaultPolarItemRenderer renderer = (DefaultPolarItemRenderer) plot.getRenderer();
        for (Pair<GroupI, DistributionI<Long>> groupDistribution : dists) {
            renderer.setSeriesPaint(i++, groupDistribution.getFirst().getColor());
        }

    }

    @Override
    public Class getInputType() {
        return DistributionI.class;
    }

    @Override
    public ImageExporterI getImageExporter() {
        return JFreeChartUtil.getImageExporter(chart);
    }

    @Override
    public SequenceExporterI[] getSequenceExporters() {
        List<SequenceExporterI> ret = new ArrayList<>(dists.size());
        for (Pair<GroupI, DistributionI<Long>> p : dists) {
            if (p.getSecond().getTotalClassifiedElements() > 0) {
                if (p.getFirst().getContentClass().equals(SeqRunI.class)) {
                    SequenceExporterI exp = new SeqExporter<>((GroupI<SeqRunI>)p.getFirst(), p.getSecond());
                    ret.add(exp);
                }
            }
        }
        return ret.toArray(new SequenceExporterI[]{});
    }
}
