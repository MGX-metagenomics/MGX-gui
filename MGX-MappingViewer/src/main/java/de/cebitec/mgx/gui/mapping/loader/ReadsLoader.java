///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package de.cebitec.mgx.gui.mapping.loader;
//
//import de.cebitec.mgx.gui.cache.Cache;
//import de.cebitec.mgx.gui.cache.CacheFactory;
//import de.cebitec.mgx.gui.controller.MGXMaster;
//import de.cebitec.mgx.gui.datamodel.MappedSequence;
//import de.cebitec.mgx.gui.datamodel.Reference;
//import de.cebitec.mgx.gui.mapping.viewer.AbstractViewer;
//import java.util.List;
//import java.util.UUID;
//
///**
// *
// * @author belmann
// */
//public class ReadsLoader implements Loader {
//
//    private final Cache<List<MappedSequence>> cache;
//
//    public ReadsLoader(MGXMaster master, Reference reference, UUID uuid) {
//        this.cache = CacheFactory.createMappedSequenceCache(master, reference, uuid);
//    }
//
//    @Override
//    public void startWorker(AbstractViewer viewer) {
//        ReadsWorker readsWorker = new ReadsWorker(viewer, cache);
//        readsWorker.execute();
//    }
//}
