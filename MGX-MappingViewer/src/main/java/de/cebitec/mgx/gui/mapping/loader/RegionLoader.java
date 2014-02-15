/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping.loader;

import de.cebitec.mgx.gui.cache.Cache;
import de.cebitec.mgx.gui.cache.CacheFactory;
import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.Reference;
import de.cebitec.mgx.gui.datamodel.Region;
import de.cebitec.mgx.gui.mapping.viewer.AbstractViewer;
import de.cebitec.mgx.gui.mapping.sequences.ReferenceHolder;
import java.util.Set;

/**
 *
 * @author belmann
 */
public class RegionLoader extends Loader {

    private Cache<Set<Region>> cache;

    public RegionLoader(MGXMaster master, Reference reference) {
        super(master, reference);
        cache = CacheFactory.createRegionCache(master, reference);
    }

    @Override
    public void startWorker(AbstractViewer viewer) {
        RegionWorker regionsWorker = new RegionWorker(viewer, master, reference, cache);
        regionsWorker.execute();
    }
}
