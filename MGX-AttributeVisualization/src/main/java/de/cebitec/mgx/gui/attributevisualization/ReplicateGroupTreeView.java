/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.attributevisualization;

import de.cebitec.mgx.api.groups.ReplicateGroupI;
import de.cebitec.mgx.gui.nodefactory.ReplicateNodeFactory;
import java.awt.dnd.DnDConstants;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.openide.explorer.view.BeanTreeView;

/**
 *
 * @author sjaenick
 */
final class ReplicateGroupTreeView extends BeanTreeView implements PropertyChangeListener {

    private final ReplicateGroupI rGroup;
    private final ReplicateNodeFactory rgnf;

    public ReplicateGroupTreeView(ReplicateGroupI rGroup) {
        this(rGroup, new ReplicateNodeFactory(rGroup));
    }

    public ReplicateGroupTreeView(ReplicateGroupI rGroup, ReplicateNodeFactory rgnf) {
        super();
        this.rGroup = rGroup;
        this.rgnf = rgnf;
        setRootVisible(false);
        setDropTarget(true);
        //setShowParentNode(true);
        setAllowedDropActions(DnDConstants.ACTION_COPY + DnDConstants.ACTION_REFERENCE);
        //setDropTarget();
        rGroup.addPropertyChangeListener(this);
    } //setShowParentNode(true);

//    private void setDropTarget() {
//        DropTarget dt = new DropTarget(this, new DropTargetAdapter() {
//            @Override
//            public void dragEnter(DropTargetDragEvent dtde) {
//                Transferable t = dtde.getTransferable();
//                if (t.isDataFlavorSupported(ExTransferable.multiFlavor)) {
//                    try {
//                        final MultiTransferObject mto = (MultiTransferObject) t.getTransferData(ExTransferable.multiFlavor);
//
//                        // multiple visualization groups
//                        if (mto.areDataFlavorsSupported(new DataFlavor[]{VisualizationGroupI.DATA_FLAVOR})) {
//                            int elems = mto.getCount();
//                            for (int i = 0; i < elems; i++) {
//                                VisualizationGroupI vGroup = (VisualizationGroupI) mto.getTransferData(i, VisualizationGroupI.DATA_FLAVOR);
//                                if (rGroup.getVisualizationGroups().contains(vGroup)) {
//                                    dtde.rejectDrag();
//                                    return;
//                                }
//                            }
//                            dtde.acceptDrag(DnDConstants.ACTION_COPY);
//                            return;
//                        }
//                    } catch (UnsupportedFlavorException | IOException e) {
//                    }
//                }
//
//                // single visualization group
//                if (dtde.isDataFlavorSupported(VisualizationGroupI.DATA_FLAVOR)) {
//                    try {
//                        VisualizationGroupI vGroup = (VisualizationGroupI) dtde.getTransferable().getTransferData(VisualizationGroupI.DATA_FLAVOR);
//                        if (vGroup != null && !rGroup.getVisualizationGroups().contains(vGroup)) {
//                            dtde.acceptDrag(DnDConstants.ACTION_COPY);
//                            return;
//                        }
//                    } catch (UnsupportedFlavorException | IOException ex) {
//                    }
//                }
//
//                // single seqrun - add within new visualization group
//                if (dtde.isDataFlavorSupported(SeqRunI.DATA_FLAVOR)) {
//                    try {
//                        SeqRunI run = (SeqRunI) dtde.getTransferable().getTransferData(SeqRunI.DATA_FLAVOR);
//
//                        //
//                        // check if the run is already present; if so, reject
//                        //
//                        for (VisualizationGroupI vg : rGroup.getVisualizationGroups()) {
//                            for (SeqRunI sr : vg.getSeqRuns()) {
//                                if (sr.equals(run)) {
//                                    dtde.rejectDrag();
//                                }
//                            }
//                        }
//                        dtde.acceptDrag(DnDConstants.ACTION_COPY);
//                        return;
//                    } catch (UnsupportedFlavorException | IOException ex) {
//                    }
//                }
//
//                // default: reject
//                dtde.rejectDrag();
//            }
//
//            @Override
//            public void drop(DropTargetDropEvent dtde) {
//                Transferable t = dtde.getTransferable();
//                if (t.isDataFlavorSupported(ExTransferable.multiFlavor)) {
//                    try {
//                        final MultiTransferObject mto = (MultiTransferObject) t.getTransferData(ExTransferable.multiFlavor);
//                        if (mto.areDataFlavorsSupported(new DataFlavor[]{VisualizationGroupI.DATA_FLAVOR})) {
//                            int elems = mto.getCount();
//                            for (int i = 0; i < elems; i++) {
//                                VisualizationGroupI vGroup = (VisualizationGroupI) mto.getTransferData(i, VisualizationGroupI.DATA_FLAVOR);
//                                if (rGroup.getVisualizationGroups().contains(vGroup)) {
//                                    dtde.rejectDrop();
//                                    return;
//                                }
//                            }
//                            try {
//                                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
//                                Set<VisualizationGroupI> newVGroups = new HashSet<>();
//                                for (int i = 0; i < elems; i++) {
//                                    VisualizationGroupI vg = (VisualizationGroupI) mto.getTransferData(i, VisualizationGroupI.DATA_FLAVOR);
//                                    newVGroups.add(vg);
//                                }
//                                rgnf.addVGroups(newVGroups);
//                            } finally {
//                                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
//                            }
//                            dtde.dropComplete(true);
//                            return;
//                        }
//                    } catch (UnsupportedFlavorException | IOException e) {
//                    }
//                }
//                if (dtde.isDataFlavorSupported(VisualizationGroupI.DATA_FLAVOR)) {
//                    try {
//                        VisualizationGroupI vGroup = (VisualizationGroupI) dtde.getTransferable().getTransferData(VisualizationGroupI.DATA_FLAVOR);
//                        if (vGroup != null && !rGroup.getVisualizationGroups().contains(vGroup)) {
//                            rgnf.addVGroup(vGroup);
//                            dtde.dropComplete(true);
//                            return;
//                        }
//                    } catch (UnsupportedFlavorException | IOException ex) {
//                    }
//                }
//
//                // single seqrun - add within new visualization group
//                if (dtde.isDataFlavorSupported(SeqRunI.DATA_FLAVOR)) {
//                    try {
//                        SeqRunI run = (SeqRunI) dtde.getTransferable().getTransferData(SeqRunI.DATA_FLAVOR);
//
//                        //
//                        // check if the run is already present; if so, reject
//                        //
//                        for (VisualizationGroupI vg : rGroup.getVisualizationGroups()) {
//                            for (SeqRunI sr : vg.getSeqRuns()) {
//                                if (sr.equals(run)) { 
//                                    dtde.rejectDrop();
//                                }
//                            }
//                        }
//                        dtde.dropComplete(true);
//                        return;
//                    } catch (UnsupportedFlavorException | IOException ex) {
//                    }
//                }
//                dtde.rejectDrop();
//            }
//        });
//        setDropTarget(dt);
//    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        //System.err.println("ListView got "+evt.getPropertyName());
    }

}
