package de.cebitec.mgx.gui.mapping;

import de.cebitec.mgx.gui.cache.Cache;
import de.cebitec.mgx.gui.cache.CacheFactory;
import de.cebitec.mgx.gui.cache.CoverageInfoCache;
import de.cebitec.mgx.gui.cache.IntIterator;
import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.Job;
import de.cebitec.mgx.gui.datamodel.MappedSequence;
import de.cebitec.mgx.gui.datamodel.Mapping;
import de.cebitec.mgx.gui.datamodel.Reference;
import de.cebitec.mgx.gui.datamodel.Region;
import de.cebitec.mgx.gui.datamodel.SeqRun;
import de.cebitec.mgx.gui.datamodel.Tool;
import de.cebitec.mgx.gui.swingutils.NonEDT;
import java.util.Set;
import java.util.SortedSet;
import java.util.UUID;

/**
 *
 * @author sjaenick
 */
public class MappingCtx {

    private final Mapping m;
    private final Reference ref;
    private final Job job;
    private Cache<String> seqCache = null;
    private Cache<Set<Region>> regCache = null;
    private CoverageInfoCache<SortedSet<MappedSequence>> mapCache = null;
    private UUID sessionUUID = null;
    private long maxCoverage = -1;

    public MappingCtx(Mapping m, Reference ref, Job job) {
        assert m.getReferenceID() == ref.getId();
        this.m = m;
        this.ref = ref;
        this.job = job;
        MGXMaster master = (MGXMaster) m.getMaster();
        sessionUUID = master.Mapping().openMapping(m.getId());
    }

    public Reference getReference() {
        return ref;
    }

    public MGXMaster getMaster() {
        return (MGXMaster) m.getMaster();
    }

    public Mapping getMapping() {
        return m;
    }

    public SeqRun getRun() {
        return job.getSeqrun();
    }

    public Tool getTool() {
        return job.getTool();
    }

    public String getSequence(int from, int to) {
        if (seqCache == null) {
            synchronized (this) {
                if (seqCache == null) {
                    seqCache = CacheFactory.createSequenceCache((MGXMaster) ref.getMaster(), ref);
                }
            }
        }
        return seqCache.get(from, to);
    }

    public Set<Region> getRegions(int from, int to) {
        if (regCache == null) {
            synchronized (this) {
                if (regCache == null) {
                    regCache = CacheFactory.createRegionCache((MGXMaster) ref.getMaster(), ref);
                }
            }
        }
        return regCache.get(from, to);
    }

    public SortedSet<MappedSequence> getMappings(int from, int to) {
        if (mapCache == null) {
            synchronized (this) {
                if (mapCache == null) {
                    mapCache = CacheFactory.createMappedSequenceCache((MGXMaster) ref.getMaster(), ref, sessionUUID);
                }
            }
        }
        return mapCache.get(from, to);
    }

    public void getCoverage(int from, int to, int[] dest) {
        if (mapCache == null) {
            synchronized (this) {
                if (mapCache == null) {
                    mapCache = CacheFactory.createMappedSequenceCache((MGXMaster) ref.getMaster(), ref, sessionUUID);
                }
            }
        }
        mapCache.getCoverage(from, to, dest);
    }

    public IntIterator getCoverageIterator(int from, int to) {
        if (mapCache == null) {
            synchronized (this) {
                if (mapCache == null) {
                    mapCache = CacheFactory.createMappedSequenceCache((MGXMaster) ref.getMaster(), ref, sessionUUID);

                }
            }
        }
        return mapCache.getCoverageIterator(from, to);
    }

    public long getMaxCoverage() {
        if (maxCoverage == -1) {
            synchronized (this) {
                if (maxCoverage == -1) {
                    final MGXMaster master = (MGXMaster) ref.getMaster();
                    NonEDT.invokeAndWait(new Runnable() {

                        @Override
                        public void run() {
                             maxCoverage = master.Mapping().getMaxCoverage(sessionUUID);
                        }
                    });
                    //maxCoverage = master.Mapping().getMaxCoverage(sessionUUID);
                }
            }
        }
        return maxCoverage;
    }

}
