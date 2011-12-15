package de.cebitec.mgx.gui.datamodel;

/**
 *
 * @author sjaenick
 */
public abstract class ModelBase {

    protected Long id;
    protected MGXMasterI master;

    public void setId(long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public MGXMasterI getMaster() {
        return master;
    }
}
