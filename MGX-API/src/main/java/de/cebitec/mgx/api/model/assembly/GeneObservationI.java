/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.model.assembly;

import de.cebitec.mgx.api.model.Identifiable;
import java.awt.datatransfer.DataFlavor;

/**
 *
 * @author sj
 */
public abstract class GeneObservationI implements Comparable<GeneObservationI> {

    public static final DataFlavor DATA_FLAVOR = new DataFlavor(GeneObservationI.class, "GeneObservationI");
    protected long id = Identifiable.INVALID_IDENTIFIER;

    public GeneObservationI() {
        //super(master, DATA_FLAVOR);
    }

    public final void setId(long id) {
        this.id = id;
    }

    public final long getId() {
        return id;
    }

    public abstract int getStart();

    public abstract void setStart(int start);

    public abstract int getStop();

    public abstract void setStop(int stop);

    public abstract String getAttributeName();

    public abstract void setAttributeName(String attributeName);

    public abstract String getAttributeTypeName();

    public abstract void setAttributeTypeName(String attributeTypeName);

}
