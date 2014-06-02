package de.cebitec.mgx.gui.pca;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.groups.ConflictingJobsException;
import de.cebitec.mgx.api.groups.ImageExporterI;
import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.PCAResultI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.misc.Point;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.visualization.filter.ToFractionFilter;
import de.cebitec.mgx.api.visualization.filter.VisFilterI;
import de.cebitec.mgx.common.VGroupManager;
import de.cebitec.mgx.common.visualization.ViewerI;
import de.cebitec.mgx.gui.charts.basic.util.JFreeChartUtil;
import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import javax.swing.JComponent;
import javax.swing.SwingWorker;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
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
public class PCAPlot extends ViewerI<DistributionI> {

    private ChartPanel cPanel = null;
    private JFreeChart chart = null;
    private PCACustomizer cust = null;

    @Override
    public JComponent getComponent() {
        return cPanel;
    }

    @Override
    public String getName() {
        return "PCA Plot";
    }

    @Override
    public ImageExporterI getImageExporter() {
        return JFreeChartUtil.getImageExporter(chart);
    }

    @Override
    public boolean canHandle(AttributeTypeI valueType) {
        Set<AttributeI> attrs = new HashSet<>();
        int distCnt = 0;
        try {
            for (Pair<VisualizationGroupI, DistributionI> p : VGroupManager.getInstance().getDistributions()) {
                attrs.addAll(p.getSecond().keySet());
                distCnt++;
            }
        } catch (ConflictingJobsException ex) {
        }
        return attrs.size() > 1 && distCnt > 1;
    }

    @Override
    public Class getInputType() {
        return DistributionI.class;
    }

    @Override
    public void show(List<Pair<VisualizationGroupI, DistributionI>> dists) {
        final MGXMasterI master = dists.get(0).getSecond().getMaster();

        if (getCustomizer().useFractions()) {
            VisFilterI fracFilter = new ToFractionFilter();
            dists = fracFilter.filter(dists);
        }
        final Pair<PC, PC> comps = getCustomizer().getPCs();
        final List<Pair<VisualizationGroupI, DistributionI>> data = dists;

        SwingWorker<PCAResultI, Void> sw = new SwingWorker<PCAResultI, Void>() {

            @Override
            protected PCAResultI doInBackground() throws Exception {
                return master.Statistics().PCA(data, comps.getFirst().getValue(), comps.getSecond().getValue());
            }
        };
        sw.execute();

        PCAResultI pca = null;
        try {
            pca = sw.get();
        } catch (InterruptedException | ExecutionException ex) {
            Exceptions.printStackTrace(ex);
            return;
        }

        final Map<XYDataItem, String> toolTips = new HashMap<>();
        getCustomizer().setLoadings(pca, toolTips);

        double[] variances = pca.getVariances();
        double varSum = 0;
        for (double d : variances) {
            varSum += d;
        }
        String pc1rel = String.format("%2.2f%n", variances[comps.getFirst().getValue() - 1] * 100 / varSum);
        String pc2rel = String.format("%2.2f%n", variances[comps.getSecond().getValue() - 1] * 100 / varSum);

        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries series = new XYSeries("");
        for (Point p : pca.getDatapoints()) {
            XYDataItem item = new XYDataItem(p.getX(), p.getY());
            series.add(item);
            toolTips.put(item, p.getName());
        }
        dataset.addSeries(series);

        // second dataseries with loadings
        XYSeriesCollection loadingset = new XYSeriesCollection();
        XYSeries loadings = getCustomizer().getLoadings();
        loadingset.addSeries(loadings);

        chart = ChartFactory.createScatterPlot(getTitle(), comps.getFirst().toString()
                + " (" + pc1rel + "%)", comps.getSecond().toString() + " (" + pc2rel + "%)", dataset, PlotOrientation.VERTICAL, false, true, false);
        chart.setBorderPaint(Color.WHITE);
        chart.setBackgroundPaint(Color.WHITE);
        cPanel = new ChartPanel(chart);
        cPanel.setDisplayToolTips(true);
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);

        // renderer for data points
        final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setBaseLinesVisible(false);
        renderer.setSeriesShape(0, new Ellipse2D.Double(0, 0, 5, 5));
        XYItemLabelGenerator labelGen = new XYItemLabelGenerator() {

            @Override
            public String generateLabel(XYDataset xyd, int series, int item) {
                XYSeriesCollection dataset = (XYSeriesCollection) xyd;
                XYDataItem dataItem = dataset.getSeries(series).getDataItem(item);
                return toolTips.get(dataItem);
            }
        };
        renderer.setSeriesItemLabelGenerator(0, labelGen);
        renderer.setSeriesItemLabelsVisible(0, Boolean.TRUE);
        plot.setRenderer(0, renderer);

        // add loadings
        plot.setDataset(1, loadingset);
        NumberAxis axis = new NumberAxis("loadings");
        plot.setRangeAxis(1, axis);
        plot.setRangeAxisLocation(1, AxisLocation.BOTTOM_OR_RIGHT);
        XYItemRenderer arrowRenderer = new ArrowRenderer();
        arrowRenderer.setSeriesItemLabelGenerator(0, labelGen);
        arrowRenderer.setSeriesItemLabelsVisible(0, Boolean.TRUE);
        arrowRenderer.setSeriesItemLabelGenerator(1, labelGen);
        arrowRenderer.setSeriesItemLabelsVisible(1, Boolean.TRUE);
        plot.setRenderer(1, arrowRenderer);
        plot.mapDatasetToRangeAxis(1, 1);
    }

    @Override
    public PCACustomizer getCustomizer() {
        if (cust == null) {
            cust = new PCACustomizer();
        }
        return cust;
    }

    @Override
    protected String getTitle() {
        return "PCA plot for " + getAttributeType().getName();
    }
}
