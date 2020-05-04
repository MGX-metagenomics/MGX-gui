package de.cebitec.mgx.api.model;

import de.cebitec.mgx.api.MGXMasterI;
import java.awt.datatransfer.DataFlavor;

/**
 *
 * @author sj
 */
public abstract class Identifiable<T extends MGXDataModelBase<T>> extends MGXDataModelBase<T> {

    public final static long INVALID_IDENTIFIER = -1;
    protected long id = INVALID_IDENTIFIER;

    public Identifiable(MGXMasterI master, DataFlavor df) {
        super(master, df);
    }

    public final void setId(long id) {
        this.id = id;
    }

    public final long getId() {
        return id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash = (int) (31 * hash + this.id);
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof Identifiable) {
            Identifiable other = (Identifiable) o;

            if ((this.id == INVALID_IDENTIFIER && other.getId() != INVALID_IDENTIFIER) || (this.id != INVALID_IDENTIFIER && this.id != other.getId())) {
                return false;
            }

            return this.getId() == other.getId() 
                    && ((this.getMaster() == null && other.getMaster() == null)
                    || (this.getMaster() != null && this.getMaster().equals(other.getMaster())));
        }
        return false;
    }
}
