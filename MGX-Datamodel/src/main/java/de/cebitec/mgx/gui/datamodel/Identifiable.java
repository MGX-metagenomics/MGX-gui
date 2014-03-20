package de.cebitec.mgx.gui.datamodel;

import java.awt.datatransfer.DataFlavor;

/**
 *
 * @author sj
 */
public abstract class Identifiable<T> extends ModelBase<T> {

    public final static long INVALID_IDENTIFIER = -1;
    protected long id = INVALID_IDENTIFIER;

    public Identifiable(DataFlavor df) {
        super(df);
    }

    public void setId(long id) {
        assert this.id == INVALID_IDENTIFIER; // prevent changing of internal ID field
        assert id != INVALID_IDENTIFIER;
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
