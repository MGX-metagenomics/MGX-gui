package de.cebitec.mgx.gui.charts3d;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.mouse.AWTMouseUtilities;
import org.jzy3d.chart.controllers.mouse.camera.AWTCameraMouseController;
import org.jzy3d.chart.controllers.thread.camera.CameraThreadController;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Scale;

/**
 *
 * @author sj
 */
public class CustomMouseControl extends AWTCameraMouseController {

    private final Chart chart;

    public CustomMouseControl(Chart chart) {
        this.chart = chart;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (threadController != null) {
            threadController.stop();
        }

        float factor = 1 + (e.getWheelRotation() / 20.0f);
        zoomAll(factor);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Coord2d mouse = new Coord2d(e.getX(), e.getY());

        // Rotate
        if (AWTMouseUtilities.isLeftDown(e)) {
            Coord2d move = mouse.sub(prevMouse).div(150);
            rotate(move);
        } // Shift
        else if (AWTMouseUtilities.isRightDown(e)) {
            Coord2d move = mouse.sub(prevMouse);
            if (move.y != 0) {
                Scale s = chart.getScale();
                s.setMax((1f + move.y / 100f) * s.getMax());
                chart.setScale(s, true);
//                        shift(move.y/1000f);
            }
        }
        prevMouse = mouse;
    }

    protected void zoomAll(final float factor) {
        for (Chart c : targets) {
            BoundingBox3d bb = c.getView().getBounds();
            c.getView().setBoundManual(new BoundingBox3d(bb.getXmin(), bb.getXmax() * factor,
                    bb.getYmin(), bb.getYmax() * factor,
                    bb.getZmin(), bb.getZmax() * factor));
            c.getView().updateBounds();
        }
    }

    public void install() {
        CameraThreadController threadCamera = new CameraThreadController(chart);
        this.addSlaveThreadController(threadCamera);
        chart.addController(this);
    }
}
