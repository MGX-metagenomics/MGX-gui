package de.cebitec.mgx.gui.datamodel;

/**
 *
 * @author sjaenick
 */
public class MGXFile {

    protected MGXMasterI master;
    protected String name;

    public MGXMasterI getMaster() {
        return master;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
