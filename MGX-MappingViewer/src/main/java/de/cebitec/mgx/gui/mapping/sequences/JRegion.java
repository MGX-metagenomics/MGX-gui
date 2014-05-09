package de.cebitec.mgx.gui.mapping.sequences;

import de.cebitec.mgx.gui.datamodel.Region;
import de.cebitec.mgx.gui.mapping.viewer.ReferenceViewer;
import de.cebitec.mgx.gui.mapping.misc.ColorProperties;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;

/**
 * Contains the content of a feature and takes care of the painting process.
 * Also contains its popup menu.
 *
 * @author ddoppmeier, rhilker
 */
public class JRegion extends JComponent {

    private static final long serialVersionUID = 347348234;
    private Region feature;
    private Dimension size;
    public static final int NORMAL_HEIGHT = 12;
    public static final int PARENT_FEATURE_HEIGHT = 8;
    public static final byte BORDER_NONE = 0;
    public static final byte BORDER_LEFT = -1;
    public static final byte BORDER_RIGHT = 1;
    public static final byte BORDER_BOTH = 2;
    private int height;
    private Font font;
    private Color color;
    private short border;

    /**
     * Contains the content of a feature and takes care of the painting process.
     * Also contains its popup menu.
     *
     * @param feature the feature to display
     * @param length length of the feature on the screen
     * @param refViewer the reference viewer on which the feature is displayed
     * @param border value among JFeature.BORDER_NONE, JFeature.BORDER_LEFT,
     * JFeature.BORDER_RIGHT, JFeature.BORDER_BOTH
     */
    public JRegion(final Region feature, double length, final ReferenceViewer refViewer, short border) {
        super();
        this.feature = feature;
        this.height = NORMAL_HEIGHT;
        this.size = new Dimension((int) length, height);
        this.setSize(size);
        this.font = new Font(Font.MONOSPACED, Font.PLAIN, 10);
        this.color = Color.ORANGE;
        this.border = border;

        this.addListeners(refViewer);
        this.setToolTipText(createToolTipText());
    }

    public Region getPersistantFeature() {
        return feature;
    }

    private String createToolTipText() {
        StringBuilder sb = new StringBuilder(100);
        sb.append("<html>");
        sb.append("<table>");
        sb.append(createTableRow("Name", feature.getName()));
        sb.append(createTableRow("Strand", (feature.isFwdStrand() ? "forward" : "reverse")));

        if (feature.isFwdStrand()) {
            sb.append(createTableRow("Start", String.valueOf(feature.getStart())));
            sb.append(createTableRow("Stop", String.valueOf(feature.getStop())));
        } else {
            sb.append(createTableRow("Start", String.valueOf(feature.getStop())));
            sb.append(createTableRow("Stop", String.valueOf(feature.getStart())));
        }
        sb.append("</table>");
        sb.append("</html>");
        return sb.toString();
    }

    private String createTableRow(String label, String value) {
        return "<tr><td align=\"right\"><b>" + label + ":</b></td><td align=\"left\">" + value + "</td></tr>";
    }

    @Override
    public void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics;

        // draw the rectangle
        g.setColor(color);

        g.fillRect(0, 0, this.getSize().width, this.height);
        g.setColor(ColorProperties.EXON_BORDER);
        g.drawRect(0, 0, this.getSize().width - 1, this.height - 1);
        //paint border in feature color, if feature is larger than screen at that border
        g.setColor(color);
        this.overpaintBorder(g, 0, this.height - 1);


        // draw the locus of the feature inside the rectangle
        g.setColor(ColorProperties.FEATURE_LABEL);
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics();

        int fontY = this.getHeight() / 2 - 2 + fm.getMaxAscent() / 2;

        String label = this.determineLabel(feature.getName(), fm);
        g.drawString(label, 5, fontY);


    }

    /**
     * Overpaints the border of the feature again with a line, if it is larger
     * than the screen and continues at the border.
     *
     * @param g graphics object to paint on
     * @param y1 first y value of the line to draw
     * @param y2 second y value of the line to draw
     */
    private void overpaintBorder(Graphics2D g, int y1, int y2) {
        switch (this.border) {
            case JRegion.BORDER_BOTH:
                g.drawLine(0, y1, 0, y2);
                g.drawLine(this.getSize().width - 1, y1, this.getSize().width - 1, y2);
                break;
            case JRegion.BORDER_LEFT:
                g.drawLine(0, y1, 0, y2);
                break;
            case JRegion.BORDER_RIGHT:
                g.drawLine(this.getSize().width - 1, y1, this.getSize().width - 1, y2);
                break;
            default:
                break;
        }
    }

    private String determineLabel(String text, FontMetrics fm) {
        // cut down the string if it extends the width of this component
        if (fm.stringWidth(text) > this.getWidth() - 10) {
            while (fm.stringWidth(text + "...") > this.getWidth() - 10 && text.length() > 0) {
                text = text.substring(0, text.length() - 1);
            }
            text += "...";
        }
        return text;
    }

    private void addListeners(final ReferenceViewer refViewer) {
        this.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showPopUp(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                showPopUp(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }

            private void showPopUp(MouseEvent e) {
                if ((e.getButton() == MouseEvent.BUTTON3) || (e.isPopupTrigger())) {
                    JPopupMenu popUp = new JPopupMenu();
                    popUp.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        this.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                refViewer.forwardChildrensMousePosition(e.getX(), JRegion.this);
            }
        });
    }
}
