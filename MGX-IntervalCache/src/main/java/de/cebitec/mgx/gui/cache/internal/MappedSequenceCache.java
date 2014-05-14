/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.cache.internal;

import com.google.common.cache.LoadingCache;
import de.cebitec.mgx.gui.cache.Cache;
import de.cebitec.mgx.gui.datamodel.MappedSequence;
import de.cebitec.mgx.gui.datamodel.Reference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author belmann
 */
public class MappedSequenceCache extends Cache<List<MappedSequence>> {

    public MappedSequenceCache(Reference ref, LoadingCache<Interval<List<MappedSequence>>, List<MappedSequence>> lcache) {
        super(ref, lcache);
    }

    public MappedSequenceCache(Reference ref, LoadingCache<Interval<List<MappedSequence>>, List<MappedSequence>> lcache, int segSize) {
        super(ref, lcache, segSize);
    }

    @Override
    public List<MappedSequence> get(int from, int to)  {
        Iterator<Interval<List<MappedSequence>>> iter = getIntervals(from, to);
        List<MappedSequence> mappedSequences = new ArrayList<>();
        while (iter.hasNext()) {
            List<MappedSequence> get = lcache.getUnchecked(iter.next());
            for (MappedSequence seq : get) {
                mappedSequences.add(seq);
            }
        }
        return mappedSequences;
    }
}
