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
    private static int SEGMENT_SIZE = 50000;

    public Cache(Reference ref, LoadingCache<Interval<T>, T> lcache) {
        this(ref, lcache, SEGMENT_SIZE);
    }

    public Cache(Reference ref, LoadingCache<Interval<T>, T> lcache, int segSize) {
        this.ref = ref;
        this.lcache = lcache;
        this.segmentSize = segSize;
    }

    public final int getSegmentSize() {
        return segmentSize;
    }
    
    public boolean contains(Interval<T> interval) {
        return lcache.getIfPresent(interval) != null;
    }

    public final Reference getReference() {
        return ref;
    }

    protected Iterator<Interval<T>> getIntervals(final int from, final int to) {
        assert to < ref.getLength();

        final int fromInterval = from / segmentSize;
        final int toInterval = Math.min(fromInterval * segmentSize + getSegmentSize() - 1, getReference().getLength() - 1);

        return new Iterator<Interval<T>>() {
            private Interval<T> cur = new Interval(Cache.this, fromInterval * segmentSize, Math.min(to, toInterval));

            @Override
            public boolean hasNext() {
                return cur != null;
            }

            @Override
            public Interval next() {
                Interval<T> i = cur;
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

    public abstract T get(int from, int to) throws ExecutionException;

}
