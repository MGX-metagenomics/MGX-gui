package de.cebitec.mgx.gui.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.MGXReferenceI;
import de.cebitec.mgx.api.model.MappedSequenceI;
import de.cebitec.mgx.api.model.ReferenceRegionI;
import de.cebitec.mgx.gui.cache.internal.MappedSequenceCache;
import de.cebitec.mgx.gui.cache.internal.RegionCache;
import de.cebitec.mgx.gui.cache.internal.SequenceCache;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.apache.commons.math3.util.FastMath;

/**
 *
 * @author sjaenick
 */
public class CacheFactory {

    public static Cache<String> createSequenceCache(final MGXMasterI master, final MGXReferenceI ref) {
        final int refLength = ref.getLength() - 1;
        CacheLoader<Interval, String> loader = new CacheLoader<Interval, String>() {
            @Override
            public String load(Interval k) throws MGXException {
                return master.Reference().getSequence(ref, k.getFrom(), FastMath.min(k.getTo(), refLength));
            }
        };

        LoadingCache<Interval, String> lcache = CacheBuilder.newBuilder()
                .expireAfterAccess(30, TimeUnit.SECONDS)
                .build(loader);
        return new SequenceCache(ref, lcache);
    }

    public static Cache<Set<ReferenceRegionI>> createRegionCache(final MGXMasterI master, final MGXReferenceI ref) {

        final int refLength = ref.getLength() - 1;

        CacheLoader<Interval, Set<ReferenceRegionI>> loader = new CacheLoader<Interval, Set<ReferenceRegionI>>() {
            @Override
            public Set<ReferenceRegionI> load(Interval k) throws MGXException {
                Iterator<ReferenceRegionI> iter = master.ReferenceRegion().byReferenceInterval(ref, k.getFrom(), FastMath.min(k.getTo(), refLength));
                Set<ReferenceRegionI> ret = new HashSet<>();
                while (iter.hasNext()) {
                    ret.add(iter.next());
                }
                return ret;
            }
        };

        LoadingCache<Interval, Set<ReferenceRegionI>> lcache = CacheBuilder.newBuilder()
                .expireAfterAccess(30, TimeUnit.SECONDS)
                .build(loader);
        return new RegionCache(ref, lcache);
    }

    public static CoverageInfoCache<Set<MappedSequenceI>> createMappedSequenceCache(final MGXMasterI master, final MGXReferenceI ref, final UUID uuid) {
        final int refLength = ref.getLength() - 1;
        CacheLoader<Interval, Set<MappedSequenceI>> loader = new CacheLoader<Interval, Set<MappedSequenceI>>() {
            @Override
            public Set<MappedSequenceI> load(Interval k) throws MGXException {
                Iterator<MappedSequenceI> iter = master.Mapping().byReferenceInterval(uuid, k.getFrom(), FastMath.min(k.getTo(), refLength));
                Set<MappedSequenceI> ret = new HashSet<>();
                while (iter.hasNext()) {
                    ret.add(iter.next());
                }
                return ret;
            }
        };

        LoadingCache<Interval, Set<MappedSequenceI>> lcache = CacheBuilder.newBuilder()
                .expireAfterAccess(30, TimeUnit.SECONDS)
                .build(loader);
        return new MappedSequenceCache(ref, lcache);
    }
}
