package de.cebitec.mgx.gui.mapping.viewer.positions.panel;

import de.cebitec.mgx.gui.mapping.viewer.positions.AdjustmentPanel;
import de.cebitec.mgx.gui.mapping.viewer.positions.BoundsInfoManager;
import de.cebitec.mgx.gui.mapping.viewer.positions.MousePositionListenerI;
import de.cebitec.mgx.gui.mapping.viewer.AbstractViewer;
import de.cebitec.mgx.gui.mapping.viewer.ReferenceViewer;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;

/**
 * A BasePanel serves as basis for other visual components.
 *
 * @author ddoppmei
 */
public class ReferenceBasePanel extends JPanel implements IBasePanel {

    private static final long serialVersionUID = 246153482;
    private AbstractViewer viewer;
    private AbstractInfoPanel rightPanel;
    private AbstractInfoPanel leftPanel;
    private BoundsInfoManager boundsManager;
    private List<MousePositionListenerI> currentMousePosListeners;
    private JPanel centerPanel;
    private AdjustmentPanel adjustmentPanelHorizontal;
    private Component topPanel;
    private JScrollPane centerScrollpane;

    public ReferenceBasePanel(BoundsInfoManager boundsManager) {
        super();
        this.setLayout(new BorderLayout());
        this.centerPanel = new JPanel(new BorderLayout());
        this.add(centerPanel, BorderLayout.CENTER);
        this.boundsManager = boundsManager;
        this.currentMousePosListeners = new ArrayList();
    }

    @Override
    public BoundsInfoManager getBoundsManager() {
        return this.boundsManager;
    }

    @Override
    public void close() {
        this.shutdownViewer();
        this.shutdownInfoPanelAndAdjustmentPanel();
        this.remove(centerPanel);
        this.centerPanel = null;
        this.updateUI();
    }

    private void shutdownViewer() {
        if (this.viewer != null) {
            this.boundsManager.removeBoundListener(viewer);
            this.currentMousePosListeners.remove(viewer);
            this.centerPanel.remove(viewer);
            this.viewer.close();
            this.viewer = null;
        }
    }

    private void shutdownInfoPanelAndAdjustmentPanel() {
        if (adjustmentPanelHorizontal != null) {
            centerPanel.remove(adjustmentPanelHorizontal);
            adjustmentPanelHorizontal = null;
        }

        if (rightPanel != null) {
            rightPanel.close();
            this.remove(rightPanel);
            currentMousePosListeners.remove(rightPanel);
            rightPanel = null;
        }

        if (leftPanel != null) {
            leftPanel.close();
            this.remove(leftPanel);
            currentMousePosListeners.remove(leftPanel);
            leftPanel = null;
        }
    }

    @Override
    public void setViewer(AbstractViewer viewer, JSlider verticalZoom) {
        this.viewer = viewer;
        verticalZoom.setOrientation(JSlider.VERTICAL);
        this.boundsManager.addBoundsListener(viewer);
        currentMousePosListeners.add(viewer);
        // if (viewer instanceof TrackViewer) {
        //   TrackViewer tv = (TrackViewer) viewer;
        //  tv.setVerticalZoomSlider(verticalZoom);
        //}
        centerPanel.add(viewer, BorderLayout.CENTER);
        centerPanel.add(verticalZoom, BorderLayout.WEST);

        this.updateSize();
    }

    @Override
    public void setViewer(AbstractViewer viewer) {
        this.viewer = viewer;
        this.boundsManager.addBoundsListener(viewer);
        currentMousePosListeners.add(viewer);
        centerPanel.add(viewer, BorderLayout.CENTER);

        this.addPlaceholder();
        this.updateSize();
    }

    @Override
    public void setHorizontalAdjustmentPanel(AdjustmentPanel adjustmentPanel) {
        this.adjustmentPanelHorizontal = adjustmentPanel;
        centerPanel.add(adjustmentPanel, BorderLayout.NORTH);
        this.updateSize();
    }

    /**
     * Adds a viewer in a scrollpane allowing for vertical scrolling. Horizontal
     * scrolling is only available by "setHorizontalAdjustmentPanel".
     *
     * @param viewer viewer to set
     */
    @Override
    public void setViewerInScrollpane(AbstractViewer viewer) {
        this.viewer = viewer;
        this.boundsManager.addBoundsListener(viewer);

        this.currentMousePosListeners.add(viewer);
        this.centerScrollpane = new JScrollPane(this.viewer);
        this.centerScrollpane.setPreferredSize(new Dimension(490, 400));
        this.centerScrollpane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        this.centerPanel.add(this.centerScrollpane, BorderLayout.CENTER);
        this.centerScrollpane.setVisible(true);
        this.viewer.setVisible(true);
        this.viewer.setScrollBar(this.centerScrollpane.getVerticalScrollBar());

        this.addPlaceholder();
        this.updateSize();

    }

    /**
     * Adds a viewer in a scrollpane allowing for vertical scrolling and
     * vertical zooming. Horizontal scrolling is only available by
     * "setHorizontalAdjustmentPanel".
     *
     * @param viewer viewer to set
     * @param verticalZoom vertical zoom slider
     */
    @Override
    public void setViewerInScrollpane(AbstractViewer viewer, JSlider verticalZoom) {
        this.viewer = viewer;
        verticalZoom.setOrientation(JSlider.VERTICAL);
        this.boundsManager.addBoundsListener(viewer);
        this.currentMousePosListeners.add(viewer);
        this.centerScrollpane = new JScrollPane(this.viewer);
        this.centerScrollpane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.centerPanel.add(this.centerScrollpane, BorderLayout.CENTER);
        this.centerPanel.add(verticalZoom, BorderLayout.WEST);
        this.viewer.setScrollBar(this.centerScrollpane.getVerticalScrollBar());

        this.addPlaceholder();
        this.updateSize();
    }

    /**
     * Adds a placeholder in case this viewer is a ReferenceViewer
     */
    private void addPlaceholder() {
        if (viewer instanceof ReferenceViewer) {
            JPanel p = new JPanel();
            p.add(new JLabel(" "));
            p.setLayout(new FlowLayout(FlowLayout.RIGHT));
            centerPanel.add(p, BorderLayout.EAST);
        }
    }

    @Override
    public void setTopInfoPanel(MousePositionListenerI infoPanel) {
        this.topPanel = (Component) infoPanel;
        centerPanel.add(topPanel, BorderLayout.NORTH);
        currentMousePosListeners.add(infoPanel);
        this.updateSize();
    }

    @Override
    public void setRightInfoPanel(AbstractInfoPanel infoPanel) {
        this.rightPanel = infoPanel;
        this.add(infoPanel, BorderLayout.EAST);
        currentMousePosListeners.add(infoPanel);
        this.updateSize();
    }

    @Override
    public void setLeftInfoPanel(AbstractInfoPanel infoPanel) {
        this.leftPanel = infoPanel;
        this.add(leftPanel, BorderLayout.WEST);
        this.updateSize();
    }

    @Override
    public void setTitlePanel(JPanel title) {
        this.add(title, BorderLayout.NORTH);
        this.updateSize();
    }

    @Override
    public void reportCurrentMousePos(int currentLogMousePos) {
//        viewController.setCurrentMousePosition(currentLogMousePos);
    }

    @Override
    public void setCurrentMousePosition(int logPos) {
        for (MousePositionListenerI c : currentMousePosListeners) {
            c.setCurrentMousePosition(logPos);
        }
    }

    @Override
    public void reportMouseOverPaintingStatus(boolean b) {
//        viewController.setMouseOverPaintingRequested(b);
    }

    @Override
    public void setMouseOverPaintingRequested(boolean requested) {
        for (MousePositionListenerI c : currentMousePosListeners) {
            c.setMouseOverPaintingRequested(requested);
        }
    }

    @Override
    public AbstractViewer getViewer() {
        return viewer;
    }

    private void updateSize() {
        this.setMaximumSize(new Dimension(Integer.MAX_VALUE, this.getPreferredSize().height));
    }
}
