package de.cebitec.mgx.gui.treeview;

import de.cebitec.mgx.gui.attributevisualization.data.VisualizationGroup;
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
    private Map<VisualItem, Image> cache = new HashMap<VisualItem, Image>();

    public PieNodeRenderer() {
        setImagePosition(Constants.TOP);
        setImageField(TreeView.nodeContent);
    }

    @Override
    protected Image getImage(VisualItem item) {
        
        if (cache.containsKey(item)) {
            return cache.get(item);
        }
        Long totalCount = Long.parseLong(item.getString(TreeView.nodeTotalCount));
        String nodeLabel = item.getString(TreeView.nodeLabel);

        int size = MIN_CIRCLE_SIZE + 10 * (int) Math.log(Math.round(2 * totalCount.doubleValue()));
        BufferedImage img = new BufferedImage(size + 1, size + 1, BufferedImage.TYPE_INT_ARGB);

        Map<VisualizationGroup, Long> content = (Map<VisualizationGroup, Long>) item.get(TreeView.nodeContent);
        PieValue[] slices = new PieValue[content.size()];
        int i = 0;
        for (Map.Entry<VisualizationGroup, Long> e : content.entrySet()) {
            slices[i++] = new PieValue(e.getValue(), e.getKey().getColor());
        }
        final Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Rectangle area = new Rectangle(size, size);
        //drawPie(g2, new Rectangle(size, size), totalCount.doubleValue(), slices);
        // draw the slices
        int startAngle = 90; // start at 12 o'clock
        for (i = slices.length - 1; i >= 0; i--) {
            // Compute the start and offset angles
            //startAngle = startAngle + (int) (curValue * 360 / total);
            int arcAngle = (int) (slices[i].value * 360 / totalCount.doubleValue());

            // Set the color and draw a filled arc
            g2.setColor(slices[i].color);
            startAngle = startAngle % 360;

            //System.err.println("arc in " + g2.getColor().toString() + " starting at " + startAngle + ", angle " + arcAngle);
            
//            if (i == 0) {
//                arcAngle = (360 + 90 - startAngle) % 360;
//                System.err.println(nodeLabel + ": coordinates adapted to "+startAngle+" - "+ arcAngle);
//            }

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
        int width = fontMetrics.stringWidth(totalCount.toString());
        TextLayout textlayout = new TextLayout(totalCount.toString(), g2.getFont(), frc);
        textlayout.draw(g2, size / 2 - width / 2, size / 2 + height / 2 - 1);
        
        cache.put(item, img);

        return img;
    }

//    private void drawPie(Graphics g, Rectangle area, double total, PieValue[] slices) {
//        // Draw each pie slice
//        //double curValue = 0.0D;
//        int startAngle = 90; // start at 12 o'clock
//        for (int i = slices.length - 1; i >= 0; i--) {
//            // Compute the start and offset angles
//            //startAngle = startAngle + (int) (curValue * 360 / total);
//            int arcAngle = (int) (slices[i].value * 360 / total);
//
//            // Set the color and draw a filled arc
//            g.setColor(slices[i].color);
//            startAngle = startAngle % 360;
//            //System.err.println("arc in "+g.getColor().toString()+" starting at "+startAngle+", angle "+ arcAngle);
//            g.fillArc(area.x, area.y, area.width, area.height, startAngle, arcAngle);
//            startAngle += arcAngle;
//        }
//    }
    private class PieValue {

        double value;
        Color color;

        public PieValue(double value, Color color) {
            this.value = value;
            this.color = color;
        }
    }
}
