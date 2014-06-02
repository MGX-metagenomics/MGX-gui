/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping.panel;

import de.cebitec.mgx.gui.mapping.ViewController;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
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
    protected int[] bounds;
    protected int intervalLen;
    private int maxCoverage = 0;
    private double scale;
    protected int midY;
    //
    protected static final RenderingHints antiAlias = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    private final boolean useAntialiasing;

    public PanelBase(final ViewController vc, boolean antiAlias) {
        super(true);
        this.useAntialiasing = antiAlias;
        this.vc = vc;
        bounds = vc.getBounds();
        intervalLen = bounds[1] - bounds[0] + 1;
        scale = 1d / (1d * intervalLen / getWidth());
        midY = getHeight() / 2;
        vc.addPropertyChangeListener(this);
        setBackground(Color.WHITE);
        setForeground(Color.DARK_GRAY);

        addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                SwingWorker<Void, Void> sw = new SwingWorker<Void, Void>() {

                    @Override
                    protected Void doInBackground() throws Exception {
                        bounds = vc.getBounds();
                        intervalLen = bounds[1] - bounds[0] + 1;
                        midY = getHeight() / 2;
                        scale = 1d / (1d * intervalLen / getWidth());
                        if (midY > 0) {
                            update();
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (getHeight() == 0) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g;

        if (useAntialiasing) {
            g2.setRenderingHints(antiAlias);
        }
        draw(g2);
    }

    abstract void draw(Graphics2D g2);

    abstract boolean update();

    @Override
    public final void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case ViewController.BOUNDS_CHANGE:
                bounds = vc.getBounds();
                intervalLen = bounds[1] - bounds[0] + 1;
                scale = 1d / (1d * intervalLen / getWidth());
                midY = getHeight() / 2;
                if (midY > 0) {
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
        int notches = e.getWheelRotation();
        int[] b = vc.getBounds();
        int len = b[1] - b[0] + 1;
        int adjust = len / 25;

        if (notches < 0) {
            b[0] += adjust;
            b[1] -= adjust;
        } else {
            b[0] -= adjust;
            b[1] += adjust;
        }
        vc.setBounds(FastMath.max(b[0], 0), FastMath.min(b[1], vc.getReference().getLength() - 1));
    }
}
