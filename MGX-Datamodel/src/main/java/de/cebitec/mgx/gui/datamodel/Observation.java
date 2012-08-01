package de.cebitec.mgx.gui.datamodel;

/**
 *
 * @author sjaenick
 */
public class Observation extends ModelBase {

    private int start;
    private int stop;
    private String attributeName;
    private String attributeTypeName;

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
}
