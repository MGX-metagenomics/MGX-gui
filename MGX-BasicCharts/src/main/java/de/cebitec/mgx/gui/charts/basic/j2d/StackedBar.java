/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.charts.basic.j2d;

import de.cebitec.mgx.api.groups.GroupI;
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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author sjaenick
 */
public class StackedBar {
    
    private final PlotPanel plotPanel;
    private final GroupI vGrp;
    private final AttributeTypeI attrType;
    private final DistributionI<Long> dist;
    private final List<BarSegment<AttributeI>> rects;
    private final List<AttributeI> attrs;
    private final int height = 25;
    private Rectangle textBounds;
    
    StackedBar(PlotPanel pp, GroupI vGrp, AttributeTypeI attrType, List<AttributeI> sortOrder, DistributionI<Long> dist) {
        this.plotPanel = pp;
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
        int maxTextWidth = plotPanel.getMaxTextWidth();
        
        float maxBarWidth = plotPanel.getWidth() - maxTextWidth - 20;
        
        float scaleX = 0;
        switch (plotPanel.getNormalizationType()) {
            case DISABLED:
                scaleX = maxBarWidth / (1f * plotPanel.getMaxNumElements());
                break;
            case ROOT:
                scaleX = maxBarWidth / (1f * plotPanel.getMaxNumAssigned(vGrp));
                break;
            case ALL:
                scaleX = maxBarWidth / (1f * dist.getTotalClassifiedElements());
                break;
        }
        
        float xOffset = x;
        float yy = y;

        // labels
        g2.setColor(Color.BLACK);
        g2.drawString(getLabel(), x, yy + ((height + textHeight) / 2) - 2);
        textBounds = new Rectangle2D.Float(x, y + ((height / 2) - 6), maxTextWidth, textHeight).getBounds();
        
        rects.clear();
        
        xOffset += maxTextWidth;
        for (AttributeI attr : attrs) {
            g2.setColor(plotPanel.getColor(attr));
            float numAssigned = dist.get(attr).floatValue();
            float delta = numAssigned * scaleX;
            BarSegment<AttributeI> segment = new BarSegment<>(attr, xOffset, y, delta, height);
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
                DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                return attr.getValue() + ": " + formatter.format(cnt);
            }
        }
        if (textBounds != null && textBounds.contains(loc)) {
            long totalClassifiedElements = dist.getTotalClassifiedElements();
            return totalClassifiedElements == 1
                    ? getLabel() + ": 1 hit"
                    : getLabel() + ": " + NumberFormat.getInstance(Locale.US).format(totalClassifiedElements) + " hits";
        }
        return null;
    }
    
    private static class BarSegment<T> extends Rectangle2D.Float {
        
        private final T data;
        
        public BarSegment(T data, float x, float y, float w, float h) {
            super(x, y, w, h);
            this.data = data;
        }
        
        public T getData() {
            return data;
        }
    }
}
