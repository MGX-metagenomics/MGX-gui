///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package de.cebitec.mgx.gui.mapping.loader;
//
//import de.cebitec.mgx.gui.cache.Cache;
//import de.cebitec.mgx.gui.cache.CacheFactory;
//import de.cebitec.mgx.gui.controller.MGXMaster;
//import de.cebitec.mgx.gui.datamodel.Reference;
//import de.cebitec.mgx.gui.datamodel.Region;
//import de.cebitec.mgx.gui.mapping.viewer.AbstractViewer;
//import java.util.Set;
//import java.util.concurrent.ExecutionException;
//import org.openide.util.Exceptions;
//
///**
// *
// * @author belmann
// */
//public class RegionLoader implements Loader {
//
//    private final Cache<Set<Region>> cache;
//    private final MGXMaster master;
//    private final Reference reference;
//
//    public RegionLoader(MGXMaster master, Reference reference) {
//        this.reference = reference;
//        this.master = master;
//        cache = CacheFactory.createRegionCache(master, reference);
//    }
//
//    @Override
//    public void startWorker(AbstractViewer viewer) {
//        RegionWorker regionsWorker = new RegionWorker(viewer, master, reference, cache);
//        regionsWorker.execute();
//        try {
//            viewer.setSequences(regionsWorker.get());
//        } catch (InterruptedException | ExecutionException ex) {
//            Exceptions.printStackTrace(ex);
//        }
//    }
//}
