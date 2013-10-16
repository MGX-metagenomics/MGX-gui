package de.cebitec.mgx.gui.cache;

import de.cebitec.mgx.gui.cache.internal.Interval;
import com.google.common.cache.LoadingCache;
import de.cebitec.mgx.gui.datamodel.Reference;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

/**
 *
 * @author sjaenick
 */
public abstract class Cache<T> {

    protected final Reference ref;
    protected final LoadingCache<Interval<T>, T> lcache;
    private final int segmentSize;
    //
    private static int SEGMENT_SIZE = 9999;

    public Cache(Reference ref, LoadingCache<Interval<T>, T> lcache) {
        this(ref, lcache, SEGMENT_SIZE);
    }

    public Cache(Reference ref, LoadingCache<Interval<T>, T> lcache, int segSize) {
        this.ref = ref;
        this.lcache = lcache;
        this.segmentSize = segSize;
    }

    public int getSegmentSize() {
        return segmentSize;
    }

    protected Iterator<Interval<T>> getIntervals(final int from, final int to) {

        final int fromInterval = from / segmentSize;

        return new Iterator<Interval<T>>() {
            private Interval<T> cur = new Interval(Cache.this, fromInterval * segmentSize);

            @Override
            public boolean hasNext() {
                return cur != null;
            }

            @Override
            public Interval next() {
                Interval<T> i = cur;
                if (cur.getTo() <= to) {
                    cur = cur.next();
                } else {
                    cur = null;
                }
                return i;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
    }
    
    public abstract T get(int from, int to) throws ExecutionException;

}
