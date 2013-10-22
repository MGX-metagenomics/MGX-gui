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
public class RegionsLoader implements LogicalBoundsListener {

    private static final Logger log = Logger.getLogger(RegionsLoader.class.getName());
    private ReferenceViewer viewer;
    private MGXMaster master;
    private Reference reference;
    
    
    public RegionsLoader(MGXMaster master, ReferenceViewer viewer, Reference reference) {
        this.master = master;
        this.viewer = viewer;
        this.reference = reference;
        cache = CacheFactory.createRegionCache(master, reference);
    }
    Cache<Set<Region>> cache;

    @Override
    public void updateLogicalBounds(final BoundsInfo bounds) {

        log.info("Loading data: Left: " + bounds.getMaxLogLeft() + " Right: " + bounds.getLogRight());

        SwingWorker fetchWorker = new SwingWorker<Iterator<Region>, Void>() {
            @Override
            protected Iterator<Region> doInBackground() throws Exception {

                Set<Region> set = null;
                try {
                    set = cache.get(bounds.getLogLeft(), bounds.getLogRight());
                } catch (ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }
                return set.iterator();
                // return currentMaster.Reference().byReferenceInterval(lId, 0, length);
            }

            @Override
            protected void done() {
                try {


                    Iterator<Region> iter = get();


                    log.info(iter.hasNext() ? "Region Iterator is not empty" : "Region Iterator is empty");
                    //viewer.list.clear();

                    //  viewer.createFeatures();
                    //  viewer.repaint();
                    
                    //if (viewer.list.size() == 200) {
                   // viewer.removeAll();
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
                    //viewer.repaint();
                } catch (InterruptedException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        };
        fetchWorker.execute();
    }

    @Override
    public Dimension getPaintingAreaDimension() {

        return true ? new Dimension(viewer.paintingAreaInfo.getPhyWidth(), viewer.paintingAreaInfo.getCompleteHeight()) : null;


    }

    @Override
    public boolean isPaintingAreaAviable() {
        return true;
    }
}
