package de.cebitec.mgx.gui.datamodel;

import de.cebitec.mgx.client.MGXMaster;

/**
 *
 * @author sj
 */
public abstract class ModelBase<T> {

    private MGXMaster master;
    private T dto;

    public ModelBase(MGXMaster master, T dto) {
        this.master = master;
        this.dto = dto;
    }

    public MGXMaster getMaster() {
        return master;
    }

    public void setMaster(MGXMaster master) {
        this.master = master;
    }

    public T getDTO() {
        return dto;
    }
}
