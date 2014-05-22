/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.cache.internal;

import com.google.common.cache.LoadingCache;
import de.cebitec.mgx.gui.cache.CoverageInfoCache;
import de.cebitec.mgx.gui.cache.IntIterator;
import de.cebitec.mgx.gui.datamodel.MappedSequence;
import de.cebitec.mgx.gui.datamodel.Reference;
import java.awt.EventQueue;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 * @author belmann
 */
public class MappedSequenceCache extends CoverageInfoCache<SortedSet<MappedSequence>> {

    public MappedSequenceCache(Reference ref, LoadingCache<Interval, SortedSet<MappedSequence>> lcache) {
        super(ref, lcache);
    }

    public MappedSequenceCache(Reference ref, LoadingCache<Interval, SortedSet<MappedSequence>> lcache, int segSize) {
        super(ref, lcache, segSize);
    }

    @Override
    public SortedSet<MappedSequence> get(int from, int to) {
        assert from >= 0;
        assert to < ref.getLength();
        Iterator<Interval> iter = getIntervals(from, to);
        SortedSet<MappedSequence> mappedSequences = new TreeSet<>();
        while (iter.hasNext()) {
            Set<MappedSequence> get = lcache.getUnchecked(iter.next());
            for (MappedSequence seq : get) {
                if (overlaps(seq, from, to)) {
                    mappedSequences.add(seq);
                }
            }
        }
        return mappedSequences;
    }

    @Override
    public void getCoverage(int from, int to, int[] dest) {
        assert from >= 0;
        to = Math.min(ref.getLength() - 1, to);
        if (dest.length < to - from + 1) {
            throw new IllegalArgumentException("Destination array too small.");
        }
        Arrays.fill(dest, 0);
        Iterator<Interval> iter = getIntervals(from, to);
        while (iter.hasNext()) {
            Interval interval = iter.next();
            Set<MappedSequence> get = lcache.getUnchecked(interval);
            for (MappedSequence ms : get) {
                for (int i = ms.getStart(); i < ms.getStop(); i++) {
                    // we need to check extra since we also receive mappings
                    // which only partially overlap with the interval
                    if (i >= from && i <= to) {
                        dest[i - from]++;
                    }
                }
            }

        }
    }

    @Override
    public IntIterator getCoverageIterator(int from, int to) {
        assert !EventQueue.isDispatchThread();
        return new IntIterator(from, Math.min(to, ref.getLength() - 1), this);
    }

    @Override
    public int getMaxCoverage(int from, int to) {
        Iterator<Interval> iter = getIntervals(from, to);
        int ret = 0;
        while (iter.hasNext()) {
            Interval interval = iter.next();
            int[] cov = new int[interval.length()];
            Arrays.fill(cov, 0);
            Set<MappedSequence> get = lcache.getUnchecked(iter.next());
            for (MappedSequence seq : get) {
                if (overlaps(seq, from, to)) {
                    for (int i = seq.getStart(); i <= seq.getStop(); i++) {
                        cov[i - seq.getStart()]++;
                    }
                }
            }
            for (int c : cov) {
                if (c > ret) {
                    ret = c;
                }
            }
        }
        return ret;
    }

    private static boolean overlaps(MappedSequence r, int from, int to) {
        return (r.getStart() >= from && r.getStart() <= to)
                || (r.getStop() >= from && r.getStop() <= to)
                || (r.getStart() <= from && r.getStop() >= to)
                || (r.getStop() <= from && r.getStart() >= to);
    }
}
