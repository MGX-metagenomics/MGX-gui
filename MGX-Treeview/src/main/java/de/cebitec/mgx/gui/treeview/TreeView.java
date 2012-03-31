//package de.cebitec.mgx.gui.treeview;
//
//import de.cebitec.mgx.gui.attributevisualization.data.VisualizationGroup;
//import de.cebitec.mgx.gui.attributevisualization.viewer.HierarchicalViewerI;
//import de.cebitec.mgx.gui.attributevisualization.viewer.ViewerI;
//import de.cebitec.mgx.gui.datamodel.Attribute;
//import de.cebitec.mgx.gui.datamodel.Pair;
//import de.cebitec.mgx.gui.datamodel.tree.Node;
//import de.cebitec.mgx.gui.datamodel.tree.Tree;
//import de.cebitec.mgx.gui.datamodel.tree.TreeFactory;
//import de.cebitec.mgx.gui.treeview.renderer.EvolutionRendererAbsolute;
//import de.cebitec.mgx.gui.treeview.renderer.RendererManager;
//import java.awt.BorderLayout;
//import java.awt.Color;
//import java.awt.Dimension;
//import java.awt.Font;
//import java.awt.geom.Point2D;
//import java.util.List;
//import java.util.Map;
//import javax.swing.JComponent;
//import javax.swing.SwingConstants;
//import org.openide.util.lookup.ServiceProvider;
//import prefuse.Constants;
//import prefuse.Display;
//import prefuse.Visualization;
//import prefuse.action.Action;
//import prefuse.controls.DragControl;
//import prefuse.controls.PanControl;
//import prefuse.controls.ZoomControl;
//import prefuse.data.tuple.TupleSet;
//import prefuse.render.AbstractShapeRenderer;
//import prefuse.render.DefaultRendererFactory;
//import prefuse.render.EdgeRenderer;
//import prefuse.render.LabelRenderer;
//import prefuse.util.FontLib;
//import prefuse.util.ui.JFastLabel;
//import prefuse.visual.VisualItem;
//
///**
// *
// * @author sjaenick
// */
//@ServiceProvider(service = ViewerI.class)
//public class TreeView extends HierarchicalViewerI {
//
//    private final static String TREE = "tree";
//    private final static String NAME = "name"; //label of a node - Attribute.class
//    public final static String DATA = "data";
//    private Display display;
//    private EdgeRenderer edgeRenderer;
//    //
////    private FitToDisplay fitToDisplayControl;
////    private FisheyeTreeFilter ftf;
////    private FontAction fontAction;
////    private AutoPanAction autoPan;
////    private MyFocusControl focusControl;
////    private NodeLinkTreeLayout treeLayout;
//    private boolean autoResize = false;
//    private int m_orientation = Constants.ORIENT_LEFT_RIGHT;
////    public static final String treeNodes = "tree.nodes";
////    public static final String treeEdges = "tree.edges";
////    public static final String label = "name";
//
//    public TreeView() {
//        display = new Display(new Visualization());
//        display.setBackground(Color.WHITE);
//        display.setForeground(Color.BLACK);
//        display.setHighQuality(true);
//    }
//
//    @Override
//    public void setTitle(String t) {
//        super.setTitle(t);
//        final JFastLabel title = new JFastLabel(t);
//        title.setPreferredSize(new Dimension(200, 30));
//        title.setHighQuality(true);
//        title.setOpaque(false);
//        title.setHorizontalAlignment(SwingConstants.CENTER);
//        title.setFont(FontLib.getFont("Arial", Font.BOLD, 20));
//        title.setBackground(new Color(0, 0, 0, 0));
//        title.setForeground(Color.BLACK);
//
//        display.setLayout(new BorderLayout());
//        display.add(title, BorderLayout.NORTH);
//    }
//
//    @Override
//    public JComponent getComponent() {
//        return display;
//    }
//
//    @Override
//    public String getName() {
//        return "Tree View";
//    }
//
//    @Override
//    public List<Pair<VisualizationGroup, Tree<Long>>> filter(List<Pair<VisualizationGroup, Tree<Long>>> trees) {
//        assert trees != null;
//        // merge hierarchies into consensus tree
//        Tree<Map<VisualizationGroup, Long>> combinedTree = TreeFactory.combineTrees(trees);
//        Node<Map<VisualizationGroup, Long>> root = combinedTree.getRoot();
//
//        prefuse.data.Tree tree = new prefuse.data.Tree();
//        tree.getNodeTable().addColumn(NAME, Attribute.class);
//        tree.getNodeTable().addColumn(DATA, Map.class);
//
//
//        prefuse.data.Node rootNode = tree.addRoot();
//        rootNode.set(NAME, root.getAttribute());
//        rootNode.set(DATA, root.getContent());
//
//        for (Node<Map<VisualizationGroup, Long>> child : root.getChildren()) {
//            addWithChildren(tree, rootNode, child);
//        }
//
//        display.getVisualization().add(TREE, tree);
//        //display.getVisualization().setInteractive(treeEdges, null, false);
//
//        display.addControlListener(new DragControl());
//        display.addControlListener(new PanControl());
//        display.addControlListener(new ZoomControl());
//
//
//        EvolutionRendererAbsolute evoAbsRenderer = new EvolutionRendererAbsolute(Constants.EDGE_TYPE_CURVE, (Map<VisualizationGroup, Long>)tree.getRoot().get(DATA) );
//        RendererManager.setEvoAbsRenderer(evoAbsRenderer);
//        edgeRenderer = evoAbsRenderer;
//
//        LabelRenderer nodeRenderer = new LabelRenderer(NAME);
//        nodeRenderer.setVerticalPadding(2);
//        nodeRenderer.setHorizontalPadding(2);
//        nodeRenderer.setHorizontalAlignment(Constants.CENTER);
//        nodeRenderer.setVerticalAlignment(Constants.CENTER);
//        nodeRenderer.setRenderType(AbstractShapeRenderer.RENDER_TYPE_FILL);
//        nodeRenderer.setRoundedCorner(8, 8);
//        nodeRenderer.setVerticalPadding(4);
//        nodeRenderer.setHorizontalPadding(4);
//
//        DefaultRendererFactory rendererFactory = new DefaultRendererFactory(nodeRenderer);
//
//        rendererFactory.setDefaultEdgeRenderer(edgeRenderer);
//        display.getVisualization().setRendererFactory(rendererFactory);
//
////        // colors
////        ItemAction nodeColor = new NodeColorAction(treeNodes);
////        ItemAction textColor = new ColorAction(treeNodes,
////                VisualItem.TEXTCOLOR, ColorLib.rgb(0, 0, 0));
////        display.getVisualization().putAction("textColor", textColor);
////        display.getVisualization().putAction("nodeColor", nodeColor);
////
////        ItemAction edgeColor = new ColorAction(treeEdges,
////                VisualItem.STROKECOLOR, ColorLib.rgb(200, 200, 200));
////        display.getVisualization().putAction("edgeColor", edgeColor);
////
////        // quick repaint
////        ActionList repaint = new ActionList();
////        repaint.add(nodeColor);
////        repaint.add(new RepaintAction());
////        display.getVisualization().putAction("repaint", repaint);
////
////        // full paint
////        ActionList fullPaint = new ActionList();
////        fullPaint.add(nodeColor);
////        display.getVisualization().putAction("fullPaint", fullPaint);
////
////        // animate paint change
////        ActionList animatePaint = new ActionList(400);
////        animatePaint.add(new ColorAnimator(treeNodes));
////        animatePaint.add(new RepaintAction());
////        display.getVisualization().putAction("animatePaint", animatePaint);
//
//        // create the tree layout action
//
////        //ForceDirectedLayout treeLayout = new ForceDirectedLayout(tree);
////        //BalloonTreeLayout treeLayout = new BalloonTreeLayout();
////        //FruchtermanReingoldLayout treeLayout = new FruchtermanReingoldLayout(tree);
////        treeLayout = new NodeLinkTreeLayout(treeString, m_orientation, 300, 30, 8);
////        treeLayout.setLayoutAnchor(new Point2D.Double(25, 300));
////
////        display.getVisualization().putAction("treeLayout", treeLayout);
////
////        CollapsedSubtreeLayout subLayout = new CollapsedSubtreeLayout(treeString, m_orientation);
////        display.getVisualization().putAction("subLayout", subLayout);
////
////        autoPan = new AutoPanAction();
////
////        // create the filtering and layout
////        // this is the part where the auto expand and collapse stuff happens 
////        ActionList filter = new ActionList();
////
//////        TupleSet selected = display.getVisualization().getFocusGroup(Visualization.FOCUS_ITEMS);
////        SearchTupleSet search = new PrefixSearchTupleSet();
////        display.getVisualization().addFocusGroup(Visualization.SEARCH_ITEMS, search);
////
////        // filter the tree to 2 levels from selected items and search results
////        CompositeTupleSet searchAndSelect = new CompositeTupleSet();
////        searchAndSelect.addSet(Visualization.FOCUS_ITEMS, display.getVisualization().getFocusGroup(Visualization.FOCUS_ITEMS));
////        searchAndSelect.addSet(Visualization.SEARCH_ITEMS, display.getVisualization().getFocusGroup(Visualization.SEARCH_ITEMS));
////        display.getVisualization().addFocusGroup("searchAndSelect", searchAndSelect);
////        ftf = new FisheyeTreeFilter(treeString, "searchAndSelect", 1);
////        filter.add(ftf);
////
////        fontAction = new FontAction(treeNodes, FontLib.getFont("Tahoma", 16));
////
////        filter.add(fontAction);
////        filter.add(treeLayout);
////        filter.add(subLayout);
////        filter.add(textColor);
////        filter.add(nodeColor);
////        filter.add(edgeColor);
////        display.getVisualization().putAction("filter", filter);
////
////        //LUPEN FUNCTION !!!!!
////        ActionList distort = new ActionList();
////        BifocalDistortion lens = new BifocalDistortion(0.05, 2.5);
////        lens.setGroup(treeNodes);
////        distort.add(lens);
////        distort.add(new RepaintAction());
////        display.getVisualization().putAction("distort", distort);
////        display.addControlListener(new AnchorUpdateControl(lens, "distort"));
////
////        // animated transition
////        ActionList animate = new ActionList(1000);
////        animate.setPacingFunction(new SlowInSlowOutPacer());
////
////        animate.add(autoPan);
////
////        // here the antialias stuff during animation can be set 
////        animate.add(new QualityControlAnimator());
////        animate.add(new VisibilityAnimator(treeString));
////        animate.add(new LocationAnimator(treeNodes));
////        animate.add(new ColorAnimator(treeNodes));
////        animate.add(new RepaintAction());
////        display.getVisualization().putAction("animate", animate);
////        display.getVisualization().alwaysRunAfter("filter", "animate");
////
////        ResizeAction resize = new ResizeAction();
////        display.getVisualization().putAction("resize", resize);
////        display.getVisualization().alwaysRunAfter("animate", "resize");
////
////        // create animator for orientation changes
////        ActionList orient = new ActionList(2000);
////        orient.setPacingFunction(new SlowInSlowOutPacer());
////        orient.add(autoPan);
////        orient.add(new QualityControlAnimator());
////        orient.add(new LocationAnimator(treeNodes));
////        orient.add(new RepaintAction());
////        display.getVisualization().putAction("orient", orient);
////
////        // initialize the display
////        display.setItemSorter(new TreeDepthItemSorter());
////
////        fitToDisplayControl = new FitToDisplay();
////        fitToDisplayControl.setZoomOverItem(false);
////
////        focusControl = new MyFocusControl(1, "filter");
////
////        display.addControlListener(fitToDisplayControl);
////        display.addControlListener(new WheelZoomControl());
////        display.addControlListener(new PanControl());
////        display.addControlListener(focusControl);
////        display.getVisualization().run("filter");
//
////        search.addTupleSetListener(new TupleSetListener() {
////
////            @Override
////            public void tupleSetChanged(TupleSet t, Tuple[] add, Tuple[] rem) {
////                display.getVisualization().cancel("animatePaint");
////                display.getVisualization().run("filter");
////                display.getVisualization().run("subLayout");
////            }
////        });
////        display.addControlListener(new ControlAdapter() {
////
////            @Override
////            public void itemEntered(VisualItem item, MouseEvent e) {
////                if (item.canGetString(label)) {
////                    String text = item.getString(label);
////                    text += " " + NumberFormat.getInstance(Locale.US).format(Integer.parseInt(item.getString("value")));
////                    String valueInherit = (item.getString("valueInherit") == null) ? " (0)" : " (" + NumberFormat.getInstance(Locale.US).format(Integer.parseInt(item.getString("valueInherit"))) + ")";
////                    text += valueInherit;
////                    title.setText(text);
////                }
////            }
////
////            @Override
////            public void itemExited(VisualItem item, MouseEvent e) {
////                title.setText(null);
////            }
////        });
//
//        return null;
//    }
//
//    private void addWithChildren(prefuse.data.Tree tree, prefuse.data.Node parent, Node<Map<VisualizationGroup, Long>> node) {
//        prefuse.data.Node self = tree.addChild(parent);
//        self.set(NAME, node.getAttribute());
//        self.set(DATA, node.getContent());
//        for (Node<Map<VisualizationGroup, Long>> child : node.getChildren()) {
//            addWithChildren(tree, self, child);
//        }
//    }
//
//    @Override
//    public Class getInputType() {
//        return Tree.class;
//    }
//
////    private void expandTree(boolean status) {
////        ftf.setDistance((status) ? 1000 : 1);
////        display.getVisualization().run("filter");
//////        isExpanded = status;
////    }
////
////    private void setEdgeRenderer(EdgeRenderer edgeRenderer) {
////        this.edgeRenderer = edgeRenderer;
////        DefaultRendererFactory rendererFactory = new DefaultRendererFactory();
////        rendererFactory.setDefaultEdgeRenderer(edgeRenderer);
////        ((DefaultRendererFactory) display.getVisualization().getRendererFactory()).setDefaultEdgeRenderer(edgeRenderer);
////        display.getVisualization().cancel("animate");
////        display.getVisualization().repaint();
////        display.getVisualization().run("animate");
////    }
////
////    protected void changeConfiguration(TreeConfiguration config) {
////        if (!config.isCollapseTree()) {
////            ftf.setDistance(1000);
////        } else {
////            ftf.setDistance(1);
////        }
//////        isExpanded = !config.isCollapseTree();
////        expandTree(!config.isCollapseTree());
////        autoResize = config.isAutoZoom();
////        autoPan.setEnabled(config.isAutoFocus());
////        setEdgeRenderer(RendererManager.getRenderer(config.getEdgeRenderer()));
////
////        treeLayout.setDepthSpacing(config.getHorizontalDistance());
////        treeLayout.setBreadthSpacing(config.getLeafDistance());
////        treeLayout.setSubtreeSpacing(config.getBranchDistance());
////        ((EdgeRendererInterface) ((DefaultRendererFactory) display.getVisualization().getRendererFactory()).getDefaultEdgeRenderer()).setLineWidth(config.getEdgeWidth());
////
////        fontAction.setDefaultFont(config.getLabelFont());
////
////        display.setBackground(config.getBackgroundColor());
////        ((ColorAction) display.getVisualization().getAction("textColor")).setDefaultColor(ColorLib.color(config.getFontColor()));
////        display.getVisualization().run("textColor");
////        ((ColorAction) display.getVisualization().getAction("edgeColor")).setDefaultColor(ColorLib.color(config.getEdgesColor()));
////        display.getVisualization().run("edgeColor");
////        ((NodeColorAction) display.getVisualization().getAction("nodeColor")).setFocus(config.getNodeFocusColor());
////        ((NodeColorAction) display.getVisualization().getAction("nodeColor")).setNormal(config.getNodeColor());
////        ((NodeColorAction) display.getVisualization().getAction("nodeColor")).setSearch(config.getNodeSearchColor());
////        if (edgeRenderer instanceof CircleRenderer) {
////            ((CircleRenderer) edgeRenderer).setCircleColor(config.getCircleColor());
////        }
////
////        display.getVisualization().run("nodeColor");
////
////        display.getVisualization().cancel("animate");
////        display.getVisualization().run("animate");
////
////        display.getVisualization().run("treeLayout");
////        display.getVisualization().run("repaint");
////
////        display.getVisualization().run("filter");
////    }
////
////    private prefuse.data.Tree postProcessTree(prefuse.data.Tree tree) {
////        prefuse.data.Node root = tree.getRoot();
////        root.set("value", "0");
////        calculateNodeValue(root);
////        return tree;
////    }
////
////    private prefuse.data.Node calculateNodeValue(prefuse.data.Node node) {
////        int nodeNb = node.getChildCount();
////        int value = node.get("value") == null ? 0 : Integer.parseInt((String) node.get("value"));
////        int index = 0;
////
////        while (index < nodeNb) {
////            prefuse.data.Node child = node.getChild(index);
////            child = calculateNodeValue(child);
////            value += child.get("value") == null ? 0 : Integer.parseInt((String) child.get("value"));
////            index++;
////        }
////
////        node.set("valueInherit", node.get("value"));
////        node.set("value", Integer.toString(value));
////        return node;
////    }
//
////    public class ResizeAction extends Action {
////
////        @Override
////        public void run(double frac) {
////            if (autoResize) {
////                TreeView.this.fitToDisplayControl.zoomToFit(TreeView.this.display);
////            }
////        }
////    }
//
//    public class AutoPanAction extends Action {
//
//        private Point2D m_start = new Point2D.Double();
//        private Point2D m_end = new Point2D.Double();
//        private Point2D m_cur = new Point2D.Double();
//        private int m_bias = 150;
//
//        @Override
//        public void run(double frac) {
//            TupleSet ts = m_vis.getFocusGroup(Visualization.FOCUS_ITEMS);
//            if (ts.getTupleCount() == 0) {
//                return;
//            }
//            if (frac == 0.0) {
//                int xbias = 0, ybias = 0;
//                switch (m_orientation) {
//                    case Constants.ORIENT_LEFT_RIGHT:
//                        xbias = m_bias;
//                        break;
//                    case Constants.ORIENT_RIGHT_LEFT:
//                        xbias = -m_bias;
//                        break;
//                    case Constants.ORIENT_TOP_BOTTOM:
//                        ybias = m_bias;
//                        break;
//                    case Constants.ORIENT_BOTTOM_TOP:
//                        ybias = -m_bias;
//                        break;
//                }
//
//                VisualItem vi = (VisualItem) ts.tuples().next();
//                m_cur.setLocation(display.getWidth() / 2, display.getHeight() / 2);
//                display.getAbsoluteCoordinate(m_cur, m_start);
//                m_end.setLocation(vi.getX() + xbias, vi.getY() + ybias);
//            } else {
//                m_cur.setLocation(m_start.getX() + frac * (m_end.getX() - m_start.getX()),
//                        m_start.getY() + frac * (m_end.getY() - m_start.getY()));
//                display.panToAbs(m_cur);
//            }
//        }
//    }
//}
