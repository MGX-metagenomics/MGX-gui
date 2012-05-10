package de.cebitec.mgx.gui.treeview;

import de.cebitec.mgx.gui.groups.VisualizationGroup;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.util.HashMap;
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
    private Map<VisualItem, Image> cache = new HashMap<>();

    public PieNodeRenderer() {
        setImagePosition(Constants.TOP);
        setImageField(TreeView.nodeContent);
    }

    @Override
    protected Image getImage(VisualItem item) {
        
        if (cache.containsKey(item)) {
            return cache.get(item);
        }
        long totalCount = item.getLong(TreeView.nodeTotalCount);
        String nodeLabel = item.getString(TreeView.nodeLabel);

        // create image and g2d object
        int size = MIN_CIRCLE_SIZE + 10 * (int) Math.log(Math.round(2 * totalCount));
        BufferedImage img = new BufferedImage(size + 1, size + 1, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Rectangle area = new Rectangle(size, size);

        
        Map<VisualizationGroup, Long> content = (Map<VisualizationGroup, Long>) item.get(TreeView.nodeContent);
        PieSlice[] slices = new PieSlice[content.size()];
        int i = 0;
        for (Map.Entry<VisualizationGroup, Long> e : content.entrySet()) {
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

            if (startAngle + arcAngle > 360) {
                int tmp = 360 - startAngle;
                g2.fillArc(area.x, area.y, area.width, area.height, startAngle, tmp);
                arcAngle = arcAngle - tmp;
                startAngle = 0;
            }
            g2.fillArc(area.x, area.y, area.width, area.height, startAngle, arcAngle);

            startAngle += arcAngle;
        }

        // pie chart border
        g2.setColor(Color.LIGHT_GRAY);
        g2.drawOval(0, 0, size, size);

        // centered label with total count 
        FontRenderContext frc = g2.getFontRenderContext();
        FontMetrics fontMetrics = g2.getFontMetrics();
        int height = fontMetrics.getHeight();
        int width = fontMetrics.stringWidth(String.valueOf(totalCount));
        TextLayout textlayout = new TextLayout(String.valueOf(totalCount), g2.getFont(), frc);
        textlayout.draw(g2, size / 2 - width / 2, size / 2 + height / 2 - 1);
        
        cache.put(item, img);
        return img;
    }

    private class PieSlice {

        double value;
        Color color;

        public PieSlice(double value, Color color) {
            this.value = value;
            this.color = color;
        }
    }
}
