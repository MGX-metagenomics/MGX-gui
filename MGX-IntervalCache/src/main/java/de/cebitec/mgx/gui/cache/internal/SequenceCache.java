
package de.cebitec.mgx.gui.cache.internal;

import com.google.common.cache.LoadingCache;
import de.cebitec.mgx.gui.cache.Cache;
import de.cebitec.mgx.gui.datamodel.Reference;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

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
    public String get(int from, int to) throws ExecutionException {
        assert from < to;
        assert to < ref.getLength() - 1; 
        int fromInterval = from / getSegmentSize();

        StringBuilder sb = new StringBuilder();
        Iterator<Interval<String>> iter = getIntervals(from, to);
        while (iter.hasNext()) {
            sb.append(lcache.get(iter.next()));
        }
        String seq = sb.substring(from - (fromInterval * getSegmentSize()), to - (fromInterval * getSegmentSize()) + 1);
        return seq;
    }
}