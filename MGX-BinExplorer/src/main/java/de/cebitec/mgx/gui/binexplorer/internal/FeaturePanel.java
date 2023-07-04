/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.binexplorer.internal;

import de.cebitec.mgx.api.model.assembly.AssembledRegionI;
import de.cebitec.mgx.common.RegionType;
import de.cebitec.mgx.gui.swingutils.Arrow;
import de.cebitec.mgx.gui.swingutils.ShapeBase;
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
import java.io.Serial;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.ToolTipManager;
import org.apache.commons.math3.util.FastMath;

/**
 *
 * @author sjaenick
 */
public class FeaturePanel extends PanelBase<ContigViewController> implements MouseListener, MouseMotionListener {

    @Serial
    private static final long serialVersionUID = 1L;

    private final static int FRAME_VOFFSET = 20;
    private final static int[] frameOffsets = new int[]{
        10 + -1 * FRAME_VOFFSET * -3,
        10 + -1 * FRAME_VOFFSET * -2,
        10 + -1 * FRAME_VOFFSET * -1,
        -1 * FRAME_VOFFSET * 1,
        -1 * FRAME_VOFFSET * 2,
        -1 * FRAME_VOFFSET * 3};
    private final List<Arrow<AssembledRegionI>> regs = new ArrayList<>();
    private final static Color lighterGray = new Color(210, 210, 210);
    //
    private final NumberFormat nf;

    /**
     * Creates new form FeaturePanel
     */
    public FeaturePanel(ContigViewController vc, NumberFormat numberFormat) {
        super(vc, true);
        nf = numberFormat;
        super.setMinimumSize(new Dimension(500, 175));
        super.setPreferredSize(new Dimension(5000, 175));
        //setMaximumSize(new Dimension(5000, 80));
        super.addMouseListener(this);
        super.addMouseMotionListener(this);
        ToolTipManager.sharedInstance().registerComponent(this);
        ToolTipManager.sharedInstance().setDismissDelay(5000);
        super.repaint();
    }

    public void clear() {
        regs.clear();
        super.repaint();
    }

    @Override
    public void draw(Graphics2D g2) {
//        // clear image
//        g2.setColor(getBackground());
//        g2.clearRect(0, 0, getWidth(), getHeight());

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

        if (bounds != null) {
            int firstpos = bounds[0];
            while (firstpos % separate != 0) {
                firstpos++;
            }
            for (int i = firstpos; i < bounds[1]; i += separate) {
                float pos = bp2px(i);
                g2.drawLine((int) pos, midY - 3, (int) pos, midY + 3);
                String text1 = String.valueOf(i);
                g2.drawString(text1, (int) pos - textWidth(g2, text1) / 2, midY + 13);
                //}
            }
        }

        synchronized (regs) {

            /*
             * create shadow effects
             */
            Composite oldComp = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));

            g2.setColor(Color.LIGHT_GRAY);
            for (ShapeBase r : regs) {
                Shape shadow = AffineTransform.getTranslateInstance(4, 3).createTransformedShape(r);
                g2.fill(shadow);
            }
            g2.setColor(lighterGray);
            for (ShapeBase r : regs) {
                Shape shadow = AffineTransform.getTranslateInstance(4, 3).createTransformedShape(r);
                g2.draw(shadow);
            }

            g2.setComposite(oldComp);

            // draw arrows (and borders)
            Color curColor = Color.RED;
            g2.setColor(curColor);

            AssembledRegionI selectedGene = vc.getSelectedRegion();

            // draw the region shapes
            for (Arrow<AssembledRegionI> r : regs) {
                Color c = r.getColor();
                if (r.getObject().equals(selectedGene)) {
                    c = Color.ORANGE;
                }
                if (!curColor.equals(c)) {
                    g2.setColor(c);
                    curColor = c;
                }
                g2.fill(r);
            }

            // draw region borders
            g2.setColor(Color.DARK_GRAY);
            for (ShapeBase r : regs) {
                g2.draw(r);
            }
        }
    }

    @Override
    public boolean update() {

        List<Arrow<AssembledRegionI>> newData = new ArrayList<>();
        if (!vc.isClosed()) {
            // fetch features
            int midY = getHeight() / 2;
            for (AssembledRegionI r : vc.getRegions()) {
                newData.add(r2a(r, midY));
            }
        }

        synchronized (regs) {
            regs.clear();
            regs.addAll(newData);
            // presort by color to avoid color switching in draw() since 
            // color switching seems to be somewhat slow
            Collections.sort(regs);
        }

        return true;
    }

    private Arrow<AssembledRegionI> r2a(final AssembledRegionI r, int midY) {
        float pos0 = bp2px(r.getStart() - 1);
        float pos1 = bp2px(r.getStop() - 1);

        if (vc.isClosed()) {
            return null;
        }

        int frame = r.getFrame();
        String framePrefix = frame > 0 ? "+" : "";
        String toolTip = "<html>"
                + "<b>Gene: </b>" + vc.getReferenceName() + "_" + r.getId()
                + "<br><hr><br>"
                + "Type: " + r.getType().toString() + "<br>"
                + "Location: " + nf.format(r.getStart()) + "-"
                + nf.format(r.getStop()) + "<br>"
                + "Frame: " + framePrefix + frame + "<br>"
                + "Length: " + nf.format(r.getLength()) + " nt<br></html>";

        // FIXME: handle region types other than CDS here
        if (r.getType() != RegionType.CDS) {
            throw new RuntimeException("Unhandled region type " + r.getType());
        }

        if (frame < 0) {
            int frameOffset = frameOffsets[frame + 3];
            return new Arrow<>(r, toolTip, pos1, midY + frameOffset - Arrow.HALF_HEIGHT, pos0 - pos1);
        } else {
            int frameOffset = frameOffsets[frame + 2];
            return new Arrow<>(r, toolTip, pos0, midY + frameOffset - Arrow.HALF_HEIGHT, pos1 - pos0);
        }

    }

    @Override
    public String getToolTipText(MouseEvent m) {
        Point loc = m.getPoint();
        if (regs != null) {
            for (Arrow<AssembledRegionI> a : regs) {
                if (a.getBounds().contains(loc)) {
                    return a.getToolTipText();
                }
            }
        }
        if (vc.getReferenceName() == null) {
            return "<html><b>Contig: </b>N/A</html>";
        }
        return "<html><b>Contig: </b>" + vc.getReferenceName() + "<br><hr><br>"
                + "Length: "
                + nf.format(vc.getReferenceLength()) + " bp</html>";

    }

    private boolean inDrag = false;
    private int dragStart = -1;

    @Override
    public void mouseClicked(MouseEvent e) {
        Point loc = e.getPoint();
        if (regs != null) {
            for (Arrow<AssembledRegionI> a : regs) {
                if (a.getBounds().contains(loc)) {
                    vc.selectRegion(a.getObject());
                    repaint();
                }
            }
        }
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
            int offset = posInRef - dragStart + 10;
            dragStart = posInRef;
            vc.setBounds(FastMath.max(0, bounds[0] - offset), FastMath.min(vc.getReferenceLength() - 1, bounds[1] - offset));
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // nop
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case ContigViewController.BIN_SELECTED:
                // ignore
                break;
            default:
                super.propertyChange(evt);
        }
    }

}
