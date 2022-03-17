package de.cebitec.mgx.gui.cache.internal;

import de.cebitec.mgx.gui.cache.Interval;
import com.google.common.cache.LoadingCache;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.MGXReferenceI;
import de.cebitec.mgx.api.model.ReferenceRegionI;
import de.cebitec.mgx.gui.cache.Cache;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sj
 */
public class RegionCache extends Cache<Set<ReferenceRegionI>> {

    public RegionCache(MGXReferenceI ref, LoadingCache<Interval, Set<ReferenceRegionI>> lcache) {
        super(ref, lcache);
    }

    public RegionCache(MGXReferenceI ref, LoadingCache<Interval, Set<ReferenceRegionI>> lcache, int segSize) {
        super(ref, lcache, segSize);
    }

    @Override
    public Set<ReferenceRegionI> getInternal(int from, int to) throws MGXException {
        Iterator<Interval> iter = getIntervals(from, to);
        Set<ReferenceRegionI> ret = new HashSet<>();
        while (iter.hasNext()) {
            try {
                Set<ReferenceRegionI> get = lcache.get(iter.next());
                for (ReferenceRegionI r : get) {
                    if (overlaps(r, from, to)) {
                        ret.add(r);
                    }
                }
            } catch (ExecutionException ex) {
                ret.clear();
                if (ex.getCause() != null && ex.getCause() instanceof MGXException) {
                    throw (MGXException) ex.getCause();
                }
                Logger.getLogger(RegionCache.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return ret;
    }

    private static boolean overlaps(ReferenceRegionI r, int from, int to) {
        int min = r.getMin();
        int max = r.getMax();

        return (min >= from && min <= to) // start in interval
                || (max >= from && max <= to) // stop in interval
                || (min <= from && max >= to); // mapping longer than interval

//        return (r.getStart() >= from && r.getStart() <= to) 
//                || (r.getStop() >= from && r.getStop() <= to)
//                || (r.getStart() <= from && r.getStop() >= to)
//                || (r.getStop() <= from && r.getStart() >= to);
    }
}
