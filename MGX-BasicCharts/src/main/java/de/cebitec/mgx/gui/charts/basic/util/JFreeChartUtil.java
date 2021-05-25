package de.cebitec.mgx.gui.charts.basic.util;

import de.cebitec.mgx.api.groups.FileType;
import de.cebitec.mgx.api.groups.GroupI;
import de.cebitec.mgx.api.groups.ImageExporterI;
import de.cebitec.mgx.api.groups.ReplicateGroupI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.misc.Triple;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.gui.vizfilter.SortOrder.SortByValue;
import gnu.trove.map.TObjectDoubleMap;
import gnu.trove.map.hash.TObjectDoubleHashMap;
import java.awt.Rectangle;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DefaultKeyedValues2DDataset;
import org.jfree.data.general.KeyedValues2DDataset;
import org.jfree.data.statistics.DefaultStatisticalCategoryDataset;
import org.jfree.data.statistics.StatisticalCategoryDataset;
import org.jfree.data.xy.DefaultTableXYDataset;
import org.jfree.data.xy.TableXYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.graphics2d.svg.SVGGraphics2D;

/**
 *
 * @author sj
 */
public class JFreeChartUtil {

    public static <T extends Number> LegendItemCollection createLegend(List<Pair<GroupI, DistributionI<T>>> in) {
        LegendItemCollection ret = new LegendItemCollection();
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        for (Pair<GroupI, DistributionI<T>> gd : in) {
            LegendItem li = new LegendItem(gd.getFirst().getDisplayName());
            li.setFillPaint(gd.getFirst().getColor());
            li.setToolTipText("Classified sequences in " + gd.getFirst().getDisplayName() + ": " + formatter.format(gd.getSecond().getTotalClassifiedElements()));
            ret.add(li);
        }
        return ret;
    }

    public static <T extends Number> LegendItemCollection createSingleLegend(GroupI grp, DistributionI<T> dist) {
        LegendItemCollection ret = new LegendItemCollection();
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        LegendItem li = new LegendItem(grp.getDisplayName());
        li.setFillPaint(grp.getColor());
        li.setToolTipText("Classified sequences in " + grp.getDisplayName() + ": " + formatter.format(dist.getTotalClassifiedElements()));
        ret.add(li);
        return ret;
    }

    public static LegendItemCollection createReplicateLegend(List<Triple<ReplicateGroupI, DistributionI<Double>, DistributionI<Double>>> in) {
        LegendItemCollection ret = new LegendItemCollection();
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        for (Triple<ReplicateGroupI, DistributionI<Double>, DistributionI<Double>> rg : in) {
            LegendItem li = new LegendItem(rg.getFirst().getName());
            li.setFillPaint(rg.getFirst().getColor());
            li.setToolTipText("Classified sequences in " + rg.getFirst().getName() + ": " + formatter.format(rg.getSecond().getTotalClassifiedElements()));
            ret.add(li);
        }
        return ret;
    }

    public static <T extends Number> CategoryDataset createCategoryDataset(List<Pair<GroupI, DistributionI<T>>> in) {
        return createCategoryDataset(in, false);
    }

    public static <T extends Number> CategoryDataset createCategoryDataset(List<Pair<GroupI, DistributionI<T>>> in, boolean ascending) {

        // summary distribution over all groups
        TObjectDoubleMap<AttributeI> summary = new TObjectDoubleHashMap<>();
        for (Pair<GroupI, DistributionI<T>> pair : in) {
            DistributionI<T> dist = pair.getSecond();
            for (Entry<AttributeI, T> p : dist.entrySet()) {
                if (summary.containsKey(p.getKey())) {
                    Double old = summary.get(p.getKey());
                    summary.put(p.getKey(), old + p.getValue().doubleValue());
                } else {
                    summary.put(p.getKey(), p.getValue().doubleValue());
                }
            }
        }
        List<AttributeI> sortList = new ArrayList<>();
        sortList.addAll(summary.keySet());
        Collections.sort(sortList, new SortByValue(summary));
        if (ascending) {
            Collections.reverse(sortList);
        }

        FastCategoryDataset dataset = new FastCategoryDataset();
        dataset.setNotify(false);

        for (AttributeI attr : sortList) {
            for (Pair<GroupI, DistributionI<T>> groupDistribution : in) {
                final String displayName = groupDistribution.getFirst().getDisplayName();
                final DistributionI<T> d = groupDistribution.getSecond();
                dataset.addValue(d.get(attr), displayName, attr.getValue());
            }
        }

//        for (Pair<GroupI, DistributionI<T>> groupDistribution : in) {
//            final String displayName = groupDistribution.getFirst().getDisplayName();
//            final DistributionI<T> d = groupDistribution.getSecond();
//
//            for (Map.Entry<AttributeI, T> entry : d.entrySet()) {
//                dataset.addValue(entry.getValue(), displayName, entry.getKey().getValue());
//            }
//        }
        if (dataset.getColumnCount() > 25) {
            return new SlidingCategoryDataset(dataset, 25);
        } else {
            return dataset;
        }
    }

    public static <T extends Number> CategoryDataset createSingleCategoryDataset(GroupI grp, DistributionI<T> dist, boolean ascending) {

        // summary distribution for sorting
        TObjectDoubleMap<AttributeI> summary = new TObjectDoubleHashMap<>();
        for (Entry<AttributeI, T> p : dist.entrySet()) {
            summary.put(p.getKey(), p.getValue().doubleValue());
        }

        List<AttributeI> sortList = new ArrayList<>();
        sortList.addAll(dist.keySet());

        Collections.sort(sortList, new SortByValue(summary));
        if (ascending) {
            Collections.reverse(sortList);
        }

        FastCategoryDataset dataset = new FastCategoryDataset();
        dataset.setNotify(false);

        for (AttributeI attr : sortList) {
            final String displayName = grp.getDisplayName();
            dataset.addValue(dist.get(attr), displayName, attr.getValue());
        }

        if (dataset.getColumnCount() > 25) {
            return new SlidingCategoryDataset(dataset, 25);
        } else {
            return dataset;
        }
    }

    public static StatisticalCategoryDataset createStatisticalCategoryDataset(List<Triple<ReplicateGroupI, DistributionI<Double>, DistributionI<Double>>> groups) {
        DefaultStatisticalCategoryDataset dataset = new DefaultStatisticalCategoryDataset();
        dataset.setNotify(false);

        for (Triple<ReplicateGroupI, DistributionI<Double>, DistributionI<Double>> group : groups) {
            DistributionI<Double> mean = group.getSecond();
            DistributionI<Double> stdv = group.getThird();

            for (Map.Entry<AttributeI, Double> entry : mean.entrySet()) {
                dataset.add(entry.getValue(), stdv.get(entry.getKey()), group.getFirst().getName(), entry.getKey().getValue());
            }
        }

        if (dataset.getColumnCount() > 25) {
            return new SlidingStatisticalCategoryDataset(dataset, 25);
        } else {
            return dataset;
        }
    }

//    public static DefaultCategoryDataset createCategoryDataset(List<Pair<VisualizationGroup, Distribution>> in) {
//        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
//        for (Pair<VisualizationGroup, Distribution> groupDistribution : in) {
//            Distribution d = groupDistribution.getSecond();
//
//            for (Map.Entry<Attribute, ? extends Number> entry : d.entrySet()) {
//                dataset.addValue(entry.getValue(), groupDistribution.getFirst().getName(), entry.getKey().getValue());
//            }
//        }
//        return dataset;
//    }
    public static <T extends Number> XYSeriesCollection createXYSeries(List<Pair<GroupI, DistributionI<T>>> in) {
        return createXYSeries(in, false);
    }

    public static <T extends Number> XYSeriesCollection createXYSeries(List<Pair<GroupI, DistributionI<T>>> in, boolean createBounds) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.setNotify(false);

        for (Pair<GroupI, DistributionI<T>> groupDistribution : in) {
            XYSeries series = new XYSeries(groupDistribution.getFirst().getDisplayName());

            double[][] values = new double[groupDistribution.getSecond().size()][];
            int idx = 0;

            double minAttrVal = Double.MAX_VALUE;
            double maxAttrVal = Double.MIN_VALUE;

            for (Map.Entry<AttributeI, T> entry : groupDistribution.getSecond().entrySet()) {
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

            for (double[] pair : values) {
                series.add(pair[0], pair[1]);
            }

            if (createBounds) {
                series.add(maxAttrVal + Double.MIN_VALUE, 0);
            }
            dataset.addSeries(series);
        }
        return dataset;
    }

    public static <T extends Number> TableXYDataset createTableXYDataset(List<Pair<GroupI, DistributionI<T>>> in) {
        DefaultTableXYDataset dataset = new DefaultTableXYDataset();
        dataset.setNotify(false);

        for (Pair<GroupI, DistributionI<T>> groupDistribution : in) {
            XYSeries series = new XYSeries(groupDistribution.getFirst().getDisplayName(), true, false);

            for (Map.Entry<AttributeI, T> entry : groupDistribution.getSecond().entrySet()) {
                double attrVal = Double.parseDouble(entry.getKey().getValue());
                double value = entry.getValue().doubleValue();
                series.add(attrVal, value);
            }
            dataset.addSeries(series);
        }
        return dataset;
    }

    public static <T extends Number> KeyedValues2DDataset createKeyedValues2DDataset(List<Pair<GroupI, DistributionI<T>>> in) {
        DefaultKeyedValues2DDataset dataset = new DefaultKeyedValues2DDataset();
        dataset.setNotify(false);

        for (Pair<GroupI, DistributionI<T>> groupDistribution : in) {
            DistributionI<T> d = groupDistribution.getSecond();

            for (Map.Entry<AttributeI, T> entry : d.entrySet()) {
                dataset.addValue(entry.getValue(), groupDistribution.getFirst().getDisplayName(), entry.getKey().getValue());
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
        if (chart == null) {
            return null;
        }

//        CategoryPlot cPlot = null;
//        try {
//            cPlot = chart.getCategoryPlot();
//        } catch (ClassCastException x) {
//        }
//        if (cPlot != null) {
//            Font oldLabelFont = cPlot.getDomainAxis().getLabelFont();
//            Font labelFont = new Font(oldLabelFont.getName(), Font.BOLD, 20);
//            cPlot.getDomainAxis().setLabelFont(labelFont);
//            cPlot.getDomainAxis().setTickLabelFont(new Font(oldLabelFont.getName(), Font.PLAIN, 18));
//
//            cPlot.getRangeAxis().setLabelFont(labelFont);
//            cPlot.getRangeAxis().setTickLabelFont(new Font(oldLabelFont.getName(), Font.PLAIN, 18));
//        } else {
//            XYPlot xyPlot = chart.getXYPlot();
//            Font oldLabelFont = xyPlot.getDomainAxis().getLabelFont();
//            Font labelFont = new Font(oldLabelFont.getName(), Font.BOLD, 20);
//            xyPlot.getDomainAxis().setLabelFont(labelFont);
//            xyPlot.getDomainAxis().setTickLabelFont(new Font(oldLabelFont.getName(), Font.PLAIN, 18));
//
//            xyPlot.getRangeAxis().setLabelFont(labelFont);
//            xyPlot.getRangeAxis().setTickLabelFont(new Font(oldLabelFont.getName(), Font.PLAIN, 18));
//        }
        return new ImageExporterI() {

            @Override
            public FileType[] getSupportedTypes() {
                return new FileType[]{FileType.PNG, FileType.JPEG, FileType.SVG};
            }

            @Override
            public Result export(FileType type, String fName) throws Exception {
                switch (type) {
                    case PNG:
                        ChartUtilities.saveChartAsPNG(new File(fName), chart, 1280, 1024);
                        return Result.SUCCESS;
                    case JPEG:
                        ChartUtilities.saveChartAsJPEG(new File(fName), chart, 1280, 1024);
                        return Result.SUCCESS;
                    case SVG:
                        SVGGraphics2D g2 = new SVGGraphics2D(1280, 768);
                        chart.draw(g2, new Rectangle(0, 0, 1280, 768));
                        String svgElement = g2.getSVGElement();
                        try ( BufferedWriter bw = new BufferedWriter(new FileWriter(fName))) {
                            bw.write(svgElement);
                        }
                        return Result.SUCCESS;
                    default:
                        return Result.ERROR;
                }
            }
        };
    }
}
