/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.cache.internal;

import com.google.common.cache.LoadingCache;
import de.cebitec.mgx.gui.cache.CoverageInfoCache;
import de.cebitec.mgx.gui.datamodel.MappedSequence;
import de.cebitec.mgx.gui.datamodel.Reference;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author belmann
 */
public class MappedSequenceCache extends CoverageInfoCache<Set<MappedSequence>> {

    public MappedSequenceCache(Reference ref, LoadingCache<Interval<Set<MappedSequence>>, Set<MappedSequence>> lcache) {
        super(ref, lcache);
    }

    public MappedSequenceCache(Reference ref, LoadingCache<Interval<Set<MappedSequence>>, Set<MappedSequence>> lcache, int segSize) {
        super(ref, lcache, segSize);
    }

    @Override
    public Set<MappedSequence> get(int from, int to) {
        Iterator<Interval<Set<MappedSequence>>> iter = getIntervals(from, to);
        Set<MappedSequence> mappedSequences = new HashSet<>();
        while (iter.hasNext()) {
            Set<MappedSequence> get = lcache.getUnchecked(iter.next());
            for (MappedSequence seq : get) {
                mappedSequences.add(seq);
            }
        }
        return mappedSequences;
    }

    @Override
    public int getMaxCoverage(int from, int to) {
        Iterator<Interval<Set<MappedSequence>>> iter = getIntervals(from, to);
        int ret = 0;
        while (iter.hasNext()) {
            Interval<Set<MappedSequence>> interval = iter.next();
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
