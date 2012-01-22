package de.cebitec.mgx.gui.datamodel;

/**
 *
 * @author sjaenick
 */
public class Attribute extends ModelBase {
    
    protected AttributeType type;
    protected String value;
    protected Long job_id;

    
    public AttributeType getTypeId() {
        return type;
    }

    public Attribute setType(AttributeType type) {
        this.type = type;
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
    public String toString() {
        return value;
    }

}
