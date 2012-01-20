package de.cebitec.mgx.gui.datamodel;

/**
 *
 * @author sjaenick
 */
public class AttributeType {
    
    protected Long id;
    protected String name;
    protected String value_type;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
}
