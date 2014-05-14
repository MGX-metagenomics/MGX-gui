/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping.panel;

import de.cebitec.mgx.gui.mapping.ViewController;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 *
 * @author sjaenick
 */
public class NavigationPanel extends javax.swing.JPanel implements PropertyChangeListener, MouseListener, MouseMotionListener {

    private final static int CAPTURE_DIST = 3; // px
    private final ViewController vc;
    private int[] previewBounds = null;
    private int[] offSet = null;
    private int intervalLen;
    private float scale;
    private int midY;

    /**
     * Creates new form NavigationPanel
     */
    public NavigationPanel(ViewController vc) {
        this.vc = vc;
        initComponents();
        setBackground(Color.WHITE);
        setForeground(Color.DARK_GRAY);
        setMaximumSize(new Dimension(5000, 50));
        vc.addPropertyChangeListener(this);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        RenderingHints rh = new RenderingHints(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHints(rh);

        scale = 1f * vc.getReference().getLength() / getWidth();
        midY = getHeight() / 2;
        
        g2.drawLine(0, midY, getWidth(), midY); // midline

        int refLength = vc.getReference().getLength();

        g2.setFont(new Font(g2.getFont().getFontName(), Font.PLAIN, 10));

        g2.drawLine(getWidth() / 4, midY - 3, getWidth() / 4, midY + 3);
        String text1 = String.valueOf(refLength / 4);
        g2.drawString(text1, getWidth() / 4 - textWidth(g2, text1) / 2, midY + 13);

        g2.drawLine(getWidth() / 2, midY - 3, getWidth() / 2, midY + 3);
        String text2 = String.valueOf(refLength / 2);
        g2.drawString(text2, getWidth() / 2 - textWidth(g2, text2) / 2, midY + 13);

        g2.drawLine(3 * getWidth() / 4, midY - 3, 3 * getWidth() / 4, midY + 3);
        String text3 = String.valueOf(3 * refLength / 4);
        g2.drawString(text3, 3 * getWidth() / 4 - textWidth(g2, text3) / 2, midY + 13);

        if (previewBounds != null) {
            float[] scaledPreview = getScaledValues(previewBounds);
            float dash1[] = {5.0f};
            BasicStroke dashed = new BasicStroke(1.0f,
                    BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER,
                    10.0f, dash1, 0.0f);

            Stroke oldStroke = g2.getStroke();
            g2.setStroke(dashed);
            g2.setColor(Color.BLACK);
            Shape curScope = new Rectangle2D.Float(scaledPreview[0], 0, scaledPreview[1] - scaledPreview[0] + 1, getHeight() - 1);
            g2.draw(curScope);
            //g2.drawRect(scaledPreview[0], 0, scaledPreview[1] - scaledPreview[0] + 1, getHeight() - 1);
            g2.setStroke(oldStroke);
        }

        Composite oldcomp = g2.getComposite();
        AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f);

        // draw box indicating current scope
        float[] scaledBounds = getScaledValues(vc.getBounds());
        g2.setComposite(ac);
        g2.setColor(Color.red);
        Shape curScope = new Rectangle2D.Float(scaledBounds[0], 0, scaledBounds[1] - scaledBounds[0] + 1, getHeight() - 1);
        g2.fill(curScope);
        //g2.fillRect(scaledBounds[0], 0, scaledBounds[1] - scaledBounds[0] + 1, getHeight() - 1);
        g2.setComposite(oldcomp);

    }

    private int textWidth(Graphics2D g, String text) {
        FontMetrics metrics = g.getFontMetrics(g.getFont());
        return metrics.stringWidth(text);
    }

//    private int getScaledValue(int i) {
//        float scale = vc.getReference().getLength() / getWidth();
//        float f = i * 1f / scale;
//        return (int) f;
//    }
    private float[] getScaledValues(int[] in) {
        float[] ret = new float[in.length];
        int pos = 0;
        for (int i : in) {
            float f = i * 1f / scale;
            ret[pos++] = f;
        }
        return ret;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 828, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 36, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        update();
        repaint();
    }

    private synchronized void update() {
//        scale = 1f * vc.getReference().getLength() / getWidth();
//        midY = getHeight() / 2;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.isConsumed()) {
            return;
        }
        int x = e.getX();
        int posInRef = (int) (x * scale);
        e.consume();

        int[] oldBounds = vc.getBounds();
        int len = oldBounds[1] - oldBounds[0] + 1;
        vc.setBounds(posInRef, Math.min(posInRef + len, vc.getReference().getLength() - 1));
    }

    private int dragType = 0;  // 1=left, 2=mid, 3=right

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.isConsumed()) {
            return;
        }
        int[] bounds = vc.getBounds();
        float[] scaledBounds = getScaledValues(bounds);

        int x = e.getX();
        int posInRef = (int) (x * scale);

        int distToStart = Math.abs(bounds[0] - posInRef);  // in bp
        int distToEnd = Math.abs(bounds[1] - posInRef);
        int distToMid = Math.abs((bounds[0] + (bounds[1] - bounds[0] + 1) / 2) - posInRef);

        int scaledDistToStart = (int) Math.abs(scaledBounds[0] - x);  // in px
        int scaledDistToEnd = (int) Math.abs(scaledBounds[1] - x);

        if (scaledDistToStart <= CAPTURE_DIST && distToStart < distToEnd && distToStart < distToMid) {
            dragType = 1;
        } else if (scaledDistToEnd <= CAPTURE_DIST && distToEnd < distToStart && distToEnd < distToMid) {
            dragType = 3;
//          } else if (distToMid < distToStart && distToMid < distToEnd && posInRef > bounds[0] && posInRef < bounds[1]) {
        } else if (posInRef > bounds[0] && posInRef < bounds[1]) {
            dragType = 2;
            offSet = new int[]{distToStart}; // remember position within interval
        }

        if (dragType != 0) {
            e.consume();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (dragType == 0 || e.isConsumed()) {
            return;
        }

        int x = e.getX();
        int posInRef = (int) (x * scale);

        int[] bounds = vc.getBounds();

        switch (dragType) {
            case 1:
                // update left bound
                if (posInRef < bounds[1]) {
                    vc.setBounds(posInRef, bounds[1]);
                }
                break;
            case 2:
                // move interval
                if (previewBounds != null) {
                    previewBounds[0] = previewBounds[0] < 0 ? 0 : previewBounds[0];
                    previewBounds[1] = previewBounds[1] > vc.getReference().getLength() - 1 ? vc.getReference().getLength() - 1 : previewBounds[1];
                    vc.setBounds(previewBounds[0], previewBounds[1]);
                }
                break;
            case 3:
                // update right
                if (posInRef > bounds[0]) {
                    vc.setBounds(bounds[0], posInRef);
                }
                break;

        }
        dragType = 0;
        previewBounds = null;
        offSet = null;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
        dragType = 0;
        previewBounds = null;
        offSet = null;
        repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (dragType == 0 || e.isConsumed()) {
            return;
        }

        int x = e.getX();
        int posInRef = (int) (x * scale);

        int[] bounds = vc.getBounds();

        switch (dragType) {
            case 1:
                // preview left bound
                if (posInRef < bounds[1]) {
                    previewBounds = new int[]{posInRef, bounds[1]};
                }
                break;
            case 2:
                // preview mid
                int len = bounds[1] - bounds[0] + 1;
                previewBounds = new int[]{posInRef - offSet[0], posInRef - offSet[0] + len};
                break;
            case 3:
                // preview right
                if (posInRef > bounds[0]) {
                    previewBounds = new int[]{bounds[0], posInRef};
                }
                break;
        }
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // nop
    }
}
