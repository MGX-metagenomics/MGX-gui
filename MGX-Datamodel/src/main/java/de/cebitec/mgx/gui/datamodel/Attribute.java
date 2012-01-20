package de.cebitec.mgx.gui.datamodel;

/**
 *
 * @author sjaenick
 */
public class Attribute extends ModelBase {

    protected Long type_id;
    protected String value;
    protected Long job_id;

    public Long getTypeId() {
        return type_id;
    }

    public Attribute setType(Long typeId) {
        this.type_id = typeId;
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

    public void setJobId(Long job_id) {
        this.job_id = job_id;
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
        if (this.type_id != other.type_id && (this.type_id == null || !this.type_id.equals(other.type_id))) {
            return false;
        }
        if ((this.value == null) ? (other.value != null) : !this.value.equals(other.value)) {
            return false;
        }
        if (this.job_id != other.job_id && (this.job_id == null || !this.job_id.equals(other.job_id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + (this.type_id != null ? this.type_id.hashCode() : 0);
        hash = 59 * hash + (this.value != null ? this.value.hashCode() : 0);
        hash = 59 * hash + (this.job_id != null ? this.job_id.hashCode() : 0);
        return hash;
    }

}
