//package de.cebitec.mgx.gui.treeview.renderer;
//
//import de.cebitec.mgx.gui.treeview.TreeView;
//import java.awt.Graphics2D;
//import java.awt.Shape;
//import java.awt.geom.Point2D;
//import java.awt.geom.Rectangle2D;
//import prefuse.Constants;
//import prefuse.data.Edge;
//import prefuse.data.Node;
//import prefuse.render.EdgeRenderer;
//import prefuse.visual.EdgeItem;
//import prefuse.visual.VisualItem;
//
///**
// * renderer for displaying the relative hit counts
// * @author rbisdorf
// */
//public class EvolutionRendererRelative extends EdgeRenderer implements EdgeRendererInterface{
//    private double lineWidth = 20;
//    private double after = 0;
//    private double before = 0;
//    private double current = 0;
//
//    public EvolutionRendererRelative() {
//        super();
//    }
//
//    public EvolutionRendererRelative(int edgeTypeCurve) {
//        super(edgeTypeCurve);
//    }
//
//    private void calculateThickness(VisualItem vi) {
//        Node parent = ((Edge) vi.getSourceTuple()).getSourceNode();
//        Node target = ((Edge) vi.getSourceTuple()).getTargetNode();
//
//        after = 0;
//        before = 0;
//        current = 0;
//
//        int position = parent.getChildIndex(target);
//        for (int i = 0; i < parent.getChildCount(); i++) {
//            Node currNode = parent.getChild(i);
//            double currWidth = calculateThicknessAlignment(currNode, parent);
//
//            if (i < position) {
//                before += currWidth;
//            } else if (i == position) {
//                current = currWidth;
//            } else if (i > position) {
//                after += currWidth;
//            }
//        }
//        setDefaultLineWidth(calculateThickness(target, parent));
//    }
//
//    protected double calculateThickness(Node target, Node parent) {
//        double total = 0;
//        if (parent.getChildCount() == 1) {
//            target = parent;
//            parent = parent.getParent();
//
//        }
//        if (parent == null) {
//            return 0.5f;
//        }
//        for (int i = 0; i < parent.getChildCount(); i++) {
//            Node child = parent.getChild(i);
//            if (child.get(TreeView.DATA) != null) {
//                total += Double.parseDouble(child.get(TreeView.DATA));
//            }
//        }
//        double percentage = target.get(TreeView.DATA) != null ? Double.parseDouble(target.get(TreeView.DATA))
//                / total : 0;
//        return (percentage * lineWidth) + 1;
//    }
//
//    protected double calculateThicknessAlignment(Node target, Node parent) {
//        double total = 0;
//        if (parent.getChildCount() == 1) {
//            target = parent;
//            parent = parent.getParent();
//        }
//        if (parent == null) {
//            return 0.5f;
//        }
//        for (int i = 0; i < parent.getChildCount(); i++) {
//            Node child = parent.getChild(i);
//            if (child.get(TreeView.DATA) != null) {
//                total += Double.parseDouble(child.get(TreeView.DATA));
//            }
//        }
//        double percentage = target.get(TreeView.DATA) != null ? Double.parseDouble(target.get(TreeView.DATA))
//                / total : 0;
//        if ((percentage * lineWidth) < 0.5f) {
//            return 0.5f;
//        }
//        return (percentage * lineWidth);
//
//    }
//
//    @Override
//    public void render(Graphics2D g, VisualItem item) {
//        setVerticalAlignment1(Constants.CENTER);
//        setVerticalAlignment2(Constants.CENTER);
//        calculateThickness(item);
//        super.render(g, item);
//    }
//
//    protected void getAlignedPointEvoStyle(Point2D p, Rectangle2D r,
//            int xAlign, int yAlign) {
//        double x = r.getX(), y = r.getY(), w = r.getWidth(), h = r.getHeight();
//        if (xAlign == Constants.CENTER) {
//            x = x + (w / 2);
//        } else if (xAlign == Constants.RIGHT) {
//            x = x + w;
//        }
//        y = y + (h / 2);
//        y += (-1) * (((after - before)) / 2);
//        p.setLocation(x, y);
//    }
//
//    @Override
//    protected Shape getRawShape(VisualItem item) {
//        EdgeItem edge = (EdgeItem) item;
//        VisualItem item1 = edge.getSourceItem();
//        VisualItem item2 = edge.getTargetItem();
//
//        int type = m_edgeType;
//        getAlignedPointEvoStyle(m_tmpPoints[0], item1.getBounds(), m_xAlign1,
//                m_yAlign1);
//        getAlignedPoint(m_tmpPoints[1], item2.getBounds(), m_xAlign2, m_yAlign2);
//        m_curWidth = (float) (m_width * getLineWidth(item));
//        m_curArrow = null;
//
//        // create the edge shape
//        Shape shape = null;
//        double n1x = m_tmpPoints[0].getX();
//        double n1y = m_tmpPoints[0].getY();
//        double n2x = m_tmpPoints[1].getX();
//        double n2y = m_tmpPoints[1].getY();
//        switch (type) {
//            case Constants.EDGE_TYPE_LINE:
//                m_line.setLine(n1x, n1y, n2x, n2y);
//                shape = m_line;
//                break;
//            case Constants.EDGE_TYPE_CURVE:
//                getCurveControlPoints(edge, m_ctrlPoints, n1x, n1y, n2x, n2y);
//                // modified n1x and n2x to calculate the center relative to line
//                // thickness
//                m_cubic.setCurve((n1x + m_curWidth / 2), n1y, m_ctrlPoints[0].getX(), m_ctrlPoints[0].getY(), m_ctrlPoints[1].getX(),
//                        m_ctrlPoints[1].getY(), ((n2x - m_curWidth / 2) + 1), n2y);
//                shape = m_cubic;
//                break;
//            default:
//                throw new IllegalStateException("Unknown edge type");
//        }
//        return shape;
//    }
//
//    // overwrite the stuff to recalculate the default beziercurve ....
//    @Override
//    protected void getCurveControlPoints(EdgeItem eitem, Point2D[] cp,
//            double x1, double y1, double x2, double y2) {
//        double dx = x2 - x1, dy = y2 - y1;
//        cp[0].setLocation(x1 + 2 * dx / 3, y1);
//        cp[1].setLocation(x2 - dx / 3, y2);
//    }
//
//    @Override
//    public double getLineWidth() {
//        return lineWidth;
//    }
//
//    @Override
//    public void setLineWidth(double lineWidth) {
//        this.lineWidth = lineWidth;
//    }
//}
