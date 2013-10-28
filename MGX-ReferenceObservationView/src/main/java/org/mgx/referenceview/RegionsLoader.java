/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mgx.referenceview;

import de.cebitec.mgx.gui.cache.Cache;
import de.cebitec.mgx.gui.cache.CacheFactory;
import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.Reference;
import de.cebitec.mgx.gui.datamodel.Region;
import de.cebitec.vamp.view.dataVisualisation.BoundsInfo;
import de.cebitec.vamp.view.dataVisualisation.LogicalBoundsListener;
import de.cebitec.vamp.view.dataVisualisation.basePanel.BasePanel;
import de.cebitec.vamp.view.dataVisualisation.referenceViewer.ReferenceViewer;
import java.awt.Dimension;
import java.util.Date;
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
public class RegionsLoader implements LogicalBoundsListener, Current {

    private static final Logger log = Logger.getLogger(RegionsLoader.class.getName());
    private ReferenceViewer viewer;
    private MGXMaster master;
    private Reference reference;
    private Date date;

    public RegionsLoader(MGXMaster master, ReferenceViewer viewer, Reference reference) {
        this.master = master;
        this.viewer = viewer;
        this.reference = reference;
        current = System.nanoTime();
        cache = CacheFactory.createRegionCache(master, reference);
    }
    Cache<Set<Region>> cache;
    volatile BoundsInfo bounds;

    @Override
    public synchronized BoundsInfo getBounds() {
        return bounds;
    }

    @Override
    public synchronized void setBounds(BoundsInfo bounds) {
        this.bounds = bounds;
    }

    @Override
    public void updateLogicalBounds(final BoundsInfo lBounds) {

        this.bounds = lBounds;
        log.info("Loading data: Left: " + this.getBounds().getLogLeft() + " Right: " + this.getBounds().getLogRight());
        RegionWorker worker = new RegionWorker(this, viewer, cache);
       
        
        // current++;
//        boolean usage = false;

//        if (usage == false) {
//            usag
//            if (!fetchWorker.isDone()) {
//                fetchWorker.cancel(true);
//            } else {
//            }
//        }
        worker.execute();
//        SwingWorker fetchWorker = new SwingWorker<Iterator<Region>, Void>() {
//            @Override
//            protected Iterator<Region> doInBackground() throws Exception {
//                if (!isCancelled()) {
////                if (bounds.getLogLeft() != 1) {
////                    log.info("not one");
////                }
//
//                    Set<Region> set = null;
//                    try {
//                        set = cache.get(getBounds().getLogLeft(), getBounds().getLogRight() - 1);
//                    } catch (ExecutionException ex) {
//                        Exceptions.printStackTrace(ex);
//                    }
//                    //   Iterator<Region> iter = set.iterator();
//
//                    return set.iterator();
//                }
//                return null;
//            }
//
//            @Override
//            protected void done() {
//                try {
//
//                    if (!isCancelled()) {
//                        Iterator<Region> iter = get();
//
//                        log.info(iter.hasNext() ? "Region Iterator is not empty" : "Region Iterator is empty");
//                        viewer.list.clear();
//                        //}
//
//                        int count = 0;
//                        while (iter.hasNext()) {
//                            //log.info("Region available: " + iter.next().getName());
//                            count++;
//                            Region reg = iter.next();
//                            if (!viewer.list.contains(reg)) {
//                                viewer.list.add(reg);
//                            }
//                        }
//                        log.info("Size: " + viewer.list.size());
//                        log.info("Loaded: " + count);
//                        viewer.createFeatures();
//                        viewer.repaint();
//
//
//                    }
//                    //viewer.repaint();  }
//                } catch (InterruptedException ex) {
//                    Exceptions.printStackTrace(ex);
//                } catch (ExecutionException ex) {
//                    Exceptions.printStackTrace(ex);
//                }
//            }
////        fetchWorker.execute();
//        };
//        fetchWorker.execute();
    }
    volatile private long current;

    @Override
    public Dimension getPaintingAreaDimension() {
        return true ? new Dimension(viewer.paintingAreaInfo.getPhyWidth(), viewer.paintingAreaInfo.getCompleteHeight()) : null;
    }

    @Override
    public boolean isPaintingAreaAviable() {
        return true;
    }

    @Override
    public synchronized long getCurrent() {
        return current;
    }

    @Override
    public synchronized void setCurrent(long current) {
        this.current = current;
    }
}
