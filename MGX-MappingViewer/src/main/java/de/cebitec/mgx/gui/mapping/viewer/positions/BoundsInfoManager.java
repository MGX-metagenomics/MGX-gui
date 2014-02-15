package de.cebitec.mgx.gui.mapping.viewer.positions;

//import de.cebitec.vamp.databackend.dataObjects.PersistantReference;
import de.cebitec.mgx.gui.mapping.sequences.ReferenceHolder;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ddoppmeier
 */
public class BoundsInfoManager implements AdjustmentPanelListenerI {

    private int currentHorizontalPosition;
    private int zoomfactor;
    private ReferenceHolder currentRefGen;
    private List<LogicalBoundsListener> boundListeners;
    private List<SynchronousNavigatorI> syncedNavigators;

    public BoundsInfoManager(ReferenceHolder refGen) {
        this.currentRefGen = refGen;
        this.boundListeners = new ArrayList();
        this.syncedNavigators = new ArrayList();
        this.zoomfactor = 1;
        this.currentHorizontalPosition = 1;
    }

    public void addBoundsListener(LogicalBoundsListener a) {
        boundListeners.add(a);
        if (a.isPaintingAreaAviable()) {
            a.updateLogicalBounds(computeBounds(a.getPaintingAreaDimension()));
        }
    }

    public void removeBoundListener(LogicalBoundsListener a) {
        if (boundListeners.contains(a)) {
            boundListeners.remove(a);
        }
    }

    public void addSynchronousNavigator(SynchronousNavigatorI navi) {
        syncedNavigators.add(navi);
        navi.setCurrentScrollValue(currentHorizontalPosition);
        navi.setCurrentZoomValue(zoomfactor);
    }

    public void removeSynchronousNavigator(SynchronousNavigatorI navi) {
        if (syncedNavigators.contains(navi)) {
            syncedNavigators.remove(navi);
        }
    }

    private void updateLogicalListeners() {
        for (LogicalBoundsListener a : boundListeners) {
            if (a.isPaintingAreaAviable()) {
                a.updateLogicalBounds(computeBounds(a.getPaintingAreaDimension()));
            }
        }
    }

    private void updateSynchronousNavigators() {
        for (SynchronousNavigatorI n : syncedNavigators) {
            n.setCurrentScrollValue(currentHorizontalPosition);
            n.setCurrentZoomValue(zoomfactor);
        }
    }

    public void getUpdatedBoundsInfo(LogicalBoundsListener a) {

        if (a.isPaintingAreaAviable()) {
            a.updateLogicalBounds(computeBounds(a.getPaintingAreaDimension()));
        }
    }

    public BoundsInfo getUpdatedBoundsInfo(Dimension d) {
        return computeBounds(d);
    }

    /**
     * Compute the horizontal bounds in connection to the reference sequence.
     * @param d dimension
     * @return BoundsInfo object containing current bounds
     */
    private BoundsInfo computeBounds(Dimension d) {
        int logWidth = (int) (d.getWidth() * 0.1 * zoomfactor);

        BoundsInfo bounds = new BoundsInfo(1, currentRefGen.getRefLength(), currentHorizontalPosition, zoomfactor, logWidth);
        return bounds;
    }

    /**
     * Notify listeners of changes of the zoom level.
     * @param sliderValue new zoom value to be applied
     */
    @Override
    public void zoomLevelUpdated(int sliderValue) {
        this.zoomfactor = sliderValue;
        this.updateSynchronousNavigators();
        this.updateLogicalListeners();
    }

    /**
     * Notify listeners of changes of the currently centered genome position.
     * @param scrollbarValue position in the genome to center
     */
    @Override
    public void navigatorBarUpdated(int scrollbarValue) {
        this.currentHorizontalPosition = scrollbarValue;
        this.updateSynchronousNavigators();
        this.updateLogicalListeners();
    }
}
