package de.cebitec.mgx.gui.treeview;

import de.cebitec.mgx.api.groups.GroupI;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RectangularShape;
import java.awt.image.BufferedImage;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;
import org.apache.commons.math3.util.FastMath;
import prefuse.Constants;
import prefuse.render.LabelRenderer;
import prefuse.visual.VisualItem;

/**
 *
 * @author sj
 */
public class PieNodeRenderer extends LabelRenderer {

    private final static float MIN_CIRCLE_SIZE = 30f;

    public PieNodeRenderer() {
        setImagePosition(Constants.TOP);
        setImageField(TreeView.nodeContent);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void render(Graphics2D g2, VisualItem item) {

        long totalCount = item.getLong(TreeView.nodeTotalElements);
        float size = MIN_CIRCLE_SIZE + 10 * (float) FastMath.log(FastMath.round(2 * totalCount));
        float radius = size / 2;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        RectangularShape shape = (RectangularShape) getShape(item);

        // top left of the bounding box for the pie chart
        float areaX = (float) (shape.getCenterX() - radius);
        float areaY = (float) shape.getMinY();

        Map<GroupI, Long> content = (Map<GroupI, Long>) item.get(TreeView.nodeContent);
        // FIXME - use fraction of total classified items for pie slice size

        // array of pie slices filled backwards since painting of items is 
        // counter-clockwise
        int i = content.size() - 1;
        PieSlice[] slices = new PieSlice[content.size()];
        for (Map.Entry<GroupI, Long> e : content.entrySet()) {
            slices[i--] = new PieSlice(e.getValue(), e.getKey().getColor());
        }

        float startAngle = 90;
        for (i = 0; i < slices.length; i++) {
            PieSlice ps = slices[i];
            float arcExtent = (float) (ps.value * 360f / totalCount);

            // for the last slice, make sure circle is fully closed
            if (i == (slices.length - 1)) {
                arcExtent = 360 - startAngle + 90 + 0.1f;
            }

            Arc2D.Float arc = new Arc2D.Float(areaX, areaY, size, size, startAngle, arcExtent, Arc2D.PIE);

            g2.setColor(ps.color);
            g2.fill(arc);

            startAngle += arcExtent;
        }

        // pie chart border
        g2.setColor(Color.LIGHT_GRAY);
        g2.draw(new Ellipse2D.Float(areaX, areaY, size, size));

        // centered label with total count 
        FontRenderContext frc = g2.getFontRenderContext();
        FontMetrics fontMetrics = g2.getFontMetrics();
        int height = fontMetrics.getHeight();
        String formattedCount = NumberFormat.getInstance(Locale.US).format(totalCount);

        int width = fontMetrics.stringWidth(formattedCount);
        TextLayout textlayout = new TextLayout(formattedCount, g2.getFont(), frc);
        textlayout.draw(g2, areaX + (size / 2 - width / 2), areaY - 1 + (size / 2 + height / 2 - 1));

        super.render(g2, item); // add the label itself
    }

    @Override
    protected Image getImage(VisualItem item) {

        long totalCount = item.getLong(TreeView.nodeTotalElements);

        // create image
        int size = (int) (MIN_CIRCLE_SIZE + 10 * FastMath.log(FastMath.round(2 * totalCount)));
        BufferedImage img = new BufferedImage(size + 1, size + 1, BufferedImage.TYPE_INT_ARGB);
        return img;
    }

    private static class PieSlice {

        final double value;
        final Color color;

        public PieSlice(double value, Color color) {
            this.value = value;
            this.color = color;
        }
    }
}
