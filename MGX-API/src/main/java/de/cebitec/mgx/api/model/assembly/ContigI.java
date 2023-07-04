/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.model.assembly;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.Identifiable;

/**
 *
 * @author sj
 */
public abstract class ContigI implements Comparable<ContigI> { //extends Identifiable<ContigI> {

    //public static final DataFlavor DATA_FLAVOR = new DataFlavor(ContigI.class, "ContigI");
    protected long id = Identifiable.INVALID_IDENTIFIER;
    private final MGXMasterI master;

    public ContigI(MGXMasterI m) {
        //super(m, DATA_FLAVOR);
        this.master = m;
    }

    public final MGXMasterI getMaster() {
        return master;
    }

    public final void setId(long id) {
        this.id = id;
    }

    public final long getId() {
        return id;
    }

    public abstract String getName();

    public abstract long getBinId();

    public abstract float getGC();

    public abstract int getLength();

    public abstract int getCoverage();

    public abstract int getPredictedSubregions();

    @Override
    public String toString() {
        return "ContigI{" + getName() + '}';
    }

}
