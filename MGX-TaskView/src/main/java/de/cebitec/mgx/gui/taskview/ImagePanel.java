package de.cebitec.mgx.gui.taskview;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.io.Serial;
import javax.swing.JComponent;
import org.openide.util.ImageUtilities;

/**
 *
 * @author sjaenick
 */
class ImagePanel extends JComponent {

    @Serial
    private static final long serialVersionUID = 1L;
    
    private Image img = null;

    void setImage(String path) {
        img = ImageUtilities.loadImage(path);
        Dimension tmp = new Dimension(img.getWidth(this), img.getHeight(this));
        setMinimumSize(tmp);
        setMaximumSize(tmp);
        setPreferredSize(tmp);
        revalidate();
        repaint();
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(img, 0, 0, null);
    }
}