package de.cebitec.mgx.gui.treeview;

import de.cebitec.mgx.api.groups.FileType;
import de.cebitec.mgx.api.groups.GroupI;
import de.cebitec.mgx.api.groups.ImageExporterI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.tree.NodeI;
import de.cebitec.mgx.api.model.tree.TreeI;
import de.cebitec.mgx.gui.datafactories.TreeFactory;
import de.cebitec.mgx.gui.viewer.api.CustomizableI;
import de.cebitec.mgx.gui.viewer.api.HierarchicalViewerI;
import de.cebitec.mgx.gui.viewer.api.ViewerI;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.Serial;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import org.jfree.svg.SVGGraphics2D;
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
import prefuse.controls.AbstractZoomControl;
import prefuse.controls.FocusControl;
import prefuse.controls.PanControl;
import prefuse.controls.ZoomToFitControl;
import prefuse.data.Tuple;
import prefuse.data.event.TupleSetListener;
import prefuse.data.search.PrefixSearchTupleSet;
import prefuse.data.tuple.TupleSet;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.EdgeRenderer;
import prefuse.render.LabelRenderer;
import prefuse.render.ShapeRenderer;
import prefuse.svg.SVGDisplaySaver;
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
public class TreeView extends HierarchicalViewerI implements ImageExporterI.Provider, CustomizableI {

    private static final String tree = "tree";
    private static final String treeNodes = "tree.nodes";
    private static final String treeEdges = "tree.edges";
    public static final String nodeLabel = "label";
    public static final String nodeContent = "content";
    public static final String nodeTotalElements = "totalElements"; //number of sequences assigned to this node
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
    public Class<?> getInputType() {
        return TreeI.class;
    }

    @Override
    public void setAttributeType(AttributeTypeI aType) {
        super.setAttributeType(aType);
        super.setTitle("Hierarchy view based on " + aType.getName());
    }

    private boolean showUnclassifieds = true;

    @Override
    public void show(List<Pair<GroupI, TreeI<Long>>> trees) {
        dispose();

        showUnclassifieds = getCustomizer().includeUnclassified();
        Set<AttributeI> blacklist = getCustomizer().getBlackList();

        // merge hierarchies into consensus tree
        TreeI<Map<GroupI, Long>> combinedTree = TreeFactory.combineTrees(trees);
        NodeI<Map<GroupI, Long>> root = combinedTree.getRoot();

//        // create an ordered list of groups
//        int i = 0;
//        VisualizationGroupI[] groupOrder = new VisualizationGroupI[trees.size()];
//        for (Pair<VisualizationGroupI, TreeI<Long>> pair : trees) {
//            groupOrder[i++] = pair.getFirst();
//        }
        //Map<String, long[]> rankCounts = calculateRankCounts(combinedTree, groupOrder);
        initDisplay();

        pTree = new prefuse.data.Tree();
        pTree.getNodeTable().addColumn(nodeLabel, String.class);
        pTree.getNodeTable().addColumn(nodeTotalElements, long.class); //number of total seqs assigned to this node
        pTree.getNodeTable().addColumn(nodeContent, Map.class);  // map<vgroup, long>

        if (!blacklist.contains(root.getAttribute())) {
            prefuse.data.Node rootNode = pTree.addRoot();
            long numSeqsAssigned = calculateNodeCount(root.getContent());
            rootNode.set(nodeLabel, root.getAttribute().getValue());
            rootNode.set(nodeContent, root.getContent());
            rootNode.set(nodeTotalElements, numSeqsAssigned);

            if (showUnclassifieds) {
                long numAssignedBelow = 0;
                Map<GroupI, Long> content = new HashMap<>();
                // copy content of parent
                for (Entry<GroupI, Long> e : root.getContent().entrySet()) {
                    content.put(e.getKey(), e.getValue());
                }

                for (NodeI<Map<GroupI, Long>> n : root.getChildren()) {
                    Map<GroupI, Long> childContent = n.getContent();
                    for (Long l : childContent.values()) {
                        numAssignedBelow += l;
                    }
                    for (Entry<GroupI, Long> e : childContent.entrySet()) {
                        Long l = content.get(e.getKey());
                        content.put(e.getKey(), l - e.getValue());
                    }
                }

                if (numSeqsAssigned - numAssignedBelow > 0) {
                    prefuse.data.Node unclassifiedNode = pTree.addChild(rootNode);
                    unclassifiedNode.set(nodeLabel, "Unclassified");
                    unclassifiedNode.set(nodeContent, content);
                    unclassifiedNode.set(nodeTotalElements, calculateNodeCount(content));
                }
            }

            for (NodeI<Map<GroupI, Long>> child : root.getChildren()) {
                addWithChildren(blacklist, rootNode, child);
            }
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

    private void addWithChildren(Set<AttributeI> blacklist, prefuse.data.Node parent, NodeI<Map<GroupI, Long>> node) {

        if (blacklist.contains(node.getAttribute())) {
            return;
        }

        prefuse.data.Node self = pTree.addChild(parent);

        long numSeqsAssigned = calculateNodeCount(node.getContent());
        self.set(nodeLabel, node.getAttribute().getValue());
        self.set(nodeContent, node.getContent());
        self.set(nodeTotalElements, numSeqsAssigned);

        if (node.hasChildren()) {
            for (NodeI<Map<GroupI, Long>> child : node.getChildren()) {
                addWithChildren(blacklist, self, child);
            }
        }

        if (showUnclassifieds && node.hasChildren()) {
            long numAssignedBelow = 0;
            Map<GroupI, Long> content = new HashMap<>();
            // copy content of parent
            for (Entry<GroupI, Long> e : node.getContent().entrySet()) {
                content.put(e.getKey(), e.getValue());
            }

            for (NodeI<Map<GroupI, Long>> n : node.getChildren()) {
                Map<GroupI, Long> childContent = n.getContent();
                for (Long l : childContent.values()) {
                    numAssignedBelow += l;
                }
                for (Entry<GroupI, Long> e : childContent.entrySet()) {
                    Long l = content.get(e.getKey());
                    content.put(e.getKey(), l - e.getValue());
                }
            }

            if (numSeqsAssigned - numAssignedBelow > 0) {
                prefuse.data.Node unclassifiedNode = pTree.addChild(self);
                unclassifiedNode.set(nodeLabel, "Unclassified");
                unclassifiedNode.set(nodeContent, content);
                unclassifiedNode.set(nodeTotalElements, calculateNodeCount(content));
            }
        }
    }

    private void initDisplay() {
        visualization = new Visualization();
        display = new Display(visualization) {

            @Override
            public void print(Graphics g) {
                if (g instanceof SVGGraphics2D) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setBackground(Color.WHITE);
                    super.print(g2);

                    setSize(getSize()); // clears offscreen img
                    setDamageRedraw(false);

                    paintDisplay(g2, getSize());

                    setDamageRedraw(true);
                    g2.dispose();

                } else {
                    super.print(g);
                }
            }

        };
        display.setBackground(Color.WHITE);
        display.setForeground(Color.BLACK);
        display.setHighQuality(true);

        if (!getCustomizer().hideTitle()) {
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
    }

    private void initRenderers() {
        //m_nodeRenderer = new LabelRenderer(nodeLabel);
        m_nodeRenderer = new PieNodeRenderer();
        m_nodeRenderer.setRenderType(ShapeRenderer.RENDER_TYPE_FILL);
        m_nodeRenderer.setHorizontalAlignment(Constants.CENTER); // was: left
        //m_nodeRenderer.setRoundedCorner(8, 8);
        m_edgeRenderer = new EdgeRenderer(Constants.EDGE_TYPE_CURVE);

        DefaultRendererFactory rf = new DefaultRendererFactory(m_nodeRenderer);
        rf.add(new InGroupPredicate(treeEdges), m_edgeRenderer);
        visualization.setRendererFactory(rf);

        // colors
        //ItemAction nodeColor = new NodeColorAction(treeNodes);
        ItemAction textColor = new ColorAction(treeNodes,
                VisualItem.TEXTCOLOR, ColorLib.rgb(0, 0, 0));
        visualization.putAction("textColor", textColor);

        ItemAction edgeColor = new ColorAction(treeEdges,
                VisualItem.STROKECOLOR, ColorLib.rgb(200, 200, 200));

        // quick repaint
        ActionList repaint = new ActionList();
        //repaint.add(nodeColor);
        repaint.add(new RepaintAction());
        visualization.putAction("repaint", repaint);

        // full paint
        ActionList fullPaint = new ActionList();
        //fullPaint.add(nodeColor);
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

        CollapsedSubtreeLayout subLayout
                = new CollapsedSubtreeLayout(tree, orientation);
        visualization.putAction("subLayout", subLayout);

        AutoPanAction autoPan = new AutoPanAction();

        // create the filtering and layout
        ActionList filter = new ActionList();
        filter.add(new FisheyeTreeFilter(tree, 2));
        filter.add(new FontAction(treeNodes, FontLib.getFont("Tahoma", 16)));
        filter.add(treeLayout);
        filter.add(subLayout);
        filter.add(textColor);
        //  filter.add(nodeColor);
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
        display.addControlListener(new MyWheelZoomControl(true, true));
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

    private static long calculateNodeCount(Map<GroupI, Long> content) {
        long total = 0;
        for (Long l : content.values()) {
            total += l;
        }
        return total;
    }

    private static Map<String, long[]> calculateRankCounts(TreeI<Map<GroupI, Long>> tree, GroupI[] groupOrder) {
        Map<String, long[]> ret = new HashMap<>();

        for (NodeI<Map<GroupI, Long>> node : tree.getNodes()) {
            String rankName = node.getAttribute().getAttributeType().getName();
            if (!ret.containsKey(rankName)) {
                long[] current = new long[groupOrder.length];
                Arrays.fill(current, 0);
                ret.put(rankName, current);
            }
            long[] current = ret.get(rankName);

            int i = 0;
            for (GroupI vg : groupOrder) {
                //for (Entry<VisualizationGroup, Long> e : node.getContent().entrySet()) {
                if (node.getContent().containsKey(vg)) {
                    current[i] += node.getContent().get(vg);
                }
                i++;
                //current[i++] += e.getValue().longValue();
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
    public TreeViewCustomizer getCustomizer() {
        if (cust == null) {
            cust = new TreeViewCustomizer();
        }
        cust.setAttributeType(getAttributeType());
        return cust;
    }

    private TreeViewCustomizer cust = null;

    @Override
    public ImageExporterI getImageExporter() {
        return new ImageExporterI() {

            @Override
            public FileType[] getSupportedTypes() {
                return new FileType[]{FileType.PNG, FileType.JPEG, FileType.SVG};
            }

            @Override
            public Result export(FileType type, String fName) throws Exception {
                switch (type) {
                    case PNG:
                    case JPEG:
                        try ( OutputStream os = new BufferedOutputStream(new FileOutputStream(fName))) {
                        if (display.saveImage(os, type.getSuffices()[0].toUpperCase(), 2)) {
                            return Result.SUCCESS;
                        }
                        return Result.ERROR;
                    }
                    case SVG:
                        try ( OutputStream os = new BufferedOutputStream(new FileOutputStream(fName))) {
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

    public static class MyWheelZoomControl extends AbstractZoomControl {

        private Point m_point = new Point();
        private final boolean inverted;
        private final boolean atPointer;

        /**
         * Creates a new <tt>WheelZoomControl</tt>. If <tt>inverted</tt> is
         * true, scrolling the mouse wheel toward you will make the graph appear
         * smaller. If <tt>atPointer</tt> is true, zooming will be centered on
         * the mouse pointer instead of the center of the display.
         *
         * @param inverted true if the scroll direction should be inverted
         * @param atPointer true if zooming should be centered on the mouse
         * pointer
         */
        public MyWheelZoomControl(boolean inverted, boolean atPointer) {
            this.inverted = inverted;
            this.atPointer = atPointer;
        }

        /**
         * Creates a new <tt>WheelZoomControl</tt> with the default zoom
         * direction and zooming on the center of the display.
         */
        public MyWheelZoomControl() {
            this(false, false);
        }

        /**
         * @see
         * prefuse.controls.Control#itemWheelMoved(prefuse.visual.VisualItem,
         * java.awt.event.MouseWheelEvent)
         */
        @Override
        public void itemWheelMoved(VisualItem item, MouseWheelEvent e) {
            if (m_zoomOverItem) {
                mouseWheelMoved(e);
            }
        }

        /**
         * @see
         * java.awt.event.MouseWheelListener#mouseWheelMoved(java.awt.event.MouseWheelEvent)
         */
        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            Display display = (Display) e.getComponent();
            if (atPointer) {
                m_point = e.getPoint();
            } else {
                m_point.x = display.getWidth() / 2;
                m_point.y = display.getHeight() / 2;
            }
            if (inverted) {
                zoom(display, m_point, 1 - 0.1f * e.getWheelRotation(), false);
            } else {
                zoom(display, m_point, 1 + 0.1f * e.getWheelRotation(), false);
            }
        }

    }

    public class OrientAction extends AbstractAction {

        @Serial
        private static final long serialVersionUID = 1L;

        private final int orientation;

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

        private final Point2D m_start = new Point2D.Double();
        private final Point2D m_end = new Point2D.Double();
        private final Point2D m_cur = new Point2D.Double();
        private final int m_bias = 150;

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
