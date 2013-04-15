package de.cebitec.mgx.gui.search;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.Observation;
import de.cebitec.mgx.gui.datamodel.Sequence;
import de.cebitec.mgx.gui.search.util.ObservationFetcher;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.openide.util.RequestProcessor;

/**
 * Laedt die einzelnen Observations von jedem Read aus dem Server.
 *
 *
 * @author pbelmann
 */
public class ServerDataWrapper {

    /**
     * Task
     */
    private RequestProcessor.Task myTask;
    /**
     * Map fuer den Read und den dazugehoerigen Observations.
     */
    private static Map<Sequence, WeakReference<Observation[]>> cache =
            Collections.<Sequence, WeakReference<Observation[]>>synchronizedMap(
            new HashMap<Sequence, WeakReference<Observation[]>>());
    /**
     * Array an Observations.
     */
    private Observation[] observations;

    /**
     * Laedt die Observations aus dem Server.
     *
     * @param m MGXMaster
     * @param seq Sequenz des Reads.
     * @param proc RequestProcessor.
     * @return Array an Observations.
     */
    private Observation[] getObservations(MGXMaster m, Sequence seq, RequestProcessor proc) {
        // if task is still running, wait for it to finish
        if (myTask != null) {
            myTask.waitFinished();
            myTask = null;
        }

        if (cache.containsKey(seq)) {
            Observation[] obs = cache.get(seq).get(); // create strong reference
            if (obs == null) {
                // weak ref expired, start new fetcher and invoke self
                fetchFromServer(m, seq, proc);
                return getObservations(m, seq, proc);
            } else {
                // weak ref alive, return value
                return obs;
            }
        }

        assert false; // not reached
        return null;
    }

    /**
     * Erstellt die Task, die sich die Observations aus dem Server laedt.
     *
     * @param m MGXMaster
     * @param seq Sequenz des Reads.
     * @param proc RequestProcessor
     */
    public void fetchFromServer(MGXMaster m, Sequence seq, RequestProcessor proc) {
        myTask = proc.post(new ObservationFetcher(m, seq, cache));
    }

    /**
     * Gibt die geordneten Observations wieder.
     *
     * @param m MGX Master
     * @param seq Sequenz des Reads
     * @param proc PrequestProcessor.
     * @return geordnete Observations.
     */
    public OrderedObservations getOrderedObervations(final MGXMaster m, final Sequence seq, final RequestProcessor proc) {
        if (!cache.containsKey(seq)) {
            // submit observation fetcher task
            fetchFromServer(m, seq, proc);
        }

        OrderedObservations compute = null;
            observations = getObservations(m, seq, proc);
            compute = new OrderedObservations(seq.getLength(),
                    new ArrayList(Arrays.asList(observations)));

        return compute;
    }
}
