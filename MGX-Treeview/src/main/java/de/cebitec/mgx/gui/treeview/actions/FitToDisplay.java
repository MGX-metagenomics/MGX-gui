package de.cebitec.mgx.gui.treeview.actions;

import java.awt.geom.Rectangle2D;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.controls.ZoomToFitControl;
import prefuse.util.GraphicsLib;
import prefuse.util.display.DisplayLib;

/**
 * fit plot to acutal window size
 * @author rbisdorf
 */
public class FitToDisplay extends ZoomToFitControl  {
    private long m_duration = 2000;
    private int m_margin = 50;
    private String m_group = Visualization.ALL_ITEMS;

    public FitToDisplay() {
        super();
    }

    public void zoomToFit(Display display) {
        if (!display.isTranformInProgress()) {
            Visualization vis = display.getVisualization();
            Rectangle2D bounds = vis.getBounds(m_group);
            GraphicsLib.expand(bounds, m_margin + (int) (1 / display.getScale()));
            DisplayLib.fitViewToBounds(display, bounds, m_duration);
        }
    }

    public void zoomToFit(Display display, Rectangle2D bounds) {
        GraphicsLib.expand(bounds, m_margin + (int) (1 / display.getScale()));
        DisplayLib.fitViewToBounds(display, bounds, 1);
    }
}
