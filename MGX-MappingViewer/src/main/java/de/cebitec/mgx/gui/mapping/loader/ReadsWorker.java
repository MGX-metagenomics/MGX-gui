/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping.loader;

import de.cebitec.mgx.gui.cache.Cache;
import de.cebitec.mgx.gui.datamodel.MappedSequence;
import de.cebitec.mgx.gui.mapping.misc.ICurrentTime;
import de.cebitec.mgx.gui.mapping.viewer.AbstractViewer;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import org.openide.util.Exceptions;

/**
 *
 * @author belmann
 */
public class ReadsWorker extends SwingWorker<Iterator<MappedSequence>, Void> {

    private Cache<List<MappedSequence>> cache;
    private AbstractViewer viewer;
    private long workerCreationTime;
    private Iterator<MappedSequence> iter;

    public ReadsWorker(AbstractViewer viewer, Cache<List<MappedSequence>> cache) {
        this.viewer = viewer;
        this.cache = cache;
        this.workerCreationTime = System.nanoTime();
    }

    @Override
    protected Iterator<MappedSequence> doInBackground() throws Exception {
        Thread.currentThread().setName("ReadsWorker");
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
        try {
            if (workerCreationTime >= viewer.getCurrentTime()) {
                viewer.setCurrentTime(this.workerCreationTime);
                iter = get();
                viewer.setSequences(iter);
            }
        } catch (InterruptedException | ExecutionException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
