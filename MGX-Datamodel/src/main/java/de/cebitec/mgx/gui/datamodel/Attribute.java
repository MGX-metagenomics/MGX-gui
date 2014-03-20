package de.cebitec.mgx.gui.datamodel;

import java.awt.datatransfer.DataFlavor;
import java.util.Objects;

/**
 *
 * @author sjaenick
 */
public class Attribute extends Identifiable<Attribute> {

    protected AttributeType atype;
    protected String value;
    protected long job_id;
    private long parent_id = Identifiable.INVALID_IDENTIFIER;
    //
    public static final DataFlavor DATA_FLAVOR = new DataFlavor(Attribute.class, "Attribute");

    public Attribute() {
        super(DATA_FLAVOR);
    }

    public long getParentID() {
        return parent_id;
    }

    public void setParentID(long parent_id) {
        this.parent_id = parent_id;
    }

    public AttributeType getAttributeType() {
        return atype;
    }

    public Attribute setAttributeType(AttributeType atype) {
        this.atype = atype;
        return this;
    }

    public String getValue() {
        return value;
    }

    public Attribute setValue(String value) {
        this.value = value;
        return this;
    }

    public long getJobId() {
        return job_id;
    }

    public Attribute setJobId(long job_id) {
        this.job_id = job_id;
        return this;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + (this.value != null ? this.value.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Attribute other = (Attribute) obj;
        if (!Objects.equals(this.atype, other.atype)) {
            return false;
        }
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        
        // parent ids can only be compared if they are from the same project
        if (this.getMaster() == other.getMaster()) {
            if (this.job_id == other.job_id && this.parent_id != other.parent_id) {
                return false;
            }
        }
        /*
         * this is still wrong; we're unable to distinguish between
         * equally-named and -ranked attributes from different projects,
         * e.g. certain taxonomic groups which occur more than once.
         */
        return true;
    }

    @Override
    public int compareTo(Attribute o) {
        return this.value.compareTo(o.value);
    }
}
