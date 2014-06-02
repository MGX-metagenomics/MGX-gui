package de.cebitec.mgx.gui.treeview.renderer;

import de.cebitec.mgx.api.groups.VisualizationGroupI;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;
import prefuse.Constants;
import prefuse.data.Edge;
import prefuse.data.Node;
import prefuse.render.EdgeRenderer;
import prefuse.visual.EdgeItem;
import prefuse.visual.VisualItem;

/**
 * renderer for displying the absolute hit values
 *
 * @author rbisdorf
 */
public class EvolutionRendererAbsolute extends EdgeRenderer implements EdgeRendererInterface {

    private long total = 0;
    private double lineWidth = 90;
    private double after = 0;
    private double before = 0;
    private double current = 0;

    public EvolutionRendererAbsolute(int total) {
        super();
        this.total = total;
        // TODO Auto-generated constructor stub
    }

    public EvolutionRendererAbsolute(int edgeType, int arrowType, int total) {
        super(edgeType, arrowType);
        this.total = total;
    }

    public EvolutionRendererAbsolute(int edgeType, Map<VisualizationGroupI, Long> data) {
        super(edgeType);
        long sum = 0;
        for (Long l : data.values()) {
            sum += l.longValue();
        }
        this.total = sum;
    }

    private void calculateThickness(VisualItem vi) {
        Node parent = ((Edge) vi.getSourceTuple()).getSourceNode();
        Node target = ((Edge) vi.getSourceTuple()).getTargetNode();

        after = 0;
        before = 0;
        current = 0;

        int position = parent.getChildIndex(target);
        for (int i = 0; i < parent.getChildCount(); i++) {
            Node currNode = parent.getChild(i);
            double currWidth = calculateThicknessAlignment(currNode);

            if (i < position) {
                before += currWidth;
            } else if (i == position) {
                current = currWidth;
            } else if (i > position) {
                after += currWidth;
            }
        }

        setDefaultLineWidth(calculateThickness(target));
    }

    protected double calculateThickness(Node target) {
        double percentage = 0.001f;
//        Map<VisualizationGroup, Long> data = (Map<VisualizationGroup, Long>) target.get(TreeView.DATA);
//        if (target != null && target.get(TreeView.DATA) != null) {
//            percentage = Double.parseDouble(target.getString("value")) / total;
//        }
        return (percentage * lineWidth) + 1.3;
    }

    protected double calculateThicknessAlignment(Node target) {
        double percentage = 0.001f;
//        if (target != null && target.getString("value") != null) {
//            percentage = Double.parseDouble(target.getString("value")) / total;
//        }
        return (percentage * lineWidth) + 1.3;
    }

    protected void getAlignedPointEvoStyle(Point2D p, Rectangle2D r,
            int xAlign, int yAlign) {
        double x = r.getX(), y = r.getY(), w = r.getWidth(), h = r.getHeight();
        if (xAlign == Constants.CENTER) {
            x = x + (w / 2);
        } else if (xAlign == Constants.RIGHT) {
            x = x + w;
        }
        y = y + (h / 2);
        y += (-1) * (((after - before)) / 2);
        p.setLocation(x, y);
    }

    @Override
    protected Shape getRawShape(VisualItem item) {
        EdgeItem edge = (EdgeItem) item;
        VisualItem item1 = edge.getSourceItem();
        VisualItem item2 = edge.getTargetItem();

        int type = m_edgeType;
        getAlignedPointEvoStyle(m_tmpPoints[0], item1.getBounds(), m_xAlign1,
                m_yAlign1);
        getAlignedPoint(m_tmpPoints[1], item2.getBounds(), m_xAlign2, m_yAlign2);
        m_curWidth = (float) (m_width * getLineWidth(item));
        m_curArrow = null;

        // create the edge shape
        Shape shape = null;
        double n1x = m_tmpPoints[0].getX();
        double n1y = m_tmpPoints[0].getY();
        double n2x = m_tmpPoints[1].getX();
        double n2y = m_tmpPoints[1].getY();
        switch (type) {
            case Constants.EDGE_TYPE_LINE:
                m_line.setLine(n1x, n1y, n2x, n2y);
                shape = m_line;
                break;
            case Constants.EDGE_TYPE_CURVE:
                getCurveControlPoints(edge, m_ctrlPoints, n1x, n1y, n2x, n2y);
                // modified n1x and n2x to calculate the center relative to line
                // thickness
                m_cubic.setCurve((n1x + m_curWidth / 2), n1y, m_ctrlPoints[0].getX(), m_ctrlPoints[0].getY(), m_ctrlPoints[1].getX(),
                        m_ctrlPoints[1].getY(), ((n2x - m_curWidth / 2) + 2), n2y);
                shape = m_cubic;
                break;
            default:
                throw new IllegalStateException("Unknown edge type");
        }
        return shape;
    }

    @Override
    public void render(Graphics2D g, VisualItem item) {
        setVerticalAlignment1(Constants.CENTER);
        setVerticalAlignment2(Constants.CENTER);
        setHorizontalAlignment1(Constants.CENTER);
        setHorizontalAlignment2(Constants.CENTER);
        calculateThickness(item);
        item.setValidated(false);
        super.render(g, item);
    }

    // overirde the stuff to recalculate the default beziercurve ....
    @Override
    protected void getCurveControlPoints(EdgeItem eitem, Point2D[] cp,
            double x1, double y1, double x2, double y2) {
        double dx = x2 - x1, dy = y2 - y1;
        cp[0].setLocation(x1 + 2 * dx / 3, y1);
        cp[1].setLocation(x2 - 1.4 * dx / 3, y2);
    }

    protected long getTotal() {
        return total;
    }

    protected void setTotal(int total) {
        this.total = total;
    }

    @Override
    public double getLineWidth() {
        return lineWidth;
    }

    @Override
    public void setLineWidth(double lineWidth) {
        this.lineWidth = lineWidth;
    }
}
