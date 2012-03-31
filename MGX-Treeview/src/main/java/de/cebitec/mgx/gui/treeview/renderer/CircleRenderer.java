//package de.cebitec.mgx.gui.treeview.renderer;
//
//import de.cebitec.mgx.gui.treeview.TreeView;
//import java.awt.Color;
//import java.awt.Graphics2D;
//import java.awt.Paint;
//import java.awt.Shape;
//import java.awt.geom.AffineTransform;
//import java.awt.geom.Ellipse2D;
//import java.awt.geom.Point2D;
//import prefuse.Constants;
//import prefuse.data.Edge;
//import prefuse.data.Node;
//import prefuse.render.EdgeRenderer;
//import prefuse.visual.EdgeItem;
//import prefuse.visual.VisualItem;
//
///**
// * renderer for displaying hit count as circles
// *
// * @author rbisdorf
// */
//public class CircleRenderer extends EdgeRenderer implements EdgeRendererInterface {
//
//    protected double circleWidth;
//    protected double total = 3000;
//    protected double maxCircleWidth = 50;
//    protected Shape circle;
//    protected Paint circleColor = Color.BLACK;
//
//    public CircleRenderer() {
//        super();
//    }
//
//    public CircleRenderer(int edgeType) {
//        super(edgeType);
//    }
//
//    public CircleRenderer(int edgeType, int arrowType) {
//        super(edgeType, arrowType);
//    }
//
//    private void calculateCircle(VisualItem vi) {
//        Node target = ((Edge) vi.getSourceTuple()).getTargetNode();
//
//        double percentage = 0;
//
//        if (target != null && target.get(TreeView.DATA) != null) {
//            percentage = Double.parseDouble(target.get(TreeView.DATA)) / total;
//        }
//
//        if (percentage > 1) {
//            circleWidth = maxCircleWidth;
//        } else if ((percentage * maxCircleWidth) < 5) {
//            circleWidth = 5;
//        } else {
//            circleWidth = (float) ((percentage * maxCircleWidth) + 1);
//        }
//    }
//
//    @Override
//    public void render(Graphics2D g, VisualItem item) {
//        setVerticalAlignment1(Constants.CENTER);
//        setVerticalAlignment2(Constants.CENTER);
//        setHorizontalAlignment2(Constants.LEFT);
//
//        calculateCircle(item);
//        setDefaultLineWidth(3f);
//
//        Shape shape = getShape(item);
//        if (shape != null) {
//            drawShape(g, item, shape);
//        }
//
//        if (circle != null) {
//            g.setPaint(circleColor);
//            g.fill(circle);
//        }
//    }
//
//    @Override
//    protected Shape getRawShape(VisualItem item) {
//        EdgeItem edge = (EdgeItem) item;
//        VisualItem item1 = edge.getSourceItem();
//        VisualItem item2 = edge.getTargetItem();
//
//        int type = m_edgeType;
//
//        getAlignedPoint(m_tmpPoints[0], item1.getBounds(),
//                m_xAlign1, m_yAlign1);
//        getAlignedPoint(m_tmpPoints[1], item2.getBounds(),
//                m_xAlign2, m_yAlign2);
//        m_curWidth = (float) (m_width * getLineWidth(item));
//
//        // create the arrow head, if needed
//        EdgeItem e = (EdgeItem) item;
//
//        // create the arrow head shape
//        AffineTransform at = getCircleTrans(m_tmpPoints[1].getX(), m_tmpPoints[1].getY(), circleWidth);
//        circle = new Ellipse2D.Float((float) circleWidth, (float) circleWidth, (float) circleWidth, (float) circleWidth);
//        circle = at.createTransformedShape(circle);
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
//                m_cubic.setCurve(n1x, n1y,
//                        m_ctrlPoints[0].getX(), m_ctrlPoints[0].getY(),
//                        m_ctrlPoints[1].getX(), m_ctrlPoints[1].getY(),
//                        n2x - (circleWidth + 10), n2y);
//                shape = m_cubic;
//                break;
//            default:
//                throw new IllegalStateException("Unknown edge type");
//        }
//        return shape;
//    }
//
//    protected AffineTransform getCircleTrans(double p1, double p2, double width) {
//        m_arrowTrans.setToTranslation(p1 - width * 2, p2 - (width * 1.5));
//        return m_arrowTrans;
//    }
//
//    @Override
//    protected void getCurveControlPoints(EdgeItem eitem, Point2D[] cp,
//            double x1, double y1, double x2, double y2) {
//        double dx = x2 - x1, dy = y2 - y1;
//        cp[0].setLocation(x1 + 2 * dx / 4, y1);
//        cp[1].setLocation(x2 - 1.4 * dx / 3, y2);
//    }
//
//    @Override
//    public double getLineWidth() {
//        return maxCircleWidth;
//    }
//
//    @Override
//    public void setLineWidth(double lineWidth) {
//        maxCircleWidth = lineWidth;
//    }
//
//    public Paint getCircleColor() {
//        return circleColor;
//    }
//
//    public void setCircleColor(Paint circleColor) {
//        this.circleColor = circleColor;
//    }
//}
