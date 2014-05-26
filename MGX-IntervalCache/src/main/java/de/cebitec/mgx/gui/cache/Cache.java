package de.cebitec.mgx.gui.cache;

import com.google.common.cache.LoadingCache;
import de.cebitec.mgx.gui.cache.internal.Interval;
import de.cebitec.mgx.gui.datamodel.Reference;
import java.util.Iterator;

/**
 *
 * @author sjaenick
 */
public abstract class Cache<T> {

    protected final Reference ref;
    protected final LoadingCache<Interval, T> lcache;
    private final int segmentSize;
    //
    private static int SEGMENT_SIZE = 50000;

    public Cache(Reference ref, LoadingCache<Interval, T> lcache) {
        this(ref, lcache, SEGMENT_SIZE);
    }

    public Cache(Reference ref, LoadingCache<Interval, T> lcache, int segSize) {
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

    public final Reference getReference() {
        return ref;
    }

    protected Iterator<Interval> getIntervals(final int from, final int to) {

        final int fromInterval = from / segmentSize;

        return new Iterator<Interval>() {

            private Interval cur = new Interval(Cache.this.getSegmentSize(), fromInterval * segmentSize); //, Math.min(to, toInterval));

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

    public abstract T get(int from, int to);

}
