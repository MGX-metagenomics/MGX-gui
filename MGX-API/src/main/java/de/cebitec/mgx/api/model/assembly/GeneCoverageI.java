/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.model.assembly;

import de.cebitec.mgx.api.model.Identifiable;

/**
 *
 * @author sj
 */
public abstract class GeneCoverageI implements Comparable<GeneCoverageI> {

    //public static final DataFlavor DATA_FLAVOR = new DataFlavor(GeneCoverageI.class, "GeneCoverageI");
    protected long id = Identifiable.INVALID_IDENTIFIER;

    public GeneCoverageI() {
    }

    public final void setId(long id) {
        this.id = id;
    }

    public final long getId() {
        return id;
    }

    public abstract long getGeneId();

    public abstract int getCoverage();

    public abstract long getRunId();

}
