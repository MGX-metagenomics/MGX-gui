/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.charts.basic.j2d;

import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.AttributeTypeI;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sjaenick
 */
public class StackedBar {

    private final PlotPanel pp;
    private final VisualizationGroupI vGrp;
    private final AttributeTypeI attrType;
    private final DistributionI<Long> dist;
    private final List<BarSegment<AttributeI>> rects;
    private final List<AttributeI> attrs;
    private final int height = 25;
    private Rectangle textBounds;

    StackedBar(PlotPanel pp, VisualizationGroupI vGrp, AttributeTypeI attrType, List<AttributeI> sortOrder, DistributionI<Long> dist) {
        this.pp = pp;
        this.vGrp = vGrp;
        this.attrType = attrType;
        this.dist = dist;
        this.rects = new ArrayList<>();
        this.attrs = new ArrayList<>();

        for (AttributeI attr : sortOrder) {
            if (dist.containsKey(attr)) {
                attrs.add(attr);
            }
        }
    }

    public String getLabel() {
        return vGrp.getDisplayName() + " / " + attrType.getName();
    }

    int drawOn(Graphics2D g2, int x, int y) {
        int textHeight = g2.getFontMetrics(g2.getFont()).getHeight();
        int maxTextWidth = pp.getMaxTextWidth();

        double maxBarWidth = pp.getWidth() - maxTextWidth - 20;
        double xx = pp.getMaxNumElements();
        double scaleX = maxBarWidth / xx;

        int xOffset = x;

        // labels
        g2.setColor(Color.BLACK);
        g2.drawString(getLabel(), x, y + ((height + textHeight) / 2) - 2);
        textBounds = new Rectangle2D.Float(x, y + ((height / 2) - 6), maxTextWidth, textHeight).getBounds();

        rects.clear();

        xOffset += maxTextWidth;
        for (AttributeI attr : attrs) {
            Long value = dist.get(attr);
            g2.setColor(pp.getColor(attr));
            double delta = value.doubleValue() * scaleX;

            BarSegment<AttributeI> segment = new BarSegment<>(attr, xOffset, y, Math.max(1d, delta), height);
            g2.fill(segment);
            rects.add(segment);
            xOffset += delta;
        }

        return height;
    }

    String getToolTipText(MouseEvent m) {
        Point loc = m.getPoint();
        for (BarSegment<AttributeI> a : rects) {
            if (a.getBounds().contains(loc)) {
                AttributeI attr = a.getData();
                Long cnt = dist.get(attr);
                return attr.getValue() + ": " + cnt;
            }
        }
        if (textBounds != null && textBounds.contains(loc)) {
            return getLabel() + ": " + dist.getTotalClassifiedElements() + " hits";
        }
        return null;
    }

    private static class BarSegment<T> extends Rectangle2D.Double {

        private final T data;

        public BarSegment(T data, double x, double y, double w, double h) {
            super(x, y, w, h);
            this.data = data;
        }

        public T getData() {
            return data;
        }
    }
}
