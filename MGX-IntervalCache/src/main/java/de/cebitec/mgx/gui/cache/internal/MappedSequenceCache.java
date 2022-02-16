/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.cache.internal;

import de.cebitec.mgx.gui.cache.Interval;
import com.google.common.cache.LoadingCache;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.exception.MGXTimeoutException;
import de.cebitec.mgx.api.model.MGXReferenceI;
import de.cebitec.mgx.api.model.MappedSequenceI;
import de.cebitec.mgx.gui.cache.CoverageInfoCache;
import de.cebitec.mgx.gui.cache.IntIterator;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import org.apache.commons.math3.util.FastMath;

/**
 *
 * @author belmann
 */
public class MappedSequenceCache extends CoverageInfoCache<Set<MappedSequenceI>> {

    public MappedSequenceCache(MGXReferenceI ref, LoadingCache<Interval, Set<MappedSequenceI>> lcache) {
        super(ref, lcache);
    }

    public MappedSequenceCache(MGXReferenceI ref, LoadingCache<Interval, Set<MappedSequenceI>> lcache, int segSize) {
        super(ref, lcache, segSize);
    }

    @Override
    public Set<MappedSequenceI> getInternal(final int from, final int to) throws MGXException {
        if (from < 0 || from > to || to > ref.getLength() - 1) {
            throw new IllegalArgumentException();
        }
        final Iterator<Interval> iter = getIntervals(from, to);
        
        // a set is needed here to avoid duplicate mapped sequences, since a
        // sequence can be present in two intervals when e.g. the alignment
        // spans across the interval boundary
        final Set<MappedSequenceI> mappedSequences = new TreeSet<>();
        try {
            while (iter.hasNext()) {
                Interval i = iter.next();
                Set<MappedSequenceI> get = lcache.get(i);
                for (MappedSequenceI seq : get) {
                    if (overlaps(seq, from, to)) {
                        mappedSequences.add(seq);
                    }
                }
            }
        } catch (ExecutionException ex) {
            if (ex.getCause() instanceof MGXException) {
                throw (MGXException) ex.getCause();
            }
            throw new MGXException(ex);
        }
        return mappedSequences;
    }

    @Override
    public void getCoverage(int from, int to, final int[] dest) throws MGXException {
        if (from < 0 || from > to) {
            throw new IllegalArgumentException();
        }
        to = FastMath.min(ref.getLength() - 1, to);
        if (dest.length < to - from + 1) {
            throw new IllegalArgumentException("Destination array too small.");
        }
        Arrays.fill(dest, 0);

        Iterator<Interval> iter = getIntervals(from, to);
        try {
            while (iter.hasNext()) {
                Interval interval = iter.next();
                Set<MappedSequenceI> get = lcache.get(interval);
                for (MappedSequenceI ms : get) {
                    for (int i = ms.getMin(); i <= ms.getMax(); i++) {
                        // we need to check extra since we also receive mappings
                        // which only partially overlap with the interval
                        if (i >= from && i <= to) {
                            dest[i - from]++;
                        }
                    }
                }

            }
        } catch (ExecutionException ex) {
            Throwable cause = ex.getCause();
            if (cause instanceof MGXTimeoutException) {
                if (isClosed()) {
                    return;
                } else {
                    throw (MGXTimeoutException) cause;
                }
            } else if (cause instanceof MGXException) {
                throw (MGXException) cause;
            }
            throw new MGXException(ex);
        }
    }

    @Override
    public IntIterator getCoverageIterator(int from, int to) throws MGXException {
        if (from < 0 || from > to || to > ref.getLength() - 1) {
            throw new IllegalArgumentException();
        }
        return new IntIterator(from, FastMath.min(to, ref.getLength() - 1), this);
    }

    @Override
    public int getMaxCoverage(int from, int to) throws MGXException {
        if (from < 0 || from > to || to > ref.getLength() - 1) {
            throw new IllegalArgumentException();
        }
        Iterator<Interval> iter = getIntervals(from, to);
        int ret = 0;
        int[] cov = null;

        try {
            while (iter.hasNext()) {
                Interval interval = iter.next();
                if (cov == null) {
                    cov = new int[interval.length()];
                }
                Arrays.fill(cov, 0);

                Set<MappedSequenceI> get = lcache.get(interval);
                for (MappedSequenceI seq : get) {
                    if (overlaps(seq, from, to)) {
                        for (int i = seq.getMin(); i <= seq.getMax(); i++) {
                            int offset = i - interval.getFrom();
                            if (offset >= 0 && offset < interval.length()) {
                                cov[offset]++;
                            }
                        }
                    }
                }

                int x = 0;
                for (int c : cov) {
                    if (c > ret) {
                        ret = c;
                    }
                    x++;
                }
            }

        } catch (ExecutionException ex) {
            if (ex.getCause() != null && ex.getCause() instanceof MGXException) {
                throw (MGXException) ex.getCause();
            }
            throw new MGXException(ex);
        }
        return ret;
    }

    static boolean overlaps(MappedSequenceI r, int from, int to) {
        int min = r.getMin();
        int max = r.getMax();

        return (min >= from && min <= to) // start in interval
                || (max >= from && max <= to) // stop in interval
                || (min <= from && max >= to); // mapping longer than interval
        // || (min >= from && max <= to);   // mapping within interval
    }
}
