package de.cebitec.mgx.gui.datamodel;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 *
 * @author sjaenick
 */
public abstract class ModelBase implements Transferable {

    protected Long id;
    protected MGXMasterI master;
    private static DataFlavor nodeFlavor = null;

    public void setId(Long id) {
        assert this.id == null; // prevent changing of internal ID field
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public MGXMasterI getMaster() {
        return master;
    }

    public void setMaster(MGXMasterI m) {
        assert master == null; // prevent duplicate setting
        master = m;
    }

    public static DataFlavor getNodeFlavor() {
        if (nodeFlavor == null) {
            try {
                nodeFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=" + ModelBase.class.getName());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return nodeFlavor;
    }

    @Override
    public Transferable getTransferData(DataFlavor df) throws UnsupportedFlavorException, IOException {
        if (df == getNodeFlavor()) {
            return this;
        } else {
            throw new UnsupportedFlavorException(df);
        }
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{getNodeFlavor()};
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor df) {
        return df == getNodeFlavor();
    }
}
