package de.cebitec.mgx.gui.datamodel;


/**
 *
 * @author sjaenick
 */
public abstract class Identifiable {

    protected Long id;

    public void setId(long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
