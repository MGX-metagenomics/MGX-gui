package de.cebitec.mgx.gui.cache;

import de.cebitec.mgx.gui.cache.internal.Interval;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import de.cebitec.mgx.gui.cache.internal.SequenceCache;
import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.Reference;

/**
 *
 * @author sjaenick
 */
public class CacheFactory {

    public static Cache<String> createSequenceCache(final MGXMaster master, final Reference ref) {

        CacheLoader<Interval<String>, String> loader = new CacheLoader<Interval<String>, String>() {
            @Override
            public String load(Interval<String> k) throws Exception {
                String seq = master.Reference().getSequence(ref, k.getFrom(), k.getTo());
                //System.err.println("segment "+k.getFrom()+"-"+k.getTo()+" "+ seq);
                return seq;
            }
        };

        LoadingCache<Interval<String>, String> lcache = CacheBuilder.newBuilder()
                .weakKeys()
                .build(loader);
        return new SequenceCache(ref, lcache);
    }
}
