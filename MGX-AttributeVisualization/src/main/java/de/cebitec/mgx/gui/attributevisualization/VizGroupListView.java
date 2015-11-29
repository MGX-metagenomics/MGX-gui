/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.attributevisualization;

import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.gui.nodefactory.GroupedSeqRunNodeFactory;
import java.awt.Cursor;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.openide.explorer.view.TreeTableView;
import org.openide.util.datatransfer.ExTransferable;
import org.openide.util.datatransfer.MultiTransferObject;

/**
 *
 * @author sjaenick
 */
final class VizGroupListView extends TreeTableView implements PropertyChangeListener {

    private final VisualizationGroupI vGroup;
    private final GroupedSeqRunNodeFactory vgnf;

    public VizGroupListView(VisualizationGroupI vGroup) {
        this(vGroup, new GroupedSeqRunNodeFactory(vGroup));
    }

    public VizGroupListView(VisualizationGroupI vGroup, GroupedSeqRunNodeFactory vgnf) {
        super();
        this.vGroup = vGroup;
        this.vgnf =  vgnf;
        setRootVisible(false);
        setDropTarget(true);
        //setShowParentNode(true);
        setAllowedDropActions(DnDConstants.ACTION_COPY + DnDConstants.ACTION_REFERENCE);
        setDropTarget();
        vGroup.addPropertyChangeListener(this);
    } //setShowParentNode(true);

    private void setDropTarget() {
        DropTarget dt = new DropTarget(this, new DropTargetAdapter() {
            @Override
            public void dragEnter(DropTargetDragEvent dtde) {
                Transferable t = dtde.getTransferable();
                if (t.isDataFlavorSupported(ExTransferable.multiFlavor)) {
                    try {
                        final MultiTransferObject mto = (MultiTransferObject) t.getTransferData(ExTransferable.multiFlavor);
                        if (mto.areDataFlavorsSupported(new DataFlavor[]{SeqRunI.DATA_FLAVOR})) {
                            int elems = mto.getCount();
                            for (int i = 0; i < elems; i++) {
                                SeqRunI run = (SeqRunI) mto.getTransferData(i, SeqRunI.DATA_FLAVOR);
                                if (vGroup.getSeqRuns().contains(run)) {
                                    dtde.rejectDrag();
                                    return;
                                }
                            }
                            dtde.acceptDrag(DnDConstants.ACTION_COPY);
                            return;
                        }
                    } catch (UnsupportedFlavorException | IOException e) {
                    }
                }
                if (dtde.isDataFlavorSupported(SeqRunI.DATA_FLAVOR)) {
                    try {
                        SeqRunI run = (SeqRunI) dtde.getTransferable().getTransferData(SeqRunI.DATA_FLAVOR);
                        if (run != null && !vGroup.getSeqRuns().contains(run)) {
                            dtde.acceptDrag(DnDConstants.ACTION_COPY);
                            return;
                        }
                    } catch (UnsupportedFlavorException | IOException ex) {
                    }
                }
                dtde.rejectDrag();
            }

            @Override
            public void drop(DropTargetDropEvent dtde) {
                Transferable t = dtde.getTransferable();
                if (t.isDataFlavorSupported(ExTransferable.multiFlavor)) {
                    try {
                        final MultiTransferObject mto = (MultiTransferObject) t.getTransferData(ExTransferable.multiFlavor);
                        if (mto.areDataFlavorsSupported(new DataFlavor[]{SeqRunI.DATA_FLAVOR})) {
                            int elems = mto.getCount();
                            for (int i = 0; i < elems; i++) {
                                SeqRunI run = (SeqRunI) mto.getTransferData(i, SeqRunI.DATA_FLAVOR);
                                if (vGroup.getSeqRuns().contains(run)) {
                                    dtde.rejectDrop();
                                    return;
                                }
                            }
                            try {
                                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                                Set<SeqRunI> newRuns = new HashSet<>();
                                for (int i = 0; i < elems; i++) {
                                    SeqRunI run = (SeqRunI) mto.getTransferData(i, SeqRunI.DATA_FLAVOR);
                                    newRuns.add(run);
                                }
                                vgnf.addSeqRuns(newRuns);
                            } finally {
                                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                            }
                            dtde.dropComplete(true);
                            return;
                        }
                    } catch (UnsupportedFlavorException | IOException e) {
                    }
                }
                if (dtde.isDataFlavorSupported(SeqRunI.DATA_FLAVOR)) {
                    try {
                        SeqRunI run = (SeqRunI) dtde.getTransferable().getTransferData(SeqRunI.DATA_FLAVOR);
                        if (run != null && !vGroup.getSeqRuns().contains(run)) {
                            vgnf.addSeqRun(run);
                            dtde.dropComplete(true);
                            return;
                        }
                    } catch (UnsupportedFlavorException | IOException ex) {
                    }
                }
                dtde.rejectDrop();
            }
        });
        setDropTarget(dt);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        //System.err.println("ListView got "+evt.getPropertyName());
    }

}
