
package de.cebitec.mgx.gui.datamodel;

/**
 *
 * @author sjaenick
 */
public class Attribute extends ModelBase {

    protected String type;
    protected String value;

    public String getType() {
        return type;
    }

    public Attribute setType(String type) {
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Attribute)) {
            return false;
        }
        Attribute other = (Attribute) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
}
