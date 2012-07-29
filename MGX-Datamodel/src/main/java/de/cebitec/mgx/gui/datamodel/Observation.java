package de.cebitec.mgx.gui.datamodel;

/**
 *
 * @author sjaenick
 */
public class Observation extends ModelBase {

    private Attribute attribute;
    protected int start;
    protected int stop;

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

    public Attribute getAttribute() {
        return attribute;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }
}
