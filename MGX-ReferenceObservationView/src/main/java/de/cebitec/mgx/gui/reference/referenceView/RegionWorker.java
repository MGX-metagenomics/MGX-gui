/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.reference.referenceView;

import de.cebitec.mgx.gui.cache.Cache;
import de.cebitec.mgx.gui.datamodel.Region;
import de.cebitec.mgx.gui.reference.dataVisualisation.referenceViewer.ReferenceViewer;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;
import javax.swing.SwingWorker;
import org.openide.util.Exceptions;

/**
 *
 * @author belmann
 */
public class RegionWorker extends SwingWorker<Iterator<Region>, Void> {

    private static final Logger log = Logger.getLogger(RegionsLoader.class.getName());
    private Cache<Set<Region>> cache;
    private ICurrentValues currentValues;
    private ReferenceViewer viewer;
    private long workerCreationTime;

    public RegionWorker(ICurrentValues current, ReferenceViewer viewer, Cache cache) {
        this.currentValues = current;
        this.viewer = viewer;
        this.cache = cache;
        this.workerCreationTime = System.nanoTime();
    }

    @Override
    protected Iterator<Region> doInBackground() throws Exception {
        Set<Region> set = null;
        try {
            set = cache.get(this.currentValues.getCurrentBounds().getLogLeft(), currentValues.getCurrentBounds().getLogRight() - 1);
        } catch (ExecutionException ex) {
            Exceptions.printStackTrace(ex);
        }
        return set.iterator();
    }

    synchronized private void repaintViewer() {
        try {
            if (workerCreationTime >= currentValues.getCurrentTime()) {
                currentValues.setCurrentTime(this.workerCreationTime);
                Iterator<Region> iter = get();
//                log.info(iter.hasNext() ? "Region Iterator is not empty" : "Region Iterator is empty");
                viewer.resetFeatures(iter);
                Region reg;
//                log.info("Size: " + viewer.list.size());
//                log.info("Loaded: " + count);
            }
        } catch (InterruptedException ex) {
            Exceptions.printStackTrace(ex);
        } catch (ExecutionException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    protected void done() {
        repaintViewer();
    }
}
