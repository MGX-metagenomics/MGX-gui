package de.cebitec.mgx.gui.qcmon;

import de.cebitec.mgx.api.model.qc.DataRowI;
import de.cebitec.mgx.api.model.qc.QCResultI;
import de.cebitec.mgx.gui.charts.basic.util.SVGChartPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYErrorRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.DefaultTableXYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.YIntervalSeries;
import org.jfree.data.xy.YIntervalSeriesCollection;

/**
 *
 * @author sj
 */
public class QCChartGenerator {

    private QCChartGenerator() {
    }

    public static SVGChartPanel createChart(QCResultI qcr) {
        if ("Sequence quality".equals(qcr.getName()) || "Forward read quality".equals(qcr.getName()) || "Reverse read quality".equals(qcr.getName())) {
            YIntervalSeriesCollection qualityDataset = new YIntervalSeriesCollection();
            DataRowI[] data = qcr.getData();
            DataRowI quality = data[0];
            DataRowI error = data[1];

            YIntervalSeries qualitySeries = new YIntervalSeries(quality.getName(), true, false);
            float[] qualityData = quality.getData();
            float[] errorData = error.getData();
            for (int x = 1; x < qualityData.length; x++) {
                qualitySeries.add(x, qualityData[x], qualityData[x] - errorData[x], qualityData[x] + errorData[x]);
            }
            qualityDataset.addSeries(qualitySeries);

            boolean showLegend = false;
            final JFreeChart chart = ChartFactory.createXYLineChart(null, null, null, qualityDataset, PlotOrientation.VERTICAL, showLegend, true, false);
            XYPlot plot = (XYPlot) chart.getPlot();

            plot.addRangeMarker(new IntervalMarker(0, 20, Color.RED, new BasicStroke(0.5f), null, null, 0.3f));
            plot.addRangeMarker(new IntervalMarker(20, 28, Color.YELLOW, new BasicStroke(0.5f), null, null, 0.3f));
            plot.addRangeMarker(new IntervalMarker(28, 500, Color.GREEN, new BasicStroke(0.5f), null, null, 0.3f));

            XYLineAndShapeRenderer dataRenderer = new XYLineAndShapeRenderer();
            dataRenderer.setSeriesPaint(0, Color.BLUE);
            dataRenderer.setSeriesShapesVisible(0, false);
            dataRenderer.setSeriesStroke(0, new BasicStroke(1.0f));
            plot.setRenderer(0, dataRenderer);
            plot.setDataset(1, qualityDataset);

            XYErrorRenderer errorRenderer = new XYErrorRenderer();
            errorRenderer.setDrawXError(false);
            errorRenderer.setErrorPaint(Color.BLACK);
            errorRenderer.setErrorStroke(new BasicStroke(1.0f));
            errorRenderer.setSeriesShapesVisible(0, false);
            errorRenderer.setSeriesShapesVisible(1, false);
            plot.setRenderer(1, errorRenderer);

            plot.getRangeAxis().setRange(new Range(0, errorRenderer.findRangeBounds(qualityDataset).getUpperBound() + 5));

            chart.setBorderPaint(Color.WHITE);
            chart.setBackgroundPaint(Color.WHITE);
            SVGChartPanel cPanel = new SVGChartPanel(chart);
            cPanel.setPopupMenu(null);
            plot.setBackgroundPaint(Color.WHITE);

            return cPanel;
        } else {
            DefaultTableXYDataset dataset = new DefaultTableXYDataset();
            for (DataRowI dr : qcr.getData()) {
                XYSeries series = new XYSeries(dr.getName(), true, false);
                int x = 1;
                for (float f : dr.getData()) {
                    series.add(x++, f);
                }

                // add some padding for read length
                if ("Read length".equals(dr.getName())) {
                    series.add(x++, 0f);
                    series.add(x++, 0f);
                    series.add(x++, 0f);
                    series.add(x++, 0f);
                }

                dataset.addSeries(series);
            }

            boolean showLegend = qcr.getData().length > 1;
            JFreeChart chart = ChartFactory.createStackedXYAreaChart(null, null, null, dataset, PlotOrientation.VERTICAL, showLegend, true, false);

            chart.setBorderPaint(Color.WHITE);
            chart.setBackgroundPaint(Color.WHITE);
            SVGChartPanel cPanel = new SVGChartPanel(chart);
            cPanel.setPopupMenu(null);
            XYPlot plot = (XYPlot) chart.getPlot();
            plot.setBackgroundPaint(Color.WHITE);

            switch (qcr.getName()) {
                case "Read length":
                    plot.getRenderer().setSeriesPaint(0, Color.decode("#1d72aa"));
                    break;
                case "GC":
                    plot.getRenderer().setSeriesPaint(0, Color.decode("#8cbb4e"));
                    break;
            }

            return cPanel;
        }
    }

}
