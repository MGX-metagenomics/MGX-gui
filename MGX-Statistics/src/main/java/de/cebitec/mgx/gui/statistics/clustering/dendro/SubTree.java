/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.statistics.clustering.dendro;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author sj
 */
public class SubTree implements SubTreeI {

    private final SubTreeI parent;
    private SubTreeI first;
    private SubTreeI second;
    private final double distance;
    private final double x;
    private double y = 0;

    public SubTree(SubTreeI parent, double distance, double x) {
        this.parent = parent;
        this.distance = distance;
        this.x = x;
    }

    @Override
    public SubTreeI getParent() {
        return parent;
    }

    @Override
    public SubTreeI getSecond() {
        return second;
    }

    @Override
    public void layout() {
        first.layout();
        second.layout();

        // overlap between direct children, move down second
        while (first.getY() + first.getHeight() > second.getY()) {
            double targetY = first.getY() + first.getHeight();
            second.moveDown(this, targetY - second.getY());
        }

        if (first.getBounds().intersects(second.getBounds())) {
            Rectangle2D shared = first.getBounds().createIntersection(second.getBounds());
            second.moveDown(this, shared.getHeight());
        }
//        if (this.getY() <= first.getY()) {
//            double targetY = first.getY() + (first.getHeight() / 2d);
//            y = targetY;
//            double secondY = second.getY();
//            targetY = first.getY() + first.getHeight();
//            second.moveDown(this, targetY - secondY);
//        }
    }

    @Override
    public void moveDown(SubTreeI src, double yDelta) {
        if (src == first) {
            y += yDelta;
            second.moveDown(this, yDelta);
//            if (parent != null) {
//                parent.moveDown(this, yDelta);
//            }
        }
        if (src == second) {
            y += yDelta;
            first.moveDown(this, yDelta);
//            if (parent != null) {
//                parent.moveDown(this, yDelta);
//            }
        }
        if (src == parent && parent != null) {
            y += yDelta;
            first.moveDown(this, yDelta);
            second.moveDown(this, yDelta);
        }
    }

    public void setChildren(SubTreeI first, SubTreeI second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public final int getLeafCount() {
        return first.getLeafCount() + second.getLeafCount();
    }

    @Override
    public double getWidth() {
        double ret = distance * SCALE_FACTOR;
        if (parent != null) {
            // enforce minimum width to avoid overlapping
            return Math.max(ret, 8);
        }
        
        // if root
        if (distance == 0 && parent == null) {
            double w = 0;
            if (first != null) {
                w = 8*SCALE_FACTOR + first.getWidth();
            }
            if (second != null) {
                w = Math.max(w+8*SCALE_FACTOR, 8*SCALE_FACTOR+second.getWidth());
            }
            return w;
        }
        // for the root node (parent == null), do not 
        // add artificial width, since this would look
        // like a rooted tree
        return ret;
    }

    @Override
    public int getHeight() {
        return first.getHeight() + second.getHeight();
    }

    @Override
    public Point2D.Double getConnectPoint() {
        int totalLeaves = getLeafCount();
        double totalHeight = first.getHeight() + second.getHeight() - VSPACE - VSPACE;
        double yy = first.getLeafCount() * totalHeight / totalLeaves;
        return new Point2D.Double(x, y + yy);
//        Point2D.Double cp1 = first.getConnectPoint();
//        Point2D.Double cp2 = second.getConnectPoint();
//        return new Point2D.Double(cp2.x, cp1.y + (cp2.y - cp1.y)/2d);
    }

    @Override
    public void plot(Graphics2D g) {

        first.plot(g);
        second.plot(g);

        // vertical line 
        Point2D.Double cp1 = first.getConnectPoint();
        Point2D.Double cp2 = second.getConnectPoint();
        g.draw(new Line2D.Double(cp1, cp2));

        if (getWidth() > 0) {
            Point2D.Double left = getConnectPoint();
            //if (first instanceof Leaf && second instanceof Leaf) {
            //System.err.println("LC " + first.getLeafCount() + " " + second.getLeafCount());
            //System.err.println("H " + first.getHeight() + " " + second.getHeight());
            //System.err.println(getWidth());
            
            
            //g.setColor(Color.BLUE);
            //g.drawRect((int) left.x - 2, (int) left.y - 2, 4, 4);
            //}
            g.setColor(Color.BLACK);
            Point2D.Double right = new Point2D.Double(cp1.x, left.y);
            g.draw(new Line2D.Double(left, right));
        }
    }

    @Override
    public void plotBounds(Graphics2D g) {
        if (first instanceof SubTree && second instanceof SubTree) {
            g.setColor(Color.red);
            g.draw(getBounds());
        }
//        if (first.getBounds().intersects(second.getBounds())) {
//            System.err.println("ouch");
        first.plotBounds(g);
        second.plotBounds(g);

//        }
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
        Rectangle2D.Double myBounds = new Rectangle2D.Double(x, y, getWidth(), getHeight());
        return myBounds.createUnion(first.getBounds()).createUnion(second.getBounds());
    }
}
