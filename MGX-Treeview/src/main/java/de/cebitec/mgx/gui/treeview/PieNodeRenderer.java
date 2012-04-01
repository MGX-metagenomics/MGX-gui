package de.cebitec.mgx.gui.treeview;

import de.cebitec.mgx.gui.attributevisualization.data.VisualizationGroup;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
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

    public PieNodeRenderer() {
        setImagePosition(Constants.TOP);
        setImageField(TreeView.nodeContent);
    }

    @Override
    protected Image getImage(VisualItem item) {
        Long totalCount = Long.parseLong(item.getString(TreeView.nodeTotalCount));
        double radius; // = Math.sqrt(totalCount.longValue() / Math.PI);
        radius = totalCount.doubleValue();
        int size = 30 + 10 * (int) Math.log(Math.round(2 * radius));
        System.err.println("using size " + size + " for total " + totalCount.toString());
        BufferedImage img = new BufferedImage(size + 1, size + 1, BufferedImage.TYPE_INT_ARGB);

        Map<VisualizationGroup, Long> content = (Map<VisualizationGroup, Long>) item.get(TreeView.nodeContent);
        PieValue[] slices = new PieValue[content.size()];
        int i = 0;
        for (Map.Entry<VisualizationGroup, Long> e : content.entrySet()) {
            slices[i++] = new PieValue(e.getValue(), e.getKey().getColor());
        }
        //Graphics g = img.getGraphics(); // 
        final Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        drawPie(g2, new Rectangle(size, size), slices);


        g2.setColor(Color.BLACK);
        g2.drawOval(0, 0, size, size);

        FontRenderContext frc = g2.getFontRenderContext(); //contains measurement _info
        //Font f = new Font("Helvetica",Font.BOLD, 24);
        int height = g2.getFontMetrics().getHeight();
        int width = g2.getFontMetrics().stringWidth(totalCount.toString());

        TextLayout textlayout = new TextLayout(totalCount.toString(), g2.getFont(), frc);
        textlayout.draw(g2, size / 2 - width / 2, size / 2 + height / 2 - 1);
        //g.drawString(totalCount.toString(), size/2-5, size/2);

        return img;
    }

    private void drawPie(Graphics g, Rectangle area, PieValue[] slices) {
        // Get total value of all slices
        double total = 0.0D;
        for (int i = 0; i < slices.length; i++) {
            total += slices[i].value;
        }

        // Draw each pie slice
        double curValue = 0.0D;
        int startAngle = 0;
        for (int i = 0; i < slices.length; i++) {
            // Compute the start and stop angles
            startAngle = (int) (curValue * 360 / total);
            int arcAngle = (int) (slices[i].value * 360 / total);

            // Ensure that rounding errors do not leave a gap between the first and last slice
            if (i == slices.length - 1) {
                arcAngle = 360 - startAngle;
            }

            // Set the color and draw a filled arc
            g.setColor(slices[i].color);
            g.fillArc(area.x, area.y, area.width, area.height, startAngle, arcAngle);

            curValue += slices[i].value;
        }
    }

    private class PieValue {

        double value;
        Color color;

        public PieValue(double value, Color color) {
            this.value = value;
            this.color = color;
        }
    }
}
