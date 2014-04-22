package de.cebitec.mgx.gui.datamodel.misc;

/**
 *
 * @author sj
 */
public class Point {

    private final double x;
    private final double y;
    private final String name;

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

    public String getName() {
        return name;
    }
}
