/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping.panel;

import de.cebitec.mgx.gui.mapping.shapes.Arrow;
import de.cebitec.mgx.gui.datamodel.Region;
import de.cebitec.mgx.gui.mapping.ViewController;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.util.HashSet;
import java.util.Set;
import javax.swing.ToolTipManager;

/**
 *
 * @author sjaenick
 */
public class FeaturePanel extends PanelBase implements MouseListener, MouseMotionListener {

    private final static int FRAME_VOFFSET = 20;
    private final static int[] frameOffsets = new int[]{
        12 + -1 * FRAME_VOFFSET * -3,
        12 + -1 * FRAME_VOFFSET * -2,
        12 + -1 * FRAME_VOFFSET * -1,
        -1 * FRAME_VOFFSET * 1,
        -1 * FRAME_VOFFSET * 2,
        -1 * FRAME_VOFFSET * 3};
    private Set<Arrow> regs = null;
    //private Set<Area> coverage = null;
    private final static Color lighterGray = new Color(210, 210, 210);
    private int[] previewBounds = null;

    /**
     * Creates new form FeaturePanel
     */
    public FeaturePanel(ViewController vc) {
        super(vc, true);
        initComponents();
        setMaximumSize(new Dimension(5000, 45));
        setPreferredSize(new Dimension(150, 45));
        setMinimumSize(new Dimension(150, 45));
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        ToolTipManager.sharedInstance().registerComponent(this);
        ToolTipManager.sharedInstance().setDismissDelay(5000);

        doLayout();
        updateUI();
        invalidate();
        this.revalidate();
    }

    @Override
    void draw(Graphics2D g2) {
        //drawCoverage(g2);

        g2.setColor(Color.DARK_GRAY);
        g2.drawLine(0, midY, getWidth(), midY); // midline
        g2.setFont(new Font(g2.getFont().getFontName(), Font.PLAIN, 10));
        int textHeight = -1 + g2.getFontMetrics(g2.getFont()).getHeight() / 2;
        int textWidth = g2.getFontMetrics(g2.getFont()).stringWidth("+3");
        g2.drawString("-1", 0, midY + frameOffsets[0] + textHeight);
        g2.drawString("-2", 0, midY + frameOffsets[1] + textHeight);
        g2.drawString("-3", 0, midY + frameOffsets[2] + textHeight);
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
        while (intervalLen / separate > 10) {
            separate += 500;
        }
        for (int i = bounds[0]; i < bounds[1]; i++) {
            if (i % separate == 0) {
                double pos = bp2px(i);
                g2.drawLine((int) pos, midY - 3, (int) pos, midY + 3);
                String text1 = String.valueOf(i);
                g2.drawString(text1, (int) pos - textWidth(g2, text1) / 2, midY + 13);
            }
        }

        if (regs == null) {
            return;
        }

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
    boolean update() {

        if (midY == 0) {
            return false;
        }

        // fetch features
        Set<Arrow> newData = new HashSet<>();
        for (Region r : vc.getRegions(bounds[0], bounds[1])) {
            newData.add(r2a(r));
        }
        regs = newData;
        
        return true;

        // 
//        Set<Area> ret = new HashSet<>();
//        int[] cove = new int[bounds[1]-bounds[0]+1];
//        vc.getCoverage(bounds[0], bounds[1], cove);
//        assert cove.length == bounds[1] - bounds[0] + 1;
//        int baseY = midY - 1;
//        int pos = bounds[0];
//
//        GeneralPath gp = null;
//        double[] gpStart = new double[2];
//        double[] lastPoint = new double[2];
//        for (int cov : cove) {
//            if (cov == 0) {
//                if (gp != null) {
//                    gp.lineTo(lastPoint[0], baseY); // down to center line
//                    gp.lineTo(gpStart[0], gpStart[1]); // close shape
//                    lastPoint[0] = gpStart[0];
//                    lastPoint[1] = gpStart[1];
//                    ret.add(new Area(gp));
//                    gp = null;
//                }
//            } else {
//                // we have some coverage..
//                double drawPos = bp2px(pos);
//                double covScale = (midY * 1d) / (vc.getMaxCoverage() * 1d);
//                double covPos = midY - (cov * covScale);
//
//                if (gp == null) {
//                    gp = new GeneralPath();
//                    gpStart[0] = drawPos; // remember positions so we can close the shape later
//                    gpStart[1] = baseY;
//                    gp.moveTo(drawPos, baseY);
//                    lastPoint[0] = drawPos;
//                    lastPoint[1] = baseY;
//                } else {
//                    // add a new point if distance >= 3px
//                    if (Math.abs(lastPoint[0] - drawPos) > 4 || Math.abs(lastPoint[1] - covPos) > 4) {
//                        gp.lineTo(drawPos, covPos);
//                        lastPoint[0] = drawPos;
//                        lastPoint[1] = covPos;
//                    }
//                }
//                //ret.add(cov2p(pos, i));
//            }
//            pos++;
//        }
//        if (gp != null) {
//            gp.lineTo(lastPoint[0], baseY); // down to center line
//            gp.lineTo(gpStart[0], gpStart[1]); // close shape
//            ret.add(new Area(gp));
//            gp = null;
//        }
//        coverage = ret;
    }

    private Arrow r2a(final Region r) {
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
        return null;
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
        //System.err.println("drag started at " + dragStart);
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
        //
        if (inDrag && dragStart != -1) {
            int posInRef = px2bp(e.getX());
            int offset = posInRef - dragStart + 1;
            dragStart = posInRef;
            vc.setBounds(Math.max(0, bounds[0] - offset), Math.min(vc.getReference().getLength() - 1, bounds[1] - offset));
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
}
