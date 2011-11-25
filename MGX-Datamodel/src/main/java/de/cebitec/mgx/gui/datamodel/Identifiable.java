package de.cebitec.mgx.gui.datamodel;

import de.cebitec.mgx.client.MGXMaster;

/**
 *
 * @author sjaenick
 */
public abstract class Identifiable {

    protected Long id;
    private MGXMaster master;

    public void setId(long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public MGXMaster getMaster() {
        return master;
    }

    public void setMaster(MGXMaster master) {
        this.master = master;
    }
}
