package de.cebitec.mgx.gui.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import de.cebitec.mgx.gui.cache.internal.Interval;
import de.cebitec.mgx.gui.cache.internal.MappedSequenceCache;
import de.cebitec.mgx.gui.cache.internal.RegionCache;
import de.cebitec.mgx.gui.cache.internal.SequenceCache;
import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.MappedSequence;
import de.cebitec.mgx.gui.datamodel.Reference;
import de.cebitec.mgx.gui.datamodel.Region;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sjaenick
 */
public class CacheFactory {

    public static Cache<String> createSequenceCache(final MGXMaster master, final Reference ref) {
        final int refLength = ref.getLength() - 1;
        CacheLoader<Interval<String>, String> loader = new CacheLoader<Interval<String>, String>() {
            @Override
            public String load(Interval<String> k) {
                return master.Reference().getSequence(ref, k.getFrom(), Math.min(k.getTo(), refLength));
            }
        };

        LoadingCache<Interval<String>, String> lcache = CacheBuilder.newBuilder()
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .build(loader);
        return new SequenceCache(ref, lcache);
    }

    public static Cache<Set<Region>> createRegionCache(final MGXMaster master, final Reference ref) {

        final int refLength = ref.getLength() - 1;

        CacheLoader<Interval<Set<Region>>, Set<Region>> loader = new CacheLoader<Interval<Set<Region>>, Set<Region>>() {
            @Override
            public Set<Region> load(Interval<Set<Region>> k) {
                //Logger.getLogger(getClass().getName()).log(Level.INFO, "server fetch " + k.getFrom() + " - " + k.getTo());
                Iterator<Region> iter = master.Reference().byReferenceInterval(ref.getId(), k.getFrom(), Math.min(k.getTo(), refLength));
                Set<Region> ret = new HashSet<>();
                while (iter.hasNext()) {
                    ret.add(iter.next());
                }
                return ret;
            }
        };

        LoadingCache<Interval<Set<Region>>, Set<Region>> lcache = CacheBuilder.newBuilder()
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .build(loader);
        return new RegionCache(ref, lcache);
    }

    public static Cache<List<MappedSequence>> createMappedSequenceCache(final MGXMaster master, final Reference ref, final UUID uuid) {
        final int refLength = ref.getLength() - 1;
        CacheLoader<Interval<List<MappedSequence>>, List<MappedSequence>> loader = new CacheLoader<Interval<List<MappedSequence>>, List<MappedSequence>>() {
            @Override
            public List<MappedSequence> load(Interval<List<MappedSequence>> k) {
                Iterator<MappedSequence> iter = master.Mapping().byReferenceInterval(uuid, k.getFrom(), Math.min(k.getTo(), refLength));
                List<MappedSequence> ret = new ArrayList<>();
                while (iter.hasNext()) {
                    ret.add(iter.next());
                }
                return ret;
            }
        };

        LoadingCache<Interval<List<MappedSequence>>, List<MappedSequence>> lcache = CacheBuilder.newBuilder()
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .build(loader);
        return new MappedSequenceCache(ref, lcache);
    }
}
