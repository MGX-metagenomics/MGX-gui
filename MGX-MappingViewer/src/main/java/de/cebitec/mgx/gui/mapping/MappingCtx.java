package de.cebitec.mgx.gui.mapping;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.MGXReferenceI;
import de.cebitec.mgx.api.model.MappedSequenceI;
import de.cebitec.mgx.api.model.MappingI;
import de.cebitec.mgx.api.model.RegionI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.ToolI;
import de.cebitec.mgx.gui.cache.Cache;
import de.cebitec.mgx.gui.cache.CacheFactory;
import de.cebitec.mgx.gui.cache.CoverageInfoCache;
import de.cebitec.mgx.gui.cache.IntIterator;
import de.cebitec.mgx.gui.swingutils.NonEDT;
import java.awt.EventQueue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;
import org.openide.util.Exceptions;

/**
 *
 * @author sjaenick
 */
public class MappingCtx {

    private final MappingI m;
    private final MGXReferenceI ref;
    private final JobI job;
    private final SeqRunI run;
    private Cache<String> seqCache = null;
    private Cache<Set<RegionI>> regCache = null;
    private CoverageInfoCache<SortedSet<MappedSequenceI>> mapCache = null;
    private UUID sessionUUID = null;
    private long maxCoverage = -1;

    public MappingCtx(MappingI m, MGXReferenceI ref, JobI job, SeqRunI run) throws MGXException {
        assert m.getReferenceID() == ref.getId();
        this.m = m;
        this.ref = ref;
        this.job = job;
        this.run = run;
        MGXMasterI master = m.getMaster();
        sessionUUID = master.Mapping().openMapping(m.getId());
    }

    public MGXReferenceI getReference() {
        return ref;
    }

    public MGXMasterI getMaster() {
        return m.getMaster();
    }

    public MappingI getMapping() {
        return m;
    }

    public SeqRunI getRun() {
        return run;
    }

    public ToolI getTool() {
        return job.getTool();
    }

    public String getSequence(int from, int to) {
        if (seqCache == null) {
            synchronized (this) {
                if (seqCache == null) {
                    seqCache = CacheFactory.createSequenceCache(ref.getMaster(), ref);
                }
            }
        }
        return seqCache.get(from, to);
    }

    public Set<RegionI> getRegions(int from, int to) {
        if (regCache == null) {
            synchronized (this) {
                if (regCache == null) {
                    regCache = CacheFactory.createRegionCache(ref.getMaster(), ref);
                }
            }
        }
        return regCache.get(from, to);
    }

    public SortedSet<MappedSequenceI> getMappings(final int from, final int to) {
        if (mapCache == null) {
            synchronized (this) {
                if (mapCache == null) {
                    mapCache = CacheFactory.createMappedSequenceCache(ref.getMaster(), ref, sessionUUID);
                }
            }
        }
        if (EventQueue.isDispatchThread()) {
            final SortedSet<MappedSequenceI> ret = new TreeSet<>();
            NonEDT.invokeAndWait(new Runnable() {

                @Override
                public void run() {
                    SortedSet<MappedSequenceI> get = mapCache.get(from, to);
                    ret.addAll(get);
                }
            });
            return ret;
        } else {
            return mapCache.get(from, to);
        }
    }

    public void getCoverage(final int from, final int to, final int[] dest) {
        if (mapCache == null) {
            synchronized (this) {
                if (mapCache == null) {
                    mapCache = CacheFactory.createMappedSequenceCache(ref.getMaster(), ref, sessionUUID);
                }
            }
        }
        if (EventQueue.isDispatchThread()) {
            NonEDT.invokeAndWait(new Runnable() {

                @Override
                public void run() {
                    mapCache.getCoverage(from, to, dest);
                }
            });

        } else {
            mapCache.getCoverage(from, to, dest);
        }
    }

    public IntIterator getCoverageIterator(int from, int to) {
        if (mapCache == null) {
            synchronized (this) {
                if (mapCache == null) {
                    mapCache = CacheFactory.createMappedSequenceCache(ref.getMaster(), ref, sessionUUID);

                }
            }
        }
        return mapCache.getCoverageIterator(from, to);
    }

    public long getMaxCoverage() {
        if (maxCoverage == -1) {
            synchronized (this) {
                if (maxCoverage == -1) {
                    final MGXMasterI master = ref.getMaster();
                    NonEDT.invokeAndWait(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                maxCoverage = master.Mapping().getMaxCoverage(sessionUUID);
                            } catch (MGXException ex) {
                                Exceptions.printStackTrace(ex);
                            }
                        }
                    });
                    //maxCoverage = master.Mapping().getMaxCoverage(sessionUUID);
                }
            }
        }
        return maxCoverage;
    }

}
