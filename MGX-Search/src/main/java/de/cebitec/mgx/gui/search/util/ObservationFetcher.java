package de.cebitec.mgx.gui.search.util;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.Observation;
import de.cebitec.mgx.gui.datamodel.Sequence;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import javax.swing.AbstractListModel;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListModel;

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
    private final ConcurrentMap<Sequence, WeakReference<Observation[]>> cache;
    private final Set<Sequence> activeTasks;

    /**
     * Konstruktor
     *
     * @param master MGXMaster
     * @param seq Sequenz des Reads.
     */
    public ObservationFetcher(Set<Sequence> activeTasks, MGXMaster master, Sequence seq, ConcurrentMap<Sequence, WeakReference<Observation[]>> cache) {
        this.activeTasks = activeTasks;
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
        //list.repaint();
        activeTasks.remove(seq);
    }
    private final static Comparator<Observation> comp = new Comparator<Observation>() {
        @Override
        public int compare(Observation o1, Observation o2) {
            // compare start positions, first one comes first
            int min1 = o1.getStart() < o1.getStop() ? o1.getStart() : o1.getStop();
            int min2 = o2.getStart() < o2.getStop() ? o2.getStart() : o2.getStop();
            int ret = Integer.compare(min2, min1);
            // if equal, compare length - longer one first
            return ret == 0 ? compareLength(o1, o2) : ret;
        }

        private int compareLength(Observation o1, Observation o2) {
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
