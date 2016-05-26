/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.model;

import de.cebitec.mgx.api.MGXMasterI;
import java.awt.datatransfer.DataFlavor;

/**
 *
 * @author sj
 */
public abstract class MGXReferenceI extends Identifiable<MGXReferenceI> {

    public static final DataFlavor DATA_FLAVOR = new DataFlavor(MGXReferenceI.class, "MGXReferenceI");

    public MGXReferenceI(MGXMasterI m, DataFlavor df) {
        super(m, df);
    }

    public abstract int getLength();

    public abstract void setLength(int length);

    public abstract String getName();

    public abstract void setName(String name);

    @Override
    public abstract String toString();

    @Override
    public abstract int compareTo(MGXReferenceI o);

}
