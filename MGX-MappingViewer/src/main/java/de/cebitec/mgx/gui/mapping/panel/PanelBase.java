/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping.panel;

import de.cebitec.mgx.gui.mapping.ViewController;
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
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import org.apache.commons.math3.util.FastMath;

/**
 *
 * @author sj
 */
public abstract class PanelBase extends JPanel implements PropertyChangeListener, MouseWheelListener {

    protected final ViewController vc;
    private int[] bounds;
    private int maxCoverage = 0;
    private double scale;
    //
    private static final RenderingHints antiAlias = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    private final boolean useAntialiasing;
    //
    private final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    private final GraphicsConfiguration gc = ge.getDefaultScreenDevice().getDefaultConfiguration();

    public PanelBase(final ViewController vc, boolean antiAlias) {
        super(true);
        this.useAntialiasing = antiAlias;
        this.vc = vc;
        bounds = vc.getBounds();
        scale = 1d / (1d * vc.getIntervalLength() / getWidth());
        vc.addPropertyChangeListener(this);
        setBackground(Color.WHITE);
        setForeground(Color.DARK_GRAY);

        addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                if (getHeight() == 0 || !isEnabled()) {
                    return;
                }
                bounds = vc.getBounds();
                scale = 1d / (1d * vc.getIntervalLength() / getWidth());

                SwingWorker<Void, Void> sw = new SwingWorker<Void, Void>() {

                    @Override
                    protected Void doInBackground() throws Exception {
                        if (update()) {
                            repaint();
                        }
                        return null;
                    }

                };
                sw.execute();
                super.componentResized(e);
            }
        });
        addMouseWheelListener(this);
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

    private Font defaultFont = null;

    @Override
    public void paint(Graphics g) {
        if (vimage == null) {
            createVolatileImage(Transparency.OPAQUE);
        }

        long now = System.currentTimeMillis();

        // draw to back buffer image
        do {
            int valid = vimage.validate(gc);
            if (valid == VolatileImage.IMAGE_INCOMPATIBLE) {
                createVolatileImage(Transparency.OPAQUE);
            }
            Graphics2D g2 = vimage.createGraphics();
            super.paint(g2);
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
        g.dispose();
        now = System.currentTimeMillis() - now;
        if (now > 35) {
            System.err.println("paint for " + getClass().getSimpleName() + " took " + now + " ms");
        }
    }

    abstract void draw(Graphics2D g2);

    public abstract boolean update();

    @Override
    public final void propertyChange(PropertyChangeEvent evt) {
        if (!isEnabled()) {
            return;
        }

        switch (evt.getPropertyName()) {
            case ViewController.BOUNDS_CHANGE:
                bounds = vc.getBounds();
                scale = 1d / (1d * vc.getIntervalLength() / getWidth());
                if (getHeight() > 0) {
                    if (update()) {
                        repaint();
                    }
                }
                break;
            case ViewController.MAX_COV_CHANGE:
                maxCoverage = (int) evt.getNewValue();
                if (update()) {
                    repaint();
                }
                break;
            default:
                System.err.println("Unknown event: " + evt.getPropertyName());
                assert false;
        }

    }

    protected int getMaxCoverage() {
        return maxCoverage;
    }

    protected double bp2px(int i) {
        return scale * (i - bounds[0]);
    }

    protected int px2bp(double d) {
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
        int[] b = vc.getBounds();
        int len = vc.getIntervalLength();
        int adjust = len / 25;

        if (notches < 0) {
            b[0] += adjust;
            b[1] -= adjust;
        } else {
            b[0] -= adjust;
            b[1] += adjust;
        }
        vc.setBounds(FastMath.max(b[0], 0), FastMath.min(b[1], vc.getReference().getLength() - 1));
        e.consume();
    }

    @Override
    public void setEnabled(boolean enable) {
        super.setEnabled(enable);
        propertyChange(new PropertyChangeEvent(vc, ViewController.BOUNDS_CHANGE, 0, 1));
    }
}
