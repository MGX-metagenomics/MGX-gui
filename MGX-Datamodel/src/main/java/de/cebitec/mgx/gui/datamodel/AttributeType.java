package de.cebitec.mgx.gui.datamodel;

/**
 *
 * @author sjaenick
 */
public class AttributeType extends ModelBase implements Comparable<AttributeType> {
    
    protected String name;
    protected String value_type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValueType() {
        return value_type;
    }

    public void setValueType(String value_type) {
        this.value_type = value_type;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AttributeType other = (AttributeType) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if ((this.value_type == null) ? (other.value_type != null) : !this.value_type.equals(other.value_type)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 97 * hash + (this.value_type != null ? this.value_type.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return name + " (" + value_type + ")";
    }

    @Override
    public int compareTo(AttributeType o) {
        // compareTo returns an int which is 0 if the two strings are identical, 
        // positive if s1 > s2, and negative if s1 < s2.
        
        int ret = this.value_type.compareTo(o.value_type);
        if (ret == 0) {
            return this.name.compareTo(o.name);
        }
        return ret;
    }
    
    
}
