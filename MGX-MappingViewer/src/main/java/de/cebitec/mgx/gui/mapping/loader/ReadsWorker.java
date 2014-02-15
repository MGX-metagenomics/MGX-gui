/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping.loader;

import de.cebitec.mgx.gui.cache.Cache;
import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.MappedSequence;
import de.cebitec.mgx.gui.datamodel.Reference;
import de.cebitec.mgx.gui.mapping.viewer.AbstractViewer;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import org.openide.util.Exceptions;

/**
 *
 * @author belmann
 */
public class ReadsWorker extends SwingWorker<Iterator<MappedSequence>, Void> {

    private MGXMaster master;
    private Reference ref;
    private Cache<List<MappedSequence>> cache;
    private AbstractViewer viewer;
    private long workerCreationTime;
    private Iterator<MappedSequence> iter;
    static ConcurrentHashMap<Long, Long> workerCreationTimeMap = new ConcurrentHashMap();

    public ReadsWorker(MGXMaster master, AbstractViewer viewer, Reference ref, Cache<List<MappedSequence>> cache) {
        this.master = master;
        this.ref = ref;
        this.viewer = viewer;
        this.cache = cache;
        this.workerCreationTime = System.nanoTime();
        workerCreationTimeMap.put(workerCreationTime, workerCreationTime);
    }

    @Override
    protected Iterator<MappedSequence> doInBackground() throws Exception {
        Thread.currentThread().setName("MyReadsWorker");
        int left = viewer.getBoundsInfo().getLogLeft();
        int right = viewer.getBoundsInfo().getLogRight();
        Iterator<MappedSequence> iter;
        synchronized (cache) {
            iter = cache.get(left, right).iterator();
        }
        return iter;
    }

    @Override
    protected void done() {
        SwingWorker worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                Iterator<Long> iter = workerCreationTimeMap.keySet().iterator();
                while (iter.hasNext()) {
                    if (iter.next() > workerCreationTime) {
                        return false;
                    }
                }
                return true;
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        updateViewer();
                    }
                } catch (InterruptedException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        };
        worker.execute();
    }

    private void updateViewer() {
        try {
            if (workerCreationTime >= viewer.getCurrentTime()) {
                viewer.setCurrentTime(this.workerCreationTime);
                iter = get();
                synchronized (viewer) {
                    SwingWorker worker = new SwingWorker<Void, Void>() {
                        @Override
                        protected Void doInBackground() throws Exception {
                            viewer.afterLoadingSequences(iter);
                            return null;
                        }
                    };
                    worker.execute();
                }
            }
        } catch (InterruptedException | ExecutionException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
