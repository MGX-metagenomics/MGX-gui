package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.access.AccessBaseI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.MGXDataModelBaseI;
import de.cebitec.mgx.client.MGXDTOMaster;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author sjaenick
 * @param <T>
 */
public abstract class AccessBase<T extends MGXDataModelBaseI<T>> extends MasterHolder implements AccessBaseI<T> {

    public AccessBase(MGXMasterI master, MGXDTOMaster dtomaster) throws MGXException {
        super(master, dtomaster);
    }

    protected static List<String> split(String message, String separator) {
        return Arrays.asList(message.split(separator));
    }
}
