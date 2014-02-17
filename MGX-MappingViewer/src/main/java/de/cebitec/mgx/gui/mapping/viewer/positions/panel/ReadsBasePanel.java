/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping.viewer.positions.panel;

import de.cebitec.mgx.gui.mapping.viewer.positions.AdjustmentPanel;
import de.cebitec.mgx.gui.mapping.viewer.positions.BoundsInfoManager;
import de.cebitec.mgx.gui.mapping.viewer.positions.MousePositionListenerI;
import de.cebitec.mgx.gui.mapping.viewer.AbstractViewer;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;

/**
 *
 * @author belmann
 */
public class ReadsBasePanel extends JPanel implements IBasePanel {

    private BoundsInfoManager boundsManager;
    private List<MousePositionListenerI> currentMousePosListeners;
    private MousePositionListenerI viewController;
    private AbstractViewer viewer;
    private JScrollPane centerScrollpane;

    public ReadsBasePanel(BoundsInfoManager boundsManager) {
        this.setLayout(new BorderLayout());
        this.boundsManager = boundsManager;
//        this.viewController = viewController;
        this.currentMousePosListeners = new ArrayList();
//        this.setBorder(BorderFactory.createLineBorder(Color.BLUE));
    }

    @Override
    public void close() {
        this.shutdownViewer();
        this.viewController = null;
        this.updateUI();
    }

    private void shutdownViewer() {
        if (this.viewer != null) {
            this.boundsManager.removeBoundListener(viewer);
            this.currentMousePosListeners.remove(viewer);
            this.remove(viewer);
            this.viewer.close();
            this.viewer = null;
        }
    }

    @Override
    public BoundsInfoManager getBoundsManager() {
        return this.boundsManager;
    }

    @Override
    public AbstractViewer getViewer() {
        return viewer;
    }

    @Override
    public void reportCurrentMousePos(int currentLogMousePos) {
//        viewController.setCurrentMousePosition(currentLogMousePos);
    }

    @Override
    public void reportMouseOverPaintingStatus(boolean b) {
    }

    @Override
    public void setCurrentMousePosition(int logPos) {
        for (MousePositionListenerI c : currentMousePosListeners) {
            c.setCurrentMousePosition(logPos);
        }
    }

    @Override
    public void setHorizontalAdjustmentPanel(AdjustmentPanel adjustmentPanel) {
    }

    @Override
    public void setLeftInfoPanel(AbstractInfoPanel infoPanel) {
    }

    @Override
    public void setMouseOverPaintingRequested(boolean requested) {
        for (MousePositionListenerI c : currentMousePosListeners) {
            c.setMouseOverPaintingRequested(requested);
        }
    }

    @Override
    public void setRightInfoPanel(AbstractInfoPanel infoPanel) {
    }

    @Override
    public void setTitlePanel(JPanel title) {
    }

    @Override
    public void setTopInfoPanel(MousePositionListenerI infoPanel) {
    }

    @Override
    public void setViewer(AbstractViewer viewer, JSlider verticalZoom) {
    }

    @Override
    public void setViewer(AbstractViewer viewer) {
        this.viewer = viewer;
        this.boundsManager.addBoundsListener(viewer);
        currentMousePosListeners.add(viewer);
        this.add(viewer, BorderLayout.CENTER);
        this.updateSize();

    }

    @Override
    public void setViewerInScrollpane(AbstractViewer viewer) {
        this.viewer = viewer;
        this.boundsManager.addBoundsListener(viewer);
        this.currentMousePosListeners.add(viewer);
        this.centerScrollpane = new JScrollPane(this.viewer);
        this.centerScrollpane.setPreferredSize(new Dimension(490, 400));
        this.centerScrollpane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.centerScrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        this.add(this.centerScrollpane, BorderLayout.CENTER);
        this.centerScrollpane.setVisible(true);
        this.viewer.setVisible(true);
        this.viewer.setScrollBar(this.centerScrollpane.getVerticalScrollBar());

        this.updateSize();
    }

    @Override
    public void setViewerInScrollpane(AbstractViewer viewer, JSlider verticalZoom) {
    }


    private void updateSize() {
        this.setMaximumSize(new Dimension(Integer.MAX_VALUE, this.getPreferredSize().height));
    }
}
