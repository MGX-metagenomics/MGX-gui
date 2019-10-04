/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.charts.basic.util;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.event.ChartChangeListener;
import org.jfree.chart.event.ChartProgressListener;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.event.TitleChangeEvent;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.title.Title;
import org.jfree.ui.RectangleInsets;

/**
 *
 * @author sj
 */
public class MGXJFreeChart extends JFreeChart {

    private final JFreeChart chart;

    public MGXJFreeChart(JFreeChart chart) {
        super(chart.getPlot());
        this.chart = chart;
    }

    public void print(Graphics2D g2, Rectangle2D area) {

        if (chart.getPlot() instanceof XYPlot) {
            print(chart.getXYPlot(), g2, area);
        } else {
            print(chart.getCategoryPlot(), g2, area);
        }
    }

    private void print(XYPlot plot, Graphics2D g2, Rectangle2D area) {

        // line thickness
        int rCount = plot.getRendererCount();
        for (int i = 0; i < rCount; i++) {
            XYItemRenderer r = plot.getRenderer(i);
            r.setSeriesStroke(i, new BasicStroke(4));
        }

        // axis and tick labels
        Font oldLabelFont = plot.getDomainAxis().getLabelFont();
        Font labelFont = new Font(oldLabelFont.getName(), Font.BOLD, 20);
        plot.getDomainAxis().setLabelFont(labelFont);
        plot.getDomainAxis().setTickLabelFont(new Font(oldLabelFont.getName(), Font.PLAIN, 18));

        plot.getRangeAxis().setLabelFont(labelFont);
        plot.getRangeAxis().setTickLabelFont(new Font(oldLabelFont.getName(), Font.PLAIN, 18));

        chart.draw(g2, area);
    }

    private void print(CategoryPlot plot, Graphics2D g2, Rectangle2D area) {
        chart.draw(g2, area);
    }

    @Override
    public RenderingHints getRenderingHints() {
        return chart.getRenderingHints();
    }

    @Override
    public void setRenderingHints(RenderingHints renderingHints) {
        chart.setRenderingHints(renderingHints);
    }

    @Override
    public boolean isBorderVisible() {
        return chart.isBorderVisible();
    }

    @Override
    public void setBorderVisible(boolean visible) {
        chart.setBorderVisible(visible);
    }

    @Override
    public Stroke getBorderStroke() {
        return chart.getBorderStroke();
    }

    @Override
    public void setBorderStroke(Stroke stroke) {
        chart.setBorderStroke(stroke);
    }

    @Override
    public Paint getBorderPaint() {
        return chart.getBorderPaint();
    }

    @Override
    public void setBorderPaint(Paint paint) {
        chart.setBorderPaint(paint);
    }

    @Override
    public RectangleInsets getPadding() {
        return chart.getPadding();
    }

    @Override
    public void setPadding(RectangleInsets padding) {
        chart.setPadding(padding);
    }

    @Override
    public TextTitle getTitle() {
        return chart.getTitle();
    }

    @Override
    public void setTitle(TextTitle title) {
        chart.setTitle(title);
    }

    @Override
    public void setTitle(String text) {
        chart.setTitle(text);
    }

    @Override
    public void addLegend(LegendTitle legend) {
        chart.addLegend(legend);
    }

    @Override
    public LegendTitle getLegend() {
        return chart.getLegend();
    }

    @Override
    public LegendTitle getLegend(int index) {
        return chart.getLegend(index);
    }

    @Override
    public void removeLegend() {
        chart.removeLegend();
    }

    @Override
    public List getSubtitles() {
        return chart.getSubtitles();
    }

    @Override
    public void setSubtitles(List subtitles) {
        chart.setSubtitles(subtitles);
    }

    @Override
    public int getSubtitleCount() {
        return chart.getSubtitleCount();
    }

    @Override
    public Title getSubtitle(int index) {
        return chart.getSubtitle(index);
    }

    @Override
    public void addSubtitle(Title subtitle) {
        chart.addSubtitle(subtitle);
    }

    @Override
    public void addSubtitle(int index, Title subtitle) {
        chart.addSubtitle(index, subtitle);
    }

    @Override
    public void clearSubtitles() {
        chart.clearSubtitles();
    }

    @Override
    public void removeSubtitle(Title title) {
        chart.removeSubtitle(title);
    }

    @Override
    public Plot getPlot() {
        return chart.getPlot();
    }

    @Override
    public CategoryPlot getCategoryPlot() {
        return chart.getCategoryPlot();
    }

    @Override
    public XYPlot getXYPlot() {
        return chart.getXYPlot();
    }

    @Override
    public boolean getAntiAlias() {
        return chart.getAntiAlias();
    }

    @Override
    public void setAntiAlias(boolean flag) {
        chart.setAntiAlias(flag);
    }

    @Override
    public Object getTextAntiAlias() {
        return chart.getTextAntiAlias();
    }

    @Override
    public void setTextAntiAlias(boolean flag) {
        chart.setTextAntiAlias(flag);
    }

    @Override
    public void setTextAntiAlias(Object val) {
        chart.setTextAntiAlias(val);
    }

    @Override
    public Paint getBackgroundPaint() {
        return chart.getBackgroundPaint();
    }

    @Override
    public void setBackgroundPaint(Paint paint) {
        chart.setBackgroundPaint(paint);
    }

    @Override
    public Image getBackgroundImage() {
        return chart.getBackgroundImage();
    }

    @Override
    public void setBackgroundImage(Image image) {
        chart.setBackgroundImage(image);
    }

    @Override
    public int getBackgroundImageAlignment() {
        return chart.getBackgroundImageAlignment();
    }

    @Override
    public void setBackgroundImageAlignment(int alignment) {
        chart.setBackgroundImageAlignment(alignment);
    }

    @Override
    public float getBackgroundImageAlpha() {
        return chart.getBackgroundImageAlpha();
    }

    @Override
    public void setBackgroundImageAlpha(float alpha) {
        chart.setBackgroundImageAlpha(alpha);
    }

    @Override
    public boolean isNotify() {
        return chart.isNotify();
    }

    @Override
    public void setNotify(boolean notify) {
        chart.setNotify(notify);
    }

    @Override
    public void draw(Graphics2D g2, Rectangle2D area) {
        chart.draw(g2, area);
    }

    @Override
    public BufferedImage createBufferedImage(int width, int height) {
        return chart.createBufferedImage(width, height);
    }

    @Override
    public BufferedImage createBufferedImage(int width, int height, ChartRenderingInfo info) {
        return chart.createBufferedImage(width, height, info);
    }

    @Override
    public BufferedImage createBufferedImage(int width, int height, int imageType, ChartRenderingInfo info) {
        return chart.createBufferedImage(width, height, imageType, info);
    }

    @Override
    public BufferedImage createBufferedImage(int imageWidth, int imageHeight, double drawWidth, double drawHeight, ChartRenderingInfo info) {
        return chart.createBufferedImage(imageWidth, imageHeight, drawWidth, drawHeight, info);
    }

    @Override
    public void handleClick(int x, int y, ChartRenderingInfo info) {
        chart.handleClick(x, y, info);
    }

    @Override
    public void addChangeListener(ChartChangeListener listener) {
        chart.addChangeListener(listener);
    }

    @Override
    public void removeChangeListener(ChartChangeListener listener) {
        chart.removeChangeListener(listener);
    }

    @Override
    public void fireChartChanged() {
        chart.fireChartChanged();
    }

    @Override
    public void addProgressListener(ChartProgressListener listener) {
        chart.addProgressListener(listener);
    }

    @Override
    public void removeProgressListener(ChartProgressListener listener) {
        chart.removeProgressListener(listener);
    }

    @Override
    public void titleChanged(TitleChangeEvent event) {
        chart.titleChanged(event);
    }

    @Override
    public void plotChanged(PlotChangeEvent event) {
        chart.plotChanged(event);
    }

    @Override
    public boolean equals(Object obj) {
        return chart.equals(obj);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return chart.clone();
    }

}
