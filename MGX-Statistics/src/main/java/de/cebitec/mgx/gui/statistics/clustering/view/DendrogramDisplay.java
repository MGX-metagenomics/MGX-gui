/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.statistics.clustering.view;

import de.cebitec.mgx.gui.statistics.clustering.view.renderer.LineEdgeRenderer;
import de.cebitec.mgx.gui.statistics.clustering.model.ITreeBuilder;
import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.animate.LocationAnimator;
import prefuse.action.animate.QualityControlAnimator;
import prefuse.action.animate.VisibilityAnimator;
import prefuse.action.assignment.ColorAction;
import prefuse.action.layout.CollapsedSubtreeLayout;
import prefuse.action.layout.Layout;
import prefuse.activity.Activity;
import prefuse.activity.SlowInSlowOutPacer;
import prefuse.controls.PanControl;
import prefuse.controls.WheelZoomControl;
import prefuse.controls.ZoomControl;
import prefuse.controls.ZoomToFitControl;
import prefuse.data.Schema;
import prefuse.data.Tree;
import prefuse.render.AbstractShapeRenderer;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.util.PrefuseLib;
import prefuse.visual.DecoratorItem;
import prefuse.visual.EdgeItem;
import prefuse.visual.VisualItem;
import prefuse.visual.expression.InGroupPredicate;

/**
 *
 * Displays a Dendrogram.
 * 
 * @author belmann
 */
public class DendrogramDisplay extends Display {

    private static final int m_orientation = Constants.ORIENT_LEFT_RIGHT;
    private static final String GRAPH_NAME = "tree";
    private static final String EDGE_ID = "tree.edges";
    private static final String NODE_ID = "tree.nodes";
    private static final String NODE_NAME = "nodeName";
    private static final String X_COORD = "x";
    private static final String EDGE_DECORATORS = "edgeDeco";
    private static final Schema DECORATOR_SCHEMA = PrefuseLib.getVisualItemSchema();

    private final ITreeBuilder graph;
    
    public DendrogramDisplay(ITreeBuilder graph) {
        super(new Visualization());
        this.graph = graph;
        this.makeDisplay();
    }

    private void makeDisplay() {
        Tree tree = this.graph.getTree();
        m_vis.addTree(GRAPH_NAME, tree);
        m_vis.setInteractive(EDGE_ID, null, false);

        LabelRenderer nodeR = new LabelRenderer(NODE_NAME);
        nodeR.setRenderType(AbstractShapeRenderer.RENDER_TYPE_FILL);
        nodeR.setHorizontalAlignment(Constants.LEFT);
        nodeR.setRoundedCorner(8, 8);
        LineEdgeRenderer edgeR = new LineEdgeRenderer();
        DefaultRendererFactory drf = new DefaultRendererFactory();
        drf.setDefaultRenderer(nodeR);
        drf.add(new InGroupPredicate(EDGE_DECORATORS), new LabelRenderer(VisualItem.LABEL));
        drf.setDefaultEdgeRenderer(edgeR);
        m_vis.setRendererFactory(drf);

        ColorAction nStroke = new ColorAction(NODE_ID, VisualItem.STROKECOLOR);
        nStroke.setDefaultColor(ColorLib.gray(100));
        ColorAction nFill = new ColorAction(NODE_ID, VisualItem.FILLCOLOR);
        nFill.setDefaultColor(ColorLib.color(new Color(76, 185, 212)));
        ColorAction edges = new ColorAction(EDGE_ID,
                VisualItem.STROKECOLOR, ColorLib.gray(200));
        ColorAction arrow = new ColorAction(EDGE_ID,
                VisualItem.FILLCOLOR, ColorLib.gray(200));
        ColorAction text = new ColorAction(NODE_ID,
                VisualItem.TEXTCOLOR, ColorLib.gray(0));

        ActionList color = new ActionList();
        color.add(nStroke);
        color.add(nFill);
        color.add(edges);
        color.add(text);
        color.add(arrow);
        m_vis.putAction("color", color);

        CollapsedSubtreeLayout subLayout =
                new CollapsedSubtreeLayout(GRAPH_NAME, m_orientation);

        DECORATOR_SCHEMA.setDefault(VisualItem.TEXTCOLOR, ColorLib.gray(0));
        m_vis.addDecorators(EDGE_DECORATORS, EDGE_ID, DECORATOR_SCHEMA);

        ActionList layout = new ActionList(Activity.INFINITY);
        layout.add(subLayout);
        layout.add(new LabelLayout(EDGE_DECORATORS));
        layout.add(new LabelLayout(EDGE_DECORATORS));
        layout.add(new RepaintAction());
        m_vis.putAction("layout", layout);

        ActionList animate = new ActionList(1000);
        animate.setPacingFunction(new SlowInSlowOutPacer());
        animate.add(new QualityControlAnimator());
        animate.add(new VisibilityAnimator(GRAPH_NAME));
        animate.add(new LocationAnimator(NODE_ID));
        animate.add(new RepaintAction());
        m_vis.putAction("animate", animate);

        DendrogramLayout treeLayout = new DendrogramLayout(GRAPH_NAME, X_COORD);
        m_vis.putAction("treeLayout", treeLayout);

        setHighQuality(true);
        addControlListener(new PanControl());
        addControlListener(new ZoomControl());
        addControlListener(new ZoomToFitControl());
        addControlListener(new WheelZoomControl());
        m_vis.run("color");
        m_vis.run("treeLayout");
        m_vis.run("animate");
        m_vis.run("layout");

    }

    /**
     * LabelLayout for Edges
     */
    class LabelLayout extends Layout {

        public LabelLayout(String group) {
            super(group);
        }

        @Override
        public void run(double frac) {
            Iterator iter = m_vis.items(m_group);
            while (iter.hasNext()) {
                DecoratorItem decorator = (DecoratorItem) iter.next();
                VisualItem decoratedItem = decorator.getDecoratedItem();
                Rectangle2D bounds = decoratedItem.getBounds();
                double x = bounds.getCenterX();
                double y = bounds.getCenterY();
                if (decoratedItem instanceof EdgeItem) {
                    VisualItem source = ((EdgeItem) decoratedItem).getSourceItem();
                    x = source.getX() + 15;
                }
                setX(decorator, null, x);
                setY(decorator, null, y);
            }
        }
    }
}
