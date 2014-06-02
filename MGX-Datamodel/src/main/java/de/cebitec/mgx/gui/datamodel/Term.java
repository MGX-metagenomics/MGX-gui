package de.cebitec.mgx.gui.datamodel;

import de.cebitec.mgx.api.model.TermI;
import de.cebitec.mgx.api.model.Identifiable;

/**
 *
 * @author sjaenick
 */
public class Term extends TermI {

    private long id = Identifiable.INVALID_IDENTIFIER;
    private long parent_id = Identifiable.INVALID_IDENTIFIER;
    private String name;
    private String description;

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public void setId(long id) {
        this.id = id;
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
    public long getParentId() {
        return parent_id;
    }

    @Override
    public void setParentId(long parent_id) {
        this.parent_id = parent_id;
    }

    @Override
    public String toString() {
        return name;
    }
}
