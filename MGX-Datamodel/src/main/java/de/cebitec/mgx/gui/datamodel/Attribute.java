package de.cebitec.mgx.gui.datamodel;

/**
 *
 * @author sjaenick
 */
public class Attribute extends ModelBase {
    
    protected AttributeType atype;
    protected String value;
    protected long job_id;
    private long parent_id = ModelBase.INVALID_IDENTIFIER;

    
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
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Attribute other = (Attribute) obj;
        if (this.atype != other.atype && (this.atype == null || !this.atype.equals(other.atype))) {
            return false;
        }
        if ((this.value == null) ? (other.value != null) : !this.value.equals(other.value)) {
            return false;
        }
        return true;
    }

    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + (this.value != null ? this.value.hashCode() : 0);
        return hash;
    }
}
