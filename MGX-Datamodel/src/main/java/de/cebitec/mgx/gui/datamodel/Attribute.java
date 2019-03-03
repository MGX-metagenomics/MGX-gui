package de.cebitec.mgx.gui.datamodel;

import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.Identifiable;
import java.util.Objects;

/**
 *
 * @author sjaenick
 */
public class Attribute extends AttributeI {

    protected AttributeTypeI atype;
    protected String value;
    protected long job_id;
    private long parent_id = Identifiable.INVALID_IDENTIFIER;

    public Attribute() { //MGXMasterI m) {
        super();
        //super(m);
    }

    @Override
    public long getParentID() {
        return parent_id;
    }

    @Override
    public void setParentID(long parent_id) {
        this.parent_id = parent_id;
    }

    @Override
    public AttributeTypeI getAttributeType() {
        return atype;
    }

    @Override
    public Attribute setAttributeType(AttributeTypeI atype) {
        assert atype != null;
        assert this.atype == null;
        this.atype = atype;
        return this;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public long getJobId() {
        return job_id;
    }

    @Override
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
        final AttributeI other = (AttributeI) obj;
        if (!Objects.equals(this.atype, other.getAttributeType())) {
            return false;
        }
        if (!Objects.equals(this.value, other.getValue())) {
            return false;
        }

        // parent ids can only be compared if they are from the same project
        if (this.getAttributeType() != null && other.getAttributeType() != null) {
            if (this.getAttributeType().getMaster() == other.getAttributeType().getMaster()) {
                if (this.job_id == other.getJobId() && this.parent_id != other.getParentID()) {
                    return false;
                }
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
    public int compareTo(AttributeI o) {
        return this.value.compareTo(o.getValue());
    }
}
