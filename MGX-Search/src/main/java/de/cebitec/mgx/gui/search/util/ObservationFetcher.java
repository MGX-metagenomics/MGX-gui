package de.cebitec.mgx.gui.search.util;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.Observation;
import de.cebitec.mgx.gui.datamodel.Sequence;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;

/**
 *
 * @author sjaenick
 */
public class ObservationFetcher implements Runnable {

    /**
     * MGXMaster.
     */
    private final MGXMaster master;
    /**
     * Sequenz des Reads.
     */
    private final Sequence seq;
    private final Map<Sequence, WeakReference<Observation[]>> cache;

    /**
     * Konstruktor
     *
     * @param master MGXMaster
     * @param seq Sequenz des Reads.
     */
    public ObservationFetcher(MGXMaster master, Sequence seq, Map<Sequence, WeakReference<Observation[]>> cache) {
        this.master = master;
        this.seq = seq;
        this.cache = cache;
    }

    /**
     * Run.
     */
    @Override
    public void run() {
        if (cache.containsKey(seq) && cache.get(seq).get() != null) {
            return;
        }
        Observation[] obs = master.Observation().ByRead(seq).toArray(new Observation[]{});
        Arrays.sort(obs, comp);
        cache.put(seq, new WeakReference(obs));
    }
    
    private final static Comparator<Observation> comp = new Comparator<Observation>() {
        @Override
        public int compare(Observation o1, Observation o2) {
            int min1 = o1.getStart() < o1.getStop() ? o1.getStart() : o1.getStop();
            int min2 = o2.getStart() < o2.getStop() ? o2.getStart() : o2.getStop();
            return Integer.compare(min1, min2);
        }
    };
}
