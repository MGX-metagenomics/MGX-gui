/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.statistics.clustering.dendro;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author sj
 */
public class Leaf implements SubTreeI {

    private final SubTree parent;
    private final String name;
    private final double distance;

    private final double x;
    private double y = 0;
    double textwidth;

    public Leaf(SubTree parent, String name, double distance, double x) {
        this.parent = parent;
        this.name = name;
        this.distance = distance;
        this.x = x;

        AffineTransform affinetransform = new AffineTransform();
        FontRenderContext frc = new FontRenderContext(affinetransform, true, true);
        Font font = new Font("Tahoma", Font.BOLD, 16);
        textwidth = font.getStringBounds(name, frc).getWidth();

    }

    @Override
    public void moveDown(SubTreeI src, double yDelta) {
        y += yDelta;
    }

    @Override
    public SubTree getParent() {
        return parent;
    }

    @Override
    public SubTreeI getSecond() {
        return null;
    }

    public String getName() {
        return name;
    }

    @Override
    public final int getLeafCount() {
        return 1;
    }

    @Override
    public final int getHeight() {
        return VSPACE + LINE_THICKNESS + VSPACE;
    }

    @Override
    public double getWidth() {
        return textwidth + 4 + (distance * SCALE_FACTOR);
    }

    @Override
    public Point2D.Double getConnectPoint() {
        return new Point2D.Double(x, y + (getHeight() / 2d));
    }

    @Override
    public void layout() {
        if (parent != null) {
            double mid = this.getY() + (getHeight() / 2d);
            if (parent.getY() <= mid) {
                parent.moveDown(this, mid - parent.getY());
            }
        }
    }

    @Override
    public void plot(Graphics2D g) {
        //System.err.println("leaf " + name + " at " + x + "," + y + ": " + distance + " with parent at " + getParent().getX() + " and width " + getParent().getWidth());

        double mid = getHeight() / 2d;
        g.setColor(Color.BLACK);
        Line2D.Double hLine = new Line2D.Double(getConnectPoint(),
                new Point2D.Double(x + (distance * SCALE_FACTOR), y + mid));
        g.draw(hLine);

        g.setFont(new Font("Tahoma", Font.BOLD, 16));
        g.drawString(name, (float) (x + (distance * SCALE_FACTOR) + 4), (float) (y + mid + 5));

        //System.err.println("plot leaf " + name + " at " + x + "," + y + "-" + (x+getWidth()) + "," + y);
    }

    @Override
    public void plotBounds(Graphics2D g) {
//        g.setColor(Color.RED);
//        g.draw(getBounds());
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public Rectangle2D getBounds() {
        double width = getWidth();
        width += 4; // spaceing between line and label
        width += 4; // spaceing after label
        width += textwidth;
        return new Rectangle2D.Double(x, y, width, getHeight());
    }

}
