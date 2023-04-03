/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.binexplorer.internal;

import de.cebitec.mgx.api.misc.SequenceViewControllerI;
import de.cebitec.mgx.api.model.assembly.AssembledRegionI;
import de.cebitec.mgx.gui.pool.MGXPool;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.VolatileImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serial;
import java.util.Arrays;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import org.apache.commons.math3.util.FastMath;

/**
 *
 * @author sj
 */
public abstract class PanelBase<T extends SequenceViewControllerI> extends JComponent implements PropertyChangeListener, MouseWheelListener {

    @Serial
    private static final long serialVersionUID = 1L;

    protected final T vc;
    protected volatile int[] bounds;
    //private int maxCoverage = 0;
    private float scale;
    //
    protected static final RenderingHints antiAlias = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    protected final boolean useAntialiasing;
    //
    private final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    private final GraphicsConfiguration gc = ge.getDefaultScreenDevice().getDefaultConfiguration();

    public PanelBase(final T vc, boolean antiAlias) {
        super();
        super.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        //super.setDoubleBuffered(true);
        this.useAntialiasing = antiAlias;
        this.vc = vc;
        bounds = vc.getBounds();
        scale = 1f / (1f * vc.getIntervalLength() / super.getWidth());
        vc.addPropertyChangeListener(this);
        super.setBackground(Color.WHITE);
        super.setForeground(Color.DARK_GRAY);

        super.addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                if (getHeight() == 0 || !isEnabled()) {
                    return;
                }
                //bounds = vc.getBounds();
                scale = 1f / (1f * vc.getIntervalLength() / getWidth());
                createVolatileImage(Transparency.OPAQUE);

                Runnable updater = new Runnable() {
                    @Override
                    public void run() {
                        if (update()) {
                            repaint();
                        }
                    }
                };
                MGXPool.getInstance().submit(updater);

                super.componentResized(e);
            }
        });
        super.addMouseWheelListener(this);

    }

    public void dispose() {
        vc.removePropertyChangeListener(this);
    }

    private VolatileImage vimage = null;

    private void createVolatileImage(int transparency) {
        if (vimage != null) {
            vimage.flush();
        }
        vimage = gc.createCompatibleVolatileImage(getWidth(), getHeight(), transparency);
        if (vimage.validate(gc) == VolatileImage.IMAGE_INCOMPATIBLE) {
            createVolatileImage(transparency);
        }
    }

    protected Font defaultFont = null;

    @Override
    public void paint(Graphics g) {
        if (vimage == null) {
            createVolatileImage(Transparency.OPAQUE);
        }

//        long now = System.currentTimeMillis();
        // draw to back buffer image
        do {
            int valid = vimage.validate(gc);
            if (valid == VolatileImage.IMAGE_INCOMPATIBLE) {
                createVolatileImage(Transparency.OPAQUE);
            }
            Graphics2D g2 = vimage.createGraphics();

            // clear image
            g2.setBackground(getBackground());
            g2.clearRect(0, 0, vimage.getWidth(), vimage.getHeight());

            if (getHeight() > 0) {
                if (useAntialiasing) {
                    g2.setRenderingHints(antiAlias);
                }
                if (defaultFont == null) {
                    defaultFont = new Font(g2.getFont().getFontName(), Font.PLAIN, 10);
                }
                g2.setFont(defaultFont);

                draw(g2);

            }
            g2.dispose();

        } while (vimage.contentsLost());

        // copy image 
        g.drawImage(vimage, 0, 0, this);
        super.paintBorder(g);
        super.paintComponent(g);
        super.paintChildren(g);
        g.dispose();
    }

    @Override
    public void print(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setBackground(Color.WHITE);
        g2.clearRect(0, 0, getWidth(), getHeight());
        super.paintBorder(g);

        if (getHeight() > 0) {
            if (useAntialiasing) {
                g2.setRenderingHints(antiAlias);
            }
            if (defaultFont == null) {
                defaultFont = new Font(g2.getFont().getFontName(), Font.PLAIN, 10);
            }
            g2.setFont(defaultFont);

            draw(g2);

        }

        g2.dispose();
//        super.paintComponent(g);
//        super.paintChildren(g);
    }

    public abstract void draw(Graphics2D g2);

    public abstract boolean update();

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        if (!isEnabled()) {
            return;
        }

        switch (evt.getPropertyName()) {
            case SequenceViewControllerI.FEATURE_SELECTED:
                // caused by interactively clicking on a feature; nothing to do here
                break;
            case SequenceViewControllerI.NAVIGATE_TO_REGION:
                // via the search window, we received a request from the view
                // controller to display a specific region
                //
                // keep the current scale, but adjust bounds so that the selected
                // feature appears in the middle of the display
                AssembledRegionI target = (AssembledRegionI) evt.getNewValue();
                int mid = target.getMin() + ((target.getMax() - target.getMin()) / 2);
                int tmp = vc.getIntervalLength() / 2;
                vc.setBounds(mid - tmp, mid + tmp);
                bounds = vc.getBounds();
                update();
                repaint();
                break;
            case SequenceViewControllerI.CONTIG_CHANGE:
                removeAll();
                break;
            case SequenceViewControllerI.BOUNDS_CHANGE:
                bounds = (int[]) evt.getNewValue();
                scale = 1f / (1f * vc.getIntervalLength() / getWidth());
                if (getHeight() > 0) {
                    int[] myBounds = (int[]) evt.getNewValue();
                    if (update()) {
                        if (bounds[0] == myBounds[0] && bounds[1] == myBounds[1]) {
                            repaint();
                        } else {
                            System.err.println("bounds changed during update");
                        }
                    }
                }
                break;
            default:
                System.err.println("Unknown event in PanelBase: " + evt.getPropertyName());
                assert false;
        }

    }

//    protected int getMaxCoverage() {
//        return maxCoverage;
//    }
//    protected int getReferenceLength() {
//        return refLength;
//    }
    protected float bp2px(int i) {
        //assert bounds != null;
        return scale * (i - bounds[0]);
    }

    protected int px2bp(float d) {
        return (int) (d / scale) + bounds[0];
    }

    protected static int textWidth(Graphics2D g, String text) {
        return g.getFontMetrics(g.getFont()).stringWidth(text);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.isConsumed()) {
            return;
        }
        int notches = e.getWheelRotation();
        int[] newBounds = Arrays.copyOf(bounds, 2);
        int len = vc.getIntervalLength();
        int adjust = FastMath.max(50, len / 25);

        if (notches < 0) {
            // zoom in
            newBounds[0] += adjust;
            newBounds[1] -= adjust;
        } else {
            newBounds[0] -= adjust;
            newBounds[1] += adjust;
        }

        // limit max zoom level
        if (newBounds[1] - newBounds[0] + 1 > 100) {
            vc.setBounds(FastMath.max(newBounds[0], 0), FastMath.min(newBounds[1], vc.getReferenceLength() - 1));
        }
        e.consume();
    }

    @Override
    public void setEnabled(boolean enable) {
        super.setEnabled(enable);
        propertyChange(new PropertyChangeEvent(vc, ContigViewController.BOUNDS_CHANGE, 0, vc.getBounds()));
    }
}
