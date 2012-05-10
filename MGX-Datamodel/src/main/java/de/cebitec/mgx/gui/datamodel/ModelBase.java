package de.cebitec.mgx.gui.datamodel;

/**
 *
 * @author sjaenick
 */
public abstract class ModelBase {
    
    public final static long INVALID_IDENTIFIER = -1;

    protected long id = INVALID_IDENTIFIER;
    protected MGXMasterI master;

    public void setId(long id) {
        assert this.id == INVALID_IDENTIFIER; // prevent changing of internal ID field
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public MGXMasterI getMaster() {
        return master;
    }

    public void setMaster(MGXMasterI m) {
        assert master == null; // prevent duplicate setting
        master = m;
    }
}
