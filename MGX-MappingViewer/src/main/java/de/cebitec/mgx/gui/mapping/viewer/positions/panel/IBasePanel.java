/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping.viewer.positions.panel;

import de.cebitec.mgx.gui.mapping.viewer.positions.AdjustmentPanel;
import de.cebitec.mgx.gui.mapping.viewer.positions.BoundsInfoManager;
import de.cebitec.mgx.gui.mapping.viewer.positions.MousePositionListenerI;
import de.cebitec.mgx.gui.mapping.viewer.AbstractViewer;
import java.awt.Dimension;
import javax.swing.JPanel;
import javax.swing.JSlider;

/**
 *
 * @author belmann
 */
public interface IBasePanel extends MousePositionListenerI {

    void close();

    BoundsInfoManager getBoundsManager();

    AbstractViewer getViewer();

    void reportCurrentMousePos(int currentLogMousePos);

    void reportMouseOverPaintingStatus(boolean b);

    @Override
    void setCurrentMousePosition(int logPos);

    void setHorizontalAdjustmentPanel(AdjustmentPanel adjustmentPanel);

    void setLeftInfoPanel(AbstractInfoPanel infoPanel);

    @Override
    void setMouseOverPaintingRequested(boolean requested);

    void setRightInfoPanel(AbstractInfoPanel infoPanel);

    void setTitlePanel(JPanel title);

    void setTopInfoPanel(MousePositionListenerI infoPanel);

    void setViewer(AbstractViewer viewer, JSlider verticalZoom);

    void setViewer(AbstractViewer viewer);

    /**
     * Adds a viewer in a scrollpane allowing for vertical scrolling. Horizontal
     * scrolling is only available by "setHorizontalAdjustmentPanel".
     *
     * @param viewer viewer to set
     */
    void setViewerInScrollpane(AbstractViewer viewer);

    /**
     * Adds a viewer in a scrollpane allowing for vertical scrolling and
     * vertical zooming. Horizontal scrolling is only available by
     * "setHorizontalAdjustmentPanel".
     *
     * @param viewer viewer to set
     * @param verticalZoom vertical zoom slider
     */
    void setViewerInScrollpane(AbstractViewer viewer, JSlider verticalZoom);

    public Dimension getSize();
    
}
