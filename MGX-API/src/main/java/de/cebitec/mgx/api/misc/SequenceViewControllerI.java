package de.cebitec.mgx.api.misc;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.RegionI;
import java.beans.PropertyChangeListener;

/**
 *
 * @author sj
 */
public interface SequenceViewControllerI<T extends RegionI> {

    public final static String CONTIG_CHANGE = "contigChange";
    public final static String BOUNDS_CHANGE = "boundsChange";
    public final static String FEATURE_SELECTED = "featureSelected";
    public static final String VIEWCONTROLLER_CLOSED = "viewControllerClosed";

    public boolean isClosed();

    public int[] getBounds();

    public int getIntervalLength();

    public String getReferenceName();

    public int getReferenceLength();

    public String getSequence();

    public String getSequence(int from, int to);

    public Iterable<T> getRegions() throws MGXException;

    public void selectRegion(T selectedRegion);

    public void setBounds(int i, int j);

    public void addPropertyChangeListener(PropertyChangeListener listener);

    public void removePropertyChangeListener(PropertyChangeListener listener);
}
