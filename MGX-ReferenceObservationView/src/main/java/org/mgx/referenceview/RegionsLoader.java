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
public class RegionsLoader implements LogicalBoundsListener, ICurrentValues {

    private static final Logger log = Logger.getLogger(RegionsLoader.class.getName());
    private ReferenceViewer viewer;
    private Cache<Set<Region>> cache;
    volatile BoundsInfo bounds;
    volatile private long currentTime;

    public RegionsLoader(MGXMaster master, ReferenceViewer viewer, Reference reference) {
        this.viewer = viewer;
        currentTime = System.nanoTime();
        cache = CacheFactory.createRegionCache(master, reference);
    }

    @Override
    public void updateLogicalBounds(final BoundsInfo lBounds) {
        this.bounds = lBounds;
//        log.info("Loading data: Left: " + this.getBounds().getLogLeft() + " Right: " + this.getBounds().getLogRight());
        RegionWorker worker = new RegionWorker(this, viewer, cache);
        worker.execute();
    }

    @Override
    public Dimension getPaintingAreaDimension() {
        return true ? new Dimension(viewer.paintingAreaInfo.getPhyWidth(), viewer.paintingAreaInfo.getCompleteHeight()) : null;
    }

    @Override
    public boolean isPaintingAreaAviable() {
        return true;
    }

    @Override
    public synchronized BoundsInfo getCurrentBounds() {
        return bounds;
    }

    @Override
    public synchronized void setCurrentBounds(BoundsInfo bounds) {
        this.bounds = bounds;
    }

    @Override
    public synchronized long getCurrentTime() {
        return currentTime;
    }

    @Override
    public synchronized void setCurrentTime(long current) {
        this.currentTime = current;
    }
}
