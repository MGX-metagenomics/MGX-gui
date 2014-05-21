package de.cebitec.mgx.gui.datamodel;

/**
 *
 * @author sj
 */
public class MappedSequence implements Comparable<MappedSequence> {

    private final long seq_id;
    private final int start;
    private final int stop;
    private final int identity; // range 0-100

    public MappedSequence(long seq_id, int start, int stop, int identity) {
        this.seq_id = seq_id;
        this.start = start;
        this.stop = stop;
        this.identity = identity;
    }

    public long getSeqId() {
        return seq_id;
    }

    public int getStart() {
        return start;
    }

    public int getStop() {
        return stop;
    }

    public int getIdentity() {
        return identity;
    }

    public int getMax() {
        return Math.max(getStart(), getStop());
    }

    public int getMin() {
        return Math.min(getStart(), getStop());
    }

    @Override
    public int compareTo(MappedSequence o) {
        int mymin = Math.min(getStart(), getStop());
        int omin = Math.min(o.getStart(), o.getStop());
        return Integer.compare(mymin, omin);
    }

    @Override
    public String toString() {
        return "MappedSequence{" + "seq_id=" + seq_id + ", start=" + start + ", stop=" + stop + '}';
    }
}
