package de.cebitec.mgx.api.model;

import de.cebitec.mgx.api.MGXMasterI;

/**
 *
 * @author sjaenick
 */
public interface MGXDataModelBaseI<T extends MGXDataModelBaseI<T>> extends ModelBaseI<T> {

    public MGXMasterI getMaster();
}
