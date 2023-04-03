package de.cebitec.mgx.gui.pca;

import de.cebitec.mgx.api.misc.PrincipalComponent;
import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.groups.ConflictingJobsException;
import de.cebitec.mgx.api.groups.GroupI;
import de.cebitec.mgx.api.groups.ImageExporterI;
import de.cebitec.mgx.api.groups.SequenceExporterI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.PCAResultI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.misc.Point;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.assembly.AssembledSeqRunI;
import de.cebitec.mgx.gui.charts.basic.util.JFreeChartUtil;
import de.cebitec.mgx.gui.charts.basic.util.SVGChartPanel;
import de.cebitec.mgx.gui.seqexporter.SeqExporter;
import de.cebitec.mgx.gui.viewer.api.AbstractViewer;
import de.cebitec.mgx.gui.viewer.api.CustomizableI;
import de.cebitec.mgx.gui.viewer.api.ViewerI;
import de.cebitec.mgx.gui.visgroups.VGroupManager;
import de.cebitec.mgx.gui.vizfilter.LongToDouble;
import de.cebitec.mgx.gui.vizfilter.ToFractionFilter;
import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
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
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author sj
 */
@ServiceProvider(service = ViewerI.class)
public class PCAPlot extends AbstractViewer<DistributionI<Long>> implements CustomizableI, SequenceExporterI.Provider, ImageExporterI.Provider {

    private SVGChartPanel cPanel = null;
    private JFreeChart chart = null;
    private PCACustomizer cust = null;
    private List<Pair<GroupI, DistributionI<Long>>> data;

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
    @SuppressWarnings("unchecked")
    public SequenceExporterI[] getSequenceExporters() {
        List<SequenceExporterI> ret = new ArrayList<>(data.size());
        Set<String> seenGeneNames = new HashSet<>();
        for (Pair<GroupI, DistributionI<Long>> p : data) {
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

    @Override
    public boolean canHandle(AttributeTypeI valueType) {
        Set<AttributeI> attrs = new HashSet<>();
        int distCnt = 0;
        try {
            for (Pair<GroupI, DistributionI<Long>> p : VGroupManager.getInstance().getDistributions()) {
                attrs.addAll(p.getSecond().keySet());
                distCnt++;
            }
        } catch (ConflictingJobsException ex) {
        }
        return attrs.size() > 1 && distCnt > 1;
    }

    @Override
    public Class<?> getInputType() {
        return DistributionI.class;
    }

    @Override
    public void show(final List<Pair<GroupI, DistributionI<Long>>> in) {

        data = in;

        final Pair<PrincipalComponent, PrincipalComponent> comps = getCustomizer().getPCs();

        SwingWorker<PCAResultI, Void> sw = new SwingWorker<PCAResultI, Void>() {

            @Override
            protected PCAResultI doInBackground() throws Exception {
                final MGXMasterI master = in.get(0).getSecond().getMaster();

                List<Pair<GroupI, DistributionI<Double>>> data;

                if (getCustomizer().useFractions()) {
                    ToFractionFilter fracFilter = new ToFractionFilter();
                    data = fracFilter.filter(in);
                } else {
                    data = new LongToDouble().filter(in);
                }
                return master.Statistics().PCA(data, comps.getFirst(), comps.getSecond());
            }
        };
        sw.execute();

        PCAResultI pca;
        try {
            pca = sw.get();
        } catch (InterruptedException | ExecutionException ex) {
            if (ex.getMessage().contains("Could not access requested principal components.")) {
                DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message("Selected principal components do not exist."));
                return;
            }
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

        for (Point p : pca.getDatapoints()) {
            XYSeries series = new XYSeries(p.getName());
            XYDataItem item = new XYDataItem(p.getX(), p.getY());
            series.add(item);
            toolTips.put(item, p.getName());
            dataset.addSeries(series);
        }

        // second dataseries with loadings
        XYSeriesCollection loadingset = new XYSeriesCollection();
        XYSeries loadings = getCustomizer().getLoadings();
        loadingset.addSeries(loadings);

        chart = ChartFactory.createScatterPlot(getTitle(), comps.getFirst().toString()
                + " (" + pc1rel + "%)", comps.getSecond().toString() + " (" + pc2rel + "%)", dataset, PlotOrientation.VERTICAL, false, true, false);
        chart.setBorderPaint(Color.WHITE);
        chart.setBackgroundPaint(Color.WHITE);
        cPanel = new SVGChartPanel(chart);
        cPanel.setDisplayToolTips(true);
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);

        ValueAxis rangeAxis = plot.getRangeAxis();
        rangeAxis.setLabelFont(new Font(rangeAxis.getLabelFont().getName(), Font.BOLD, 22));
        rangeAxis.setTickLabelFont(new Font(rangeAxis.getTickLabelFont().getName(), Font.PLAIN, 16));
        ValueAxis domainAxis = plot.getDomainAxis();
        domainAxis.setLabelFont(new Font(domainAxis.getLabelFont().getName(), Font.BOLD, 22));
        domainAxis.setTickLabelFont(new Font(domainAxis.getTickLabelFont().getName(), Font.PLAIN, 16));

        // renderer for data points
        final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setDefaultLinesVisible(false);
        XYItemLabelGenerator labelGen = new XYItemLabelGenerator() {

            @Override
            public String generateLabel(XYDataset xyd, int series, int item) {
                XYSeriesCollection dataset = (XYSeriesCollection) xyd;
                XYDataItem dataItem = dataset.getSeries(series).getDataItem(item);
                return toolTips.get(dataItem);
            }
        };

        int i = 0;
        for (Point p : pca.getDatapoints()) {
            renderer.setSeriesShape(i, new Ellipse2D.Double(0, 0, 7, 7));
            renderer.setSeriesItemLabelGenerator(i, labelGen);
            renderer.setSeriesItemLabelsVisible(i, Boolean.TRUE);
            renderer.setSeriesItemLabelFont(i, new Font(plot.getNoDataMessageFont().getName(), Font.PLAIN, 14));
            GroupI vGrp = VGroupManager.getInstance().getGroup(p.getName());
            renderer.setSeriesPaint(i, vGrp != null ? vGrp.getColor() : Color.BLACK);
            i++;
        }

        plot.setRenderer(0, renderer);

        // add loadings
        plot.setDataset(1, loadingset);
        NumberAxis axis = new NumberAxis("loadings");
        axis.setLabelFont(new Font(axis.getLabelFont().getName(), Font.BOLD, 22));
        axis.setTickLabelFont(new Font(axis.getTickLabelFont().getName(), Font.PLAIN, 16));
        plot.setRangeAxis(1, axis);
        plot.setRangeAxisLocation(1, AxisLocation.BOTTOM_OR_RIGHT);
        ArrowRenderer arrowRenderer = new ArrowRenderer();
        arrowRenderer.setSeriesItemLabelFont(0, new Font(plot.getNoDataMessageFont().getName(), Font.PLAIN, 14));
        arrowRenderer.setSeriesItemLabelGenerator(0, labelGen);
        arrowRenderer.setSeriesItemLabelsVisible(0, Boolean.TRUE);
        arrowRenderer.setSeriesItemLabelGenerator(1, labelGen);
        arrowRenderer.setSeriesItemLabelsVisible(1, Boolean.TRUE);
        arrowRenderer.setSeriesItemLabelFont(1, new Font(plot.getNoDataMessageFont().getName(), Font.PLAIN, 14));
        plot.setRenderer(1, arrowRenderer);
        arrowRenderer.setDataSetIndex(1);
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
