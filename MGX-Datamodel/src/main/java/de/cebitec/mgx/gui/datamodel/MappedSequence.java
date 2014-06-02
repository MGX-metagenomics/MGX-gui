package de.cebitec.mgx.gui.datamodel;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.MappedSequenceI;

/**
 *
 * @author sjaenick
 */
public class MappedSequence extends MappedSequenceI {

    private final long seq_id;
    private final int identity; // range 0-100

    public MappedSequence(MGXMasterI m, long seq_id, int start, int stop, int identity) {
        super(m, start, stop);
        this.seq_id = seq_id;
        this.identity = identity;
    }

    @Override
    public long getSeqId() {
        return seq_id;
    }

    @Override
    public int getIdentity() {
        return identity;
    }

    @Override
    public int compareTo(MappedSequenceI o) {
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
        if (this.getStart() != other.getStart()) {
            return false;
        }
        if (this.getStop() != other.getStop()) {
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
        hash = 11 * hash + this.getStart();
        hash = 11 * hash + this.getStop();
        hash = 11 * hash + this.identity;
        return hash;
    }

    @Override
    public String toString() {
        return "MappedSequence{" + "seq_id=" + seq_id + ", start=" + getStart() + ", stop=" + getStop() + ", identity=" + identity + '}';
    }

}
