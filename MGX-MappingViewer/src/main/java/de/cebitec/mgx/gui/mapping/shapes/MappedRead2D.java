/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping.shapes;

import de.cebitec.mgx.api.model.MappedSequenceI;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author sj
 */
public class MappedRead2D implements Shape, Comparable<MappedRead2D> {

    private final MappedSequenceI ms;
    private final Shape shape;
    private final static Color[] gradient = new Color[101];

    static {
        generateGradient();
    }

    public MappedRead2D(MappedSequenceI ms, double x, double y, double height, double length) {
        this.ms = ms;
        shape = new Rectangle2D.Double(x, y, length, height);
    }

    public MappedSequenceI getSequence() {
        return ms;
    }

    public Color getColor() {
        return gradient[(int)ms.getIdentity()];
    }

    @Override
    public Rectangle getBounds() {
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

    private static void generateGradient() {
        Color color1 = Color.RED;
        Color color2 = Color.GREEN;
        for (int i = 0; i < 101; i++) {
            float ratio = (float) i / (float) 101;
            int red = (int) (color2.getRed() * ratio + color1.getRed() * (1 - ratio));
            int green = (int) (color2.getGreen() * ratio + color1.getGreen() * (1 - ratio));
            int blue = (int) (color2.getBlue() * ratio + color1.getBlue() * (1 - ratio));
            gradient[i] = new Color(red, green, blue);
        }
    }

    @Override
    public int compareTo(MappedRead2D o) {
        int ret = Float.compare(ms.getIdentity(), o.ms.getIdentity());
        if (ret != 0) {
            return ret;
        }
        return ms.compareTo(o.ms);
    }

    public String getToolTipText() {
        return "<html>Start: " + ms.getStart() + "<br>Stop: " + ms.getStop() + "<br>" + String.valueOf(ms.getIdentity()) + "% identity</html>";
    }
}
