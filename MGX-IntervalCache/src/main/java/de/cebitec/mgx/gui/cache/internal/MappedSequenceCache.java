/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.cache.internal;

import com.google.common.cache.LoadingCache;
import de.cebitec.mgx.api.model.MGXReferenceI;
import de.cebitec.mgx.api.model.MappedSequenceI;
import de.cebitec.mgx.gui.cache.CoverageInfoCache;
import de.cebitec.mgx.gui.cache.IntIterator;
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
public class MappedSequenceCache extends CoverageInfoCache<SortedSet<MappedSequenceI>> {

    public MappedSequenceCache(MGXReferenceI ref, LoadingCache<Interval, SortedSet<MappedSequenceI>> lcache) {
        super(ref, lcache);
    }

    public MappedSequenceCache(MGXReferenceI ref, LoadingCache<Interval, SortedSet<MappedSequenceI>> lcache, int segSize) {
        super(ref, lcache, segSize);
    }

    @Override
    public SortedSet<MappedSequenceI> get(int from, int to) {
        if (from < 0 || from > to || to > ref.getLength() - 1) {
            throw new IllegalArgumentException();
        }
        Iterator<Interval> iter = getIntervals(from, to);
        SortedSet<MappedSequenceI> mappedSequences = new TreeSet<>();
        while (iter.hasNext()) {
            Interval i = iter.next();
            Set<MappedSequenceI> get = lcache.getUnchecked(i);
            for (MappedSequenceI seq : get) {
                if (overlaps(seq, from, to)) {
//                    if (mappedSequences.contains(seq)) {
//                        System.err.println("already there?");
//                    }
                    mappedSequences.add(seq);
//                } else {
//                    System.err.println(seq.getSeqId() + ": " + seq.getStart() + "-" + seq.getStop() + " outside of " + from + "-" + to);
                }
            }
        }
        return mappedSequences;
    }

    @Override
    public void getCoverage(int from, int to, int[] dest) {
        if (from < 0 || from > to) {
            throw new IllegalArgumentException();
        }
        to = Math.min(ref.getLength() - 1, to);
        if (dest.length < to - from + 1) {
            throw new IllegalArgumentException("Destination array too small.");
        }
        Arrays.fill(dest, 0);
        Iterator<Interval> iter = getIntervals(from, to);
        while (iter.hasNext()) {
            Interval interval = iter.next();
            Set<MappedSequenceI> get = lcache.getUnchecked(interval);
            for (MappedSequenceI ms : get) {
                for (int i = ms.getMin(); i < ms.getMax(); i++) {
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
        if (from < 0 || from > to || to > ref.getLength() - 1) {
            throw new IllegalArgumentException();
        }
        assert !EventQueue.isDispatchThread();
        return new IntIterator(from, Math.min(to, ref.getLength() - 1), this);
    }

    @Override
    public int getMaxCoverage(int from, int to) {
        if (from < 0 || from > to || to > ref.getLength() - 1) {
            throw new IllegalArgumentException();
        }
        Iterator<Interval> iter = getIntervals(from, to);
        int ret = 0;
        int[] cov = null;
        while (iter.hasNext()) {
            Interval interval = iter.next();
            if (cov == null) {
                cov = new int[interval.length()];
            }
            Arrays.fill(cov, 0);

            Set<MappedSequenceI> get = lcache.getUnchecked(interval);
            for (MappedSequenceI seq : get) {
                if (overlaps(seq, from, to)) {
                    for (int i = seq.getMin(); i <= seq.getMax(); i++) {
                        int offset = i - interval.getFrom();
                        if (offset >= 0 && offset < interval.length()) {
                            cov[offset]++;
                        }
                    }
                } else {
                    //System.err.println(seq.getStart()+"-"+seq.getStop()+" outside of "+interval.getFrom()+"-"+interval.getTo());
                }
            }

            int x = 0;
            for (int c : cov) {
                if (c > ret) {
                    ret = c;
                    //System.err.println("max " + ret + " at position " + (interval.getFrom() + x));
                }
                x++;
            }
            //System.err.println("max " + ret + " after interval " + interval.getFrom() + "-" + interval.getTo());
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
