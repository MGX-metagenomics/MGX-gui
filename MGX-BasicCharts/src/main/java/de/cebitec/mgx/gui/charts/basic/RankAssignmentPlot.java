package de.cebitec.mgx.gui.charts.basic;

import de.cebitec.mgx.api.groups.FileType;
import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.groups.ImageExporterI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.tree.TreeI;
import de.cebitec.mgx.common.DistributionFactory;
import de.cebitec.mgx.common.TreeFactory;
import de.cebitec.mgx.common.visualization.CustomizableI;
import de.cebitec.mgx.common.visualization.HierarchicalViewerI;
import de.cebitec.mgx.common.visualization.ViewerI;
import de.cebitec.mgx.gui.charts.basic.customizer.StackedRankCustomizer;
import de.cebitec.mgx.gui.charts.basic.j2d.Normalization;
import de.cebitec.mgx.gui.charts.basic.j2d.PlotPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author sjaenick
 */
@ServiceProvider(service = ViewerI.class)
public class RankAssignmentPlot extends HierarchicalViewerI implements ImageExporterI.Provider, CustomizableI {

    private StackedRankCustomizer customizer = null;

    public RankAssignmentPlot() {
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

    Map<VisualizationGroupI, Long> maxAssignedByGroup = new HashMap<>();

    @Override
    public void show(List<Pair<VisualizationGroupI, TreeI<Long>>> data) {

        // get longest path in tree to determine ordering of attribute types
        TreeI<Map<VisualizationGroupI, Long>> combinedTree = TreeFactory.combineTrees(data);
        AttributeTypeI[] longestPath = TreeFactory.getLongestPath(combinedTree);

        maxAssignedByGroup.clear();

        // collect distributions per rank
        Map<AttributeTypeI, List<DistributionI<Long>>> byRank = new HashMap<>();
        for (AttributeTypeI attrType : longestPath) {
            List<DistributionI<Long>> dists = new ArrayList<>(data.size());
            for (Pair<VisualizationGroupI, TreeI<Long>> p : data) {
                DistributionI<Long> fromTree = DistributionFactory.fromTree(p.getSecond(), attrType);
                dists.add(fromTree);

                // this is only needed when normalizing to the root
                if (getCustomizer().normToRoot()) {
                    if (!maxAssignedByGroup.containsKey(p.getFirst())) {
                        maxAssignedByGroup.put(p.getFirst(), fromTree.getTotalClassifiedElements());
                    } else if (fromTree.getTotalClassifiedElements() > maxAssignedByGroup.get(p.getFirst())) {
                        maxAssignedByGroup.put(p.getFirst(), fromTree.getTotalClassifiedElements());
                    }
                }
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

        Normalization norm = Normalization.DISABLED;
        if (getCustomizer().normToRoot()) {
            norm = Normalization.ROOT;
        } else if (getCustomizer().normAll()) {
            norm = Normalization.ALL;
        }

        PlotPanel plotPanel = new PlotPanel(norm, maxAssignedByGroup);

        for (AttributeTypeI attrType : longestPath) {
            int i = 0;
            for (DistributionI<Long> dist : byRank.get(attrType)) {
                VisualizationGroupI vGrp = data.get(i).getFirst();
                plotPanel.createBar(vGrp, attrType, sortedAttrsByAbundance.get(attrType), dist);
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
        jp.setBackground(Color.WHITE);
        jp.setViewportView(plotPanel);
        jp.setWheelScrollingEnabled(false);
        jcomp = jp;
    }

    @Override
    public StackedRankCustomizer getCustomizer() {
        if (customizer == null) {
            customizer = new StackedRankCustomizer();
        }
        return customizer;
    }

    @Override
    public ImageExporterI getImageExporter() {
        return new ImageExporterI() {

            @Override
            public FileType[] getSupportedTypes() {
                return new FileType[]{FileType.PNG, FileType.SVG};
            }

            @Override
            public Result export(FileType type, String fName) throws Exception {
                switch (type) {
                    case PNG:
                        BufferedImage bi = new BufferedImage(jcomp.getSize().width, jcomp.getSize().height, BufferedImage.TYPE_INT_ARGB);
                        Graphics g = bi.createGraphics();
                        jcomp.paint(g);
                        g.dispose();
                        try {
                            ImageIO.write(bi, "png", new File(fName));
                        } catch (Exception e) {
                            return Result.ERROR;
                        }
                        return Result.SUCCESS;
                    /*
                         *
                         * JPEG support disabled for now because it produces a red background
                         *
                     */
//                    case JPEG:
//                        BufferedImage bi2 = new BufferedImage(jcomp.getSize().width, jcomp.getSize().height, BufferedImage.TYPE_INT_ARGB);
//                        Graphics g2 = bi2.createGraphics();
//                        jcomp.paint(g2);
//                        g2.dispose();
//                        try {
//                            ImageIO.write(bi2, "jpg", new File(fName));
//                        } catch (Exception e) {
//                            return Result.ERROR;
//                        }
//                        return Result.SUCCESS;
                    case SVG:
                        SVGGraphics2D svg = new SVGGraphics2D(1280, 1024);
                        jcomp.paintComponents(svg);
                        String svgElement = svg.getSVGElement();
                        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fName))) {
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
