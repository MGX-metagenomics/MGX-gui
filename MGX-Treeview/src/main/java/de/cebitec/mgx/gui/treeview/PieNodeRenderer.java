package de.cebitec.mgx.gui.treeview;

import de.cebitec.mgx.api.groups.VisualizationGroupI;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.RectangularShape;
import java.awt.image.BufferedImage;
import java.util.Map;
import prefuse.Constants;
import prefuse.render.LabelRenderer;
import prefuse.visual.VisualItem;

/**
 *
 * @author sj
 */
public class PieNodeRenderer extends LabelRenderer {

    private final static int MIN_CIRCLE_SIZE = 30;

    public PieNodeRenderer() {
        setImagePosition(Constants.TOP);
        setImageField(TreeView.nodeContent);
    }

    @Override
    public void render(Graphics2D g2, VisualItem item) {

        long totalCount = item.getLong(TreeView.nodeTotalElements);
        int size = MIN_CIRCLE_SIZE + 10 * (int) Math.log(Math.round(2 * totalCount));
        int radius = size / 2;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        RectangularShape shape = (RectangularShape) getShape(item);
        
        // bounding box for the pie chart
        Rectangle area = new Rectangle((int) shape.getCenterX() - radius, (int) shape.getMinY(), size, size);

        Map<VisualizationGroupI, Long> content = (Map<VisualizationGroupI, Long>) item.get(TreeView.nodeContent);
        PieSlice[] slices = new PieSlice[content.size()];
        int i = 0;
        // FIXME - use fraction of total classified items for pie slice size
        for (Map.Entry<VisualizationGroupI, Long> e : content.entrySet()) {
            slices[i++] = new PieSlice(e.getValue(), e.getKey().getColor());
        }

        // draw the slices, starting at 12 o'clock
        int startAngle = 90;
        for (i = slices.length - 1; i >= 0; i--) {
            // Compute the start and offset angles
            int arcAngle = (int) (slices[i].value * 360 / totalCount);

            // Set the color and draw a filled arc
            g2.setColor(slices[i].color);
            startAngle = startAngle % 360;

            // "angle overflow" - split into two separate parts
            if (startAngle + arcAngle > 360) {
                int tmp = 360 - startAngle;
                g2.fillArc(area.x, area.y, area.width, area.height, startAngle, tmp);
                arcAngle = arcAngle - tmp;
                startAngle = 0;
            }

            if (i != 0) {
                g2.fillArc(area.x, area.y, area.width, area.height, startAngle, arcAngle);
            } else {
                g2.fillArc(area.x, area.y, area.width, area.height, startAngle, 90 - startAngle);
            }

            startAngle += arcAngle;
        }

        // pie chart border
        g2.setColor(Color.LIGHT_GRAY);
        g2.drawOval(area.x, area.y, size, size);

        // centered label with total count 
        FontRenderContext frc = g2.getFontRenderContext();
        FontMetrics fontMetrics = g2.getFontMetrics();
        int height = fontMetrics.getHeight();
        int width = fontMetrics.stringWidth(String.valueOf(totalCount));
        TextLayout textlayout = new TextLayout(String.valueOf(totalCount), g2.getFont(), frc);
        textlayout.draw(g2, area.x + (size / 2 - width / 2), area.y - 1 + (size / 2 + height / 2 - 1));

        super.render(g2, item); // add the label itself
    }

    @Override
    protected Image getImage(VisualItem item) {

        long totalCount = item.getLong(TreeView.nodeTotalElements);

        // create image and g2d object
        int size = MIN_CIRCLE_SIZE + 10 * (int) Math.log(Math.round(2 * totalCount));
        BufferedImage img = new BufferedImage(size + 1, size + 1, BufferedImage.TYPE_INT_ARGB);
        return img;
    }

    private class PieSlice {

        final double value;
        final Color color;

        public PieSlice(double value, Color color) {
            this.value = value;
            this.color = color;
        }
    }
}
