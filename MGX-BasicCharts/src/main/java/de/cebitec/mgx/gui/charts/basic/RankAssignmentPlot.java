package de.cebitec.mgx.gui.charts.basic;

import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.gui.charts.basic.customizer.BarChartCustomizer;
import de.cebitec.mgx.api.groups.ImageExporterI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.tree.TreeI;
import de.cebitec.mgx.common.DistributionFactory;
import de.cebitec.mgx.common.TreeFactory;
import de.cebitec.mgx.common.visualization.HierarchicalViewerI;
import de.cebitec.mgx.common.visualization.ViewerI;
import de.cebitec.mgx.gui.charts.basic.j2d.PlotPanel;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author sjaenick
 */
@ServiceProvider(service = ViewerI.class)
public class RankAssignmentPlot extends HierarchicalViewerI {

//    private ChartPanel cPanel = null;
//    private JFreeChart chart = null;
//    private DefaultCategoryDataset dataset;
    public RankAssignmentPlot() {
        // disable the stupid glossy effect
//        ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
//        BarRenderer.setDefaultShadowsVisible(false);
//        XYBarRenderer.setDefaultShadowsVisible(false);
    }

    @Override
    public String getName() {
        return "Stacked Rank";
    }

    private JComponent jcomp;

    @Override
    public JComponent getComponent() {
        return jcomp;
    }

    @Override
    public void show(List<Pair<VisualizationGroupI, TreeI<Long>>> data) {

        TreeI<Map<VisualizationGroupI, Long>> combinedTree = TreeFactory.combineTrees(data);
        AttributeTypeI[] longestPath = TreeFactory.getLongestPath(combinedTree);

        Map<AttributeTypeI, List<DistributionI<Long>>> byRank = new HashMap<>();

        for (AttributeTypeI attrType : longestPath) {
            List<DistributionI<Long>> dists = new ArrayList<>();
            for (Pair<VisualizationGroupI, TreeI<Long>> p : data) {
                dists.add(DistributionFactory.fromTree(p.getSecond(), attrType));
            }
            byRank.put(attrType, dists);
        }

        Map<AttributeTypeI, List<AttributeI>> sortedAttrsByAbundance = new HashMap<>();
        for (AttributeTypeI attrType : longestPath) {
            final Map<AttributeI, Long> sum = new HashMap<>();
            for (DistributionI<Long> d : byRank.get(attrType)) {
                for (Entry<AttributeI, Long> e : d.entrySet()) {
                    if (sum.containsKey(e.getKey())) {
                        sum.put(e.getKey(), e.getValue() + sum.get(e.getKey()));
                    } else {
                        sum.put(e.getKey(), e.getValue());
                    }
                }
            }
            List<AttributeI> sortedDesc = new ArrayList<>(sum.size());
            sortedDesc.addAll(sum.keySet());
            Collections.sort(sortedDesc, new Comparator<AttributeI>() {

                @Override
                public int compare(AttributeI a, AttributeI b) {
                    Long d1 = sum.get(a);
                    Long d2 = sum.get(b);
                    return d2.compareTo(d1);
                }
            });
            sortedAttrsByAbundance.put(attrType, sortedDesc);
        }

        PlotPanel p = new PlotPanel();

        for (AttributeTypeI attrType : longestPath) {
            int i = 0;
            for (DistributionI<Long> d : byRank.get(attrType)) {
                VisualizationGroupI vGrp = data.get(i).getFirst();
                p.createBar(vGrp, attrType, sortedAttrsByAbundance.get(attrType), d);
                i++;
            }
        }

        JScrollPane jp = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER) {

            @Override
            protected void processMouseWheelEvent(MouseWheelEvent e) {
                if (!isWheelScrollingEnabled()) {
                    if (getParent() != null) {
                        getParent().dispatchEvent(
                                SwingUtilities.convertMouseEvent(this, e, getParent()));
                    }
                    return;
                }
                super.processMouseWheelEvent(e);
            }

        };
        jp.setViewportView(p);
        jp.setWheelScrollingEnabled(false);
        jcomp = jp;

//        dataset = new DefaultCategoryDataset();
//        GroupedStackedBarRenderer renderer = new GroupedStackedBarRenderer();
//        renderer.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator("<html>Name: {0} <br> Rank: {1} <br> Count: {2} ({3})</html>", NumberFormat.getInstance()));
//
//        KeyToGroupMap map = new KeyToGroupMap("G1");
//
//        Map<String, Paint> attrPaints = new HashMap<>();
//        List<VisualizationGroupI> groups = new ArrayList<>(data.size());
//        List<AttributeI> allAttrs = new ArrayList<>();
//        Map<VisualizationGroupI, Map<AttributeTypeI, DistributionI<Long>>> byGroup = new HashMap<>();
//        for (Pair<VisualizationGroupI, TreeI<Long>> p : data) {
//            groups.add(p.getFirst());
//            Map<AttributeTypeI, DistributionI<Long>> byAttrType = new HashMap<>();
//            for (AttributeTypeI aType : longestPath) {
//                DistributionI<Long> dist = DistributionFactory.fromTree(p.getSecond(), aType);
//                SortOrder<Long> sorter = new SortOrder<>(aType, SortOrder.DESCENDING);
//                dist = sorter.filterDist(dist);
//
//                byAttrType.put(aType, dist);
//                for (AttributeI attr : dist.keySet()) {
//                    if (!allAttrs.contains(attr)) {
//                        allAttrs.add(attr);
//                        attrPaints.put(attr.getValue(), paints[paintIdx % paints.length]);
//                        paintIdx++;
//                    }
//                }
//            }
//            byGroup.put(p.getFirst(), byAttrType);
//        }
//
//        int seriesIdx = 0;
//        for (VisualizationGroupI vgrp : groups) {
//            Map<AttributeTypeI, DistributionI<Long>> byAttrType = byGroup.get(vgrp);
//            for (AttributeI attr : allAttrs) {
//                for (AttributeTypeI rank : longestPath) {
//                    DistributionI<Long> curDist = byAttrType.get(rank);
//                    dataset.addValue(curDist.containsKey(attr) ? curDist.get(attr) : 0.0, vgrp.getName() + " " + attr.getValue(), rank.getName());
//                }
//                renderer.setSeriesPaint(seriesIdx++, attrPaints.get(attr.getValue()));
//                map.mapKeyToGroup(vgrp.getName() + " " + attr.getValue(), vgrp.getName());
//            }
//        }
//
//        renderer.setSeriesToGroupMap(map);
//        renderer.setItemMargin(0.0);
//
//        chart = ChartFactory.createStackedBarChart(
//                getTitle(),
//                "Rank",
//                "Count",
//                dataset,
//                PlotOrientation.VERTICAL,
//                false,
//                true,
//                false
//        );
//
//        SubCategoryAxis domainAxis = new SubCategoryAxis("");
//        domainAxis.setCategoryMargin(0.05);
//        for (Pair<VisualizationGroupI, TreeI<Long>> p : data) {
//            domainAxis.addSubCategory(p.getFirst().getName());
//        }
//        CategoryPlot plot = (CategoryPlot) chart.getPlot();
//        plot.setDomainAxis(domainAxis);
//        plot.setRenderer(renderer);
//
//        chart.setBorderPaint(Color.WHITE);
//        chart.setBackgroundPaint(Color.WHITE);
//        chart.setAntiAlias(true);
//        cPanel = new ChartPanel(chart);
    }

    @Override
    public BarChartCustomizer getCustomizer() {
        return null;
    }

    @Override
    public ImageExporterI getImageExporter() {
        return null; // JFreeChartUtil.getImageExporter(chart);
    }

}
