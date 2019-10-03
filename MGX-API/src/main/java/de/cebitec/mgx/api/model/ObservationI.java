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
 * @author sjaenick
 */
public abstract class ObservationI extends MGXDataModelBase<ObservationI> implements Comparable<ObservationI> {
    //
    public static final DataFlavor DATA_FLAVOR = new DataFlavor(ObservationI.class, "ObservationI");

    public ObservationI(MGXMasterI master) {
        super(master, DATA_FLAVOR);
    }

    public abstract int getStart();

    public abstract void setStart(int start);

    public abstract int getStop();

    public abstract void setStop(int stop);

    public abstract String getAttributeName();

    public abstract void setAttributeName(String attributeName);

    public abstract String getAttributeTypeName();

    public abstract void setAttributeTypeName(String attributeTypeName);

    @Override
    public abstract int compareTo(ObservationI o);
    
}
