package de.cebitec.mgx.gui.mapping;

import de.cebitec.mgx.gui.cache.Cache;
import de.cebitec.mgx.gui.cache.CacheFactory;
import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.Job;
import de.cebitec.mgx.gui.datamodel.Mapping;
import de.cebitec.mgx.gui.datamodel.Reference;
import de.cebitec.mgx.gui.datamodel.SeqRun;
import de.cebitec.mgx.gui.datamodel.Tool;
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

    public MappingCtx(Mapping m, Reference ref, Job job) {
        assert m.getReferenceID() == ref.getId();
        this.m = m;
        this.ref = ref;
        this.job = job; 
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
}
