/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.statistics.clustering.dendro;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author sj
 */
public interface SubTreeI {

    public final static int SCALE_FACTOR = 60;
    public final static int VSPACE = 10;
    public final static int LINE_THICKNESS = 3;

    public void layout();

    public void moveDown(SubTreeI src, double yDelta);

    public SubTreeI getParent();

    public SubTreeI getSecond();

    public int getLeafCount();

    public int getHeight();

    public double getWidth();

    public Point2D.Double getConnectPoint();

    public void plot(Graphics2D g);

    public void plotBounds(Graphics2D g);

    double getX();

    double getY();

    public Rectangle2D getBounds();
}
