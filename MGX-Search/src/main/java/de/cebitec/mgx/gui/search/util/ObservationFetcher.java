package de.cebitec.mgx.gui.search.util;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.ObservationI;
import de.cebitec.mgx.api.model.SequenceI;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import org.openide.util.Exceptions;

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
        try {
            Iterator<ObservationI>  iter = master.Observation().ByRead(seq);
            List<ObservationI> obs = new ArrayList<>();
            while (iter.hasNext()) {
                obs.add(iter.next());
            }
            Collections.sort(obs, new ObservationSorter());
            cache.put(seq, new WeakReference<>(obs.toArray(new ObservationI[]{})));
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
        }

        //list.repaint();
        activeTasks.remove(seq);
    }
   
}
