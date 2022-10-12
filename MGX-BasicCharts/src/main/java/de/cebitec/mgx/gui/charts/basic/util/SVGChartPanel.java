/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.charts.basic.util;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.panel.Overlay;
import org.jfree.svg.SVGGraphics2D;

/**
 *
 * @author sj
 */
public class SVGChartPanel extends ChartPanel {

    public SVGChartPanel(JFreeChart chart) {
        super(chart);
    }

    public SVGChartPanel(JFreeChart chart, boolean useBuffer) {
        super(chart, useBuffer);
    }

    public SVGChartPanel(JFreeChart chart, boolean properties, boolean save, boolean print, boolean zoom, boolean tooltips) {
        super(chart, properties, save, print, zoom, tooltips);
    }

    public SVGChartPanel(JFreeChart chart, int width, int height, int minimumDrawWidth, int minimumDrawHeight, int maximumDrawWidth, int maximumDrawHeight, boolean useBuffer, boolean properties, boolean save, boolean print, boolean zoom, boolean tooltips) {
        super(chart, width, height, minimumDrawWidth, minimumDrawHeight, maximumDrawWidth, maximumDrawHeight, useBuffer, properties, save, print, zoom, tooltips);
    }

    public SVGChartPanel(JFreeChart chart, int width, int height, int minimumDrawWidth, int minimumDrawHeight, int maximumDrawWidth, int maximumDrawHeight, boolean useBuffer, boolean properties, boolean copy, boolean save, boolean print, boolean zoom, boolean tooltips) {
        super(chart, width, height, minimumDrawWidth, minimumDrawHeight, maximumDrawWidth, maximumDrawHeight, useBuffer, properties, copy, save, print, zoom, tooltips);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void paintComponent(Graphics g) {
        if (g instanceof SVGGraphics2D) {
            Graphics2D g2 = (Graphics2D) g;

            // first determine the size of the chart rendering area...
            Dimension size = getSize();
            Insets insets = getInsets();
            Rectangle2D available = new Rectangle2D.Double(insets.left, insets.top,
                    size.getWidth() - insets.left - insets.right,
                    size.getHeight() - insets.top - insets.bottom);

            // work out if scaling is required...
            boolean scale = false;
            double drawWidth = available.getWidth();
            double drawHeight = available.getHeight();
            double scaleX = 1.0;
            double scaleY = 1.0;
            int minimumDrawWidth = getMinimumDrawWidth();
            int minimumDrawHeight = getMinimumDrawHeight();
            int maximumDrawWidth = getMaximumDrawWidth();
            int maximumDrawHeight = getMaximumDrawHeight();

            if (drawWidth < minimumDrawWidth) {
                scaleX = drawWidth / minimumDrawWidth;
                drawWidth = minimumDrawWidth;
                scale = true;
            } else if (drawWidth > maximumDrawWidth) {
                scaleX = drawWidth / maximumDrawWidth;
                drawWidth = maximumDrawWidth;
                scale = true;
            }

            if (drawHeight < minimumDrawHeight) {
                scaleY = drawHeight / minimumDrawHeight;
                drawHeight = minimumDrawHeight;
                scale = true;
            } else if (drawHeight > maximumDrawHeight) {
                scaleY = drawHeight / maximumDrawHeight;
                drawHeight = maximumDrawHeight;
                scale = true;
            }

            Rectangle2D chartArea = new Rectangle2D.Double(0.0, 0.0, drawWidth,
                    drawHeight);

            AffineTransform saved = g2.getTransform();
            g2.translate(insets.left, insets.top);
            if (scale) {
                AffineTransform st = AffineTransform.getScaleInstance(
                        scaleX, scaleY);
                g2.transform(st);
            }
            getChart().draw(g2, chartArea, getAnchor(), getChartRenderingInfo());
            g2.setTransform(saved);

            //
            // need to use reflection to access private
            // list of overlays
            //
            List<Overlay> ovl = null;
            Field f;
            try {
                f = getClass().getDeclaredField("overlays");
                if (f != null && !f.isAccessible()) {
                    f.setAccessible(true);
                    ovl = (List<Overlay>) f.get(this);
                }
            } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException ex) {
            }

            if (ovl != null) {
                Iterator<Overlay> iterator = ovl.iterator();
                while (iterator.hasNext()) {
                    Overlay overlay = iterator.next();
                    overlay.paintOverlay(g2, this);
                }
            }

            g2.dispose();
            setAnchor(null);
            setHorizontalTraceLine(null);
            setVerticalTraceLine(null);
        } else {
            super.paintComponent(g);
        }
    }

}
