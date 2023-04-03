/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping.panel;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.MappedSequenceI;
import de.cebitec.mgx.api.model.ReferenceRegionI;
import de.cebitec.mgx.gui.mapping.impl.ViewController;
import de.cebitec.mgx.gui.mapping.impl.ViewControllerI;
import de.cebitec.mgx.gui.swingutils.Arrow;
import de.cebitec.mgx.gui.swingutils.Rectangle;
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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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
public class FeaturePanel extends PanelBase<ViewControllerI> implements MouseListener, MouseMotionListener {

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
    private final List<ShapeBase> regs = new ArrayList<>();
    private final static Color lighterGray = new Color(210, 210, 210);
    //
    private final static NumberFormat nf = NumberFormat.getInstance(Locale.US);

    /**
     * Creates new form FeaturePanel
     */
    public FeaturePanel(ViewControllerI vc) {
        super(vc, true);
        super.setMinimumSize(new Dimension(500, 175));
        super.setPreferredSize(new Dimension(5000, 175));
        //setMaximumSize(new Dimension(5000, 80));
        super.addMouseListener(this);
        super.addMouseMotionListener(this);
        ToolTipManager.sharedInstance().registerComponent(this);
        ToolTipManager.sharedInstance().setDismissDelay(5000);
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
        //int[] bounds = vc.getBounds();
        int firstpos = bounds[0];
        while (firstpos % separate != 0) {
            firstpos++;
        }
        for (int i = firstpos; i < bounds[1]; i += separate) {
            //if (i % separate == 0) {
            float pos = bp2px(i);
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

            // draw the region shapes
            for (ShapeBase r : regs) {
                if (!curColor.equals(r.getColor())) {
                    g2.setColor(r.getColor());
                    curColor = r.getColor();
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
        // fetch features
        List<ShapeBase> newData = new ArrayList<>();
        try {
            int midY = getHeight() / 2;
            for (ReferenceRegionI r : vc.getRegions()) {
                newData.add(r2a(r, midY));
            }
        } catch (MGXException ex) {
            if (vc.isClosed()) {
                regs.clear();
                return true;
            }
            Exceptions.printStackTrace(ex);
        }

        synchronized (regs) {
            regs.clear();
            regs.addAll(newData);
            Collections.sort(regs);
        }

        return true;
    }

    private ShapeBase r2a(final ReferenceRegionI r, int midY) {
        //int midY = getHeight() / 2;
        float pos0 = bp2px(r.getStart() - 1);
        float pos1 = bp2px(r.getStop() - 1);

        String type = r.getType() != null
                ? r.getType().toString() + ": "
                : "";
        String framePrefix = r.getFrame() > 0 ? "+" : "";
        String toolTip = "<html><b>" + type + r.getName() + "</b><hr>"
                + "Location: " + nf.format(r.getStart()) + "-"
                + nf.format(r.getStop()) + "<br>"
                + "Frame: " + framePrefix + r.getFrame() + "<br><br>"
                + r.getDescription() + "</html>";

        switch (r.getType()) {
            case CDS:
                if (r.getFrame() < 0) {
                    int frameOffset = frameOffsets[r.getFrame() + 3];
                    return new Arrow<>(r, toolTip, pos1, midY + frameOffset - Arrow.HALF_HEIGHT, pos0 - pos1);
                } else {
                    int frameOffset = frameOffsets[r.getFrame() + 2];
                    return new Arrow<>(r, toolTip, pos0, midY + frameOffset - Arrow.HALF_HEIGHT, pos1 - pos0);
                }
            case RRNA:
            case TRNA:
                if (r.getFrame() < 0) {
                    int frameOffset = frameOffsets[r.getFrame() + 3];
                    Rectangle rect = new Rectangle(toolTip, pos1, midY + frameOffset - Rectangle.HALF_HEIGHT, pos0 - pos1);
                    return rect;
                } else {
                    int frameOffset = frameOffsets[r.getFrame() + 2];
                    Rectangle rect = new Rectangle(toolTip, pos0, midY + frameOffset - Rectangle.HALF_HEIGHT, pos1 - pos0);
                    return rect;
                }
            default:
                System.err.println("Unhandled region type " + r.getType().toString() + ", using default shape");
                if (r.getFrame() < 0) {
                    int frameOffset = frameOffsets[r.getFrame() + 3];
                    Rectangle rect = new Rectangle(toolTip, pos1, midY + frameOffset - Rectangle.HALF_HEIGHT, pos0 - pos1);
                    return rect;
                } else {
                    int frameOffset = frameOffsets[r.getFrame() + 2];
                    Rectangle rect = new Rectangle(toolTip, pos0, midY + frameOffset - Rectangle.HALF_HEIGHT, pos1 - pos0);
                    return rect;
                }
        }
    }

    @Override
    public String getToolTipText(MouseEvent m) {
        Point loc = m.getPoint();
        if (regs != null) {
            for (ShapeBase a : regs) {
                if (a.getBounds().contains(loc)) {
                    return a.getToolTipText();
                }
            }
        }
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        String seqLen;
        seqLen = formatter.format(getReferenceLength());
        return "<html><b>" + vc.getReferenceName() + "</b><hr>"
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
            vc.setBounds(FastMath.max(0, bounds[0] - offset), FastMath.min(getReferenceLength() - 1, bounds[1] - offset));
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
    public void process(SortedSet<MappedSequenceI> mapped) {
        // fetch features
        Set<ShapeBase> newData = new HashSet<>();
        try {
            int midY = getHeight() / 2;
            for (ReferenceRegionI r : vc.getRegions()) {
                newData.add(r2a(r, midY));
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
