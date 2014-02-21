/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping.viewer.positions.panel;

import de.cebitec.mgx.gui.mapping.viewer.positions.AdjustmentPanel;
import de.cebitec.mgx.gui.mapping.viewer.positions.BoundsInfoManager;
import de.cebitec.mgx.gui.mapping.viewer.positions.MousePositionListenerI;
import de.cebitec.mgx.gui.mapping.viewer.AbstractViewer;
import de.cebitec.mgx.gui.mapping.viewer.ReferenceViewer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author belmann
 */
public class ReadsBasePanel extends JPanel implements IBasePanel {

    private JPanel horizontalScrollbarPlaceholder;
    private BoundsInfoManager boundsManager;
    private List<MousePositionListenerI> currentMousePosListeners;
    private MousePositionListenerI viewController;
    private AbstractViewer viewer;
    private JScrollPane centerScrollpane;

    public ReadsBasePanel(BoundsInfoManager boundsManager) {
        this.setLayout(new BorderLayout());
        this.boundsManager = boundsManager;
        this.currentMousePosListeners = new ArrayList();
        horizontalScrollbarPlaceholder = new JPanel();
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

        horizontalScrollbarPlaceholder.add(new JLabel(" "));
        horizontalScrollbarPlaceholder.setLayout(new FlowLayout(FlowLayout.RIGHT));
        this.viewer = viewer;
        this.boundsManager.addBoundsListener(viewer);
        this.currentMousePosListeners.add(viewer);
        this.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK));
        this.centerScrollpane = new JScrollPane(this.viewer);
        this.centerScrollpane.setBorder(BorderFactory.createEmptyBorder());
        this.centerScrollpane.setViewportBorder(BorderFactory.createEmptyBorder());
        this.centerScrollpane.setPreferredSize(new Dimension(490, 400));
        this.centerScrollpane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.centerScrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        this.add(this.centerScrollpane, BorderLayout.CENTER);
        this.centerScrollpane.setVisible(true);
        this.centerScrollpane.getViewport().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (centerScrollpane.getVerticalScrollBar().isVisible()) {
                    remove(horizontalScrollbarPlaceholder);
                } else {
                    add(horizontalScrollbarPlaceholder, BorderLayout.EAST);
                }
                revalidate();
            }
        });
        this.viewer.setVisible(true);
        this.viewer.setScrollBar(this.centerScrollpane.getVerticalScrollBar());
        this.viewer.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                centerScrollpane.updateUI();

            }
        });

        this.updateSize();
    }

    @Override
    public void setViewerInScrollpane(AbstractViewer viewer, JSlider verticalZoom) {
    }

    private void updateSize() {
        this.setMaximumSize(new Dimension(Integer.MAX_VALUE, this.getPreferredSize().height));
    }
}
