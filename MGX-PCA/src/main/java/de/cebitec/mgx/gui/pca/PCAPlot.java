package de.cebitec.mgx.gui.pca;

import de.cebitec.mgx.gui.attributevisualization.viewer.ViewerI;
import de.cebitec.mgx.gui.charts.basic.util.JFreeChartUtil;
import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.AttributeType;
import de.cebitec.mgx.gui.datamodel.misc.Distribution;
import de.cebitec.mgx.gui.datamodel.misc.PCAResult;
import de.cebitec.mgx.gui.datamodel.misc.Pair;
import de.cebitec.mgx.gui.datamodel.misc.Point;
import de.cebitec.mgx.gui.groups.ImageExporterI;
import de.cebitec.mgx.gui.groups.VGroupManager;
import de.cebitec.mgx.gui.groups.VisualizationGroup;
import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.text.DecimalFormat;
import java.util.List;
import javax.swing.JComponent;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author sj
 */
@ServiceProvider(service = ViewerI.class)
public class PCAPlot extends ViewerI<Distribution> {

    private ChartPanel cPanel = null;
    private JFreeChart chart = null;

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
    public boolean canHandle(AttributeType valueType) {
        return VGroupManager.getInstance().getActiveGroups().size() > 1;
    }

    @Override
    public Class getInputType() {
        return Distribution.class;
    }

    @Override
    public void show(List<Pair<VisualizationGroup, Distribution>> dists) {
        MGXMaster master = (MGXMaster) dists.get(0).getSecond().getMaster();
        PCAResult pca = master.Statistics().PCA(dists);

        double[] variances = pca.getVariances();
        double varSum = 0;
        for (double d : variances) {
            varSum += d;
        }
        String pc1rel = String.format("%2.2f%n", variances[0] * 100 / varSum);
        String pc2rel = String.format("%2.2f%n", variances[1] * 100 / varSum);

        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries series = new XYSeries("FOO");
        for (Point p : pca.getDatapoints()) {
            series.add(new XYDataItem(p.getX(), p.getY()));
        }
        dataset.addSeries(series);
        XYSeries loadings = new XYSeries("BAR");
        for (Point p : pca.getLoadings()) {
            loadings.add(new XYDataItem(p.getX(), p.getY()));
        }
        dataset.addSeries(loadings);

        chart = ChartFactory.createXYLineChart(getTitle(), "PC1 (" + pc1rel + "%)", "PC2 (" + pc2rel + "%)", dataset, PlotOrientation.VERTICAL, false, true, false);
        chart.setBorderPaint(Color.WHITE);
        chart.setBackgroundPaint(Color.WHITE);
        cPanel = new ChartPanel(chart);
        cPanel.setDisplayToolTips(true);
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);

        NumberAxis axis2 = new NumberAxis("Range Axis 2");
        plot.setRangeAxis(1, axis2);
        plot.setRangeAxisLocation(1, AxisLocation.BOTTOM_OR_RIGHT);

        // renderer for data points
        final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setBaseLinesVisible(false);
        renderer.setSeriesShape(0, new Ellipse2D.Double(0, 0, 5, 5));
        XYItemLabelGenerator labelGen = new XYItemLabelGenerator() {

            @Override
            public String generateLabel(XYDataset xyd, int i, int i1) {
                return "FOOO";
            }
        };
        renderer.setSeriesItemLabelGenerator(1, labelGen);
        renderer.setSeriesItemLabelsVisible(1, Boolean.TRUE);
    }

    @Override
    public JComponent getCustomizer() {
        return null;
    }

}
