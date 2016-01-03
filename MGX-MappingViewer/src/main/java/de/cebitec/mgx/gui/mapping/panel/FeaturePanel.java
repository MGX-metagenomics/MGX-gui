/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping.panel;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.MappedSequenceI;
import de.cebitec.mgx.api.model.RegionI;
import de.cebitec.mgx.gui.mapping.ViewController;
import de.cebitec.mgx.gui.mapping.shapes.Arrow;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.SortedSet;
import javax.swing.ToolTipManager;
import org.apache.commons.math3.util.FastMath;
import org.openide.util.Exceptions;

/**
 *
 * @author sjaenick
 */
public class FeaturePanel extends PanelBase implements MouseListener, MouseMotionListener {

    private final static int FRAME_VOFFSET = 20;
    private final static int[] frameOffsets = new int[]{
        10 + -1 * FRAME_VOFFSET * -3,
        10 + -1 * FRAME_VOFFSET * -2,
        10 + -1 * FRAME_VOFFSET * -1,
        -1 * FRAME_VOFFSET * 1,
        -1 * FRAME_VOFFSET * 2,
        -1 * FRAME_VOFFSET * 3};
    private final Set<Arrow> regs = new HashSet<>();
    private final static Color lighterGray = new Color(210, 210, 210);

    /**
     * Creates new form FeaturePanel
     */
    public FeaturePanel(ViewController vc) {
        super(vc, true);
        initComponents();
        setMinimumSize(new Dimension(500, 175));
        setPreferredSize(new Dimension(5000, 175));
        //setMaximumSize(new Dimension(5000, 80));
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        ToolTipManager.sharedInstance().registerComponent(this);
        ToolTipManager.sharedInstance().setDismissDelay(5000);
        repaint();
    }

    @Override
    void draw(Graphics2D g2) {
        int midY = getHeight() / 2;

        g2.setColor(Color.DARK_GRAY);
        g2.drawLine(0, midY, getWidth(), midY); // midline
        int textHeight = -1 + g2.getFontMetrics(g2.getFont()).getHeight() / 2;
        int textWidth = g2.getFontMetrics(g2.getFont()).stringWidth("+3");
        g2.drawString("-3", 0, midY + frameOffsets[0] + textHeight);
        g2.drawString("-2", 0, midY + frameOffsets[1] + textHeight);
        g2.drawString("-1", 0, midY + frameOffsets[2] + textHeight);
        g2.drawString("+1", 0, midY + frameOffsets[3] + textHeight);
        g2.drawString("+2", 0, midY + frameOffsets[4] + textHeight);
        g2.drawString("+3", 0, midY + frameOffsets[5] + textHeight);
        g2.setColor(Color.LIGHT_GRAY);
        for (int f : frameOffsets) {
            g2.drawLine(textWidth + 2, midY + f, getWidth(), midY + f);
        }
        g2.setColor(Color.DARK_GRAY);

        /*
         * add tick marks with genome positions
         */
        int separate = 500;
        while (vc.getIntervalLength() / separate > 10) {
            separate += 500;
        }
        //int[] bounds = vc.getBounds();
        int firstpos = bounds[0];
        while (firstpos % separate != 0) {
            firstpos++;
        }
        for (int i = firstpos; i < bounds[1]; i += separate) {
            //if (i % separate == 0) {
            double pos = bp2px(i);
            g2.drawLine((int) pos, midY - 3, (int) pos, midY + 3);
            String text1 = String.valueOf(i);
            g2.drawString(text1, (int) pos - textWidth(g2, text1) / 2, midY + 13);
            //}
        }

        if (regs.isEmpty()) {
            return;
        }

        synchronized (regs) {

            /*
             * create shadow effects
             */
            Composite oldComp = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));

            g2.setColor(Color.LIGHT_GRAY);
            for (Arrow r : regs) {
                Shape shadow = AffineTransform.getTranslateInstance(4, 3).createTransformedShape(r);
                g2.fill(shadow);
            }
            g2.setColor(lighterGray);
            for (Arrow r : regs) {
                Shape shadow = AffineTransform.getTranslateInstance(4, 3).createTransformedShape(r);
                g2.draw(shadow);
            }

            g2.setComposite(oldComp);

            // draw arrows (and borders)
            g2.setColor(Color.GREEN);
            for (Arrow r : regs) {
                g2.fill(r);
            }
            g2.setColor(Color.DARK_GRAY);
            for (Arrow r : regs) {
                g2.draw(r);
            }
        }
    }

//    private void drawCoverage(Graphics2D g2) {
//        if (getMaxCoverage() == 0 || coverage == null) {
//            return;
//        }
//        Composite oldcomp = g2.getComposite();
//        AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f);
//        g2.setComposite(ac);
//        g2.setColor(Color.red);
//        for (Area l : coverage) {
//            g2.fill(l);
//        }
//        g2.setComposite(oldcomp);
//    }
    @Override
    public boolean update() {
        // fetch features
        Set<Arrow> newData = new HashSet<>();
        try {
            for (RegionI r : vc.getRegions()) {
                newData.add(r2a(r));
            }
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
        }

        synchronized (regs) {
            regs.clear();
            regs.addAll(newData);
        }

        return true;
    }

    private Arrow r2a(final RegionI r) {
        int midY = getHeight() / 2;
        double pos0 = bp2px(r.getStart() - 1);
        double pos1 = bp2px(r.getStop() - 1);
        if (r.getFrame() < 0) {
            int frameOffset = frameOffsets[r.getFrame() + 3];
            return new Arrow(r, pos1, midY + frameOffset - Arrow.HALF_HEIGHT, pos0 - pos1);
        } else {
            int frameOffset = frameOffsets[r.getFrame() + 2];
            return new Arrow(r, pos0, midY + frameOffset - Arrow.HALF_HEIGHT, pos1 - pos0);
        }
    }

    @Override
    public String getToolTipText(MouseEvent m) {
        Point loc = m.getPoint();
        if (regs != null) {
            for (Arrow a : regs) {
                if (a.getBounds().contains(loc)) {
                    return a.getToolTipText();
                }
            }
        }
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        String seqLen = formatter.format(vc.getReference().getLength());

        return "<html><b>" + vc.getReference().getName() + "</b><hr>"
                + seqLen + " bp</html>";
    }
    private boolean inDrag = false;
    private int dragStart = -1;

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        //System.err.println("button is " + e.getButton());
        //  if (e.getButton() == MouseEvent.BUTTON1) {
        inDrag = true;
        int x = e.getX();
        dragStart = px2bp(x);
        setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
        //System.err.println("drag started at " + dragStart);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        inDrag = false;
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        //
        if (inDrag && dragStart != -1) {
            int posInRef = px2bp(e.getX());
            int offset = posInRef - dragStart + 1;
            dragStart = posInRef;
            vc.setBounds(FastMath.max(0, bounds[0] - offset), FastMath.min(vc.getReference().getLength() - 1, bounds[1] - offset));
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // nop
    }

//    private void fetchMaxCoverage() {
//        if (maxCoverage != 0) {
//            return;
//        }
//        maxCoverage = vc.getMaxCoverage();
//    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setMaximumSize(new java.awt.Dimension(32767, 50));
        setMinimumSize(new java.awt.Dimension(5, 50));
        setPreferredSize(new java.awt.Dimension(852, 50));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 852, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 110, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    public void process(SortedSet<MappedSequenceI> mapped) {
        // fetch features
        Set<Arrow> newData = new HashSet<>();
        try {
            for (RegionI r : vc.getRegions()) {
                newData.add(r2a(r));
            }
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
        }

        synchronized (regs) {
            regs.clear();
            regs.addAll(newData);
        }
        repaint();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case ViewController.MIN_IDENTITY_CHANGE:
            case ViewController.MAX_COV_CHANGE:
                return;
        }
        super.propertyChange(evt);
    }

}
