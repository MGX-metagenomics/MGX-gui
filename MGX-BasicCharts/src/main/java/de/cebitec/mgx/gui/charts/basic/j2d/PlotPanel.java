/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.charts.basic.j2d;

import de.cebitec.mgx.api.groups.GroupI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.AttributeTypeI;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;
import javax.swing.ToolTipManager;
import org.jfree.chart.ChartColor;

/**
 *
 * @author sjaenick
 */
public class PlotPanel extends JPanel {

    private final Normalization norm;
    private final Map<GroupI, Long> maxAssignedByGroup;
    //
    private final List<StackedBar> bars = new ArrayList<>();
    private final Map<AttributeI, Color> colorMap = new HashMap<>();
    private final int vSpacePx = 10;

    public PlotPanel(Normalization norm, Map<GroupI, Long> maxAssignedByGroup) {
        super();
        this.norm = norm;
        this.maxAssignedByGroup = maxAssignedByGroup;
        ToolTipManager.sharedInstance().registerComponent(this);
        ToolTipManager.sharedInstance().setDismissDelay(5000);
        super.setBackground(Color.WHITE);
        super.setMinimumSize(new Dimension(100, 100));
    }

    private final int PAD_TOP = 10;
    private final int PAD_RIGHT = 20;
    private int maxTextWidth = 0;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
        g2.setFont(new Font(g2.getFont().getFontName(), Font.PLAIN, 12));

        maxTextWidth = 0;
        for (StackedBar sb : bars) {
            int textWidth = g2.getFontMetrics(g2.getFont()).stringWidth(sb.getLabel());
            maxTextWidth = textWidth > maxTextWidth ? textWidth : maxTextWidth;
        }

        int x = 10;
        int y = PAD_TOP;

        for (StackedBar sb : bars) {
            y += sb.drawOn(g2, x, y);
            y += vSpacePx;
        }

        if (y - vSpacePx > getHeight()) {
            setSize(getWidth(), y - vSpacePx + 3);
            setPreferredSize(new Dimension(getWidth(), y - vSpacePx + 3));
        }
    }

    public int getMaxTextWidth() {
        return maxTextWidth + (2 * 5); // add padding
    }

    @Override
    public String getToolTipText(MouseEvent m) {
        for (StackedBar a : bars) {
            String text = a.getToolTipText(m);
            if (text != null) {
                return text;
            }
        }
        return super.getToolTipText(m);
    }

    private long maxNumElements = 0;

    public void createBar(GroupI vgrp, AttributeTypeI aType, List<AttributeI> sortOrder, DistributionI<Long> dist) {
        if (dist.getTotalClassifiedElements() > maxNumElements) {
            maxNumElements = dist.getTotalClassifiedElements();
        }
        StackedBar ret = new StackedBar(this, vgrp, aType, sortOrder, dist);
        bars.add(ret);
        //return ret;
    }

    long getMaxNumElements() {
        return maxNumElements;
    }
    
    long getMaxNumAssigned(GroupI vGrp) {
        return maxAssignedByGroup.get(vGrp);
    }

    final Normalization getNormalizationType() {
        return norm;
    }

    Color getColor(AttributeI attr) {
        if (!colorMap.containsKey(attr)) {
            colorMap.put(attr, paints[paintIdx % paints.length]);
            paintIdx++;
        }
        return colorMap.get(attr);
    }
    private int paintIdx = 0;
    private final Color[] paints = createDefaultPaintArray();

    private static Color[] createDefaultPaintArray() {

        return new Color[]{
            new Color(0xFF, 0x55, 0x55),
            new Color(0x55, 0x55, 0xFF),
            new Color(0x55, 0xFF, 0x55),
            new Color(0xFF, 0xFF, 0x55),
            new Color(0xFF, 0x55, 0xFF),
            new Color(0x55, 0xFF, 0xFF),
            Color.pink,
            Color.gray,
            ChartColor.DARK_RED,
            ChartColor.DARK_BLUE,
            ChartColor.DARK_GREEN,
            ChartColor.DARK_YELLOW,
            ChartColor.DARK_MAGENTA,
            ChartColor.DARK_CYAN,
            Color.darkGray,
            ChartColor.LIGHT_RED,
            ChartColor.LIGHT_BLUE,
            ChartColor.LIGHT_GREEN,
            ChartColor.LIGHT_YELLOW,
            ChartColor.LIGHT_MAGENTA,
            ChartColor.LIGHT_CYAN,
            Color.lightGray,
            ChartColor.VERY_DARK_RED,
            ChartColor.VERY_DARK_BLUE,
            ChartColor.VERY_DARK_GREEN,
            ChartColor.VERY_DARK_YELLOW,
            ChartColor.VERY_DARK_MAGENTA,
            ChartColor.VERY_DARK_CYAN,
            ChartColor.VERY_LIGHT_RED,
            ChartColor.VERY_LIGHT_BLUE,
            ChartColor.VERY_LIGHT_GREEN,
            ChartColor.VERY_LIGHT_YELLOW,
            ChartColor.VERY_LIGHT_MAGENTA,
            ChartColor.VERY_LIGHT_CYAN
        };
    }
}
