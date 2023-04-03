package de.cebitec.mgx.gui.radialtree;

import de.cebitec.mgx.api.groups.FileType;
import de.cebitec.mgx.api.groups.GroupI;
import de.cebitec.mgx.api.groups.ImageExporterI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.tree.NodeI;
import de.cebitec.mgx.api.model.tree.TreeI;
import de.cebitec.mgx.gui.radialtree.internal.ArcLabelRenderer;
import de.cebitec.mgx.gui.radialtree.internal.DecoratorLabelRenderer;
import de.cebitec.mgx.gui.radialtree.internal.MouseWheelControl;
import de.cebitec.mgx.gui.radialtree.internal.SectorRenderer;
import de.cebitec.mgx.gui.radialtree.internal.StarburstLayout;
import de.cebitec.mgx.gui.viewer.api.CustomizableI;
import de.cebitec.mgx.gui.viewer.api.HierarchicalViewerI;
import de.cebitec.mgx.gui.viewer.api.ViewerI;
import de.cebitec.mgx.gui.visgroups.VGroupManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import org.openide.util.lookup.ServiceProvider;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.Action;
import prefuse.action.ActionList;
import prefuse.action.GroupAction;
import prefuse.action.ItemAction;
import prefuse.action.RepaintAction;
import prefuse.action.animate.ColorAnimator;
import prefuse.action.animate.QualityControlAnimator;
import prefuse.action.animate.VisibilityAnimator;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.FontAction;
import prefuse.action.filter.FisheyeTreeFilter;
import prefuse.action.layout.CollapsedSubtreeLayout;
import prefuse.activity.SlowInSlowOutPacer;
import prefuse.controls.FocusControl;
import prefuse.controls.HoverActionControl;
import prefuse.controls.PanControl;
import prefuse.controls.ZoomControl;
import prefuse.controls.ZoomToFitControl;
import prefuse.data.Tuple;
import prefuse.data.event.TupleSetListener;
import prefuse.data.expression.OrPredicate;
import prefuse.data.search.PrefixSearchTupleSet;
import prefuse.data.search.SearchTupleSet;
import prefuse.data.tuple.CompositeTupleSet;
import prefuse.data.tuple.TupleSet;
import prefuse.render.AbstractShapeRenderer;
import prefuse.render.DefaultRendererFactory;
import prefuse.svg.SVGDisplaySaver;
import prefuse.util.ColorLib;
import prefuse.util.FontLib;
import prefuse.util.GraphicsLib;
import prefuse.util.display.DisplayLib;
import prefuse.util.ui.JFastLabel;
import prefuse.visual.VisualItem;
import prefuse.visual.expression.InGroupPredicate;
import prefuse.visual.expression.StartVisiblePredicate;
import prefuse.visual.expression.VisiblePredicate;
import prefuse.visual.sort.TreeDepthItemSorter;

/**
 *
 * @author sj
 */
@ServiceProvider(service = ViewerI.class)
public class RadialTree extends HierarchicalViewerI implements ImageExporterI.Provider, CustomizableI {

    public static final String nodeLabel = "label";
    public static final String nodeContent = "content";
    //public static final String nodeTotalElements = "totalElements"; //number of sequences assigned to this node
    //public static final String sameRankCount = "rankCount"; // total number of sequences assigned at same rank
    //
    private static final String tree = "tree";
    private static final String treeNodes = "tree.nodes";
    private static final String treeEdges = "tree.edges";
    private static final String labels = "labels";
    private String m_label;
    private Action resizeAction;
    private FisheyeTreeFilter fisheyeTreeFilter;
    private Display display;
    private Visualization visualization;
    private JFastLabel title;
    private prefuse.data.Tree pTree = null;
    private RadialTreeCustomizer cust = null;

    @Override
    public void show(List<Pair<GroupI, TreeI<Long>>> dists) {
        Pair<GroupI, TreeI<Long>> p = dists.get(0);
        TreeI<Long> mgxTree = p.getSecond();
        
        getCustomizer().setTree(mgxTree);

        initDisplay();

        NodeI<Long> root = mgxTree.getRoot();
        pTree = new prefuse.data.Tree();
        pTree.getNodeTable().addColumn(nodeLabel, String.class);
        //pTree.getNodeTable().addColumn(nodeTotalElements, long.class);
        //pTree.getNodeTable().addColumn(sameRankCount, Map.class);
        pTree.getNodeTable().addColumn(nodeContent, float.class);

        prefuse.data.Node rootNode = pTree.addRoot();
        rootNode.set(nodeLabel, root.getAttribute().getValue());
        rootNode.set(nodeContent, root.getContent().floatValue());
        //rootNode.set(nodeTotalElements, calculateNodeCount(root.getContent()));
        //rootNode.set(sameRankCount, rankCounts);

        for (NodeI<Long> child : root.getChildren()) {
            addWithChildren(pTree, rootNode, child);
        }

        m_label = nodeLabel; //unclear?

        // -- set up visualization --
        visualization.add(tree, pTree);
        visualization.setVisible(treeEdges, null, false);

        // -- set up renderers --
        DefaultRendererFactory rf = createRenderers();
        visualization.setRendererFactory(rf);

        // -- set up processing actions --
        // create the tree layout action; adds layout schema to nodes
        StarburstLayout treeLayout = new StarburstLayout(tree);
        // set location and turn off autoscale so graph layout doesn't revert to original view when mouse wheel is rolled
        treeLayout.setAutoScale(false);
        treeLayout.setLayoutAnchor(new Point2D.Double());
        treeLayout.setWidthType(StarburstLayout.WidthType.FIELD, nodeContent);
        // Uncomment next line to restrict graph to semi-circle
        //treeLayout.setAngularBounds(-Math.PI/2, Math.PI);  // TODO add widget to interactively adjust this
        visualization.putAction("treeLayout", treeLayout);

        // add decorators (has to be after layout because decorators rendered rely on Schema provided by StarburstLayout
        visualization.addDecorators(labels, treeNodes, new OrPredicate(new VisiblePredicate(), new StartVisiblePredicate()), ArcLabelRenderer.LABEL_SCHEMA);

        // fonts and colors for decorator items (labels)
        FontAction fonts = new StarburstLayout.ScaleFontAction(labels, m_label);
        ItemAction textColor = new TextColorAction(labels);

        CollapsedSubtreeLayout subLayout = new CollapsedSubtreeLayout(tree);
        visualization.putAction("subLayout", subLayout);

        // define focus groups
        TupleSet selected = visualization.getFocusGroup(Visualization.SELECTED_ITEMS);
        SearchTupleSet search = new PrefixSearchTupleSet();
        visualization.addFocusGroup(Visualization.SEARCH_ITEMS, search);

        // filter the tree to 2 levels from selected items and search results
        CompositeTupleSet searchAndSelect = new CompositeTupleSet();
        searchAndSelect.addSet(Visualization.SELECTED_ITEMS, visualization.getFocusGroup(Visualization.SELECTED_ITEMS));
        searchAndSelect.addSet(Visualization.SEARCH_ITEMS, visualization.getFocusGroup(Visualization.SEARCH_ITEMS));
        visualization.addFocusGroup("searchAndSelect", searchAndSelect);
        fisheyeTreeFilter = new FisheyeTreeFilter(tree, "searchAndSelect", 1);

        // colors
        ItemAction nodeColor = new NodeColorAction(treeNodes);
        ColorAction nodeStrokeColor = new ColorAction(treeNodes, VisualItem.STROKECOLOR) {
            @Override
            public int getColor(VisualItem item) {
                return ColorLib.darker(item.getFillColor());
            }
        };

        // recolor
        ActionList recolor = new ActionList();
        recolor.add(nodeColor);
        recolor.add(nodeStrokeColor);
        recolor.add(textColor);
        visualization.putAction("recolor", recolor);

        // animate paint change
        ActionList animatePaint = new ActionList(400);
        animatePaint.add(new ColorAnimator(treeNodes));
        animatePaint.add(new RepaintAction());
        visualization.putAction("animatePaint", animatePaint);

        // recentre and rezoom on reload
        resizeAction = new Action() {
            @Override
            public void run(double frac) {
                // animate reset zoom to fit the data (must run only AFTER layout)
                Rectangle2D bounds = m_vis.getBounds(tree);

                if (bounds.getWidth() == 0) {
                    return;
                }
                GraphicsLib.expand(bounds, 10 + (int) (1 / m_vis.getDisplay(0).getScale()));
                DisplayLib.fitViewToBounds(m_vis.getDisplay(0), bounds, (long) 1250);
            }
        };
        visualization.putAction("resize", resizeAction);

        // create the filtering and layout
        ActionList filter = new ActionList();
        filter.add(fisheyeTreeFilter);
        filter.add(new TreeRootAction(tree));
        filter.add(treeLayout);
        filter.add(new StarburstLayout.LabelLayout(labels));
        filter.add(fonts);
        filter.add(subLayout);
        filter.add(textColor);
        filter.add(nodeColor);
        filter.add(nodeStrokeColor);
        visualization.putAction("filter", filter);

        // animated transition
        final ActionList animate = new ActionList(1250);
        animate.setPacingFunction(new SlowInSlowOutPacer());
        animate.add(new QualityControlAnimator());
        animate.add(new VisibilityAnimator(tree));
        animate.add(new ColorAnimator(treeNodes));
        animate.add(new VisibilityAnimator(labels));
        animate.add(new ColorAnimator(labels));
        animate.add(new RepaintAction());
        visualization.putAction("animate", animate);
        visualization.alwaysRunAfter("filter", "animate");
        visualization.alwaysRunAfter("animate", "resize");

        // repaint
        ActionList repaint = new ActionList() {
            @Override
            public void run(double frac) {
                // only repaint if animation is not already running; otherwise we get flicker if repaint
                // is called from HoverActionControl while a visibility animation is running
                if (!animate.isRunning()) {
                    super.run(frac);
                }
            }
        };
        repaint.add(recolor);
        repaint.add(new RepaintAction());
        visualization.putAction("repaint", repaint);

        // ------------------------------------------------
        // initialize the display
        display.setSize(600, 600);
        display.setItemSorter(new TreeDepthItemSorter());
        display.addControlListener(new ZoomToFitControl());
        display.addControlListener(new ZoomControl());
        display.addControlListener(new PanControl(false));
        display.addControlListener(new FocusControl(2, "filter"));
        display.addControlListener(new FocusControl(Visualization.SELECTED_ITEMS, 1, "filter"));
        display.addControlListener(new HoverActionControl("repaint"));
        display.addControlListener(new MouseWheelControl("filter", "angleFactor"));

        // ------------------------------------------------
        // filter graph and perform layout
        visualization.run("filter");

        // maintain a set of items that should be interpolated linearly
        // this isn't absolutely necessary, but makes the animations nicer
        // the PolarLocationAnimator should read this set and act accordingly
        selected.addTupleSetListener(new TupleSetListener() {
            @Override
            public void tupleSetChanged(TupleSet t, Tuple[] add, Tuple[] rem) {
                visualization.cancel("animate");
                visualization.run("filter");
            }
        });

        search.addTupleSetListener(new TupleSetListener() {
            @Override
            public void tupleSetChanged(TupleSet t, Tuple[] add, Tuple[] rem) {
                visualization.cancel("animate");
                visualization.run("filter");
            }
        });
    }

    private static void addWithChildren(prefuse.data.Tree pTree, prefuse.data.Node parent, NodeI<Long> node) {
        prefuse.data.Node self = pTree.addChild(parent);
        self.set(nodeLabel, node.getAttribute().getValue());
        self.set(nodeContent, node.getContent().floatValue());
        //self.set(nodeTotalElements, calculateNodeCount(node.getContent()));
        //self.set(sameRankCount, rankCounts);

        if (node.hasChildren()) {
            for (NodeI<Long> child : node.getChildren()) {
                addWithChildren(pTree, self, child);
            }
        }
    }

    private void initDisplay() {
        visualization = new Visualization();
        display = new Display(visualization);
        display.setBackground(Color.WHITE);
        display.setForeground(Color.BLACK);
        display.setHighQuality(true);

        title = new JFastLabel(getTitle());
        title.setPreferredSize(new Dimension(200, 30));
        title.setHighQuality(true);
        title.setOpaque(false);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setFont(FontLib.getFont("Arial", Font.BOLD, 20));
        title.setBackground(new Color(0, 0, 0, 0));
        title.setForeground(Color.BLACK);

        display.setLayout(new BorderLayout());
        display.add(title, BorderLayout.NORTH);
    }

    private DefaultRendererFactory createRenderers() {
        DefaultRendererFactory rf = new DefaultRendererFactory();

        //renderer to draw shapes for filled nodes
        SectorRenderer sectorRenderer = new SectorRenderer();
        sectorRenderer.setRenderType(AbstractShapeRenderer.RENDER_TYPE_DRAW_AND_FILL);

        // for angular rotating of non-curved labels 
        DecoratorLabelRenderer decoratorLabelRenderer = new DecoratorLabelRenderer(m_label, false, 2);
        // decoratorLabelRenderer.setHorizontalAlignment(Constants.LEFT);

        // for arching of labels within node
        ArcLabelRenderer arcLabelRenderer = new ArcLabelRenderer(m_label, 2, 30);

        // set up RendererFactory
        rf.add("ingroup('labels') and rotation == 0", arcLabelRenderer); // all sector labels that are not rotated
        rf.add("ingroup('labels') and rotation != 0", decoratorLabelRenderer); // all rotated sector labels
        rf.add(new InGroupPredicate(treeEdges), null);
        rf.setDefaultRenderer(sectorRenderer); // filled sectors
        return rf;
    }

    @Override
    public void dispose() {
        if (pTree != null) {
            pTree.clear();
            pTree.dispose();
        }
        visualization = null;
        display = null;
        super.dispose();
    }

    @Override
    public Class<?> getInputType() {
        return TreeI.class;
    }

    @Override
    public RadialTreeCustomizer getCustomizer() {
        if (cust == null) {
            cust = new RadialTreeCustomizer();
        }
        return cust;
    }

    @Override
    public JComponent getComponent() {
        return display;
    }

    @Override
    public String getName() {
        return "Radial Tree";
    }

    @Override
    public boolean canHandle(AttributeTypeI valueType) {
        return super.canHandle(valueType) && VGroupManager.getInstance().getActiveGroups().size() == 1;
    }

    @Override
    public ImageExporterI getImageExporter() {
        return new ImageExporterI() {

            @Override
            public FileType[] getSupportedTypes() {
                return new FileType[]{FileType.PNG, FileType.JPEG};
            }

            @Override
            public Result export(FileType type, String fName) throws Exception {
                switch (type) {
                    case PNG:
                    case JPEG:
                        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(fName))) {
                            if (display.saveImage(os, type.getSuffices()[0].toUpperCase(), 2)) {
                                return Result.SUCCESS;
                            }
                            return Result.ERROR;
                        }
                    case SVG:
                        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(fName))) {
                            if (SVGDisplaySaver.saveSVG(display, os, 2)) {
                                return Result.SUCCESS;
                            }
                            return Result.ERROR;
                        }
                    default:
                        return Result.ERROR;
                }
            }
        };
    }

    /**
     * Switch the root of the tree by requesting a new spanning tree at the
     * desired root and hiding all nodes above
     */
    public class TreeRootAction extends GroupAction {

        public TreeRootAction(String graphGroup) {
            super(graphGroup);
        }

        @Override
        public void run(double frac) {
            TupleSet focus = m_vis.getGroup(Visualization.FOCUS_ITEMS);
            if (focus == null || focus.getTupleCount() == 0) {
                return;
            }
            prefuse.data.Tree g = (prefuse.data.Tree) m_vis.getGroup(m_group);
            prefuse.data.Node f = null;
            Iterator tuples = focus.tuples();
            while (tuples.hasNext() && !g.containsTuple(f = (prefuse.data.Node) tuples.next())) {
                f = null;
            }
            if (f == null) {
                return;
            }
            g.getSpanningTree(f);
        }
    }

    /**
     * Set node fill colors
     */
    public static class NodeColorAction extends ColorAction {

        public NodeColorAction(String group) {
            super(group, VisualItem.FILLCOLOR, ColorLib.rgb(61, 130, 246));
            add("_hover and ingroup('" + Visualization.SELECTED_ITEMS + "')", ColorLib.brighter(ColorLib.rgb(0, 190, 204)));
            // search results
            add("_hover and ingroup('_search_')", ColorLib.brighter(ColorLib.rgb(152, 255, 92)));
            add("_hover", ColorLib.brighter(ColorLib.rgb(61, 130, 246)));
            // selected subtrees
            add("ingroup('" + Visualization.SELECTED_ITEMS + "')", ColorLib.rgb(0, 190, 204));
            // search results
            add("ingroup('_search_')", ColorLib.rgb(152, 255, 92));
            // the root
            //add("ingroup('_focus_')", ColorLib.rgb(198, 229, 229));
        }
    } // end of inner class NodeColorAction

    /**
     * Set node text colors
     */
    public static class TextColorAction extends ColorAction {

        public TextColorAction(String group) {
            super(group, VisualItem.TEXTCOLOR, ColorLib.gray(20));
            add("_hover", ColorLib.rgb(255, 0, 0));
        }
    } // end of inner class TextColorAction
}
