package de.cebitec.mgx.gui.charts.basic.util;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;
import org.apache.commons.math3.util.FastMath;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.plot.SpiderWebPlot;
import org.jfree.data.category.CategoryDataset;

/**
 *
 * @author sj
 *
 * http://tc.chinawin.net/it/softwaredev/article-1584c.html
 *
 */
public class TickedSpiderWebPlot extends SpiderWebPlot {

    private static final long serialVersionUID = 4005814203754627127L;
    private final int ticks = DEFAULT_TICKS;
    private static final int DEFAULT_TICKS = 5;
    private final NumberFormat format = NumberFormat.getInstance();
    private static final double PERPENDICULAR = 90;
    private static final double TICK_SCALE = 0.015;
    private final int valueLabelGap = DEFAULT_GAP;
    private static final int DEFAULT_GAP = 10;
    private static final double THRESHOLD = 15;

    public TickedSpiderWebPlot(CategoryDataset createCategoryDataset) {
        super(createCategoryDataset);
    }

    @Override
    protected void drawLabel(final Graphics2D g2, final Rectangle2D plotArea,
            final double value, final int cat, final double startAngle,
            final double extent) {
        super.drawLabel(g2, plotArea, value, cat, startAngle, extent);
        final FontRenderContext frc = g2.getFontRenderContext();
        final double[] transformed = new double[2];
        final double[] transformer = new double[2];
        final Arc2D arc1 = new Arc2D.Double(plotArea, startAngle, 0, Arc2D.OPEN);
        for (int i = 1; i <= ticks; i++) {
            final Point2D point1 = arc1.getEndPoint();
            final double deltaX = plotArea.getCenterX();
            final double deltaY = plotArea.getCenterY();
            double labelX = point1.getX() - deltaX;
            double labelY = point1.getY() - deltaY;
            final double scale = ((double) i / (double) ticks);
            final AffineTransform tx = AffineTransform.getScaleInstance(scale,
                    scale);
            final AffineTransform pointTrans = AffineTransform
                    .getScaleInstance(scale + TICK_SCALE, scale + TICK_SCALE);
            transformer[0] = labelX;
            transformer[1] = labelY;
            pointTrans.transform(transformer, 0, transformed, 0, 1);
            final double pointX = transformed[0] + deltaX;
            final double pointY = transformed[1] + deltaY;
            tx.transform(transformer, 0, transformed, 0, 1);
            labelX = transformed[0] + deltaX;
            labelY = transformed[1] + deltaY;
            double rotated = (PERPENDICULAR);
            AffineTransform rotateTrans = AffineTransform.getRotateInstance(
                    FastMath.toRadians(rotated), labelX, labelY);
            transformer[0] = pointX;
            transformer[1] = pointY;
            rotateTrans.transform(transformer, 0, transformed, 0, 1);
            final double x1 = transformed[0];
            final double y1 = transformed[1];
            rotated = (-PERPENDICULAR);
            rotateTrans = AffineTransform.getRotateInstance(FastMath
                    .toRadians(rotated), labelX, labelY);
            rotateTrans.transform(transformer, 0, transformed, 0, 1);
            final Composite saveComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                    1.0f));
            g2.draw(new Line2D.Double(transformed[0], transformed[1], x1, y1));
            if (startAngle == this.getStartAngle()) {
                final String label = format
                        .format(((double) i / (double) ticks)
                                * this.getMaxValue());
                final LineMetrics lm = getLabelFont()
                        .getLineMetrics(label, frc);
                final double ascent = lm.getAscent();
                if (FastMath.abs(labelX - plotArea.getCenterX()) < THRESHOLD) {
                    labelX += valueLabelGap;
                    labelY += ascent / (float) 2;
                } else if (FastMath.abs(labelY - plotArea.getCenterY()) < THRESHOLD) {
                    labelY += valueLabelGap;
                } else if (labelX >= plotArea.getCenterX()) {
                    if (labelY < plotArea.getCenterY()) {
                        labelX += valueLabelGap;
                        labelY += valueLabelGap;
                    } else {
                        labelX -= valueLabelGap;
                        labelY += valueLabelGap;
                    }
                } else {
                    if (labelY > plotArea.getCenterY()) {
                        labelX -= valueLabelGap;
                        labelY -= valueLabelGap;
                    } else {
                        labelX += valueLabelGap;
                        labelY -= valueLabelGap;
                    }
                }
                g2.setPaint(getLabelPaint());
                g2.setFont(getLabelFont());
                g2.drawString(label, (float) labelX, (float) labelY);
            }
            g2.setComposite(saveComposite);
        }
    }

    private LegendItemCollection lic = null;
    public void setFixedLegendItems(LegendItemCollection createLegend) {
        lic = createLegend;
    }

    @Override
    public LegendItemCollection getLegendItems() {
        if (lic != null) {
            return lic;
        }
        return super.getLegendItems();
    }
    
}

///**
// * 通過構造DefaultCategoryDataset創建Spider Chart。
// *
// */
//public class MySpriderWebPlotFactory {
//
//    public static JFreeChart createChart() {
//        TickedSpiderWebPlot spiderwebplot = new TickedSpiderWebPlot(createDataset());
//        JFreeChart jfreechart = new JFreeChart("前三個季度水果銷售報告",
//                TextTitle.DEFAULT_FONT, spiderwebplot, false);
//        LegendTitle legendtitle = new LegendTitle(spiderwebplot);
//        legendtitle.setPosition(RectangleEdge.RIGHT);
//        jfreechart.addSubtitle(legendtitle);
//        return jfreechart;
//    }
//}
