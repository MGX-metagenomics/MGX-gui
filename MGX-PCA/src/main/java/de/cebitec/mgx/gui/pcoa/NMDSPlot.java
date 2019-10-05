package de.cebitec.mgx.gui.pcoa;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.groups.ConflictingJobsException;
import de.cebitec.mgx.api.groups.ImageExporterI;
import de.cebitec.mgx.api.groups.SequenceExporterI;
import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.misc.Point;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.visualization.filter.VisFilterI;
import de.cebitec.mgx.gui.charts.basic.util.JFreeChartUtil;
import de.cebitec.mgx.gui.charts.basic.util.SVGChartPanel;
import de.cebitec.mgx.gui.common.VGroupManager;
import de.cebitec.mgx.gui.common.visualization.AbstractViewer;
import de.cebitec.mgx.gui.common.visualization.CustomizableI;
import de.cebitec.mgx.gui.common.visualization.ViewerI;
import de.cebitec.mgx.gui.seqexporter.SeqExporter;
import de.cebitec.mgx.gui.vizfilter.LongToDouble;
import de.cebitec.mgx.gui.vizfilter.ToFractionFilter;
import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import javax.swing.JComponent;
import javax.swing.SwingWorker;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.openide.util.Exceptions;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author sj
 */
@ServiceProvider(service = ViewerI.class)
public class NMDSPlot extends AbstractViewer<DistributionI<Long>> implements CustomizableI, ImageExporterI.Provider, SequenceExporterI.Provider {

    private SVGChartPanel cPanel = null;
    private JFreeChart chart = null;
    private PCoACustomizer cust = null;
    private List<Pair<VisualizationGroupI, DistributionI<Double>>> data;

    @Override
    public JComponent getComponent() {
        return cPanel;
    }

    @Override
    public String getName() {
        return "NMDS Plot";
    }

    @Override
    public ImageExporterI getImageExporter() {
        return JFreeChartUtil.getImageExporter(chart);
    }

    @Override
    public SequenceExporterI[] getSequenceExporters() {
        List<SequenceExporterI> ret = new ArrayList<>(data.size());
        for (Pair<VisualizationGroupI, DistributionI<Double>> p : data) {
            if (p.getSecond().getTotalClassifiedElements() > 0) {
                SequenceExporterI exp = new SeqExporter<>(p.getFirst(), p.getSecond());
                ret.add(exp);
            }
        }
        return ret.toArray(new SequenceExporterI[]{});
    }

    @Override
    public boolean canHandle(AttributeTypeI valueType) {
        Set<AttributeI> attrs = new HashSet<>();
        int distCnt = 0;
        try {
            for (Pair<VisualizationGroupI, DistributionI<Long>> p : getVGroupManager().getDistributions()) {
                attrs.addAll(p.getSecond().keySet());
                distCnt++;
            }
        } catch (ConflictingJobsException ex) {
        }
        return attrs.size() > 1 && distCnt > 2;
    }

    @Override
    public Class getInputType() {
        return DistributionI.class;
    }

    @Override
    public void show(List<Pair<VisualizationGroupI, DistributionI<Long>>> in) {
        final MGXMasterI master = in.get(0).getSecond().getMaster();

        if (getCustomizer().useFractions()) {
            VisFilterI<DistributionI<Long>, DistributionI<Double>> fracFilter = new ToFractionFilter();
            data = fracFilter.filter(in);
        } else {
            data = new LongToDouble().filter(in);
        }

        final List<Pair<VisualizationGroupI, DistributionI<Double>>> xdata = data;

        SwingWorker<Collection<Point>, Void> sw = new SwingWorker<Collection<Point>, Void>() {

            @Override
            protected Collection<Point> doInBackground() throws Exception {
                return master.Statistics().NMDS(xdata);
            }
        };
        sw.execute();

        Collection<Point> nmds;
        try {
            nmds = sw.get();
        } catch (InterruptedException | ExecutionException ex) {
            Exceptions.printStackTrace(ex);
            return;
        }

        if (nmds == null) {
            return;
        }

        final Map<XYDataItem, String> toolTips = new HashMap<>();

        XYSeriesCollection dataset = new XYSeriesCollection();
        for (Point p : nmds) {
            XYSeries series = new XYSeries(p.getName());
            XYDataItem item = new XYDataItem(p.getX(), p.getY());
            series.add(item);
            toolTips.put(item, p.getName());
            dataset.addSeries(series);
        }

        chart = ChartFactory.createScatterPlot(getTitle(), "MDS 1", "MDS 2", dataset, PlotOrientation.VERTICAL, false, true, false);
        chart.setBorderPaint(Color.WHITE);
        chart.setBackgroundPaint(Color.WHITE);
        cPanel = new SVGChartPanel(chart);
        cPanel.setDisplayToolTips(true);
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);

        // renderer for data points
        final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setBaseLinesVisible(false);
        XYItemLabelGenerator labelGen = new XYItemLabelGenerator() {

            @Override
            public String generateLabel(XYDataset xyd, int series, int item) {
                XYSeriesCollection dataset = (XYSeriesCollection) xyd;
                XYDataItem dataItem = dataset.getSeries(series).getDataItem(item);
                return toolTips.get(dataItem);
            }
        };

        int i = 0;
        for (Point p : nmds) {
            renderer.setSeriesShape(i, new Ellipse2D.Double(0, 0, 7, 7));
            renderer.setSeriesItemLabelGenerator(i, labelGen);
            renderer.setSeriesItemLabelsVisible(i, Boolean.TRUE);
            VisualizationGroupI vGrp = VGroupManager.getInstance().getVisualizationGroup(p.getName());
            renderer.setSeriesPaint(i, vGrp != null ? vGrp.getColor() : Color.BLACK);
            i++;
        }
        plot.setRenderer(0, renderer);
    }

    @Override
    public PCoACustomizer getCustomizer() {
        if (cust == null) {
            cust = new PCoACustomizer();
        }
        return cust;
    }

    @Override
    protected String getTitle() {
        return "NMDS plot for " + getAttributeType().getName();
    }
}
