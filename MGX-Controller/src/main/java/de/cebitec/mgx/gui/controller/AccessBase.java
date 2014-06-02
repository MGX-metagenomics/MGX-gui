package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.access.AccessBaseI;
import de.cebitec.mgx.api.model.ModelBase;
import de.cebitec.mgx.client.MGXDTOMaster;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author sjaenick
 * @param <T>
 */
public abstract class AccessBase<T extends ModelBase<T>> implements AccessBaseI<T> {

    private final MGXMasterI master;
    private final MGXDTOMaster dtomaster;

    public AccessBase(MGXMasterI master, MGXDTOMaster dtomaster) {
        assert master != null;
        assert dtomaster != null;
        this.master = master;
        this.dtomaster = dtomaster;
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
