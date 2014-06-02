package de.cebitec.mgx.gui.datamodel;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.MGXReferenceI;

/**
 *
 * @author belmann
 */
public class Reference extends MGXReferenceI {

    private String name;
    private int length;

    public Reference(MGXMasterI m) {
        super(m, DATA_FLAVOR);
    }

    @Override
    public int getLength() {
        return length;
    }

    @Override
    public void setLength(int length) {
        this.length = length;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(MGXReferenceI o) {
        return this.name.compareTo(o.getName());
    }
}
