//package de.cebitec.mgx.gui.treeview;
//
//import de.cebitec.mgx.gui.treeview.TreeView.AutoPanAction;
//import de.cebitec.mgx.gui.treeview.TreeView.ResizeAction;
//import de.cebitec.mgx.gui.treeview.actions.FitToDisplay;
//import de.cebitec.mgx.gui.treeview.actions.MyFocusControl;
//import de.cebitec.mgx.gui.treeview.actions.NodeColorAction;
//import de.cebitec.mgx.gui.treeview.renderer.*;
//import java.awt.BorderLayout;
//import java.awt.Color;
//import java.awt.Dimension;
//import java.awt.Font;
//import java.awt.event.MouseEvent;
//import java.awt.geom.Point2D;
//import java.text.NumberFormat;
//import java.util.Locale;
//import javax.swing.SwingConstants;
//import prefuse.Constants;
//import prefuse.Display;
//import prefuse.Visualization;
//import prefuse.action.Action;
//import prefuse.action.ActionList;
//import prefuse.action.ItemAction;
//import prefuse.action.RepaintAction;
//import prefuse.action.animate.ColorAnimator;
//import prefuse.action.animate.LocationAnimator;
//import prefuse.action.animate.QualityControlAnimator;
//import prefuse.action.animate.VisibilityAnimator;
//import prefuse.action.assignment.ColorAction;
//import prefuse.action.assignment.FontAction;
//import prefuse.action.distortion.BifocalDistortion;
//import prefuse.action.filter.FisheyeTreeFilter;
//import prefuse.action.layout.CollapsedSubtreeLayout;
//import prefuse.action.layout.graph.NodeLinkTreeLayout;
//import prefuse.activity.SlowInSlowOutPacer;
//import prefuse.controls.*;
//import prefuse.data.Node;
//import prefuse.data.Tree;
//import prefuse.data.Tuple;
//import prefuse.data.event.TupleSetListener;
//import prefuse.data.search.PrefixSearchTupleSet;
//import prefuse.data.search.SearchTupleSet;
//import prefuse.data.tuple.CompositeTupleSet;
//import prefuse.data.tuple.TupleSet;
//import prefuse.render.AbstractShapeRenderer;
//import prefuse.render.DefaultRendererFactory;
//import prefuse.render.EdgeRenderer;
//import prefuse.render.LabelRenderer;
//import prefuse.util.ColorLib;
//import prefuse.util.FontLib;
//import prefuse.util.ui.JFastLabel;
//import prefuse.visual.VisualItem;
//import prefuse.visual.sort.TreeDepthItemSorter;
//
///**
// * class for creating a tree visualization for a prefuse tree object
// * @author rbisdorf
// */
//public class TreeVisualisation {
//    public static final String tree = "tree";
//    public static final String treeNodes = "tree.nodes";
//    public static final String treeEdges = "tree.edges";
//    public static final String label = "name";
//    
//    private boolean autoResize = false;
//    private int m_orientation = Constants.ORIENT_LEFT_RIGHT;
////    private boolean isExpanded = false;
//    
//    private Display display;
//    private FitToDisplay fitToDisplayControl;
//    private FisheyeTreeFilter ftf;
//    private EdgeRenderer edgeRenderer;
//    private FontAction fontAction;
//    private AutoPanAction autoPan;
//    private MyFocusControl focusControl;
//    private NodeLinkTreeLayout treeLayout;
//
//    public TreeVisualisation(Tree tree) {
//        loadTree(tree);
//        expandTree(true);
//    }
//
//    private void loadTree(Tree treeObj) {
//        display = new Display(new Visualization());
//        display.setSize(800, 700);
//        display.setBackground(Color.WHITE);
//        display.setForeground(Color.BLACK);
//        display.setHighQuality(true);
//        
//        initializeDisplay(treeObj);
//
//        final JFastLabel title = new JFastLabel(" ");
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
//
//        display.addControlListener(new ControlAdapter() {
//            @Override
//            public void itemEntered(VisualItem item, MouseEvent e) {
//                if (item.canGetString(label)) {
//                    String text = item.getString(label);
//                    text += " " + NumberFormat.getInstance(Locale.US).format(Integer.parseInt(item.getString("value")));
//                    String valueInherit = (item.getString("valueInherit")==null) ? " (0)" : " (" + NumberFormat.getInstance(Locale.US).format(Integer.parseInt(item.getString("valueInherit"))) + ")";
//                    text += valueInherit;
//                    title.setText(text);
//                }
//            }
//
//            @Override
//            public void itemExited(VisualItem item, MouseEvent e) {
//                title.setText(null);
//            }
//        });
//    }
//
//    private void initializeDisplay(Tree treeObj) {
//        postProcessTree(treeObj);
//
//        display.getVisualization().add(tree, treeObj);
//        display.getVisualization().setInteractive(treeEdges, null, false);
//        
//        display.addControlListener(new DragControl());
//        /* allow items to be dragged around */
//
//        display.addControlListener(new PanControl());
//        /* allow the display to be panned (moved left/right, up/down) (left-drag)*/
//
//        display.addControlListener(new ZoomControl());
//        
//        EvolutionRendererAbsolute evoAbsRenderer = new EvolutionRendererAbsolute(Constants.EDGE_TYPE_CURVE, Integer.parseInt((String) (treeObj.getRoot()).get("value")));
//        RendererManager.setEvoAbsRenderer(evoAbsRenderer);
//        edgeRenderer = evoAbsRenderer;
//
//        LabelRenderer nodeRenderer = new LabelRenderer(label);
//        nodeRenderer.setVerticalPadding(2);
//        nodeRenderer.setHorizontalPadding(2);
//
//        nodeRenderer.setHorizontalAlignment(Constants.CENTER);
//        nodeRenderer.setVerticalAlignment(Constants.CENTER);
//
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
//        // colors
//        ItemAction nodeColor = new NodeColorAction(treeNodes);
//        ItemAction textColor = new ColorAction(treeNodes,
//                VisualItem.TEXTCOLOR, ColorLib.rgb(0, 0, 0));
//        display.getVisualization().putAction("textColor", textColor);
//        display.getVisualization().putAction("nodeColor", nodeColor);
//
//        ItemAction edgeColor = new ColorAction(treeEdges,
//                VisualItem.STROKECOLOR, ColorLib.rgb(200, 200, 200));
//        display.getVisualization().putAction("edgeColor", edgeColor);
//        
//        // quick repaint
//        ActionList repaint = new ActionList();
//        repaint.add(nodeColor);
//        repaint.add(new RepaintAction());
//        display.getVisualization().putAction("repaint", repaint);
//
//        // full paint
//        ActionList fullPaint = new ActionList();
//        fullPaint.add(nodeColor);
//        display.getVisualization().putAction("fullPaint", fullPaint);
//
//        // animate paint change
//        ActionList animatePaint = new ActionList(400);
//        animatePaint.add(new ColorAnimator(treeNodes));
//        animatePaint.add(new RepaintAction());
//        display.getVisualization().putAction("animatePaint", animatePaint);
//
//        // create the tree layout action
//
//        //ForceDirectedLayout treeLayout = new ForceDirectedLayout(tree);
//        //BalloonTreeLayout treeLayout = new BalloonTreeLayout();
//        //FruchtermanReingoldLayout treeLayout = new FruchtermanReingoldLayout(tree);
//        treeLayout = new NodeLinkTreeLayout(tree, m_orientation, 300, 30, 8);
//        treeLayout.setLayoutAnchor(new Point2D.Double(25, 300));
//
//        display.getVisualization().putAction("treeLayout", treeLayout);
//
//        CollapsedSubtreeLayout subLayout = new CollapsedSubtreeLayout(tree, m_orientation);
//        display.getVisualization().putAction("subLayout", subLayout);
//
//        autoPan = new AutoPanAction();
//
//        // create the filtering and layout
//        // this is the part where the auto expand and collapse stuff happens 
//        ActionList filter = new ActionList();
//
////        TupleSet selected = display.getVisualization().getFocusGroup(Visualization.FOCUS_ITEMS);
//        SearchTupleSet search = new PrefixSearchTupleSet();
//        display.getVisualization().addFocusGroup(Visualization.SEARCH_ITEMS, search);
//
//        // filter the tree to 2 levels from selected items and search results
//        CompositeTupleSet searchAndSelect = new CompositeTupleSet();
//        searchAndSelect.addSet(Visualization.FOCUS_ITEMS, display.getVisualization().getFocusGroup(Visualization.FOCUS_ITEMS));
//        searchAndSelect.addSet(Visualization.SEARCH_ITEMS, display.getVisualization().getFocusGroup(Visualization.SEARCH_ITEMS));
//        display.getVisualization().addFocusGroup("searchAndSelect", searchAndSelect);
//        ftf = new FisheyeTreeFilter(tree, "searchAndSelect", 1);
//        filter.add(ftf);
//        
//        fontAction = new FontAction(treeNodes, FontLib.getFont("Tahoma", 16));
//
//        filter.add(fontAction);
//        filter.add(treeLayout);
//        filter.add(subLayout);
//        filter.add(textColor);
//        filter.add(nodeColor);
//        filter.add(edgeColor);
//        display.getVisualization().putAction("filter", filter);
//
//        //LUPEN FUNCTION !!!!!
//        ActionList distort = new ActionList();
//        BifocalDistortion lens = new BifocalDistortion(0.05, 2.5);
//        lens.setGroup(treeNodes);
//        distort.add(lens);
//        distort.add(new RepaintAction());
//        display.getVisualization().putAction("distort", distort);
//        display.addControlListener(new AnchorUpdateControl(lens, "distort"));
//
//        // animated transition
//        ActionList animate = new ActionList(1000);
//        animate.setPacingFunction(new SlowInSlowOutPacer());
//
//        animate.add(autoPan);
//
//        // here the antialias stuff during animation can be set 
//        animate.add(new QualityControlAnimator());
//        animate.add(new VisibilityAnimator(tree));
//        animate.add(new LocationAnimator(treeNodes));
//        animate.add(new ColorAnimator(treeNodes));
//        animate.add(new RepaintAction());
//        display.getVisualization().putAction("animate", animate);
//        display.getVisualization().alwaysRunAfter("filter", "animate");
//
//        ResizeAction resize = new ResizeAction();
//        display.getVisualization().putAction("resize", resize);
//        display.getVisualization().alwaysRunAfter("animate", "resize");
//
//        // create animator for orientation changes
//        ActionList orient = new ActionList(2000);
//        orient.setPacingFunction(new SlowInSlowOutPacer());
//        orient.add(autoPan);
//        orient.add(new QualityControlAnimator());
//        orient.add(new LocationAnimator(treeNodes));
//        orient.add(new RepaintAction());
//        display.getVisualization().putAction("orient", orient);
//
//        // initialize the display
//        display.setItemSorter(new TreeDepthItemSorter());
//
//        fitToDisplayControl = new FitToDisplay();
//        fitToDisplayControl.setZoomOverItem(false);
//
//        focusControl = new MyFocusControl(1, "filter");
//
//        display.addControlListener(fitToDisplayControl);
//        display.addControlListener(new WheelZoomControl());
//        display.addControlListener(new PanControl());
//        display.addControlListener(focusControl);
//        display.getVisualization().run("filter");
//
//        search.addTupleSetListener(new TupleSetListener() {
//            @Override
//            public void tupleSetChanged(TupleSet t, Tuple[] add, Tuple[] rem) {
//                display.getVisualization().cancel("animatePaint");
//                display.getVisualization().run("filter");
//                display.getVisualization().run("subLayout");
//            }
//        });
//    }
//    
//
//
//    
//}