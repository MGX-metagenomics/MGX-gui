package de.cebitec.mgx.gui.cache;

import com.google.common.cache.LoadingCache;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.MGXReferenceI;
import java.util.Iterator;

/**
 *
 * @author sjaenick
 */
public abstract class Cache<T> {

    protected final MGXReferenceI ref;
    protected final LoadingCache<Interval, T> lcache;
    private final int segmentSize;
    //
    private static int SEGMENT_SIZE = 50000;

    public Cache(MGXReferenceI ref, LoadingCache<Interval, T> lcache) {
        this(ref, lcache, SEGMENT_SIZE);
    }

    public Cache(MGXReferenceI ref, LoadingCache<Interval, T> lcache, int segSize) {
        this.ref = ref;
        this.lcache = lcache;
        this.segmentSize = segSize;
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

    protected Iterator<Interval> getIntervals(final int from, final int to) {

        final int fromInterval = from / segmentSize;

        return new Iterator<Interval>() {

            private Interval cur = new Interval(segmentSize, fromInterval * segmentSize); //, FastMath.min(to, toInterval));

            @Override
            public boolean hasNext() {
                return cur != null;
            }

            @Override
            public Interval next() {
                Interval i = cur;
                if (cur.getTo() <= to) {
                    cur = cur.next(to);
                } else {
                    cur = null;
                }
                return i;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Not supported.");
            }
        };
    }

    public abstract T get(int from, int to) throws MGXException;

}
