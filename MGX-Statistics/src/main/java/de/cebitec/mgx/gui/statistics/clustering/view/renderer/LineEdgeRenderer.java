/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.statistics.clustering.view.renderer;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import prefuse.Constants;
import prefuse.render.EdgeRenderer;
import prefuse.util.GraphicsLib;
import prefuse.visual.EdgeItem;
import prefuse.visual.VisualItem;

/**
 *Renders edges as a half of a rectangle.
 * 
 * @author belmann
 */
public class LineEdgeRenderer extends EdgeRenderer {

    @Override
    protected Shape getRawShape(VisualItem item) {
        EdgeItem edge = (EdgeItem) item;
        VisualItem item1 = edge.getSourceItem();
        VisualItem item2 = edge.getTargetItem();
        
        getAlignedPoint(m_tmpPoints[0], item1.getBounds(),
                m_xAlign1, m_yAlign1);
        getAlignedPoint(m_tmpPoints[1], item2.getBounds(),
                m_xAlign2, m_yAlign2);
        m_curWidth = (float) (m_width * getLineWidth(item));
        
        // create the arrow head, if needed
        EdgeItem e = (EdgeItem) item;
        if (e.isDirected() && m_edgeArrow != Constants.EDGE_ARROW_NONE) {
            // get starting and ending edge endpoints
            boolean forward = (m_edgeArrow == Constants.EDGE_ARROW_FORWARD);
            Point2D start = null, end = null;
            start = m_tmpPoints[forward ? 0 : 1];
            end = m_tmpPoints[forward ? 1 : 0];

            // compute the intersection with the target bounding box
            VisualItem dest = forward ? e.getTargetItem() : e.getSourceItem();
            int i = GraphicsLib.intersectLineRectangle(start, end,
                    dest.getBounds(), m_isctPoints);
            if (i > 0) {
                end = m_isctPoints[0];
            }

            // create the arrow head shape
            AffineTransform at = getArrowTrans(start, end, m_curWidth);
            m_curArrow = at.createTransformedShape(m_arrowHead);

            // update the endpoints for the edge shape
            // need to bias this by arrow head size
            Point2D lineEnd = m_tmpPoints[forward ? 1 : 0];
            lineEnd.setLocation(0, -m_arrowHeight);
            at.transform(lineEnd, lineEnd);
        } else {
            m_curArrow = null;
        }

        // create the edge shape
        //Shape shape = null;
        double n1x = m_tmpPoints[0].getX();
        double n1y = m_tmpPoints[0].getY();
        double n2x = m_tmpPoints[1].getX();
        double n2y = m_tmpPoints[1].getY();

        Path2D path = new Path2D.Double();
        path.moveTo(n1x, n1y);
        path.lineTo(n1x, n2y);
        path.lineTo(n2x, n2y);
        //shape = path;
        // return the edge shape
        return path;
    }
}
