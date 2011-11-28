package de.cebitec.mgx.gui.datamodel;



/**
 *
 * @author sjaenick
 */
public abstract class Identifiable {

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

//    public void setMaster(MGXMasterI master) {
//        this.master = master;
//    }
}
