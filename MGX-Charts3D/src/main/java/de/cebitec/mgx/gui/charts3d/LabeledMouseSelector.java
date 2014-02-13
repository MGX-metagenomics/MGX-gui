//package de.cebitec.mgx.gui.charts3d;
//
//import java.awt.Graphics2D;
//import java.awt.event.KeyEvent;
//import java.awt.event.KeyListener;
//import java.util.List;
//import org.jzy3d.chart.Chart;
//import org.jzy3d.chart.controllers.mouse.selection.AWTAbstractMouseSelector;
//import org.jzy3d.maths.BoundingBox2d;
//import org.jzy3d.maths.Coord2d;
//import org.jzy3d.plot3d.primitives.AbstractDrawable;
//import org.jzy3d.plot3d.rendering.scene.Scene;
//import org.jzy3d.plot3d.rendering.view.View;
//
///**
// *
// * @author sj
// */
//public class LabeledMouseSelector extends AWTAbstractMouseSelector implements KeyListener {
//
//    private final Chart target;
//
//    public LabeledMouseSelector(Chart target) {
//        this.target = target;
//    }
//
//    @Override
//    protected void processSelection(Scene scene, View view, int width, int height) {
//        view.project();
//        BarChartBar bestMatch = null;
//        for (AbstractDrawable ad : scene.getGraph().getAll()) {
//            if (!(ad instanceof BarChartBar)) {
//                continue;
//            }
//            BarChartBar bb = (BarChartBar) ad;
//            bb.setSelected(false);
//            List<Coord2d> l = bb.getBoundsToScreenProj();
//
//            BoundingBox2d bb2 = new BoundingBox2d(l);
//            boolean match = bb2.contains(new Coord2d(out.x, out.y));
//            if (match) {
//                if (bestMatch == null
//                        || (view.getCamera().getEye().distance(bestMatch.getShape().getBounds().getCenter())
//                        > view.getCamera().getEye().distance(bb.getShape().getBounds().getCenter()))
//                        && bb.getShape().isDisplayed()) {
//                    bestMatch = bb;
//                }
//            }
//        }
//        if (bestMatch != null) {
//            bestMatch.setSelected(true);
//        }
//    }
//
//    @Override
//    protected void drawSelection(Graphics2D g, int width, int height) {
//    }
//
//    @Override
//    public void keyReleased(KeyEvent e) {
//        switch (e.getKeyCode()) {
//            case KeyEvent.VK_SHIFT:
//                this.releaseChart();
//                break;
//            default:
//                break;
//        }
//        holding = false;
//        target.render(); // update message display
//    }
//
//    @Override
//    public void keyTyped(KeyEvent e) {
//    }
//
//    @Override
//    public void keyPressed(KeyEvent e) {
//        if (!holding) {
//            switch (e.getKeyCode()) {
//                case KeyEvent.VK_SHIFT:
//                    this.attachChart(target);
//                    break;
//                default:
//                    break;
//            }
//            holding = true;
//            target.render();
//        }
//    }
//    protected boolean holding = false;
//
//    @Override
//    public void clearLastSelection() {
//    }
//}
