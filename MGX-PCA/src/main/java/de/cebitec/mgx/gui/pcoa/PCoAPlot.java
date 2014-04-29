package de.cebitec.mgx.gui.pcoa;

import de.cebitec.mgx.gui.attributevisualization.viewer.ViewerI;
import de.cebitec.mgx.gui.charts.basic.util.JFreeChartUtil;
import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.Attribute;
import de.cebitec.mgx.gui.datamodel.AttributeType;
import de.cebitec.mgx.gui.datamodel.misc.Distribution;
import de.cebitec.mgx.gui.datamodel.misc.Pair;
import de.cebitec.mgx.gui.datamodel.misc.Point;
import de.cebitec.mgx.gui.groups.ConflictingJobsException;
import de.cebitec.mgx.gui.groups.ImageExporterI;
import de.cebitec.mgx.gui.groups.VGroupManager;
import de.cebitec.mgx.gui.groups.VisualizationGroup;
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
public class PCoAPlot extends ViewerI<Distribution> {

    private ChartPanel cPanel = null;
    private JFreeChart chart = null;
    //private PCACustomizer cust = null;

    @Override
    public JComponent getComponent() {
        return cPanel;
    }

    @Override
    public String getName() {
        return "PCoA Plot";
    }

    @Override
    public ImageExporterI getImageExporter() {
        return JFreeChartUtil.getImageExporter(chart);
    }

    @Override
    public boolean canHandle(AttributeType valueType) {
        Set<Attribute> attrs = new HashSet<>();
        try {
            for (Pair<VisualizationGroup, Distribution> p : VGroupManager.getInstance().getDistributions()) {
                attrs.addAll(p.getSecond().keySet());
            }
        } catch (ConflictingJobsException ex) {
        }
        return attrs.size() > 1 && VGroupManager.getInstance().getActiveGroups().size() > 1;
    }

    @Override
    public Class getInputType() {
        return Distribution.class;
    }

    @Override
    public void show(List<Pair<VisualizationGroup, Distribution>> dists) {
        final MGXMaster master = (MGXMaster) dists.get(0).getSecond().getMaster();

//        if (getCustomizer().useFractions()) {
//            VisFilterI fracFilter = new ToFractionFilter();
//            dists = fracFilter.filter(dists);
//        }
        
        final List<Pair<VisualizationGroup, Distribution>> data = dists;

        SwingWorker<List<Point>, Void> sw = new SwingWorker<List<Point>, Void>() {

            @Override
            protected List<Point> doInBackground() throws Exception {
                return master.Statistics().PCoA(data);
            }
        };
        sw.execute();

        List<Point> pcoa = null;
        try {
            pcoa = sw.get();
        } catch (InterruptedException | ExecutionException ex) {
            Exceptions.printStackTrace(ex);
            return;
        }

        final Map<XYDataItem, String> toolTips = new HashMap<>();

        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries series = new XYSeries("");
        for (Point p : pcoa) {
            XYDataItem item = new XYDataItem(p.getX(), p.getY());
            series.add(item);
            toolTips.put(item, p.getName());
        }
        dataset.addSeries(series);

        chart = ChartFactory.createScatterPlot(getTitle(), "C1"
               , "C2", dataset, PlotOrientation.VERTICAL, false, true, false);
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
    }

    @Override
    public JComponent getCustomizer() {
        return null;
    }

    @Override
    protected String getTitle() {
        return "PCoA plot for " + getAttributeType().getName();
    }
}
