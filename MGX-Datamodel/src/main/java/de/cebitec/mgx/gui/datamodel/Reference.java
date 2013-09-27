package de.cebitec.mgx.gui.datamodel;

import java.awt.datatransfer.DataFlavor;

/**
 *
 * @author belmann
 */
public class Reference extends Identifiable {

    private String name;
    private int length;
    public static final DataFlavor DATA_FLAVOR = new DataFlavor(Reference.class, "Reference");

    public Reference() {
        super(DATA_FLAVOR);
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
