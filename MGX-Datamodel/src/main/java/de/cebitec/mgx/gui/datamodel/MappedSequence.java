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
        int ret = Integer.compare(getMin(), o.getMin());
        if (ret != 0) {
            return ret;
        }
        ret = Integer.compare(getMax(), o.getMax());
        if (ret != 0) {
            return ret;
        }
        ret = Integer.compare(getIdentity(), o.getIdentity());
        if (ret != 0) {
            return ret;
        }
        return Long.compare(getSeqId(), o.getSeqId());
    }

//    @Override
//    public int hashCode() {
//        int hash = 7;
//        hash = 29 * hash + (int) (this.seq_id ^ (this.seq_id >>> 32));
//        hash = 29 * hash + this.start;
//        return hash;
//    }
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MappedSequence other = (MappedSequence) obj;
        if (this.seq_id != other.seq_id) {
            return false;
        }
        if (this.start != other.start) {
            return false;
        }
        if (this.stop != other.stop) {
            return false;
        }
        if (this.identity != other.identity) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + (int) (this.seq_id ^ (this.seq_id >>> 32));
        hash = 11 * hash + this.start;
        hash = 11 * hash + this.stop;
        hash = 11 * hash + this.identity;
        return hash;
    }

    @Override
    public String toString() {
        return "MappedSequence{" + "seq_id=" + seq_id + ", start=" + start + ", stop=" + stop + ", identity=" + identity + '}';
    }

}
