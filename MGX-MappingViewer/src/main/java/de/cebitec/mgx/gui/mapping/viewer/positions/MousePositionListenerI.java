package de.cebitec.mgx.gui.mapping.viewer.positions;

/**
 * Listener for the current mouse position.
 *
 * @author ddoppmeier
 */
public interface MousePositionListenerI {

    public void setCurrentMousePosition(int logPos);

    public void setMouseOverPaintingRequested(boolean requested);

}
