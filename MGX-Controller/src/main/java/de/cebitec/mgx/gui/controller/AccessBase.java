package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.access.AccessBaseI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.exception.MGXLoggedoutException;
import de.cebitec.mgx.api.model.MGXDataModelBaseI;
import de.cebitec.mgx.client.MGXDTOMaster;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author sjaenick
 * @param <T>
 */
public abstract class AccessBase<T extends MGXDataModelBaseI<T>> implements AccessBaseI<T> {

    private final MGXMasterI master;
    private final MGXDTOMaster dtomaster;

    public AccessBase(MGXMasterI master, MGXDTOMaster dtomaster) throws MGXException {
        this.master = master;
        this.dtomaster = dtomaster;
        if (master.isDeleted()) {
            throw new MGXLoggedoutException("You are disconnected.");
        }
    }

    protected MGXDTOMaster getDTOmaster() {
        return dtomaster;
    }

    protected MGXMasterI getMaster() {
        return master;
    }

//    @Override
//    public abstract long create(T obj);
//
//    @Override
//    public abstract T fetch(long id);
//
//    @Override
//    public abstract Iterator<T> fetchall();
//
//    @Override
//    public abstract void update(T obj);
//
//    @Override
//    public abstract TaskI delete(T obj);
    protected static List<String> split(String message, String separator) {
        return Arrays.asList(message.split(separator));
    }
}
