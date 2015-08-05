/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping.shapes;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author sjaenick
 */
public class ColoredRectangle extends Rectangle2D.Double implements Comparable<ColoredRectangle> {
    
    private final Color color;

    public ColoredRectangle(Color color, double x, double y, double w, double h) {
        super(x, y, w, h);
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public int compareTo(ColoredRectangle o) {
        return Integer.compare(color.getRGB(), o.getColor().getRGB());
    }
    
}
