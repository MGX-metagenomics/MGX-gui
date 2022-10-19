package de.cebitec.mgx.gui.mapping;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.exception.MGXTimeoutException;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.MGXReferenceI;
import de.cebitec.mgx.api.model.MappedSequenceI;
import de.cebitec.mgx.api.model.MappingI;
import de.cebitec.mgx.api.model.ModelBaseI;
import de.cebitec.mgx.api.model.ReferenceRegionI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.ToolI;
import de.cebitec.mgx.gui.cache.Cache;
import de.cebitec.mgx.gui.cache.CacheFactory;
import de.cebitec.mgx.gui.cache.CoverageInfoCache;
import de.cebitec.mgx.gui.cache.IntIterator;
import de.cebitec.mgx.pevents.ParallelPropertyChangeSupport;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.openide.util.Exceptions;

/**
 *
 * @author sjaenick
 */
public class MappingCtx implements PropertyChangeListener, AutoCloseable {

    private final MappingI mapping;
    private final MGXReferenceI ref;
    private final JobI job;
    private Cache<String> seqCache = null;
    private Cache<Set<ReferenceRegionI>> regCache = null;
    private CoverageInfoCache<Set<MappedSequenceI>> mapCache = null;
    private UUID sessionUUID = null;
    private long maxCoverage = -1;
    private long genomicCoverage = -1;
    private final int refLength;
    private final ParallelPropertyChangeSupport pcs = new ParallelPropertyChangeSupport(this);
    private volatile boolean isClosed = false;
    public static final String MAPPING_CLOSED = "MappingClosed";

    public MappingCtx(MappingI m, MGXReferenceI ref, JobI job) throws MGXException {
        this.mapping = m;
        this.ref = ref;
        this.job = job;
        this.refLength = ref.getLength();
        MGXMasterI master = m.getMaster();

        if (m.getJobID() != job.getId() || m.getReferenceID() != ref.getId() || m.getSeqrunID() != job.getSeqruns()[0].getId()) {
            throw new IllegalArgumentException("Inconsistent data, cannot create mapping context.");
        }

        // listen for data object changes
        m.addPropertyChangeListener(this);
        ref.addPropertyChangeListener(this);
        job.addPropertyChangeListener(this);
        for (SeqRunI run : job.getSeqruns()) {
            run.addPropertyChangeListener(this);
        }

        sessionUUID = master.Mapping().openMapping(m.getId());

        regCache = CacheFactory.createRegionCache(ref.getMaster(), ref);
        mapCache = CacheFactory.createMappedSequenceCache(ref.getMaster(), ref, sessionUUID);
    }

    public final int getReferenceLength() {
        return refLength;
    }

    public final MGXReferenceI getReference() {
        return ref;
    }

    public final MGXMasterI getMaster() {
        return mapping.getMaster();
    }

    public final MappingI getMapping() {
        return mapping;
    }

    public final SeqRunI[] getRuns() {
        return job.getSeqruns();
    }

    public final ToolI getTool() {
        return job.getTool();
    }

    public final JobI getJob() {
        return job;
    }

    public final String getSequence(int from, int to) throws MGXException {
        if (seqCache == null) {
            synchronized (this) {
                if (seqCache == null) {
                    seqCache = CacheFactory.createSequenceCache(ref.getMaster(), ref);
                }
            }
        }
        return seqCache.get(from, to);
    }

    public final Set<ReferenceRegionI> getRegions(int from, int to) throws MGXException {
//        if (regCache == null) {
//            synchronized (this) {
//                if (regCache == null) {
//                    regCache = CacheFactory.createRegionCache(ref.getMaster(), ref);
//                }
//            }
//        }
        if (isClosed()) {
            throw new MGXException("Mapping context is closed.");
        }
        return regCache.get(from, to);
    }

    public final List<MappedSequenceI> getMappings(final int from, final int to, final int minIdentity) throws MGXException {

        if (isClosed()) {
            throw new MGXException("Mapping context is closed.");
        }
        final Iterator<MappedSequenceI> iterator = mapCache.get(from, to).iterator();
        List<MappedSequenceI> sortedMappings = new ArrayList<>();
        while (iterator.hasNext()) {
            MappedSequenceI candidate = iterator.next();
            if (candidate.getIdentity() >= minIdentity) {
                sortedMappings.add(candidate);
            }
        }

        //
        // sort by identity (highest first) and by min position, ascending
        //
        Collections.sort(sortedMappings, new Comparator<MappedSequenceI>() {
            @Override
            public int compare(MappedSequenceI o1, MappedSequenceI o2) {
                int ret = Float.compare(o2.getIdentity(), o1.getIdentity());
                if (ret != 0) {
                    return ret;
                }
                return Integer.compare(o1.getMin(), o2.getMin());
            }
        });
        return sortedMappings;
    }

    public final void getCoverage(final int from, final int to, final int[] dest) throws MGXException {
//        if (mapCache == null) {
//            synchronized (this) {
//                if (mapCache == null) {
//                    mapCache = CacheFactory.createMappedSequenceCache(ref.getMaster(), ref, sessionUUID);
//                }
//            }
//        }
        mapCache.getCoverage(from, to, dest);
    }

    public final IntIterator getCoverageIterator(final int from, final int to) throws MGXException {
//        if (mapCache == null) {
//            synchronized (this) {
//                if (mapCache == null) {
//                    mapCache = CacheFactory.createMappedSequenceCache(ref.getMaster(), ref, sessionUUID);
//
//                }
//            }
//        }
        return mapCache.getCoverageIterator(from, to);
    }

    public final long getMaxCoverage() throws MGXException {
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

    public final long getGenomicCoverage() throws MGXException {
        if (genomicCoverage == -1) {
            synchronized (this) {
                if (genomicCoverage == -1) {
                    final MGXMasterI master = ref.getMaster();
                    genomicCoverage = master.Mapping().getGenomicCoverage(sessionUUID);
                }
            }
        }
        return genomicCoverage;
    }

    public final void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public final void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    @Override
    public synchronized void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(ModelBaseI.OBJECT_DELETED)) {
            close();
        }
    }

    public final boolean isClosed() {
        return isClosed;
    }

    @Override
    public final synchronized void close() {
        if (!isClosed()) {
            isClosed = true;
            // close caches first
            if (seqCache != null) {
                seqCache.close();
                seqCache = null;
            }
            if (regCache != null) {
                regCache.close();
                regCache = null;
            }
            if (mapCache != null) {
                mapCache.close();
                mapCache = null;
            }

            try {
                MGXMasterI master = ref.getMaster();
                if (!master.isDeleted() && !mapping.isDeleted()) {
                    try {
                        master.Mapping().closeMapping(sessionUUID);
                    } catch (MGXTimeoutException mte) {
                        // session already closed on server, silently ignore
                    }
                }
            } catch (MGXException ex) {
                Exceptions.printStackTrace(ex);
            }
            mapping.removePropertyChangeListener(this);
            ref.removePropertyChangeListener(this);
            for (SeqRunI run : job.getSeqruns()) {
                run.removePropertyChangeListener(this);
            }
            job.removePropertyChangeListener(this);

            pcs.firePropertyChange(MAPPING_CLOSED, false, true);
            pcs.close();
        }
    }

}
