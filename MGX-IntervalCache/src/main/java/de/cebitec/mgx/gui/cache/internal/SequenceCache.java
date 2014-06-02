package de.cebitec.mgx.gui.cache.internal;

import com.google.common.cache.LoadingCache;
import de.cebitec.mgx.api.model.MGXReferenceI;
import de.cebitec.mgx.gui.cache.Cache;
import java.util.Iterator;

/**
 *
 * @author sjaenick
 */
public class SequenceCache extends Cache<String> {

    public SequenceCache(MGXReferenceI ref, LoadingCache<Interval, String> lcache) {
        super(ref, lcache);
    }

    public SequenceCache(MGXReferenceI ref, LoadingCache<Interval, String> lcache, int segSize) {
        super(ref, lcache, segSize);
    }

    @Override
    public String get(int from, int to) {
        assert from > -1;
        assert from < to;
        assert to < ref.getLength();
        int fromInterval = from / getSegmentSize();

        StringBuilder sb = new StringBuilder(to-from+1);
        Iterator<Interval> iter = getIntervals(from, to);
        while (iter.hasNext()) {
            sb.append(lcache.getUnchecked(iter.next()));
        }
        String seq = sb.substring(from - (fromInterval * getSegmentSize()), to - (fromInterval * getSegmentSize()) + 1);
        return seq;
    }
}
