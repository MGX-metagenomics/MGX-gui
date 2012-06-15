package de.cebitec.mgx.gui.treeview;

import de.cebitec.mgx.gui.attributevisualization.viewer.HierarchicalViewerI;
import de.cebitec.mgx.gui.attributevisualization.viewer.ViewerI;
import de.cebitec.mgx.gui.datamodel.Attribute;
import de.cebitec.mgx.gui.datamodel.AttributeType;
import de.cebitec.mgx.gui.datamodel.Pair;
import de.cebitec.mgx.gui.datamodel.tree.Node;
import de.cebitec.mgx.gui.datamodel.tree.Tree;
import de.cebitec.mgx.gui.datamodel.tree.TreeFactory;
import de.cebitec.mgx.gui.groups.VisualizationGroup;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import org.openide.util.lookup.ServiceProvider;
import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.Action;
import prefuse.action.ActionList;
import prefuse.action.ItemAction;
import prefuse.action.RepaintAction;
import prefuse.action.animate.ColorAnimator;
import prefuse.action.animate.LocationAnimator;
import prefuse.action.animate.QualityControlAnimator;
import prefuse.action.animate.VisibilityAnimator;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.FontAction;
import prefuse.action.filter.FisheyeTreeFilter;
import prefuse.action.layout.CollapsedSubtreeLayout;
import prefuse.action.layout.graph.NodeLinkTreeLayout;
import prefuse.activity.SlowInSlowOutPacer;
import prefuse.controls.FocusControl;
import prefuse.controls.PanControl;
import prefuse.controls.ZoomControl;
import prefuse.controls.ZoomToFitControl;
import prefuse.data.Tuple;
import prefuse.data.event.TupleSetListener;
import prefuse.data.search.PrefixSearchTupleSet;
import prefuse.data.tuple.TupleSet;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.EdgeRenderer;
import prefuse.render.LabelRenderer;
import prefuse.render.ShapeRenderer;
import prefuse.util.ColorLib;
import prefuse.util.FontLib;
import prefuse.util.ui.JFastLabel;
import prefuse.visual.VisualItem;
import prefuse.visual.expression.InGroupPredicate;
import prefuse.visual.sort.TreeDepthItemSorter;

/**
 *
 * @author sjaenick
 */
@ServiceProvider(service = ViewerI.class)
public class TreeView extends HierarchicalViewerI {

    private static final String tree = "tree";
    private static final String treeNodes = "tree.nodes";
    private static final String treeEdges = "tree.edges";
    public static final String nodeLabel = "label";
    public static final String nodeContent = "content";
    public static final String nodeTotalElements = "totalElements"; //number of sequences assigned to this node
    public static final String sameRankCount = "rankCount"; // total number of sequences assigned at same rank

    //
    private prefuse.data.Tree pTree = null;
    private LabelRenderer m_nodeRenderer;
    private EdgeRenderer m_edgeRenderer;
    private int orientation = Constants.ORIENT_LEFT_RIGHT;
    private Display display;
    private Visualization visualization;
    private JFastLabel title;

    @Override
    public JComponent getComponent() {
        return display;
    }

    @Override
    public String getName() {
        return "Tree View";
    }

    @Override
    public void setAttributeType(AttributeType aType) {
        super.setAttributeType(aType);
        super.setTitle("Hierarchy view based on " + aType.getName());
    }

    @Override
    public void show(List<Pair<VisualizationGroup, Tree<Long>>> trees) {
        dispose();
        // merge hierarchies into consensus tree
        Tree<Map<VisualizationGroup, Long>> combinedTree = TreeFactory.combineTrees(trees);
        Node<Map<VisualizationGroup, Long>> root = combinedTree.getRoot();
        
        Map<String, Long[]> rankCounts = calculateRankCounts(combinedTree);

        initDisplay();

        pTree = new prefuse.data.Tree();
        pTree.getNodeTable().addColumn(nodeLabel, Attribute.class);
        pTree.getNodeTable().addColumn(nodeTotalElements, long.class);
        pTree.getNodeTable().addColumn(sameRankCount, Map.class);
        pTree.getNodeTable().addColumn(nodeContent, Map.class);


        prefuse.data.Node rootNode = pTree.addRoot();
        rootNode.set(nodeLabel, root.getAttribute());
        rootNode.set(nodeContent, root.getContent());
        rootNode.set(nodeTotalElements, calculateNodeCount(root.getContent()));
        rootNode.set(sameRankCount, rankCounts);

        for (Node<Map<VisualizationGroup, Long>> child : root.getChildren()) {
            addWithChildren(pTree, rootNode, child, rankCounts);
        }

        visualization.reset();
        visualization.add(tree, pTree);

        initRenderers();
    }

    @Override
    public void dispose() {
        if (pTree != null) {
            pTree.clear();
            pTree.dispose();
        }
        visualization = null;
        display = null;
        m_edgeRenderer = null;
        m_nodeRenderer = null;
        super.dispose();
    }

    private static void addWithChildren(prefuse.data.Tree pTree, prefuse.data.Node parent, Node<Map<VisualizationGroup, Long>> node, Map<String, Long[]> rankCounts) {
        prefuse.data.Node self = pTree.addChild(parent);
        self.set(nodeLabel, node.getAttribute());
        self.set(nodeContent, node.getContent());
        self.set(nodeTotalElements, calculateNodeCount(node.getContent()));
        self.set(sameRankCount, rankCounts);

        for (Node<Map<VisualizationGroup, Long>> child : node.getChildren()) {
            addWithChildren(pTree, self, child, rankCounts);
        }
    }

    @Override
    public Class getInputType() {
        return Tree.class;
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

    private void initRenderers() {
        //m_nodeRenderer = new LabelRenderer(nodeLabel);
        m_nodeRenderer = new PieNodeRenderer();
        m_nodeRenderer.setRenderType(ShapeRenderer.RENDER_TYPE_FILL);
        m_nodeRenderer.setHorizontalAlignment(Constants.LEFT);
        m_nodeRenderer.setRoundedCorner(8, 8);
        m_edgeRenderer = new EdgeRenderer(Constants.EDGE_TYPE_CURVE);

        DefaultRendererFactory rf = new DefaultRendererFactory(m_nodeRenderer);
        rf.add(new InGroupPredicate(treeEdges), m_edgeRenderer);
        visualization.setRendererFactory(rf);

        // colors
        ItemAction nodeColor = new NodeColorAction(treeNodes);
        ItemAction textColor = new ColorAction(treeNodes,
                VisualItem.TEXTCOLOR, ColorLib.rgb(0, 0, 0));
        visualization.putAction("textColor", textColor);

        ItemAction edgeColor = new ColorAction(treeEdges,
                VisualItem.STROKECOLOR, ColorLib.rgb(200, 200, 200));

        // quick repaint
        ActionList repaint = new ActionList();
        repaint.add(nodeColor);
        repaint.add(new RepaintAction());
        visualization.putAction("repaint", repaint);

        // full paint
        ActionList fullPaint = new ActionList();
        fullPaint.add(nodeColor);
        visualization.putAction("fullPaint", fullPaint);

        // animate paint change
        ActionList animatePaint = new ActionList(400);
        animatePaint.add(new ColorAnimator(treeNodes));
        animatePaint.add(new RepaintAction());
        visualization.putAction("animatePaint", animatePaint);

        // create the tree layout action
        NodeLinkTreeLayout treeLayout = new NodeLinkTreeLayout(tree,
                orientation, 50, 0, 8);
        treeLayout.setLayoutAnchor(new Point2D.Double(25, 300));
        visualization.putAction("treeLayout", treeLayout);

        CollapsedSubtreeLayout subLayout =
                new CollapsedSubtreeLayout(tree, orientation);
        visualization.putAction("subLayout", subLayout);

        AutoPanAction autoPan = new AutoPanAction();

        // create the filtering and layout
        ActionList filter = new ActionList();
        filter.add(new FisheyeTreeFilter(tree, 2));
        filter.add(new FontAction(treeNodes, FontLib.getFont("Tahoma", 16)));
        filter.add(treeLayout);
        filter.add(subLayout);
        filter.add(textColor);
        filter.add(nodeColor);
        filter.add(edgeColor);
        visualization.putAction("filter", filter);

        // animated transition
        ActionList animate = new ActionList(1000);
        animate.setPacingFunction(new SlowInSlowOutPacer());
        animate.add(autoPan);
        animate.add(new QualityControlAnimator());
        animate.add(new VisibilityAnimator(tree));
        animate.add(new LocationAnimator(treeNodes));
        animate.add(new ColorAnimator(treeNodes));
        animate.add(new RepaintAction());
        visualization.putAction("animate", animate);
        visualization.alwaysRunAfter("filter", "animate");

        // create animator for orientation changes
        ActionList orient = new ActionList(2000);
        orient.setPacingFunction(new SlowInSlowOutPacer());
        orient.add(autoPan);
        orient.add(new QualityControlAnimator());
        orient.add(new LocationAnimator(treeNodes));
        orient.add(new RepaintAction());
        visualization.putAction("orient", orient);

        // ------------------------------------------------

        // initialize the display
        display.setSize(700, 600);
        display.setItemSorter(new TreeDepthItemSorter());
        display.addControlListener(new ZoomToFitControl());
        display.addControlListener(new ZoomControl());
        display.addControlListener(new PanControl());
        display.addControlListener(new FocusControl(1, "filter"));

        display.registerKeyboardAction(
                new OrientAction(Constants.ORIENT_LEFT_RIGHT),
                "left-to-right", KeyStroke.getKeyStroke("ctrl 1"), Display.WHEN_FOCUSED);
        display.registerKeyboardAction(
                new OrientAction(Constants.ORIENT_TOP_BOTTOM),
                "top-to-bottom", KeyStroke.getKeyStroke("ctrl 2"), Display.WHEN_FOCUSED);
        display.registerKeyboardAction(
                new OrientAction(Constants.ORIENT_RIGHT_LEFT),
                "right-to-left", KeyStroke.getKeyStroke("ctrl 3"), Display.WHEN_FOCUSED);
        display.registerKeyboardAction(
                new OrientAction(Constants.ORIENT_BOTTOM_TOP),
                "bottom-to-top", KeyStroke.getKeyStroke("ctrl 4"), Display.WHEN_FOCUSED);

        // ------------------------------------------------

        // filter graph and perform layout
        setOrientation(orientation);
        visualization.run("filter");

        TupleSet search = new PrefixSearchTupleSet();
        visualization.removeGroup(Visualization.SEARCH_ITEMS);
        visualization.addFocusGroup(Visualization.SEARCH_ITEMS, search);
        search.addTupleSetListener(new TupleSetListener() {

            @Override
            public void tupleSetChanged(TupleSet t, Tuple[] add, Tuple[] rem) {
                visualization.cancel("animatePaint");
                visualization.run("fullPaint");
                visualization.run("animatePaint");
            }
        });
    }

    private static long calculateNodeCount(Map<VisualizationGroup, Long> content) {
        long total = 0;
        for (long l : content.values()) {
            total += l;
        }
        return total;
    }
    
    private static Map<String, Long[]> calculateRankCounts(Tree<Map<VisualizationGroup, Long>> tree) {
        Map<String, Long[]> ret = new HashMap<>();
        
        for (Node<Map<VisualizationGroup, Long>> node: tree.getNodes()) {
            if (!ret.containsKey(node.getAttribute().getAttributeType().getName())) {
                ret.put(node.getAttribute().getAttributeType().getName(), new Long[node.getContent().size()]);
                
                Long[] current = new Long[node.getContent().size()];
                for (int i=0; i < node.getContent().size(); i++) {
                    current[i] = Long.valueOf(0);
                }
                ret.put(node.getAttribute().getAttributeType().getName(), current);
            }
            Long[] current = ret.get(node.getAttribute().getAttributeType().getName());
            int i=0;
            for (Entry<VisualizationGroup, Long> e :node.getContent().entrySet()) {
                current[i++] += e.getValue().longValue();
            }
        }
        return ret;
    }

    public void setOrientation(int orientation) {
        NodeLinkTreeLayout rtl = (NodeLinkTreeLayout) visualization.getAction("treeLayout");
        CollapsedSubtreeLayout stl = (CollapsedSubtreeLayout) visualization.getAction("subLayout");
        switch (orientation) {
            case Constants.ORIENT_LEFT_RIGHT:
                m_nodeRenderer.setHorizontalAlignment(Constants.LEFT);
                m_edgeRenderer.setHorizontalAlignment1(Constants.RIGHT);
                m_edgeRenderer.setHorizontalAlignment2(Constants.LEFT);
                m_edgeRenderer.setVerticalAlignment1(Constants.CENTER);
                m_edgeRenderer.setVerticalAlignment2(Constants.CENTER);
                break;
            case Constants.ORIENT_RIGHT_LEFT:
                m_nodeRenderer.setHorizontalAlignment(Constants.RIGHT);
                m_edgeRenderer.setHorizontalAlignment1(Constants.LEFT);
                m_edgeRenderer.setHorizontalAlignment2(Constants.RIGHT);
                m_edgeRenderer.setVerticalAlignment1(Constants.CENTER);
                m_edgeRenderer.setVerticalAlignment2(Constants.CENTER);
                break;
            case Constants.ORIENT_TOP_BOTTOM:
                m_nodeRenderer.setHorizontalAlignment(Constants.CENTER);
                m_edgeRenderer.setHorizontalAlignment1(Constants.CENTER);
                m_edgeRenderer.setHorizontalAlignment2(Constants.CENTER);
                m_edgeRenderer.setVerticalAlignment1(Constants.BOTTOM);
                m_edgeRenderer.setVerticalAlignment2(Constants.TOP);
                break;
            case Constants.ORIENT_BOTTOM_TOP:
                m_nodeRenderer.setHorizontalAlignment(Constants.CENTER);
                m_edgeRenderer.setHorizontalAlignment1(Constants.CENTER);
                m_edgeRenderer.setHorizontalAlignment2(Constants.CENTER);
                m_edgeRenderer.setVerticalAlignment1(Constants.TOP);
                m_edgeRenderer.setVerticalAlignment2(Constants.BOTTOM);
                break;
            default:
                throw new IllegalArgumentException(
                        "Unrecognized orientation value: " + orientation);
        }
        this.orientation = orientation;
        rtl.setOrientation(orientation);
        stl.setOrientation(orientation);
    }

    public int getOrientation() {
        return orientation;
    }

    @Override
    public JComponent getCustomizer() {
        return null;
    }

    // FIXME display#saveImage
    public class OrientAction extends AbstractAction {

        private int orientation;

        public OrientAction(int orientation) {
            this.orientation = orientation;
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            setOrientation(orientation);
            display.getVisualization().cancel("orient");
            display.getVisualization().run("treeLayout");
            display.getVisualization().run("orient");
        }
    }

    public class AutoPanAction extends Action {

        private Point2D m_start = new Point2D.Double();
        private Point2D m_end = new Point2D.Double();
        private Point2D m_cur = new Point2D.Double();
        private int m_bias = 150;

        @Override
        public void run(double frac) {
            TupleSet ts = m_vis.getFocusGroup(Visualization.FOCUS_ITEMS);
            if (ts.getTupleCount() == 0) {
                return;
            }

            if (frac == 0.0) {
                int xbias = 0, ybias = 0;
                switch (orientation) {
                    case Constants.ORIENT_LEFT_RIGHT:
                        xbias = m_bias;
                        break;
                    case Constants.ORIENT_RIGHT_LEFT:
                        xbias = -m_bias;
                        break;
                    case Constants.ORIENT_TOP_BOTTOM:
                        ybias = m_bias;
                        break;
                    case Constants.ORIENT_BOTTOM_TOP:
                        ybias = -m_bias;
                        break;
                }

                VisualItem vi = (VisualItem) ts.tuples().next();
                m_cur.setLocation(display.getWidth() / 2, display.getHeight() / 2);
                display.getAbsoluteCoordinate(m_cur, m_start);
                m_end.setLocation(vi.getX() + xbias, vi.getY() + ybias);
            } else {
                m_cur.setLocation(m_start.getX() + frac * (m_end.getX() - m_start.getX()),
                        m_start.getY() + frac * (m_end.getY() - m_start.getY()));
                display.panToAbs(m_cur);
            }
        }
    }

    public static class NodeColorAction extends ColorAction {

        public NodeColorAction(String group) {
            super(group, VisualItem.FILLCOLOR);
        }

        @Override
        public int getColor(VisualItem item) {
            if (m_vis.isInGroup(item, Visualization.SEARCH_ITEMS)) {
                return ColorLib.rgb(255, 190, 190);
            } else if (m_vis.isInGroup(item, Visualization.FOCUS_ITEMS)) {
                return ColorLib.rgb(198, 229, 229);
            } else if (item.getDOI() > -1) {
                return ColorLib.rgb(164, 193, 193);
            } else {
                return ColorLib.rgba(255, 255, 255, 0);
            }
        }
    }
}
