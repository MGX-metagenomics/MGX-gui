package de.cebitec.mgx.gui.goldstandard.util;

import de.cebitec.mgx.api.model.SeqRunI;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.awt.datatransfer.DataFlavor;
import java.io.Serial;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.TransferHandler;
import org.openide.util.datatransfer.ExTransferable;
import org.openide.util.datatransfer.MultiTransferObject;

/**
 *
 * @author patrick
 */
public class SingleListTransferHandler extends TransferHandler {

    @Serial
    private static final long serialVersionUID = 1L;

    public SingleListTransferHandler() {
        super();

    }

    @Override
    public boolean canImport(TransferSupport support) {
        Transferable t = support.getTransferable();
        if (t.isDataFlavorSupported(ExTransferable.multiFlavor)) {
            try {
                final MultiTransferObject mto = (MultiTransferObject) t.getTransferData(ExTransferable.multiFlavor);
                if (mto.areDataFlavorsSupported(new DataFlavor[]{SeqRunI.DATA_FLAVOR})) {
                    int elems = mto.getCount();
                    if (elems != 1) {
                        return false;
                    }
                    SeqRunI run = (SeqRunI) mto.getTransferData(0, SeqRunI.DATA_FLAVOR);
                    return true;
                }
            } catch (UnsupportedFlavorException | IOException e) {
            }
        }

        return t.isDataFlavorSupported(SeqRunI.DATA_FLAVOR);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean importData(TransferSupport support) {
        try {
            if (!canImport(support)) {
                return false;
            }

            JList<SeqRunI> list = (JList<SeqRunI>) support.getComponent();
            DefaultListModel<SeqRunI> listModel = (DefaultListModel<SeqRunI>) list.getModel();

            Transferable t = support.getTransferable();
            SeqRunI run = (SeqRunI) t.getTransferData(SeqRunI.DATA_FLAVOR);

            listModel.add(0, run);

        } catch (UnsupportedFlavorException | IOException ex) {
            return false;
        }

        return true;
    }

}
