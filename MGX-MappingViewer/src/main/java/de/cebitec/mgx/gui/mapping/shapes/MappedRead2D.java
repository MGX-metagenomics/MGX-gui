/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping.shapes;

import de.cebitec.mgx.api.model.MappedSequenceI;
import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.io.Serial;
import java.text.NumberFormat;
import java.util.Locale;

/**
 *
 * @author sj
 */
public class MappedRead2D extends Rectangle2D.Float implements Comparable<MappedRead2D> {

    @Serial
    private static final long serialVersionUID = 1L;
    
    private final MappedSequenceI ms;
    //private final Shape shape;
    private final static Color[] gradient = new Color[101];

    static {
        generateGradient();
    }

    public MappedRead2D(MappedSequenceI ms, float x, float y, float height, float length) {
        super(x, y, length, height);
        this.ms = ms;
        //shape = new Rectangle2D.Double(x, y, length, height);
    }

    public MappedSequenceI getSequence() {
        return ms;
    }

    public Color getColor() {
        return gradient[(int)ms.getIdentity()];
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
        int ret = java.lang.Float.compare(ms.getIdentity(), o.ms.getIdentity());
        if (ret != 0) {
            return ret;
        }
        return ms.compareTo(o.ms);
    }

    public String getToolTipText() {
        NumberFormat nf = NumberFormat.getInstance(Locale.US);
        return "<html>Start: " + nf.format(ms.getStart()) 
                + "<br>Stop: " + nf.format(ms.getStop()) 
                + "<br>" + String.format("%.2f", ms.getIdentity()) 
                + "% identity</html>";
    }
}
