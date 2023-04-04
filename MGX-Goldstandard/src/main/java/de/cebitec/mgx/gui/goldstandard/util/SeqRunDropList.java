package de.cebitec.mgx.gui.goldstandard.util;

import de.cebitec.mgx.api.model.SeqRunI;
import java.awt.Color;
import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.IOException;
import java.io.Serial;
import java.util.Collection;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import org.openide.util.datatransfer.ExTransferable;
import org.openide.util.datatransfer.MultiTransferObject;
import org.openide.nodes.Node;
import org.openide.nodes.NodeTransfer;

/**
 *
 * @author patrick
 */
public class SeqRunDropList extends JPanel implements DropTargetListener {

    @Serial
    private static final long serialVersionUID = 1L;

    private final DefaultListModel<SeqRunI> listModel = new DefaultListModel<>();
    private DropTarget dropTarget;
//    private JLabel jLabel1;
//    private JScrollPane jScrollPane1;
    private JList<SeqRunI> list;

    public SeqRunDropList() {
    }

    @SuppressWarnings("unchecked")
    public void init() {
        //setLayout(null);
        list = new JList<>();
        list.setModel(listModel);
        dropTarget = new DropTarget(list, this);
        list.setDragEnabled(true);
//        SeqRunListCellRenderer renderer = new SeqRunListCellRenderer();
//        list.setCellRenderer(renderer);
        //list.setTransferHandler(new FileTransferHandler());
//        jScrollPane1 = new JScrollPane(list);
        add(list);
    }

    @Override
    public void dragEnter(DropTargetDragEvent dtde) {
        Transferable t = dtde.getTransferable();
        Node node = NodeTransfer.node(t, DnDConstants.ACTION_REFERENCE + NodeTransfer.CLIPBOARD_COPY);
        Collection<? extends SeqRunI> seqruns = node.getLookup().lookupAll(SeqRunI.class);

        if (seqruns.size() > 1) {
            try {
                final MultiTransferObject mto = (MultiTransferObject) t.getTransferData(ExTransferable.multiFlavor);
                if (mto.areDataFlavorsSupported(new DataFlavor[]{SeqRunI.DATA_FLAVOR})) {
                    int elems = mto.getCount();
                    for (int i = 0; i < elems; i++) {
                        SeqRunI run = (SeqRunI) mto.getTransferData(i, SeqRunI.DATA_FLAVOR);
                    }
                    dtde.acceptDrag(DnDConstants.ACTION_COPY);
                    return;
                }
            } catch (UnsupportedFlavorException | IOException e) {
            }
        }

        if (seqruns.size() == 1) {
            SeqRunI run = seqruns.iterator().next();
            dtde.acceptDrag(DnDConstants.ACTION_COPY);
            return;
        }

        dtde.rejectDrag();

//        if (t.isDataFlavorSupported(ExTransferable.multiFlavor)) {
//            try {
//                final MultiTransferObject mto = (MultiTransferObject) t.getTransferData(ExTransferable.multiFlavor);
//                if (mto.areDataFlavorsSupported(new DataFlavor[]{SeqRunI.DATA_FLAVOR})) {
//                    int elems = mto.getCount();
//                    for (int i = 0; i < elems; i++) {
//                        SeqRunI run = (SeqRunI) mto.getTransferData(i, SeqRunI.DATA_FLAVOR);
//                    }
//                    dtde.acceptDrag(DnDConstants.ACTION_COPY);
//                    return;
//                }
//            } catch (UnsupportedFlavorException | IOException e) {
//            }
//        }
//
//        if (dtde.isDataFlavorSupported(SeqRunI.DATA_FLAVOR)) {
//            try {
//                SeqRunI run = (SeqRunI) dtde.getTransferable().getTransferData(SeqRunI.DATA_FLAVOR);
//                dtde.acceptDrag(DnDConstants.ACTION_COPY);
//                return;
//            } catch (UnsupportedFlavorException | IOException ex) {
//            }
//        }
//
//        dtde.rejectDrag();
    }

    @Override
    public void dragOver(DropTargetDragEvent arg0) {
        // nothing
    }

    @Override
    public void dropActionChanged(DropTargetDragEvent arg0) {
        // nothing
    }

    @Override
    public void dragExit(DropTargetEvent arg0) {
        // nothing
    }

    @SuppressWarnings("unchecked")
    @Override
    public void drop(DropTargetDropEvent evt) {
        int action = evt.getDropAction();
        evt.acceptDrop(action);
        Transferable data = evt.getTransferable();
        Node node = NodeTransfer.node(data, DnDConstants.ACTION_REFERENCE + NodeTransfer.CLIPBOARD_COPY);
        Collection<? extends SeqRunI> seqruns = node.getLookup().lookupAll(SeqRunI.class);
        if (!seqruns.isEmpty()) {
            for (SeqRunI run : seqruns) {
                listModel.addElement(run);
            }
        }
        evt.dropComplete(true);
    }
}

/**
 * A FileListCellRenderer for a File.
 */
class SeqRunListCellRenderer extends DefaultListCellRenderer {

    private static final long serialVersionUID = -7799441088157759804L;
    private final JLabel label;
    private final Color textSelectionColor = Color.BLACK;
    private final Color backgroundSelectionColor = Color.CYAN;
    private final Color textNonSelectionColor = Color.BLACK;
    private final Color backgroundNonSelectionColor = Color.WHITE;

    SeqRunListCellRenderer() {
        label = new JLabel();
        label.setOpaque(true);
    }

    @Override
    public Component getListCellRendererComponent(
            JList<?> list,
            Object value,
            int index,
            boolean selected,
            boolean expanded) {

        if (value instanceof SeqRunI) {
            SeqRunI seqrun = (SeqRunI) value;
            label.setText(seqrun.getName());
            label.setToolTipText(seqrun.getAccession());

            if (selected) {
                label.setBackground(backgroundSelectionColor);
                label.setForeground(textSelectionColor);
            } else {
                label.setBackground(backgroundNonSelectionColor);
                label.setForeground(textNonSelectionColor);
            }
        }

        return label;
    }
}
