package de.cebitec.mgx.gui.charts.basic.util;

import de.cebitec.mgx.gui.datamodel.Attribute;
import de.cebitec.mgx.gui.datamodel.misc.Distribution;
import de.cebitec.mgx.gui.datamodel.misc.Pair;
import de.cebitec.mgx.gui.groups.ImageExporterI;
import de.cebitec.mgx.gui.groups.VisualizationGroup;
import de.cebitec.mgx.gui.util.FileChooserUtils;
import de.cebitec.mgx.gui.util.FileType;
import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultKeyedValues2DDataset;
import org.jfree.data.general.KeyedValues2DDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Exceptions;

/**
 *
 * @author sj
 */
public class JFreeChartUtil {

    public static LegendItemCollection createLegend(List<Pair<VisualizationGroup, Distribution>> in) {
        LegendItemCollection ret = new LegendItemCollection();
        for (Pair<VisualizationGroup, Distribution> gd : in) {
            LegendItem li = new LegendItem(gd.getFirst().getName());
            li.setFillPaint(gd.getFirst().getColor());
            li.setToolTipText("Classified sequences in "+gd.getFirst().getName()+": "+ gd.getSecond().getTotalClassifiedElements());
            ret.add(li);
        }
        return ret;
    }

    public static DefaultCategoryDataset createCategoryDataset(List<Pair<VisualizationGroup, Distribution>> in) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Pair<VisualizationGroup, Distribution> groupDistribution : in) {
            Distribution d = groupDistribution.getSecond();

            for (Map.Entry<Attribute, ? extends Number> entry : d.entrySet()) {
                dataset.addValue(entry.getValue(), groupDistribution.getFirst().getName(), entry.getKey().getValue());
            }
        }
        return dataset;
    }

    public static XYSeriesCollection createXYSeries(List<Pair<VisualizationGroup, Distribution>> in) {
        return createXYSeries(in, false);
    }

    public static XYSeriesCollection createXYSeries(List<Pair<VisualizationGroup, Distribution>> in, boolean createBounds) {
        XYSeriesCollection dataset = new XYSeriesCollection();

        for (Pair<VisualizationGroup, Distribution> groupDistribution : in) {
            XYSeries series = new XYSeries(groupDistribution.getFirst().getName());

            double[][] values = new double[groupDistribution.getSecond().size()][];
            int idx = 0;

            double minAttrVal = Double.MAX_VALUE;
            double maxAttrVal = Double.MIN_VALUE;

            for (Map.Entry<Attribute, ? extends Number> entry : groupDistribution.getSecond().entrySet()) {
                double attrVal = Double.parseDouble(entry.getKey().getValue());
                double value = entry.getValue().doubleValue();

                values[idx++] = new double[]{attrVal, value};

                if (createBounds) {
                    minAttrVal = attrVal < minAttrVal ? attrVal : minAttrVal;
                    maxAttrVal = attrVal > maxAttrVal ? attrVal : maxAttrVal;
                }
            }

            if (createBounds) {
                series.add(minAttrVal - Double.MIN_VALUE, 0);
            }

            for (int i = 0; i < values.length; i++) {
                double[] pair = values[i];
                series.add(pair[0], pair[1]);
            }

            if (createBounds) {
                series.add(maxAttrVal + Double.MIN_VALUE, 0);
            }

//            for (Map.Entry<Attribute, ? extends Number> entry : groupDistribution.getSecond().entrySet()) {
//                double d = Double.parseDouble(entry.getKey().getValue());
//                series.add(d, entry.getValue());
//            }
            dataset.addSeries(series);
        }
        return dataset;
    }

    public static KeyedValues2DDataset createKeyedValues2DDataset(List<Pair<VisualizationGroup, Distribution>> in) {
        DefaultKeyedValues2DDataset dataset = new DefaultKeyedValues2DDataset();
        for (Pair<VisualizationGroup, Distribution> groupDistribution : in) {
            Distribution d = groupDistribution.getSecond();

            for (Map.Entry<Attribute, ? extends Number> entry : d.entrySet()) {
                dataset.addValue(entry.getValue(), groupDistribution.getFirst().getName(), entry.getKey().getValue());
            }
        }

        return dataset;
    }

//    public static void saveSVG(JFreeChart chart, File f) {
//        DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
//        Document document = domImpl.createDocument(null, "svg", null);
//        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);
//        svgGenerator.getGeneratorContext().setPrecision(6);
//        chart.draw(svgGenerator, new Rectangle2D.Double(0, 0, 800, 600), null);
//        boolean useCSS = true;
//        Writer out;
//        try {
//            out = new OutputStreamWriter(new FileOutputStream(f), "UTF-8");
//            svgGenerator.stream(out, useCSS);
//        } catch (FileNotFoundException | UnsupportedEncodingException | SVGGraphics2DIOException ex) {
//            Exceptions.printStackTrace(ex);
//        }
//    }
    public static ImageExporterI getImageExporter(final JFreeChart chart) {
        return new ImageExporterI() {
            @Override
            public void export() {
                String fname = FileChooserUtils.selectNewFilename(new FileType[]{FileType.PNG, FileType.SVG});
                if (fname == null) {
                    return;
                }

                if (fname.endsWith("png")) {
                    try {
                        ChartUtilities.saveChartAsPNG(new File(fname), chart, 1280, 1024);
                    } catch (IOException ex) {
                        NotifyDescriptor nd = new NotifyDescriptor.Message("Error: " + ex.getMessage(), NotifyDescriptor.ERROR_MESSAGE);
                        DialogDisplayer.getDefault().notify(nd);
                        return;
                    }
                } else { // SVG
                    SVGGraphics2D g2 = new SVGGraphics2D(1280, 1024);
                    chart.draw(g2, new Rectangle(0, 0, 1280, 1024));
                    String svgElement = g2.getSVGElement();
                    try (BufferedWriter bw = new BufferedWriter(new FileWriter(fname))) {
                        bw.write(svgElement);
                    } catch (IOException ex) {
                        NotifyDescriptor nd = new NotifyDescriptor.Message("Error: " + ex.getMessage(), NotifyDescriptor.ERROR_MESSAGE);
                        DialogDisplayer.getDefault().notify(nd);
                        return;
                    }
                }
                NotifyDescriptor nd = new NotifyDescriptor.Message("Chart saved to " + fname, NotifyDescriptor.INFORMATION_MESSAGE);
                DialogDisplayer.getDefault().notify(nd);
            }
        };
    }

    public static void savePNG(JFreeChart chart, File f) {
        try {
            ChartUtilities.saveChartAsPNG(f, chart, 800, 600);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public static void saveJPEG(JFreeChart chart, File f) {
        try {
            ChartUtilities.saveChartAsJPEG(f, chart, 800, 600);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
