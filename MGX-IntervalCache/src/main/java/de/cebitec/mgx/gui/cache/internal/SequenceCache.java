package de.cebitec.mgx.gui.cache.internal;

import de.cebitec.mgx.gui.cache.Interval;
import com.google.common.cache.LoadingCache;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.MGXReferenceI;
import de.cebitec.mgx.gui.cache.Cache;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    public String getInternal(int from, int to) throws MGXException {
        assert from > -1;
        assert from < to;
        assert to < ref.getLength();
        int fromInterval = from / getSegmentSize();

        StringBuilder sb = new StringBuilder(to - from + 1);
        Iterator<Interval> iter = getIntervals(from, to);
        Interval i = null;
        try {
            while (iter.hasNext()) {
                i = iter.next();
                sb.append(lcache.get(i));

            }
        } catch (ExecutionException ex) {
            if (ex.getCause() != null && ex.getCause() instanceof MGXException) {
                throw (MGXException) ex.getCause();
            }
            Logger.getLogger(SequenceCache.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        try {
            return sb.substring(from - (fromInterval * getSegmentSize()), to - (fromInterval * getSegmentSize()) + 1);
        } catch (StringIndexOutOfBoundsException ex) {
            System.err.println("Illegal coords: " + from + "-" + to + " outside of string length " + sb.length());
            assert false;
        }
        return "";
    }
}
