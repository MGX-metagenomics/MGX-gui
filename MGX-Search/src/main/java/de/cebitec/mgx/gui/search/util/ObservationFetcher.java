package de.cebitec.mgx.gui.search.util;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.Observation;
import de.cebitec.mgx.gui.datamodel.Sequence;
import java.lang.ref.WeakReference;
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
        } else {
            Observation[] obs = master.Observation().ByRead(seq).toArray(new Observation[]{});
            cache.put(seq, new WeakReference(obs));
        }
    }
}
