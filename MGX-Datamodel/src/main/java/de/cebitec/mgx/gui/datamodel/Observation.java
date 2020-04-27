package de.cebitec.mgx.gui.datamodel;

import de.cebitec.mgx.api.model.ObservationI;

/**
 *
 * @author sjaenick
 */
public class Observation extends ObservationI {

    private int start;
    private int stop;
    private String attributeName;
    private String attributeTypeName;

    public Observation() {
        super();
    }

    @Override
    public int getStart() {
        return start;
    }

    @Override
    public void setStart(int start) {
        this.start = start;
    }

    @Override
    public int getStop() {
        return stop;
    }

    @Override
    public void setStop(int stop) {
        this.stop = stop;
    }

    @Override
    public String getAttributeName() {
        return attributeName;
    }

    @Override
    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    @Override
    public String getAttributeTypeName() {
        return attributeTypeName;
    }

    @Override
    public void setAttributeTypeName(String attributeTypeName) {
        this.attributeTypeName = attributeTypeName;
    }

    @Override
    public int compareTo(ObservationI o) {
        int mymin = start < stop ? start : stop;
        int omin = o.getStart() < o.getStop() ? o.getStart() : o.getStop();
        return Integer.compare(mymin, omin);
    }
}
