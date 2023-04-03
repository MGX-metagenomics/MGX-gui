package de.cebitec.mgx.gui.goldstandard.util;

import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.model.SeqRunI;
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
import java.io.Serial;
import java.util.Collection;
import org.openide.explorer.view.TreeTableView;
import org.openide.nodes.Node;
import org.openide.nodes.NodeTransfer;
import org.openide.util.datatransfer.ExTransferable;
import org.openide.util.datatransfer.MultiTransferObject;

/**
 *
 * @author patrick
 */
public class SeqRunList extends TreeTableView implements PropertyChangeListener {

    @Serial
    private static final long serialVersionUID = 1L;

    private final VisualizationGroupI vGroup;
    private final SingleSeqRunNodeFactory vgnf;

    public SeqRunList(VisualizationGroupI vGroup, SingleSeqRunNodeFactory vgnf) {
        super();
        this.vGroup = vGroup;
        this.vgnf = vgnf;
        setRootVisible(false);
        setDropTarget(true);
        //setShowParentNode(true);
        setAllowedDropActions(DnDConstants.ACTION_COPY + DnDConstants.ACTION_REFERENCE);
        setDropTarget();
        vGroup.addPropertyChangeListener(this);
    }

    private void setDropTarget() {
        DropTarget dt = new DropTarget(this, new DropTargetAdapter() {
            @Override
            public void dragEnter(DropTargetDragEvent dtde) {
                Transferable t = dtde.getTransferable();
                Node node = NodeTransfer.node(t, DnDConstants.ACTION_REFERENCE + NodeTransfer.CLIPBOARD_COPY);
                Collection<? extends SeqRunI> seqruns = node.getLookup().lookupAll(SeqRunI.class);
                if (t.isDataFlavorSupported(ExTransferable.multiFlavor)) {
                    try {
                        final MultiTransferObject mto = (MultiTransferObject) t.getTransferData(ExTransferable.multiFlavor);
                        if (mto.areDataFlavorsSupported(new DataFlavor[]{SeqRunI.DATA_FLAVOR})) {
                            int elems = mto.getCount();
                            for (int i = 0; i < elems; i++) {
                                SeqRunI run = (SeqRunI) mto.getTransferData(i, SeqRunI.DATA_FLAVOR);
                                if (vGroup.getContent().contains(run)) {
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
//                if (dtde.isDataFlavorSupported(new DataFlavor(Node.class, "org.openide.nodes.Node"))) {
                    try {
                        SeqRunI run = (SeqRunI) dtde.getTransferable().getTransferData(SeqRunI.DATA_FLAVOR);
                        if (run != null && !vGroup.getContent().contains(run)) {
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
                                if (vGroup.getContent().contains(run)) {
                                    dtde.rejectDrop();
                                    return;
                                }
                            }
                            for (int i = 0; i < elems; i++) {
                                SeqRunI run = (SeqRunI) mto.getTransferData(i, SeqRunI.DATA_FLAVOR);
                                //SeqRunNode srn = new SeqRunNode(run.getMaster(), run, Children.LEAF);
                                //vgnf.addNode(srn);
                                vgnf.addSeqRun(run);
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
                        if (run != null && !vGroup.getContent().contains(run)) {
                            //SeqRunNode srn = new SeqRunNode(run.getMaster(), run, Children.LEAF);
                            //vgnf.addNode(srn);
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
