package de.cebitec.mgx.api.misc;

import org.apache.commons.math3.util.FastMath;

/**
 *
 * @author sj
 */
public class Point implements Comparable<Point> {

    private final double x;
    private final double y;
    private String name;

    public Point(double x, double y) {
        this(x, y, null);
    }

    public Point(double x, double y, String name) {
        this.x = x;
        this.y = y;
        this.name = name;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public double getLength() {
        return FastMath.sqrt(FastMath.pow(x, 2) + Math.pow(y, 2));
    }

    @Override
    public int compareTo(Point o) {
        return Double.compare(getLength(), o.getLength());
    }
}
