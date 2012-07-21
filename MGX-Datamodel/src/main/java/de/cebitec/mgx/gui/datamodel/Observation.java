package de.cebitec.mgx.gui.datamodel;

/**
 *
 * @author sjaenick
 */
public class Observation extends ModelBase {

    private Sequence seq;
    private Job job;
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

    @Override
    public int hashCode() {
        return (int) (seq.getId() + job.getId() + attribute.getId());
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Observation) {
            Observation other = (Observation) object;
            return (other.seq.getId() == this.seq.getId()) && (other.job.getId() == this.job.getId()) && (other.attribute.getId() == this.attribute.getId());
        }
        return false;
    }
}
