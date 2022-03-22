/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.swingutils;

import de.cebitec.mgx.api.model.LocationI;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author sj
 */
public final class Arrow<T extends LocationI> implements ShapeBase {

//    public final static int FORWARD = 1;
//    public final static int REVERSE = 2;

    /*
     *                  3
     *                  |\
     *     1------------2 \
     *     |               \4
     *     |               /
     *     7------------6 /
     *                  |/
     *                  5
     *
     *        2    
     *       /| 
     *      / 3-------------4   
     *    1/                |
     *     \                |
     *      \ 6-------------5
     *       \|
     *        7
     *
     */
    private final Shape shape;
    private final String toolTip;
    private final T obj;
    private final static int TRIANGLE_WIDTH = 8;
    private final static int TRIANGLE_HEIGHT = 12;
    private final static int RECT_HEIGHT = 6;
    public final static int HEIGHT = TRIANGLE_HEIGHT;
    public final static float HALF_HEIGHT = HEIGHT / 2;

    public Arrow(final T r, String toolTip, float x, float y, float length) {

        this.obj = r;
        this.toolTip = toolTip;

        //float height = 14;
        float mid = y + HALF_HEIGHT;

        if (length < 1) {
            length = 1;
        }

        if (r.getStart() < r.getStop()) {
            GeneralPath triangle = new GeneralPath();
            if (length < TRIANGLE_WIDTH) {
                triangle.moveTo(x, mid - HALF_HEIGHT - 1);   // 3
                triangle.lineTo(x + length, mid);   // 4
                triangle.lineTo(x, mid + HALF_HEIGHT); // 5
                triangle.lineTo(x, mid - HALF_HEIGHT - 1);   // 3
            } else {
                triangle.moveTo(x, mid - RECT_HEIGHT / 2);  // 1
                triangle.lineTo(x + length - TRIANGLE_WIDTH, mid - RECT_HEIGHT / 2); //2
                triangle.lineTo(x + length - TRIANGLE_WIDTH, mid - HALF_HEIGHT - 1);   // 3
                triangle.lineTo(x + length, mid);   // 4
                triangle.lineTo(x + length - TRIANGLE_WIDTH, mid + HALF_HEIGHT); // 5
                triangle.lineTo(x + length - TRIANGLE_WIDTH, mid + RECT_HEIGHT / 2);  // 6
                triangle.lineTo(x, mid + RECT_HEIGHT / 2); // 7
                triangle.lineTo(x, mid - RECT_HEIGHT / 2); // 1/8
            }
            shape = new Area(triangle);
        } else {
            GeneralPath triangle = new GeneralPath();
            if (length < TRIANGLE_WIDTH) {
                triangle.moveTo(x, mid);     // 1
                triangle.lineTo(x + length, y - 1);  //2 
                triangle.lineTo(x + length, y + HEIGHT + 1); // 7
                triangle.lineTo(x, mid);  // 1/8
            } else {
                triangle.moveTo(x, mid);     // 1
                triangle.lineTo(x + TRIANGLE_WIDTH, y - 1);  //2 
                triangle.lineTo(x + TRIANGLE_WIDTH, mid - RECT_HEIGHT / 2); // 3
                triangle.lineTo(x + length, mid - RECT_HEIGHT / 2); // 4
                triangle.lineTo(x + length, mid + RECT_HEIGHT / 2);  // 5
                triangle.lineTo(x + TRIANGLE_WIDTH, mid + RECT_HEIGHT / 2); // 6
                triangle.lineTo(x + TRIANGLE_WIDTH, y + HEIGHT + 1); // 7
                triangle.lineTo(x, mid);  // 1/8
            }
            shape = new Area(triangle);
        }

    }
    
    public T getObject() {
        return obj;
    }

    @Override
    public final Color getColor() {
        return Color.GREEN;
    }

    @Override
    public java.awt.Rectangle getBounds() {
        return shape.getBounds();
    }

    @Override
    public Rectangle2D getBounds2D() {
        return shape.getBounds2D();
    }

    @Override
    public boolean contains(double x, double y) {
        return shape.contains(x, y);
    }

    @Override
    public boolean contains(Point2D p) {
        return shape.contains(p);
    }

    @Override
    public boolean intersects(double x, double y, double w, double h) {
        return shape.intersects(x, y, w, h);
    }

    @Override
    public boolean intersects(Rectangle2D r) {
        return shape.intersects(r);
    }

    @Override
    public boolean contains(double x, double y, double w, double h) {
        return shape.contains(x, y, w, h);
    }

    @Override
    public boolean contains(Rectangle2D r) {
        return shape.contains(r);
    }

    @Override
    public PathIterator getPathIterator(AffineTransform at) {
        return shape.getPathIterator(at);
    }

    @Override
    public PathIterator getPathIterator(AffineTransform at, double flatness) {
        return shape.getPathIterator(at, flatness);
    }

    @Override
    public String getToolTipText() {
        return toolTip;
    }

    @Override
    public int compareTo(ShapeBase o) {
        return Integer.compare(getColor().getRGB(), o.getColor().getRGB());
    }
}
