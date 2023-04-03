package de.cebitec.mgx.gui.pca;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.io.Serial;
import org.apache.commons.math3.util.FastMath;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.util.LineUtils;
import org.jfree.chart.util.ShapeUtils;
import org.jfree.data.xy.XYDataset;

/**
 *
 * @author sjaenick
 */
public class ArrowRenderer extends XYLineAndShapeRenderer {

    @Serial
    private static final long serialVersionUID = 1L;

    public ArrowRenderer() {
        super();
        GeneralPath s = new GeneralPath();
        s.moveTo(-4, 0);
        s.lineTo(0, -6);
        s.lineTo(4, 0);
        s.lineTo(-4, 0);
        super.setDefaultShape(s);
        super.setSeriesShape(0, s);
        super.setSeriesShape(1, s);
        //setDrawOutlines(true);

    }

    @Override
    protected void drawPrimaryLine(XYItemRendererState state, Graphics2D g2, XYPlot plot, XYDataset dataset, int pass, int series, int item, ValueAxis domainAxis, ValueAxis rangeAxis, Rectangle2D dataArea) {
        // get the data point...
        double x1 = dataset.getXValue(series, item);
        double y1 = dataset.getYValue(series, item);
        if (Double.isNaN(y1) || Double.isNaN(x1)) {
            return;
        }

        RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
        RectangleEdge yAxisLocation = plot.getRangeAxisEdge();

        double transX0 = domainAxis.valueToJava2D(0, dataArea, xAxisLocation);
        double transY0 = rangeAxis.valueToJava2D(0, dataArea, yAxisLocation);

        double transX1 = domainAxis.valueToJava2D(x1, dataArea, xAxisLocation);
        double transY1 = rangeAxis.valueToJava2D(y1, dataArea, yAxisLocation);

        // only draw if we have good values
        if (Double.isNaN(transX0) || Double.isNaN(transY0)
                || Double.isNaN(transX1) || Double.isNaN(transY1)) {
            return;
        }

        PlotOrientation orientation = plot.getOrientation();
        boolean visible;
        if (orientation == PlotOrientation.HORIZONTAL) {
            state.workingLine.setLine(transY0, transX0, transY1, transX1);
        } else if (orientation == PlotOrientation.VERTICAL) {
            state.workingLine.setLine(transX0, transY0, transX1, transY1);
        }
        visible = LineUtils.clipLine(state.workingLine, dataArea);
        if (visible) {
            drawFirstPassShape(g2, pass, series, item, state.workingLine);
        }
    }

    @Override
    protected void drawSecondaryPass(Graphics2D g2, XYPlot plot,
            XYDataset dataset, int pass, int series, int item,
            ValueAxis domainAxis, Rectangle2D dataArea, ValueAxis rangeAxis,
            CrosshairState crosshairState, EntityCollection entities) {

        Shape entityArea = null;

        // get the data point...
        double x1 = dataset.getXValue(series, item);
        double y1 = dataset.getYValue(series, item);
        if (Double.isNaN(y1) || Double.isNaN(x1)) {
            return;
        }

        PlotOrientation orientation = plot.getOrientation();
        RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
        RectangleEdge yAxisLocation = plot.getRangeAxisEdge();

        double transX0 = domainAxis.valueToJava2D(0, dataArea, xAxisLocation);
        double transY0 = rangeAxis.valueToJava2D(0, dataArea, yAxisLocation);

        double transX1 = domainAxis.valueToJava2D(x1, dataArea, xAxisLocation);
        double transY1 = rangeAxis.valueToJava2D(y1, dataArea, yAxisLocation);

        double vecX = transX1 - transX0;
        double vecY = transY1 - transY0;

        if (getItemShapeVisible(series, item)) {
            Shape shape = getItemShape(series, item);
            // align shape direction
            double angle = FastMath.PI - FastMath.acos(vecY / FastMath.sqrt(vecX * vecX + vecY * vecY));
            shape = AffineTransform.getRotateInstance(angle).createTransformedShape(shape);
            if (vecX < 0) {
                // arrow leftwards, flip shape
                shape = AffineTransform.getScaleInstance(-1, 1).createTransformedShape(shape);
            }

            if (orientation == PlotOrientation.HORIZONTAL) {
                shape = ShapeUtils.createTranslatedShape(shape, transY1,
                        transX1);
            } else if (orientation == PlotOrientation.VERTICAL) {
                shape = ShapeUtils.createTranslatedShape(shape, transX1,
                        transY1);
            }

            entityArea = shape;
            if (shape.intersects(dataArea)) {
                if (getItemShapeFilled(series, item)) {
                    if (getUseFillPaint()) {
                        g2.setPaint(getItemFillPaint(series, item));
                    } else {
                        g2.setPaint(getItemPaint(series, item));
                    }
                    g2.fill(shape);
                }
                if (getDrawOutlines()) {
                    if (getUseOutlinePaint()) {
                        g2.setPaint(getItemOutlinePaint(series, item));
                    } else {
                        g2.setPaint(getItemPaint(series, item));
                    }
                    g2.setStroke(getItemOutlineStroke(series, item));
                    g2.draw(shape);
                    // g2.drawString(String.valueOf(angle), (float)transX1 + 5, (float)transY1 + 5);
                }
            }
        }

        double xx = transX1;
        double yy = transY1;
        if (orientation == PlotOrientation.HORIZONTAL) {
            xx = transY1;
            yy = transX1;
        }

        // draw the item label if there is one...
        if (isItemLabelVisible(series, item)) {
            drawItemLabel(g2, orientation, dataset, series, item, xx, yy,
                    (y1 < 0.0));
        }

        int domainAxisIndex = plot.getDomainAxisIndex(domainAxis);
        //int rangeAxisIndex = plot.getRangeAxisIndex(rangeAxis);

        updateCrosshairValues(crosshairState, x1, y1, domainAxisIndex,
                transX1, transY1, orientation);
//        updateCrosshairValues(crosshairState, x1, y1, datasetIndex,
//                transX1, transY1, orientation);
        // add an entity for the item, but only if it falls within the data
        // area...
        if (entities != null && ShapeUtils.isPointInRect(dataArea, xx, yy)) {
            addEntity(entities, entityArea, dataset, series, item, xx, yy);
        }
    }

    int datasetIndex = -1;

    void setDataSetIndex(int i) {
        datasetIndex = i;
    }

}
