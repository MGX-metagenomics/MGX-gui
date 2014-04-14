/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.statistics.viewer;

import de.cebitec.mgx.newick.NewickParser;
import de.cebitec.mgx.newick.NodeI;
import de.cebitec.mgx.newick.ParserException;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import javax.swing.JComponent;
import javax.swing.JFrame;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.layout.Layout;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.activity.Activity;
import prefuse.controls.DragControl;
import prefuse.controls.PanControl;
import prefuse.controls.ZoomControl;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Schema;
import prefuse.render.AbstractShapeRenderer;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.util.FontLib;
import prefuse.util.PrefuseLib;
import prefuse.visual.DecoratorItem;
import prefuse.visual.VisualItem;
import prefuse.visual.expression.InGroupPredicate;

/**
 *
 * @author sj
 */
public class ClusteringPlot extends JComponent {

    private Graph graph;
    private Visualization viz;
    private Display display;

    public void create(NodeI root) {
        graph = new Graph();
        graph.addColumn("name", String.class);
        graph.addColumn("weight", Double.class);

        Node rootNode = graph.addNode();
        rootNode.set("name", root.getName());
        rootNode.set("weight", root.getWeight());
        addRecursive(rootNode, root);

        viz = new Visualization();
        viz.add("graph", graph);

        display = new Display(viz);
        display.setSize(500, 500);
        display.addControlListener(new DragControl());
        display.addControlListener(new PanControl());
        display.addControlListener(new ZoomControl());

        FinalRenderer sr = new FinalRenderer();
        DefaultRendererFactory drf = new DefaultRendererFactory(sr);

        drf.add(new InGroupPredicate("labeldeco"), new LabelRenderer("name"));
        viz.setRendererFactory(drf);

        Schema s = PrefuseLib.getVisualItemSchema();
        s.setDefault(VisualItem.INTERACTIVE, false);
        s.setDefault(VisualItem.TEXTCOLOR, ColorLib.rgb(122, 122, 122));
        s.setDefault(VisualItem.FONT, FontLib.getFont("Tahoma", 16));
        viz.addDecorators("labeldeco", "graph.nodes", s);

        ActionList color = new ActionList();
        ColorAction fill = new ColorAction("graph.nodes", VisualItem.FILLCOLOR, ColorLib.rgb(0, 200, 0));
        ColorAction edges = new ColorAction("graph.edges", VisualItem.STROKECOLOR, ColorLib.gray(200));
        color.add(fill);
        color.add(edges);

        ActionList layout = new ActionList(Activity.INFINITY);
        layout.add(new ForceDirectedLayout("graph", true));
        layout.add(new FinalDecoratorLayout("labeldeco"));
        layout.add(new RepaintAction());

        viz.putAction("color", color);
        viz.putAction("layout", layout);
    }

    public Display getDisplay() {
        return display;
    }

    public Visualization getVisualization() {
        return viz;
    }

    private void addRecursive(Node pNode, NodeI tNode) {
        for (NodeI tn : tNode.getChildren()) {
            Node n = graph.addNode();
            n.set("name", tNode.getName());
            n.set("weight", tNode.getWeight());
            graph.addEdge(pNode, n);
            addRecursive(n, tn);
        }
    }

    public static void main(String[] args) throws ParserException {
        String newick = "(Bovine,(Gibbon,(Orang,(Gorilla,(Chimp, Human)))),Mouse);";
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ClusteringPlot cp = new ClusteringPlot();
        NodeI tn = NewickParser.parse(newick);
        cp.create(tn);
        f.add(cp.getDisplay());
        f.pack();
        f.setVisible(true);

        cp.getVisualization().run("color");
        cp.getVisualization().run("layout");
    }

    private class FinalRenderer extends AbstractShapeRenderer {

        private Ellipse2D foo = new Ellipse2D.Double();

        @Override
        protected Shape getRawShape(VisualItem vi) {
            foo.setFrame(vi.getX(), vi.getY(), 25, 10);
            return foo;
        }
    }

    private class FinalDecoratorLayout extends Layout {

        public FinalDecoratorLayout(String string) {
            super(string);
        }

        @Override
        public void run(double d) {
            Iterator iter = m_vis.items(m_group);
            while (iter.hasNext()) {
                DecoratorItem di = (DecoratorItem) iter.next();
                VisualItem target = di.getDecoratedItem();
                Rectangle2D r2d = target.getBounds();
                double cx = r2d.getCenterX();
                double cy = r2d.getCenterY();
                setX(di, null, cx);
                setY(di, null, cy);
            }
        }
    }

}
