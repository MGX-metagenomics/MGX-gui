package de.cebitec.mgx.gui.cache.internal;

import com.google.common.cache.LoadingCache;
import de.cebitec.mgx.gui.cache.Cache;
import de.cebitec.mgx.gui.datamodel.Reference;
import de.cebitec.mgx.gui.datamodel.Region;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author sj
 */
public class RegionCache extends Cache<Set<Region>> {

    public RegionCache(Reference ref, LoadingCache<Interval, Set<Region>> lcache) {
        super(ref, lcache);
    }

    public RegionCache(Reference ref, LoadingCache<Interval, Set<Region>> lcache, int segSize) {
        super(ref, lcache, segSize);
    }

    @Override
    public Set<Region> get(int from, int to) {
        Iterator<Interval> iter = getIntervals(from, to);
        Set<Region> ret = new HashSet<>();
        while (iter.hasNext()) {
            Set<Region> get = lcache.getUnchecked(iter.next());
            for (Region r : get) {
                if (overlaps(r, from, to)) {
                    ret.add(r);
                }
            }
        }
        return ret;
    }
    
    private static boolean overlaps(Region r, int from, int to) {
        return (r.getStart() >= from && r.getStart() <= to) 
                || (r.getStop() >= from && r.getStop() <= to)
                || (r.getStart() <= from && r.getStop() >= to)
                || (r.getStop() <= from && r.getStart() >= to);
    }
}
