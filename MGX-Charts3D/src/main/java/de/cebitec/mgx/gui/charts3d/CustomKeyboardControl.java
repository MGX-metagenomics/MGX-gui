package de.cebitec.mgx.gui.charts3d;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import org.jzy3d.chart.Chart;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;

/**
 *
 * @author sj
 */
class CustomKeyboardControl extends KeyAdapter {

    private final Chart chart;

    public CustomKeyboardControl(Chart chart) {
        this.chart = chart;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_1:
                chart.getView().setViewPoint(new Coord3d(Math.PI / 3, Math.PI / 6, 0), true);
                break;
//            case KeyEvent.VK_2:
//                chart.getView().setViewPoint(new Coord3d(0, 0, 0), true);
//                break;
            case KeyEvent.VK_2:
                chart.getView().setViewPoint(new Coord3d(0, -Math.PI, 0), true);
                break;
            case KeyEvent.VK_3:
                chart.getView().setViewPoint(new Coord3d(-Math.PI / 2, 0, 0), true);
                break;
            case KeyEvent.VK_UP:
                BoundingBox3d bb = chart.getView().getBounds();
                if (!e.isShiftDown()) {
                    chart.getView().setBoundManual(new BoundingBox3d(bb.getXmin(), bb.getXmax(),
                            bb.getYmin() + 2 * BarChartBar.BAR_RADIUS + 2 * BarChartBar.BAR_FEAT_BUFFER_RADIUS, bb.getYmax(),
                            bb.getZmin(), bb.getZmax()));
                } else {
                    chart.getView().setBoundManual(new BoundingBox3d(bb.getXmin(), bb.getXmax(),
                            bb.getYmin(), bb.getYmax() + 2 * BarChartBar.BAR_RADIUS + 2 * BarChartBar.BAR_FEAT_BUFFER_RADIUS,
                            bb.getZmin(), bb.getZmax()));
                }
                chart.getView().updateBounds();
                break;
            case KeyEvent.VK_DOWN:
                bb = chart.getView().getBounds();
                if (!e.isShiftDown()) {
                    chart.getView().setBoundManual(new BoundingBox3d(bb.getXmin(), bb.getXmax(),
                            bb.getYmin() - 2 * BarChartBar.BAR_RADIUS - 2 * BarChartBar.BAR_FEAT_BUFFER_RADIUS, bb.getYmax(),
                            bb.getZmin(), bb.getZmax()));
                } else {
                    chart.getView().setBoundManual(new BoundingBox3d(bb.getXmin(), bb.getXmax(),
                            bb.getYmin(), bb.getYmax() - 2 * BarChartBar.BAR_RADIUS - 2 * BarChartBar.BAR_FEAT_BUFFER_RADIUS,
                            bb.getZmin(), bb.getZmax()));
                }
                chart.getView().updateBounds();
                break;
            default:
                break;
        }
        chart.render();
    }

}
