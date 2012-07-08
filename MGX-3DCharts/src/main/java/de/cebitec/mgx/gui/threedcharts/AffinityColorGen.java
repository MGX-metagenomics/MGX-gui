package de.cebitec.mgx.gui.threedcharts;

import org.jzy3d.colors.Color;
import org.jzy3d.colors.IColorMappable;
import org.jzy3d.colors.colormaps.ColorMapRedAndGreen;

/**
 *
 * @author sj
 */
public class AffinityColorGen extends ColorMapRedAndGreen {

    @Override
    public Color getColor(IColorMappable colorable, float z) {
        if (z > 2) {
            return Color.GREEN;
        }
        return new Color(1f - z / 2.1f, 0.001f + z / 2.1f, 0.1f);
    }

    @Override
    public Color getColor(IColorMappable colorable, float x, float y, float z) {
        return getColor(colorable, z);
    }
}