package de.cebitec.mgx.gui.cache;

import de.cebitec.mgx.gui.cache.internal.Interval;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
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

/**
 *
 * @author sjaenick
 */
public class CacheFactory {

    public static Cache<String> createSequenceCache(final MGXMaster master, final Reference ref) {

        CacheLoader<Interval<String>, String> loader = new CacheLoader<Interval<String>, String>() {
            @Override
            public String load(Interval<String> k) throws Exception {
                String seq = master.Reference().getSequence(ref, k.getFrom(), k.getTo());
                //System.err.println("segment "+k.getFrom()+"-"+k.getTo()+" "+ seq);
                return seq;
            }
        };

        LoadingCache<Interval<String>, String> lcache = CacheBuilder.newBuilder()
                .weakKeys()
                .build(loader);
        return new SequenceCache(ref, lcache);
    }

    public static Cache<Set<Region>> createRegionCache(final MGXMaster master, final Reference ref) {

        CacheLoader<Interval<Set<Region>>, Set<Region>> loader = new CacheLoader<Interval<Set<Region>>, Set<Region>>() {
            @Override
            public Set<Region> load(Interval<Set<Region>> k) throws Exception {

                int to = k.getTo();
                if (ref.getLength() < to) {
                    to = ref.getLength() - 1;
                }
                Iterator<Region> iter = master.Reference().byReferenceInterval(ref.getId(), k.getFrom(), to);
                Set<Region> ret = new HashSet<>();
                while (iter.hasNext()) {
                    ret.add(iter.next());
                }
                return ret;
            }
        };

        LoadingCache<Interval<Set<Region>>, Set<Region>> lcache = CacheBuilder.newBuilder()
                .weakKeys()
                .build(loader);
        return new RegionCache(ref, lcache);
    }

    public static Cache<List<MappedSequence>> createMappedSequenceCache(final MGXMaster master, final Reference ref, final UUID uuid) {

        CacheLoader<Interval<List<MappedSequence>>, List<MappedSequence>> loader = new CacheLoader<Interval<List<MappedSequence>>, List<MappedSequence>>() {
            @Override
            public List<MappedSequence> load(Interval<List<MappedSequence>> k) throws Exception {

                int to = k.getTo();
                if (ref.getLength() < to) {
                    to = ref.getLength() - 1;
                }
                Iterator<MappedSequence> iter = master.Mapping().byReferenceInterval(uuid, k.getFrom(), to);
                List<MappedSequence> ret = new ArrayList<>();
                while (iter.hasNext()) {
                    ret.add(iter.next());
                }
                return ret;
            }
        };

        LoadingCache<Interval<List<MappedSequence>>, List<MappedSequence>> lcache = CacheBuilder.newBuilder()
                .weakKeys()
                .build(loader);
        return new MappedSequenceCache(ref, lcache);
    }
}
