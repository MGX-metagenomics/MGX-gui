/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mgx.referenceview;

import de.cebitec.mgx.gui.cache.Cache;
import de.cebitec.mgx.gui.datamodel.Region;
import de.cebitec.vamp.view.dataVisualisation.referenceViewer.ReferenceViewer;
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
    private Current current;
    private ReferenceViewer viewer;
    private long curr;

    public RegionWorker(Current current, ReferenceViewer viewer, Cache cache) {
        curr = System.nanoTime();
        this.current = current;
        this.viewer = viewer;
        this.cache = cache;
        //      this.curr = curr;
    }

    @Override
    protected Iterator<Region> doInBackground() throws Exception {
        if (!isCancelled()) {
//                if (bounds.getLogLeft() != 1) {
//                    log.info("not one");
//                }

            Set<Region> set = null;
            try {
                set = cache.get(this.current.getBounds().getLogLeft(), current.getBounds().getLogRight() - 1);
            } catch (ExecutionException ex) {
                Exceptions.printStackTrace(ex);
            }
            //   Iterator<Region> iter = set.iterator();

            return set.iterator();
        }
        return null;
    }

    synchronized private void repaintViewer() {

        try {
            if (this.curr >= current.getCurrent()) {
                current.setCurrent(this.curr);
                if (!isCancelled()) {
                    Iterator<Region> iter = get();

                    log.info(iter.hasNext() ? "Region Iterator is not empty" : "Region Iterator is empty");
                    viewer.list.clear();
                    //}

                    int count = 0;
                    while (iter.hasNext()) {
                        //log.info("Region available: " + iter.next().getName());
                        count++;
                        Region reg = iter.next();
                        if (!viewer.list.contains(reg)) {
                            viewer.list.add(reg);
                        }
                    }
                    log.info("Size: " + viewer.list.size());
                    log.info("Loaded: " + count);
                    viewer.createFeatures();
                    viewer.repaint();


                }
            }
            //viewer.repaint();  }
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
