package de.cebitec.mgx.gui.taskview;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JPanel;
import org.openide.util.ImageUtilities;

/**
 *
 * @author sjaenick
 */
public class ImagePanel extends JPanel {

    private Image img;

    public ImagePanel(String path) {
        img = ImageUtilities.loadImage(path);
    }

    @Override
    public void paint(Graphics g) {
        if (img != null) {
            g.drawImage(img, 0, 0, this);
        }
    }
}