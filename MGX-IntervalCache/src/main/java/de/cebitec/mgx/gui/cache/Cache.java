package de.cebitec.mgx.gui.cache;

import com.google.common.cache.LoadingCache;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.exception.MGXTimeoutException;
import de.cebitec.mgx.api.model.MGXReferenceI;
import de.cebitec.mgx.api.model.ModelBaseI;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;

/**
 *
 * @author sjaenick
 */
public abstract class Cache<T> implements PropertyChangeListener {

    protected final MGXReferenceI ref;
    protected final LoadingCache<Interval, T> lcache;
    private final int segmentSize;
    //
    private static int SEGMENT_SIZE = 50000;
    //
    private volatile boolean isClosed = false;

    public Cache(MGXReferenceI ref, LoadingCache<Interval, T> lcache) {
        this(ref, lcache, SEGMENT_SIZE);
    }

    public Cache(MGXReferenceI ref, LoadingCache<Interval, T> lcache, int segSize) {
        this.ref = ref;
        this.lcache = lcache;
        this.segmentSize = segSize;
        ref.addPropertyChangeListener(this);
    }

    @Override
    public final void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource().equals(ref) && ModelBaseI.OBJECT_DELETED.equals(evt.getPropertyName())) {
            close();
        }
    }

    public final int getSegmentSize() {
        return segmentSize;
    }

    public boolean contains(Interval interval) {
        return lcache.getIfPresent(interval) != null;
    }

    public final MGXReferenceI getReference() {
        return ref;
    }

    public void close() {
        ref.removePropertyChangeListener(this);
        isClosed = true;
        lcache.cleanUp();
    }
    
    protected boolean isClosed() {
        return isClosed;
    }

    protected final Iterator<Interval> getIntervals(final int from, final int to) {
        return IntervalFactory.createSegments(from, to, segmentSize);
    }

    public abstract T getInternal(int from, int to) throws MGXException;
    
    public final T get(int from, int to) throws MGXException {
        /*
         * during shutdown, the client might close the server-side mapping
         * sessions even though background workers are still busy fetching
         * data from the server; in this case, the server would throw an
         * MGXTimeoutException indicating the session has already been closed.
         *
         * Thus, check if the instance has already been closed and rethrow the
         * exception only if isClosed == true
        */
        
        try {
            return getInternal(from, to);
        } catch (MGXTimeoutException mte) {
            if (!isClosed) {
                throw mte;
            }
        }
        return null;
    }

}
