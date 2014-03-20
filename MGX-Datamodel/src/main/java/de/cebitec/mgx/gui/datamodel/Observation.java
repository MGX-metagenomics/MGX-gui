package de.cebitec.mgx.gui.datamodel;

import java.awt.datatransfer.DataFlavor;

/**
 *
 * @author sjaenick
 */
public class Observation extends ModelBase<Observation> {

    private int start;
    private int stop;
    private String attributeName;
    private String attributeTypeName;
    //
    public static final DataFlavor DATA_FLAVOR = new DataFlavor(Observation.class, "Observation");

    public Observation() {
        super(DATA_FLAVOR);
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getStop() {
        return stop;
    }

    public void setStop(int stop) {
        this.stop = stop;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getAttributeTypeName() {
        return attributeTypeName;
    }

    public void setAttributeTypeName(String attributeTypeName) {
        this.attributeTypeName = attributeTypeName;
    }

    @Override
    public int compareTo(Observation o) {
        int mymin = start < stop ? start : stop;
        int omin = o.start < o.stop ? o.start : o.stop;
        return Integer.compare(mymin, omin);
    }
}
