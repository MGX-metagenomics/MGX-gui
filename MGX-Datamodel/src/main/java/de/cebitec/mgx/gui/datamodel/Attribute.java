package de.cebitec.mgx.gui.datamodel;

/**
 *
 * @author sjaenick
 */
public class Attribute extends ModelBase {
    
    protected AttributeType atype;
    protected String value;
    protected Long job_id;
    private Long parent_id = null;

    
    public Long getParentID() {
        return parent_id;
    }

    public void setParentID(Long parent_id) {
        this.parent_id = parent_id;
    }
    
    public AttributeType getType() {
        return atype;
    }

    public Attribute setType(AttributeType atype) {
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

    public Long getJobId() {
        return job_id;
    }

    public Attribute setJobId(Long job_id) {
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
