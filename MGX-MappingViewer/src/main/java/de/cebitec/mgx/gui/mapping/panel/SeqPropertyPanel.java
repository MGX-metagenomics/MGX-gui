/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping.panel;

import de.cebitec.mgx.api.misc.Point;
import de.cebitec.mgx.gui.cache.Interval;
import de.cebitec.mgx.gui.cache.IntervalFactory;
import de.cebitec.mgx.gui.mapping.impl.ViewController;
import de.cebitec.mgx.gui.mapping.impl.ViewControllerI;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.beans.PropertyChangeEvent;
import java.io.Serial;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.math3.util.FastMath;

/**
 *
 * @author sjaenick
 */
public class SeqPropertyPanel extends PanelBase<ViewControllerI> {

    @Serial
    private static final long serialVersionUID = 1L;
    
    public SeqPropertyPanel(ViewController vc, boolean antiAlias) {
        super(vc, antiAlias);
        super.setPreferredSize(new Dimension(5000, 35));
        super.setMaximumSize(new Dimension(5000, 35));
        super.setBorder(javax.swing.BorderFactory.createLineBorder(Color.BLACK));
    }

    @Override
    public void draw(Graphics2D g2) {
        float height = getHeight();
        float heightScale = height / 100f;
        boolean isFirst = true;

        g2.setColor(Color.GREEN);
        GeneralPath polyline = new GeneralPath(GeneralPath.WIND_EVEN_ODD, points.size());

        synchronized (points) {
            for (Point p : points) {
                float x = bp2px((int) p.getX());
                float y = (float) (p.getY() * heightScale);
                if (isFirst) {
                    polyline.moveTo(x, y);
                    isFirst = false;
                } else {
                    polyline.lineTo(x, y);
                }
            }
        }
        g2.draw(polyline);
    }

    private int window = 50;
    private final List<Point> points = new ArrayList<>();

    @Override
    public boolean update() {
        String seq;

        window = FastMath.max(50, vc.getIntervalLength() / 500);
        int shift = window / 10;
        int win_half = window / 2;
        int lowBound = FastMath.max(0, bounds[0] - win_half);
        int hiBound = FastMath.min(getReferenceLength(), bounds[1] + win_half);

        List<Point> newPoints = new ArrayList<>();

        Iterator<Interval> slidingWindow = IntervalFactory.slidingWindow(lowBound, hiBound, window, shift);
        while (slidingWindow.hasNext()) {
            Interval i = slidingWindow.next();
            seq = vc.getSequence(i.getFrom(), i.getTo());
            float midPos = i.getFrom() + (i.length() / 2);
            float gc = gc(seq);
            newPoints.add(new Point(midPos, gc));
        }

        synchronized (points) {
            points.clear();
            points.addAll(newPoints);
        }

        return true;
    }

    private float gc(String seq) {
        float at = 0;
        float gc = 0;
        for (int i = 0; i < seq.length(); i++) {
            switch (seq.charAt(i)) {
                case 'A':
                    at++;
                    break;
                case 'T':
                    at++;
                    break;
                case 'G':
                    gc++;
                    break;
                case 'C':
                    gc++;
                    break;
                case 'N':
                    break;
                default:
                    break;
            }
        }
        float f = 100f * (gc / (at + gc));
        return f;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case ViewController.BOUNDS_CHANGE:
                bounds = (int[]) evt.getNewValue();
                if (getHeight() > 0) {
                    if (update()) {
                        repaint();
                    }
                }
                break;
            case ViewController.MIN_IDENTITY_CHANGE:
            case ViewController.MAX_COV_CHANGE:
                break;
        }
    }

}
