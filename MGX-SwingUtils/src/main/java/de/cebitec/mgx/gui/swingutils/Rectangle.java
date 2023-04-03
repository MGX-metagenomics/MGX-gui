/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.swingutils;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.io.Serial;

/**
 *
 * @author sj
 */
public final class Rectangle extends Rectangle2D.Float implements ShapeBase {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String toolTip;
    //
    public final static int HEIGHT = 8;
    public final static float HALF_HEIGHT = HEIGHT / 2;

    public Rectangle(String tooltip, float x, float y, float length) {
        super();
        this.toolTip = tooltip;

        if (length < 1) {
            length = 1;
        }
        super.setRect(x, y, length, HEIGHT);
        //shape = new Rectangle2D.Double(x, y, length, HEIGHT);
    }

    @Override
    public final Color getColor() {
        return Color.BLUE;
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
