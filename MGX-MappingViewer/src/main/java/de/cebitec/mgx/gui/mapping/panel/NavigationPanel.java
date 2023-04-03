/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping.panel;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.gui.cache.IntIterator;
import de.cebitec.mgx.gui.mapping.impl.ViewControllerI;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.Serial;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import javax.swing.ToolTipManager;
import org.apache.commons.math3.util.FastMath;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.util.Exceptions;

/**
 *
 * @author sjaenick
 */
public class NavigationPanel extends PanelBase<ViewControllerI> implements MouseListener, MouseMotionListener {

    @Serial
    private static final long serialVersionUID = 1L;
    
    private final static int CAPTURE_DIST = 3; // px
    private float scaleFactor;
    private int[] previewBounds = null;
    private int[] offSet = null;
    // back image
    private BufferedImage coverageImage = null;
    private final int BACKIMAGE_WIDTH = 2500;
    private final int BACKIMAGE_HEIGHT = 100;
    //
    private BufferedImage scaledImage = null;

    private long maxCov = -1;
    //
    private final static float dash1[] = {5.0f};
    private final BasicStroke dashed = new BasicStroke(1.0f,
            BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_MITER,
            10.0f, dash1, 0.0f);

    /**
     * Creates new form NavigationPanel
     */
    public NavigationPanel(final ViewControllerI vc) {
        super(vc, true);
        super.setPreferredSize(new Dimension(5000, 35));
        super.setMaximumSize(new Dimension(5000, 35));

        super.addMouseListener(this);
        super.addMouseMotionListener(this);
        ToolTipManager.sharedInstance().registerComponent(this);
        ToolTipManager.sharedInstance().setDismissDelay(5000);

        scaleFactor = 1f * getReferenceLength() / getWidth();

        super.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);

                scaleFactor = 1f * getReferenceLength() / getWidth();

                if (getHeight() == 0 || getWidth() == 0) {
                    return;
                }

                if (scaledImage != null && (scaledImage.getWidth() != getWidth() || scaledImage.getHeight() != getHeight())) {
                    scaledImage = null; // invalidate
                }

                if (coverageImage != null && scaledImage == null) {
                    createScaledImage();
                }
                repaint();
            }

        });
        super.setBorder(javax.swing.BorderFactory.createLineBorder(Color.BLACK));

        generateCoverageImage();
    }

    @Override
    public void draw(Graphics2D g2) {

        if (scaledImage != null) {
            g2.drawImage(scaledImage, 0, 0, this);
        }

        if (previewBounds != null) {
            Stroke oldStroke = g2.getStroke();
            g2.setStroke(dashed);
            g2.setColor(Color.BLACK);
            float[] scaledPreviewBounds = getScaledValues(previewBounds);
            Rectangle2D.Float currentPreviewScope = new Rectangle2D.Float(scaledPreviewBounds[0], 0, scaledPreviewBounds[1] - scaledPreviewBounds[0] + 1, getHeight() - 1);
            g2.draw(currentPreviewScope);
            g2.setStroke(oldStroke);
        }

        // draw box indicating current scope
        if (bounds != null) {
            Composite oldcomp = g2.getComposite();
            AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f);
            g2.setComposite(ac);
            g2.setColor(Color.red);
            float[] scaledBounds = getScaledValues(bounds);
            Rectangle2D.Float currentScope = new Rectangle2D.Float(scaledBounds[0], 0, scaledBounds[1] - scaledBounds[0] + 1, getHeight() - 1);
            g2.fill(currentScope);
            g2.setComposite(oldcomp);
        }
        drawAxis(g2);
    }

    private float[] getScaledValues(int[] in) {
        if (in == null) {
            return null;
        }
        float[] ret = new float[in.length];
        int pos = 0;
        for (int i : in) {
            float f = bp2px(i);
            ret[pos++] = f;
        }
        return ret;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.isConsumed()) {
            return;
        }
        int posInRef = px2bp(e.getX());
        e.consume();
        int len = bounds[1] - bounds[0] + 1;
        vc.setBounds(posInRef, FastMath.min(posInRef + len, getReferenceLength() - 1));
    }

    private int dragType = 0;  // 1=left, 2=mid, 3=right

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.isConsumed()) {
            return;
        }

        //int[] bounds = vc.getBounds();
        float[] scaledBounds = getScaledValues(bounds);

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

        //int[] bounds = vc.getBounds();
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
                    previewBounds[1] = FastMath.min(getReferenceLength() - 1, previewBounds[1]);
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

        //int[] bounds1 = vc.getBounds();
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
    protected float bp2px(int i) {
        return 1f * i / scaleFactor;
    }

    @Override
    protected int px2bp(float d) {
        return (int) (d * scaleFactor);
    }

    @Override
    public boolean update() {
        scaleFactor = 1f * getReferenceLength() / getWidth();
        return true;
    }

    private void generateCoverageImage() {

        SwingWorker<BufferedImage, Void> worker = new SwingWorker<BufferedImage, Void>() {

            @Override
            protected BufferedImage doInBackground() throws Exception {
                BufferedImage backImage;
                if (getWidth() > 0 && getHeight() > 0) {
                    backImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
                } else {
                    backImage = new BufferedImage(BACKIMAGE_WIDTH, BACKIMAGE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
                }
                Graphics2D g2 = backImage.createGraphics();
                g2.setBackground(getBackground());
                g2.fillRect(0, 0, getWidth(), getHeight());

                if (useAntialiasing) {
                    g2.setRenderingHints(antiAlias);
                }

                Composite oldcomp = g2.getComposite();
                AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f);
                g2.setComposite(ac);
                plotCoverage(backImage);
                g2.setComposite(oldcomp);
                return backImage;
            }

            @Override
            protected void done() {
                super.done();
                try {
                    coverageImage = get();
                    scaledImage = null;
                    createScaledImage();
                } catch (InterruptedException | ExecutionException ex) {
                    if (!vc.isClosed()) {
                        Exceptions.printStackTrace(ex);
                    }
                } finally {
                    repaint();
                }
            }
        };
        worker.execute();
    }

    private void plotCoverage(final BufferedImage targetImage) throws MGXException {

        if (maxCov != -1) {
            return;
        }
        ProgressHandle ph = ProgressHandle.createHandle("Fetching coverage data");
        ph.start(getReferenceLength());

        maxCov = vc.getMaxCoverage();

        float covScaleX = 1f * getReferenceLength() / targetImage.getWidth();
        float covScaleY = (float) ((targetImage.getHeight() * 1f) / (FastMath.log(maxCov * 1d)));

        Graphics2D g2 = targetImage.createGraphics();

        int baseY = targetImage.getHeight() - 1;
        int pos = 0;

        GeneralPath gp = null;
        float[] gpStart = new float[2];
        float[] lastPoint = new float[2];

        try {
            IntIterator covIter = vc.getCoverageIterator();
            while (covIter.hasNext()) {
                int cov = covIter.next();

                // ViewController might change to 'closed' state e.g. if the
                // topcomponent is closed with the swingworker still busy.
                if (vc.isClosed()) {
                    return;
                }

                if (cov == 0) {
                    if (gp != null) {
                        gp.lineTo(lastPoint[0], baseY); // down to bottom line
                        gp.lineTo(gpStart[0], gpStart[1]); // close shape
                        gp.closePath();
                        drawArea(g2, new Area(gp));
                        lastPoint[0] = gpStart[0];
                        lastPoint[1] = gpStart[1];
                        gp = null;
                    }
                } else {
                    // we have some coverage..
                    float drawPos = pos * 1f / covScaleX;
                    float covPos = (float) (baseY - (covScaleY * FastMath.log(cov)));

                    if (gp == null) {
                        gp = new GeneralPath();
                        gpStart[0] = drawPos; // remember positions so we can close the shape later
                        gpStart[1] = baseY;
                        gp.moveTo(drawPos, baseY);

                        gp.lineTo(drawPos, covPos);
                        lastPoint[0] = drawPos;
                        lastPoint[1] = baseY;
                    } else // add a new point if distance >= 3px
                    if (FastMath.abs(lastPoint[0] - drawPos) > 3 || FastMath.abs(lastPoint[1] - covPos) > 3) {
                        gp.lineTo(drawPos, covPos);
                        lastPoint[0] = drawPos;
                        lastPoint[1] = covPos;
                    }
                }

                ph.progress(pos);
                pos++;
            }

            if (gp != null) {
                gp.lineTo(lastPoint[0], baseY); // down to bottom line
                gp.lineTo(gpStart[0], gpStart[1]); // close shape
                gp.closePath();
                drawArea(g2, new Area(gp));
                gp = null;
            }
        } finally {
            ph.finish();
            repaint();
        }
    }

    @Override
    public void print(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setBackground(Color.WHITE);
        g2.clearRect(0, 0, getWidth(), getHeight());
        super.paintBorder(g);

        try {
            maxCov = vc.getMaxCoverage();
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
        }

        float covScaleX = 1f * getReferenceLength() / getWidth();
        float covScaleY = (float) ((getHeight() * 1f) / (FastMath.log(maxCov * 1d)));

        int baseY = getHeight() - 1;
        int pos = 0;

        GeneralPath gp = null;
        float[] gpStart = new float[2];
        float[] lastPoint = new float[2];

        try {
            IntIterator covIter = vc.getCoverageIterator();
            while (covIter.hasNext()) {
                int cov = covIter.next();

                // ViewController might change to 'closed' state e.g. if the
                // topcomponent is closed with the swingworker still busy.
                if (vc.isClosed()) {
                    return;
                }

                if (cov == 0) {
                    if (gp != null) {
                        gp.lineTo(lastPoint[0], baseY); // down to bottom line
                        gp.lineTo(gpStart[0], gpStart[1]); // close shape
                        gp.closePath();
                        drawArea(g2, new Area(gp));
                        lastPoint[0] = gpStart[0];
                        lastPoint[1] = gpStart[1];
                        gp = null;
                    }
                } else {
                    // we have some coverage..
                    float drawPos = pos * 1f / covScaleX;
                    float covPos = (float) (baseY - (covScaleY * FastMath.log(cov)));

                    if (gp == null) {
                        gp = new GeneralPath();
                        gpStart[0] = drawPos; // remember positions so we can close the shape later
                        gpStart[1] = baseY;
                        gp.moveTo(drawPos, baseY);

                        gp.lineTo(drawPos, covPos);
                        lastPoint[0] = drawPos;
                        lastPoint[1] = baseY;
                    } else // add a new point if distance >= 3px
                    if (FastMath.abs(lastPoint[0] - drawPos) > 3 || FastMath.abs(lastPoint[1] - covPos) > 3) {
                        gp.lineTo(drawPos, covPos);
                        lastPoint[0] = drawPos;
                        lastPoint[1] = covPos;
                    }
                }

                pos++;
            }

            if (gp != null) {
                gp.lineTo(lastPoint[0], baseY); // down to bottom line
                gp.lineTo(gpStart[0], gpStart[1]); // close shape
                gp.closePath();
                drawArea(g2, new Area(gp));
                gp = null;
            }
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
        }

        // draw box indicating current scope
        if (bounds != null) {
            Composite oldcomp = g2.getComposite();
            AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f);
            g2.setComposite(ac);
            g2.setColor(Color.red);
            float[] scaledBounds = getScaledValues(bounds);
            Rectangle2D.Float currentScope = new Rectangle2D.Float(scaledBounds[0], 0, scaledBounds[1] - scaledBounds[0] + 1, getHeight() - 1);
            g2.fill(currentScope);
            g2.setComposite(oldcomp);
        }

        drawAxis(g2);

        g2.dispose();
    }

    @Override
    public String getToolTipText(MouseEvent m) {
        int bpPos = px2bp(m.getX());
        int[] buf = new int[]{0};
        try {
            vc.getCoverage(bpPos, bpPos, buf);
        } catch (MGXException ex) {
            buf = null;
        }
        String tmp = buf != null ? String.valueOf(buf[0]) : "unknown";

        NumberFormat nf = NumberFormat.getInstance(Locale.US);

        try {
            long genomicCoverage = vc.getGenomicCoverage();
            double refCoveragePct = 100d * genomicCoverage / vc.getReferenceLength();

            return "<html>" + vc.getReferenceName() + "<br>Position: " + nf.format(bpPos) + "<br>Coverage: "
                    + nf.format(Long.valueOf(tmp)) + "<br>Reference coverage: " + nf.format(vc.getGenomicCoverage()) + " bp ("
                    + String.format("%.2f", refCoveragePct)
                    + "%)</html>";
        } catch (MGXException ex) {
            return null;
        }
    }

    private void drawArea(Graphics2D g2, Area a) {
        g2.setColor(Color.LIGHT_GRAY);
        g2.fill(a);
        g2.setColor(Color.DARK_GRAY);
        g2.draw(a);
    }

    private void drawAxis(Graphics2D g2) {
        g2.setColor(Color.DARK_GRAY);

        if (defaultFont != null) {
            g2.setFont(defaultFont);
        }

        int midY = getHeight() / 2;

        g2.drawLine(0, midY, getWidth(), midY); // midline

        g2.drawLine(getWidth() / 4, midY - 3, getWidth() / 4, midY + 3);
        String text1 = String.valueOf(getReferenceLength() / 4);
        g2.drawString(text1, getWidth() / 4 - textWidth(g2, text1) / 2, midY + 13);

        g2.drawLine(getWidth() / 2, midY - 3, getWidth() / 2, midY + 3);
        String text2 = String.valueOf(getReferenceLength() / 2);
        g2.drawString(text2, getWidth() / 2 - textWidth(g2, text2) / 2, midY + 13);

        g2.drawLine(3 * getWidth() / 4, midY - 3, 3 * getWidth() / 4, midY + 3);
        String text3 = String.valueOf(3 * getReferenceLength() / 4);
        g2.drawString(text3, 3 * getWidth() / 4 - textWidth(g2, text3) / 2, midY + 13);
    }

    private void createScaledImage() {
        if (coverageImage != null) {
            if (coverageImage.getWidth() == getWidth() && coverageImage.getHeight() == getHeight()) {
                scaledImage = coverageImage;
            } else {
                BufferedImage resized = new BufferedImage(getWidth(), getHeight(), coverageImage.getType());
                Graphics2D g = resized.createGraphics();
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g.drawImage(coverageImage, 0, 0, getWidth(), getHeight(), 0, 0, coverageImage.getWidth(),
                        coverageImage.getHeight(), null);
                g.dispose();
                scaledImage = resized;
            }
        }
    }
}
