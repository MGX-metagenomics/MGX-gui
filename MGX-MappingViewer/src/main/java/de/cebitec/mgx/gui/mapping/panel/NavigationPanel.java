/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping.panel;

import de.cebitec.mgx.gui.cache.IntIterator;
import de.cebitec.mgx.gui.mapping.ViewController;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.ToolTipManager;
import org.apache.commons.math3.util.FastMath;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;

/**
 *
 * @author sjaenick
 */
public class NavigationPanel extends PanelBase implements MouseListener, MouseMotionListener {

    private final static int CAPTURE_DIST = 3; // px
    private final int refLength;
    private int[] previewBounds = null;
    private int[] offSet = null;
    private double scaleFactor;
    private final Set<Area> coverage = new HashSet<>();
    private long maxCov = -1;

    /**
     * Creates new form NavigationPanel
     */
    public NavigationPanel(ViewController vc) {
        super(vc, true);
        refLength = vc.getReference().getLength();
        initComponents();
        setPreferredSize(new Dimension(5000, 35));
        setMaximumSize(new Dimension(5000, 35));
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        ToolTipManager.sharedInstance().registerComponent(this);
        ToolTipManager.sharedInstance().setDismissDelay(5000);
        //update();
        repaint();
    }

    @Override
    void draw(Graphics2D g2) {

        drawCoverage(g2);

        g2.setColor(Color.DARK_GRAY);

        g2.drawLine(0, midY, getWidth(), midY); // midline

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
            double[] scaledPreview = getScaledValues(previewBounds);
            float dash1[] = {5.0f};
            BasicStroke dashed = new BasicStroke(1.0f,
                    BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER,
                    10.0f, dash1, 0.0f);

            Stroke oldStroke = g2.getStroke();
            g2.setStroke(dashed);
            g2.setColor(Color.BLACK);
            Shape curScope = new Rectangle2D.Double(scaledPreview[0], 0, scaledPreview[1] - scaledPreview[0] + 1, getHeight() - 1);
            g2.draw(curScope);
            g2.setStroke(oldStroke);
        }

        Composite oldcomp = g2.getComposite();
        AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f);

        // draw box indicating current scope
        double[] scaledBounds = getScaledValues(vc.getBounds());
        g2.setComposite(ac);
        g2.setColor(Color.red);
        Shape curScope = new Rectangle2D.Double(scaledBounds[0], 0, scaledBounds[1] - scaledBounds[0] + 1, getHeight() - 1);
        g2.fill(curScope);
        //g2.fillRect(scaledBounds[0], 0, scaledBounds[1] - scaledBounds[0] + 1, getHeight() - 1);
        g2.setComposite(oldcomp);

    }

    private void drawCoverage(Graphics2D g2) {
        Composite oldcomp = g2.getComposite();
        AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f);
        g2.setComposite(ac);
        g2.setColor(Color.LIGHT_GRAY);

        for (Area l : coverage) {
            g2.fill(l);
        }
        g2.setColor(Color.DARK_GRAY);
        for (Area l : coverage) {
            g2.draw(l);
        }
        g2.setComposite(oldcomp);
    }

    private double[] getScaledValues(int[] in) {
        double[] ret = new double[in.length];
        int pos = 0;
        for (int i : in) {
            double f = bp2px(i);
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
    public void mouseClicked(MouseEvent e) {
        if (e.isConsumed()) {
            return;
        }
        int posInRef = px2bp(e.getX());
        e.consume();

        int[] oldBounds = vc.getBounds();
        int len = oldBounds[1] - oldBounds[0] + 1;
        vc.setBounds(posInRef, FastMath.min(posInRef + len, vc.getReference().getLength() - 1));
    }

    private int dragType = 0;  // 1=left, 2=mid, 3=right

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.isConsumed()) {
            return;
        }
        double[] scaledBounds = getScaledValues(bounds);

        int x = e.getX();
        int posInRef = px2bp(e.getX());

        int distToStart = FastMath.abs(bounds[0] - posInRef);  // in bp
        int distToEnd = FastMath.abs(bounds[1] - posInRef);
        int distToMid = FastMath.abs((bounds[0] + (bounds[1] - bounds[0] + 1) / 2) - posInRef);

        int scaledDistToStart = (int) FastMath.abs(scaledBounds[0] - x);  // in px
        int scaledDistToEnd = (int) FastMath.abs(scaledBounds[1] - x);

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

        int posInRef = px2bp(e.getX());

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
                    previewBounds[0] = FastMath.max(0, previewBounds[0]);
                    previewBounds[1] = FastMath.min(vc.getReference().getLength() - 1, previewBounds[1]);
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

        int posInRef = px2bp(e.getX());

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

    @Override
    protected double bp2px(int i) {
        return i * 1d / scaleFactor;
    }

    @Override
    protected int px2bp(double d) {
        return (int) (d * scaleFactor);
    }

    @Override
    boolean update() {
        scaleFactor = 1d * refLength / getWidth();
        midY = getHeight() / 2;

        if (coverage.isEmpty()) {
            generateCoverage();
        }
        return true;
    }

    private void generateCoverage() {

        if (maxCov != -1) {
            return;
        }

        final ProgressHandle ph = ProgressHandleFactory.createHandle("Fetching coverage data");
        ph.start();

        maxCov = vc.getMaxCoverage();

        IntIterator covIter = vc.getCoverageIterator();
        List<Area> ret = new ArrayList<>();

        int baseY = getHeight() - 1;
        int pos = 0;
        int max = -1;
        double covScale = (getHeight() * 1d) / (FastMath.log(maxCov * 1d));

        GeneralPath gp = null;
        double[] gpStart = new double[2];
        double[] lastPoint = new double[2];
        while (covIter.hasNext()) {
            int cov = covIter.next();
            if (cov == maxCov) {
                System.err.println("found max at " + pos);
            }
            if (cov > max) {
                max = cov;
            }
            if (cov == 0) {
                if (gp != null) {
                    gp.lineTo(lastPoint[0], baseY); // down to bottom line
                    gp.lineTo(gpStart[0], gpStart[1]); // close shape
                    gp.closePath();
                    lastPoint[0] = gpStart[0];
                    lastPoint[1] = gpStart[1];
                    ret.add(new Area(gp));
                    gp = null;
                }
            } else {
                // we have some coverage..
                double drawPos = bp2px(pos);
                double covPos = baseY - (FastMath.log(cov) * covScale);

                if (gp == null) {
                    gp = new GeneralPath();
                    gpStart[0] = drawPos; // remember positions so we can close the shape later
                    gpStart[1] = baseY;
                    gp.moveTo(drawPos, baseY);

                    gp.lineTo(drawPos, covPos);
                    lastPoint[0] = drawPos;
                    lastPoint[1] = baseY;
                } else {
                    // add a new point if distance >= 3px
                    if (FastMath.abs(lastPoint[0] - drawPos) > 3 || FastMath.abs(lastPoint[1] - covPos) > 3) {
                        gp.lineTo(drawPos, covPos);
                        lastPoint[0] = drawPos;
                        lastPoint[1] = covPos;
                    }
                }
            }
            pos++;
        }
        if (gp != null) {
            gp.lineTo(lastPoint[0], baseY); // down to bottom line
            gp.lineTo(gpStart[0], gpStart[1]); // close shape
            gp.closePath();
            ret.add(new Area(gp));
            gp = null;
        }

        System.err.println("saw max " + max + " expected" + maxCov);

        synchronized (coverage) {
            coverage.clear();
            coverage.addAll(ret);
        }

        ph.finish();

        repaint();
    }

    @Override
    public String getToolTipText(MouseEvent m) {
        int bpPos = px2bp(m.getX());
        int[] buf = new int[]{0};
        vc.getCoverage(bpPos, bpPos, buf);
        return "<html>Position: " + bpPos + "<br>Coverage: "
                + buf[0] + "</html>";
    }
}
