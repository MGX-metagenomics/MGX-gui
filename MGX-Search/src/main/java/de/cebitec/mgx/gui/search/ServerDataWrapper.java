package de.cebitec.mgx.gui.search;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.ObservationI;
import de.cebitec.mgx.api.model.SequenceI;
import java.lang.ref.WeakReference;
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
    private static final Map<SequenceI, WeakReference<ObservationI[]>> cache =
            Collections.<SequenceI, WeakReference<ObservationI[]>>synchronizedMap(
            new HashMap<SequenceI, WeakReference<ObservationI[]>>());
    /**
     * Array an Observations.
     */
    private ObservationI[] observations;

    /**
     * Laedt die Observations aus dem Server.
     *
     * @param m MGXMaster
     * @param seq Sequenz des Reads.
     * @param proc RequestProcessor.
     * @return Array an Observations.
     */
    private ObservationI[] getObservations(MGXMasterI m, SequenceI seq, RequestProcessor proc) {
        // if task is still running, wait for it to finish
        if (myTask != null) {
            myTask.waitFinished();
            myTask = null;
        }

        if (cache.containsKey(seq)) {
            ObservationI[] obs = cache.get(seq).get(); // create strong reference
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
    public void fetchFromServer(MGXMasterI m, SequenceI seq, RequestProcessor proc) {
       // myTask = proc.post(new ObservationFetcher(m, seq, cache));
    }

    /**
     * Gibt die geordneten Observations wieder.
     *
     * @param m MGX Master
     * @param seq Sequenz des Reads
     * @param proc PrequestProcessor.
     * @return geordnete Observations.
     */
    public OrderedObservations getOrderedObervations(final MGXMasterI m, final SequenceI seq, final RequestProcessor proc) {
        if (!cache.containsKey(seq)) {
            // submit observation fetcher task
            fetchFromServer(m, seq, proc);
        }

        OrderedObservations compute = null;
        observations = getObservations(m, seq, proc);
        compute = new OrderedObservations(seq.getLength(), Arrays.asList(observations));

        return compute;
    }
}
