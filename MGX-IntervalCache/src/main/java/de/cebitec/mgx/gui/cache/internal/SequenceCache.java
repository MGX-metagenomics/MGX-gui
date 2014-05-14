package de.cebitec.mgx.gui.cache.internal;

import com.google.common.cache.LoadingCache;
import de.cebitec.mgx.gui.cache.Cache;
import de.cebitec.mgx.gui.datamodel.Reference;
import java.util.Iterator;

/**
 *
 * @author sjaenick
 */
public class SequenceCache extends Cache<String> {

    public SequenceCache(Reference ref, LoadingCache<Interval<String>, String> lcache) {
        super(ref, lcache);
    }

    public SequenceCache(Reference ref, LoadingCache<Interval<String>, String> lcache, int segSize) {
        super(ref, lcache, segSize);
    }

    @Override
    public String get(int from, int to) {
        assert from > -1;
        assert from < to;
        assert to < ref.getLength();
        int fromInterval = from / getSegmentSize();

        StringBuilder sb = new StringBuilder();
        Iterator<Interval<String>> iter = getIntervals(from, to);
        while (iter.hasNext()) {
            sb.append(lcache.getUnchecked(iter.next()));
        }
        String seq = sb.substring(from - (fromInterval * getSegmentSize()), to - (fromInterval * getSegmentSize()) + 1);
        return seq;
    }
}
