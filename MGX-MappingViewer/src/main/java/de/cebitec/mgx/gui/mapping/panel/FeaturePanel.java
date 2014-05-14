/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping.panel;

import de.cebitec.mgx.gui.datamodel.Region;
import de.cebitec.mgx.gui.mapping.ViewController;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import org.openide.util.Exceptions;

/**
 *
 * @author sjaenick
 */
public class FeaturePanel extends javax.swing.JPanel implements PropertyChangeListener, MouseListener, MouseMotionListener {

    private final static int FRAME_VOFFSET = 20;
    private final ViewController vc;
    private int[] bounds;
    private int intervalLen;
    private float scale;
    private int midY;
    private final static int[] frameOffsets = new int[]{
        15 + -1 * FRAME_VOFFSET * -3,
        15 + -1 * FRAME_VOFFSET * -2,
        15 + -1 * FRAME_VOFFSET * -1,
        -1 * FRAME_VOFFSET * 1,
        -1 * FRAME_VOFFSET * 2,
        -1 * FRAME_VOFFSET * 3};
    private static final RenderingHints antiAlias = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    private Set<Arrow> regs = new HashSet<>();

    /**
     * Creates new form FeaturePanel
     */
    public FeaturePanel(ViewController vc) {
        super();
        this.vc = vc;
        initComponents();
        setBackground(Color.WHITE);
        setForeground(Color.DARK_GRAY);
        setMaximumSize(new Dimension(5000, 150));
        vc.addPropertyChangeListener(this);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        update();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHints(antiAlias);
        g2.drawLine(0,  midY, getWidth(),  midY); // midline
        g2.setColor(Color.LIGHT_GRAY);
        for (int f : frameOffsets) {
            g2.drawLine(0, midY + f, getWidth(),  midY + f);
        }
        g.setColor(Color.DARK_GRAY);
        g.setFont(new Font(g.getFont().getFontName(), Font.PLAIN, 10));

        int separate = 500;
        if (intervalLen > 5000) {
            separate = 5000;
        }
        for (int i = bounds[0]; i < bounds[1]; i++) {
            if (i % separate == 0) {
                float pos = getScaledValue(i - bounds[0]);
                g2.drawLine((int) pos,  midY - 3, (int) pos,  midY + 3);
                String text1 = String.valueOf(i);
                g2.drawString(text1, pos - textWidth(g, text1) / 2, midY + 13);
            }
        }
        g2.setColor(Color.GREEN);
        for (Arrow r : regs) {
//            Rectangle2D b = r.getBounds2D();
//            g2.setColor(Color.LIGHT_GRAY);
//            g2.drawRect((int) b.getX(), (int) b.getY(), (int) b.getWidth(), (int) b.getHeight());
//            g2.setColor(Color.GREEN);
            g2.fill(r);
//            g2.setColor(Color.LIGHT_GRAY);
//            g2.draw(r);
            //g2.drawString(r.getRegion().getName(), r.getBounds().x, r.getBounds().y + 2);
        }
    }

    private float getScaledValue(int i) {
        float f = i * scale;
        return f;
    }

    private int[] getScaledValues(int i, int j) {
        int[] ret = new int[2];
        ret[0] = (int) (i * scale);
        ret[1] = (int) (j * scale);
        return ret;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        update();
        repaint();
    }

    private static int textWidth(Graphics g, String text) {
        FontMetrics metrics = g.getFontMetrics(g.getFont());
        return metrics.stringWidth(text);
    }

    private synchronized void update() {
        bounds = vc.getBounds();
        intervalLen = bounds[1] - bounds[0] + 1;
        scale = 1f / (1f * intervalLen / getWidth());
        midY = getHeight() / 2;

        if (!EventQueue.isDispatchThread()) {
            regs.clear();
            for (Region r : vc.getRegions(bounds[0], bounds[1])) {
                regs.add(r2a(r));
            }
        } else {
            SwingWorker<Set<Arrow>, Void> sw = new SwingWorker<Set<Arrow>, Void>() {

                @Override
                protected Set<Arrow> doInBackground() throws Exception {
                    Set<Arrow> newData = new HashSet<>();
                    for (Region r : vc.getRegions(bounds[0], bounds[1])) {
                        newData.add(r2a(r));
                    }
                    return newData;
                }
            };
            sw.execute();
            try {
                regs = sw.get();
            } catch (InterruptedException | ExecutionException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    private final Arrow r2a(final Region r) {
        int[] pos = getScaledValues(r.getStart() - bounds[0] - 1, r.getStop() - bounds[0] - 1);
        if (r.getFrame() < 0) {
            int frameOffset = frameOffsets[r.getFrame() + 3];
            return new Arrow(r, pos[1], midY + frameOffset - Arrow.HALF_HEIGHT, pos[0] - pos[1]);
        } else {
            int frameOffset = frameOffsets[r.getFrame() + 2];
            return new Arrow(r, pos[0], midY + frameOffset - Arrow.HALF_HEIGHT, pos[1] - pos[0]);
        }
    }

//    @Override
//    public String getToolTipText(MouseEvent m) {
//        Point loc = m.getPoint();
//        Iterator iter = this.arrows.iterator();
//        while (iter.hasNext()) {
//            Arrow a = (Arrow) iter.next();
//            if (a.contains(loc)) {
//                return a.getToolTipText();
//            }
//        }
//        return null;
//    }
    private boolean inDrag = false;
    private int dragStart = -1;

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        inDrag = true;
        int x = e.getX();
        float sc = 1f * intervalLen / getWidth();
        int posInRef = (int) (bounds[0] + (x * sc));
        dragStart = posInRef;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        inDrag = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (inDrag) {
            int x = e.getX();
            float sc = 1f * intervalLen / getWidth();
            int posInRef = (int) (bounds[0] + (x * sc));
            int offset = posInRef - dragStart;
            dragStart = posInRef;
            vc.setBounds(Math.max(0, bounds[0] - offset), Math.min(vc.getReference().getLength() - 1, bounds[1] - offset));
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // nop
    }

//    private final class RegionSort implements Comparator<Region> {
//
//        @Override
//        public int compare(Region r1, Region r2) {
//            int min1 = Math.min(r1.getStart(), r1.getStop());
//            int min2 = Math.min(r2.getStart(), r2.getStop());
//            return Integer.compare(min2, min2);
//        }
//
//    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 852, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 78, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
