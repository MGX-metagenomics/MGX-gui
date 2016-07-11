///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package de.cebitec.mgx.gui.search.util;
//
//import de.cebitec.mgx.api.MGXMasterI;
//import de.cebitec.mgx.api.model.ModelBaseI;
//import de.cebitec.mgx.api.model.SeqRunI;
//import de.cebitec.mgx.gui.swingutils.BaseModel;
//import java.beans.PropertyChangeEvent;
//import java.beans.PropertyChangeListener;
//import java.util.Collections;
//import java.util.Iterator;
//import java.util.concurrent.ExecutionException;
//import javax.swing.SwingWorker;
//import org.openide.util.Exceptions;
//
///**
// *
// * @author sjaenick
// */
//public class SeqRunModel extends BaseModel<SeqRunI> implements PropertyChangeListener {
//
//    private MGXMasterI currentMaster;
//
//    public void setMaster(MGXMasterI m) {
//        if (currentMaster != null) {
//            currentMaster.removePropertyChangeListener(this);
//        }
//        currentMaster = m;
//        currentMaster.addPropertyChangeListener(this);
//    }
//
//    @Override
//    public void update() {
//        if (currentMaster == null) {
//            return;
//        }
//
//        SwingWorker<Iterator<SeqRunI>, Void> sw = new SwingWorker<Iterator<SeqRunI>, Void>() {
//
//            @Override
//            protected Iterator<SeqRunI> doInBackground() throws Exception {
//                return currentMaster.SeqRun().fetchall();
//            }
//        };
//        sw.execute();
//
//        Iterator<SeqRunI> iter;
//        try {
//            iter = sw.get();
//        } catch (InterruptedException | ExecutionException ex) {
//            Exceptions.printStackTrace(ex);
//            return;
//        }
//
//        content.clear();
//        while (iter.hasNext()) {
//            SeqRunI seq = iter.next();
//            content.add(seq);
//        }
//        Collections.sort(content);
//        fireContentsChanged();
//    }
//
//    @Override
//    public void propertyChange(PropertyChangeEvent evt) {
//        if (evt.getSource().equals(currentMaster) && ModelBaseI.OBJECT_DELETED.equals(evt.getPropertyName())) {
//            currentMaster.removePropertyChangeListener(this);
//            currentMaster = null;
//            if (!content.isEmpty()) {
//                content.clear();
//                fireContentsChanged();
//            }
//        }
//    }
//}
