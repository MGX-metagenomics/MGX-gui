/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping.shapes;

import de.cebitec.mgx.api.model.RegionI;
import java.awt.Color;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author sj
 */
public final class Rectangle extends Rectangle2D.Float implements ShapeBase {

    private final RegionI region;
    //
    public final static int HEIGHT = 8;
    public final static float HALF_HEIGHT = HEIGHT / 2;

    public Rectangle(RegionI region, float x, float y, float length) {
        super();
        this.region = region;

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
    public final RegionI getRegion() {
        return region;
    }

    @Override
    public String getToolTipText() {
        String type = region.getType() != null
                ? region.getType() + ": "
                : "";
        String framePrefix = region.getFrame() > 0 ? "+" : "";
        return "<html><b>" + type + region.getName() + "</b><hr>"
                + "Location: " + region.getStart() + "-" + region.getStop() + "<br>"
                + "Frame: " + framePrefix + region.getFrame() + "<br><br>"
                + region.getDescription() + "</html>";
    }

    @Override
    public int compareTo(ShapeBase o) {
        return Integer.compare(getColor().getRGB(), o.getColor().getRGB());
    }
}
