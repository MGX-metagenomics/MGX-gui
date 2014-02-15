/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping.loader;

import de.cebitec.mgx.gui.cache.Cache;
import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.MappedSequence;
import de.cebitec.mgx.gui.datamodel.Reference;
import de.cebitec.mgx.gui.datamodel.Region;
import de.cebitec.mgx.gui.mapping.viewer.AbstractViewer;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;
import javax.swing.SwingWorker;
import org.openide.util.Exceptions;

/**
 *
 * @author belmann
 */
public class RegionWorker extends SwingWorker<Iterator<Region>, Void> {

    private static final Logger log = Logger.getLogger(Loader.class.getName());
    private Cache<Set<Region>> cache;
    private AbstractViewer viewer;
    private long workerCreationTime;
    static ConcurrentHashMap<Long, Long> workerCreationTimeMap = new ConcurrentHashMap();
    private Iterator<Region> iter;

    public RegionWorker(AbstractViewer viewer, MGXMaster master, Reference reference, Cache<Set<Region>> cache) {

        this.viewer = viewer;
        this.cache = cache;
        this.workerCreationTime = System.nanoTime();
        workerCreationTimeMap.put(workerCreationTime, workerCreationTime);
    }

    @Override
    protected Iterator<Region> doInBackground() throws Exception {
        Set<Region> set = null;
        try {
            set = cache.get(viewer.getBoundsInfo().getLogLeft(), viewer.getBoundsInfo().getLogRight());
        } catch (ExecutionException ex) {
            Exceptions.printStackTrace(ex);
        }
        return set.iterator();
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
