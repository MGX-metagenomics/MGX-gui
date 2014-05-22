/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.cache;

import de.cebitec.mgx.gui.cache.internal.Interval;
import de.cebitec.mgx.gui.datamodel.MappedSequence;
import java.util.Arrays;
import java.util.Iterator;
import java.util.SortedSet;

/**
 *
 * @author sj
 */
public class IntIterator {

    private final int to;
    private int curPos;
    private final CoverageInfoCache<SortedSet<MappedSequence>> lcache;
    private int[] coverage;
    private final Iterator<Interval> iter;
    private Interval curInterval;

    public IntIterator(int from, int to, CoverageInfoCache<SortedSet<MappedSequence>> lcache) {
        this.to = to;
        this.curPos = from-1;
        this.lcache = lcache;
        iter = lcache.getIntervals(from, to);
        if (iter != null && iter.hasNext()) {
            curInterval = iter.next();
            coverage = new int[curInterval.getTo() - curInterval.getFrom() + 1];
            lcache.getCoverage(curInterval.getFrom(), curInterval.getTo(), coverage);
        } else {
            throw new NullPointerException();
        }
    }

    public boolean hasNext() {
        return curPos < to;
    }

    public int next() {
        curPos++;
        if (curPos > curInterval.getTo()) {
            assert iter.hasNext();
            curInterval = iter.next();
            //Arrays.fill(coverage, 0);
            lcache.getCoverage(curInterval.getFrom(), curInterval.getTo(), coverage);
        }
        int ret = coverage[curPos - curInterval.getFrom()];
        return ret;
    }

}
