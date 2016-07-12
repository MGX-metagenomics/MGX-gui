package de.cebitec.mgx.gui.datamodel;

import de.cebitec.mgx.api.model.SequenceI;

/**
 *
 * @author sjaenick
 */
public class Sequence extends SequenceI {

    protected String name;
    protected int length = -1;
    protected String sequence = null;

    public Sequence() {
    }

//    public Sequence(MGXMasterI m) {
//        super(m);
//    }
    
    @Override
     public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getSequence() {
        return sequence;
    }

    @Override
    public void setSequence(String sequence) {
        this.sequence = sequence;
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
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(SequenceI o) {
        return name.compareTo(o.getName());
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Sequence)) {
            return false;
        }
        Sequence other = (Sequence) object;
        if ((this.id == INVALID_IDENTIFIER && other.id != INVALID_IDENTIFIER) || (this.id != INVALID_IDENTIFIER && this.id != other.id)) {
            return false;
        }
        return true;
    }
}
