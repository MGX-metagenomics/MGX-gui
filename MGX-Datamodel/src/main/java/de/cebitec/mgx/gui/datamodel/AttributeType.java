package de.cebitec.mgx.gui.datamodel;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.AttributeTypeI;

/**
 *
 * @author sjaenick
 */
public class AttributeType extends AttributeTypeI {

    protected String name;
    protected char value_type;
    protected char structure;

    public AttributeType(MGXMasterI m, long id, String name, char value_type, char structure) {
        super(m);
        this.id = id;
        this.name = name;
        this.value_type = value_type;
        this.structure = structure;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public char getValueType() {
        return value_type;
    }

    @Override
    public void setValueType(char value_type) {
        this.value_type = value_type;
    }

    @Override
    public char getStructure() {
        return structure;
    }

    @Override
    public AttributeType setStructure(char structure) {
        this.structure = structure;
        return this;
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
        if (this.value_type != other.value_type) {
            return false;
        }
        if (this.structure != other.structure) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 97 * hash + (this.value_type);
        hash = 97 * hash + (this.structure);
        return hash;
    }

    @Override
    public String toString() {
        return name; // "AttributeType{" + "name=" + name + ", value_type=" + value_type + ", structure=" + structure + '}';
    }

    @Override
    public int compareTo(AttributeTypeI o) {
        // compareTo returns an int which is 0 if the two strings are identical, 
        // positive if s1 > s2, and negative if s1 < s2.

        int ret = this.value_type == o.getValueType() ? 0
                : this.value_type - o.getValueType() < 0 ? -1 : 1;
        if (ret == 0) {
            ret = this.structure == o.getStructure() ? 0
                    : this.structure - o.getStructure() < 0 ? -1 : 1;
            if (ret == 0) {
                return this.name.compareTo(o.getName());
            }
        }
        return ret;
    }
}
