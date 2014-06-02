package de.cebitec.mgx.gui.search.util;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.ObservationI;
import de.cebitec.mgx.api.model.SequenceI;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 *
 * @author sjaenick
 */
public class ObservationFetcher implements Runnable {

    /**
     * MGXMaster.
     */
    private final MGXMasterI master;
    /**
     * Sequenz des Reads.
     */
    private final SequenceI seq;
    private final ConcurrentMap<SequenceI, WeakReference<ObservationI[]>> cache;
    private final Set<SequenceI> activeTasks;

    /**
     * Konstruktor
     *
     * @param master MGXMaster
     * @param seq Sequenz des Reads.
     */
    public ObservationFetcher(Set<SequenceI> activeTasks, MGXMasterI master, SequenceI seq, ConcurrentMap<SequenceI, WeakReference<ObservationI[]>> cache) {
        this.activeTasks = activeTasks;
        this.master = master;
        this.seq = seq;
        this.cache = cache;
        activeTasks.add(seq);
    }

    /**
     * Run.
     */
    @Override
    public void run() {
        if (cache.containsKey(seq) && cache.get(seq).get() != null) {
            return;
        }
        Iterator<ObservationI> iter = master.Observation().ByRead(seq);
        List<ObservationI> obs = new ArrayList<>();
        while (iter.hasNext()) {
            obs.add(iter.next());
        }
        Collections.sort(obs, comp);
        cache.put(seq, new WeakReference(obs));
        //list.repaint();
        activeTasks.remove(seq);
    }
    private final static Comparator<ObservationI> comp = new Comparator<ObservationI>() {
        @Override
        public int compare(ObservationI o1, ObservationI o2) {
            // compare start positions, first one comes first
            int min1 = o1.getStart() < o1.getStop() ? o1.getStart() : o1.getStop();
            int min2 = o2.getStart() < o2.getStop() ? o2.getStart() : o2.getStop();
            int ret = Integer.compare(min2, min1);
            // if equal, compare length - longer one first
            return ret == 0 ? compareLength(o1, o2) : ret;
        }

        private int compareLength(ObservationI o1, ObservationI o2) {
            int l1 = o1.getStart() < o1.getStop()
                    ? o1.getStop() - o1.getStart() + 1
                    : o1.getStart() - o1.getStop() + 1;
            int l2 = o2.getStart() < o2.getStop()
                    ? o2.getStop() - o2.getStart() + 1
                    : o2.getStart() - o2.getStop() + 1;
            return Integer.compare(l1, l2);
        }
    };
}
