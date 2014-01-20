/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.cache.internal;

import com.google.common.cache.LoadingCache;
import de.cebitec.mgx.gui.cache.Cache;
import de.cebitec.mgx.gui.datamodel.MappedSequence;
import de.cebitec.mgx.gui.datamodel.Reference;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 *
 * @author belmann
 */
public class MappedSequenceCache extends Cache<Set<MappedSequence>> {

    public MappedSequenceCache(Reference ref, LoadingCache<Interval<Set<MappedSequence>>, Set<MappedSequence>> lcache) {
        super(ref, lcache);
    }

    public MappedSequenceCache(Reference ref, LoadingCache<Interval<Set<MappedSequence>>, Set<MappedSequence>> lcache, int segSize) {
        super(ref, lcache, segSize);
    }

    @Override
    public Set<MappedSequence> get(int from, int to) throws ExecutionException {
        Iterator<Interval<Set<MappedSequence>>> iter = getIntervals(from, to);

        Set<MappedSequence> mappedSequences = new HashSet<MappedSequence>();
        while (iter.hasNext()) {
            Set<MappedSequence> get = lcache.get(iter.next());
            for (MappedSequence seq : get) {
                mappedSequences.add(seq);
            }
        }
        return mappedSequences;
    }
}
