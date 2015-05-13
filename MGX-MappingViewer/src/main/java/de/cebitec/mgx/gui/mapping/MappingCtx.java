package de.cebitec.mgx.gui.mapping;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.MGXReferenceI;
import de.cebitec.mgx.api.model.MappedSequenceI;
import de.cebitec.mgx.api.model.MappingI;
import de.cebitec.mgx.api.model.ModelBase;
import de.cebitec.mgx.api.model.RegionI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.ToolI;
import de.cebitec.mgx.gui.cache.Cache;
import de.cebitec.mgx.gui.cache.CacheFactory;
import de.cebitec.mgx.gui.cache.CoverageInfoCache;
import de.cebitec.mgx.gui.cache.IntIterator;
import de.cebitec.mgx.pevents.ParallelPropertyChangeSupport;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Set;
import java.util.SortedSet;
import java.util.UUID;
import org.openide.util.Exceptions;

/**
 *
 * @author sjaenick
 */
public class MappingCtx implements PropertyChangeListener, AutoCloseable {

    private final MappingI m;
    private final MGXReferenceI ref;
    private final JobI job;
    private final SeqRunI run;
    private Cache<String> seqCache = null;
    private Cache<Set<RegionI>> regCache = null;
    private CoverageInfoCache<SortedSet<MappedSequenceI>> mapCache = null;
    private UUID sessionUUID = null;
    private long maxCoverage = -1;
    private final PropertyChangeSupport pcs = new ParallelPropertyChangeSupport(this);

    public MappingCtx(MappingI m, MGXReferenceI ref, JobI job, SeqRunI run) throws MGXException {
        assert m.getReferenceID() == ref.getId();
        this.m = m;
        this.ref = ref;
        this.job = job;
        this.run = run;
        MGXMasterI master = m.getMaster();

        if (m.getJobID() != job.getId() || m.getReferenceID() != ref.getId() || m.getSeqrunID() != run.getId()) {
            throw new IllegalArgumentException("Inconsistent data, cannot create mapping context.");
        }

        // listen for data object changes
        m.addPropertyChangeListener(this);
        ref.addPropertyChangeListener(this);
        job.addPropertyChangeListener(this);
        run.addPropertyChangeListener(this);

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

    public String getSequence(int from, int to) throws MGXException {
        if (seqCache == null) {
            synchronized (this) {
                if (seqCache == null) {
                    seqCache = CacheFactory.createSequenceCache(ref.getMaster(), ref);
                }
            }
        }
        return seqCache.get(from, to);
    }

    public Set<RegionI> getRegions(int from, int to) throws MGXException {
        if (regCache == null) {
            synchronized (this) {
                if (regCache == null) {
                    regCache = CacheFactory.createRegionCache(ref.getMaster(), ref);
                }
            }
        }
        return regCache.get(from, to);
    }

    public SortedSet<MappedSequenceI> getMappings(final int from, final int to) throws MGXException {
        if (mapCache == null) {
            synchronized (this) {
                if (mapCache == null) {
                    mapCache = CacheFactory.createMappedSequenceCache(ref.getMaster(), ref, sessionUUID);
                }
            }
        }
        return mapCache.get(from, to);
    }

    public void getCoverage(final int from, final int to, final int[] dest) throws MGXException {
        if (mapCache == null) {
            synchronized (this) {
                if (mapCache == null) {
                    mapCache = CacheFactory.createMappedSequenceCache(ref.getMaster(), ref, sessionUUID);
                }
            }
        }
        mapCache.getCoverage(from, to, dest);
    }

    public IntIterator getCoverageIterator(final int from, final int to) throws MGXException {
        if (mapCache == null) {
            synchronized (this) {
                if (mapCache == null) {
                    mapCache = CacheFactory.createMappedSequenceCache(ref.getMaster(), ref, sessionUUID);

                }
            }
        }
        return mapCache.getCoverageIterator(from, to);
    }

    public long getMaxCoverage() throws MGXException {
        if (maxCoverage == -1) {
            synchronized (this) {
                if (maxCoverage == -1) {
                    final MGXMasterI master = ref.getMaster();
                    maxCoverage = master.Mapping().getMaxCoverage(sessionUUID);
                }
            }
        }
        return maxCoverage;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(ModelBase.OBJECT_DELETED)) {
            close();
        }
    }

    @Override
    public void close() {
        try {
            ref.getMaster().Mapping().closeMapping(sessionUUID);
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
        }
        m.removePropertyChangeListener(this);
        ref.removePropertyChangeListener(this);
        run.removePropertyChangeListener(this);
        job.removePropertyChangeListener(this);
        pcs.firePropertyChange(ModelBase.OBJECT_DELETED, 0, 1);
    }

}
