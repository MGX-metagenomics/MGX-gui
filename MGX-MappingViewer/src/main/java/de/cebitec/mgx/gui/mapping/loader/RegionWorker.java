///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package de.cebitec.mgx.gui.mapping.loader;
//
//import de.cebitec.mgx.gui.cache.Cache;
//import de.cebitec.mgx.gui.controller.MGXMaster;
//import de.cebitec.mgx.gui.datamodel.Reference;
//import de.cebitec.mgx.gui.datamodel.Region;
//import de.cebitec.mgx.gui.mapping.viewer.AbstractViewer;
//import java.util.Iterator;
//import java.util.Set;
//import java.util.concurrent.ExecutionException;
//import javax.swing.SwingWorker;
//import org.openide.util.Exceptions;
//
///**
// *
// * @author belmann
// */
//public class RegionWorker extends SwingWorker<Iterator<Region>, Void> {
//
//    private Cache<Set<Region>> cache;
//    private AbstractViewer viewer;
//
//    public RegionWorker(AbstractViewer viewer, MGXMaster master, Reference reference, Cache<Set<Region>> cache) {
//        this.viewer = viewer;
//        this.cache = cache;
//    }
//
//    @Override
//    protected Iterator<Region> doInBackground() throws Exception {
//        Set<Region> set = null;
//        try {
//            set = cache.get(viewer.getBoundsInfo().getLogLeft(), viewer.getBoundsInfo().getLogRight());
//        } catch (ExecutionException ex) {
//            Exceptions.printStackTrace(ex);
//        }
//        return set.iterator();
//    }
//}
