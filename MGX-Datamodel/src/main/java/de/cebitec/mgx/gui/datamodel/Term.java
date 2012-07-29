package de.cebitec.mgx.gui.datamodel;

/**
 *
 * @author sjaenick
 */
public class Term {

    private long id = Identifiable.INVALID_IDENTIFIER;
    private long parent_id = Identifiable.INVALID_IDENTIFIER;
    private String name;
    private String description;

    public long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getParentId() {
        return parent_id;
    }

    public void setParentId(long parent_id) {
        this.parent_id = parent_id;
    }

    @Override
    public String toString() {
        return name;
    }
}
