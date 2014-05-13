package de.cebitec.mgx.gui.mapping;

import de.cebitec.mgx.gui.cache.Cache;
import de.cebitec.mgx.gui.cache.CacheFactory;
import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.Job;
import de.cebitec.mgx.gui.datamodel.MappedSequence;
import de.cebitec.mgx.gui.datamodel.Mapping;
import de.cebitec.mgx.gui.datamodel.Reference;
import de.cebitec.mgx.gui.datamodel.Region;
import de.cebitec.mgx.gui.datamodel.SeqRun;
import de.cebitec.mgx.gui.datamodel.Tool;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

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
    private Cache<List<MappedSequence>> mapCache = null;
    private UUID sessionUUID = null;

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

    public String getSequence(int from, int to) throws ExecutionException {
        if (seqCache == null) {
            synchronized (this) {
                if (seqCache == null) {
                    seqCache = CacheFactory.createSequenceCache((MGXMaster) ref.getMaster(), ref);
                }
            }
        }
        return seqCache.get(from, to);
    }

    public Set<Region> getRegions(int from, int to) throws ExecutionException {
        if (regCache == null) {
            regCache = CacheFactory.createRegionCache((MGXMaster) ref.getMaster(), ref);
        }
        return regCache.get(from, to);
    }

    public List<MappedSequence> getMappings(int from, int to) throws ExecutionException {
        if (mapCache == null) {
            synchronized (this) {
                if (mapCache == null) {
                    mapCache = CacheFactory.createMappedSequenceCache((MGXMaster) ref.getMaster(), ref, sessionUUID);
                }
            }
        }
        return mapCache.get(from, to);
    }
}
