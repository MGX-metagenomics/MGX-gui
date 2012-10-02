/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.search;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.Observation;
import de.cebitec.mgx.gui.datamodel.Sequence;
import de.cebitec.mgx.gui.search.ComputeObservations.Layer;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.openide.util.RequestProcessor;

/**
 *
 * @author pbelmann
 */
public class ModelObservation {

    private RequestProcessor.Task myTask;
    private Sequence seq;
    public static Map<Sequence, WeakReference<Observation[]>> cache = Collections.<Sequence, WeakReference<Observation[]>>synchronizedMap(new HashMap<Sequence, WeakReference<Observation[]>>());
    private Observation[] observations;

    public Observation[] getObservations(MGXMaster m, Sequence seq, RequestProcessor proc) {
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

        assert false;
        return null;
    }

    public void fetchFromServer(MGXMaster m, Sequence seq, RequestProcessor proc) {
        myTask = proc.post(new ModelObservation.ObsFetcher(m, seq));
    }

    public MGXMaster currentMaster;
    
    
    
    public ComputeObservations setCurrentData(final MGXMaster m, final Sequence seq, final RequestProcessor proc) {
        if (!cache.containsKey(seq)) {
            // submit observation fetcher task
            fetchFromServer(m, seq, proc);

        } 
        currentMaster = m;
         ComputeObservations compute = null;
//        else {
            try {
                observations = getObservations(m, seq, proc);
            compute = new ComputeObservations(seq.getLength(),
                              new ArrayList(Arrays.asList(observations)), 10,
                               seq.getLength());
            } catch (AssertionError error) {     
 
            }
//        }

            
        return compute;


    }

    private class ObsFetcher implements Runnable { //, Future<Collection<Observation>> {

        private final MGXMaster master;
        private final Sequence seq;

        public ObsFetcher(MGXMaster master, Sequence seq) {
            this.master = master;
            this.seq = seq;
        }

        @Override
        public void run() {
            Observation[] obs = master.Observation().ByRead(seq).toArray(new Observation[]{});
            cache.put(seq, new WeakReference(obs));
        }
    }
}
