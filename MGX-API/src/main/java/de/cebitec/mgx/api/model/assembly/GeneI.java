/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.model.assembly;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.Identifiable;
import de.cebitec.mgx.api.model.LocationI;
import java.awt.datatransfer.DataFlavor;

/**
 *
 * @author sj
 */
public abstract class GeneI extends Identifiable<GeneI> implements LocationI {

    public static final DataFlavor DATA_FLAVOR = new DataFlavor(GeneI.class, "GeneI");

    public GeneI(MGXMasterI m) {
        super(m, DATA_FLAVOR);
    }

    public abstract long getContigId();

    public abstract int getCoverage();

    public final int getAALength() {
        int nuclLen;
        if (getStart() < getStop()) {
            nuclLen = getStop() - getStart() + 1;
        } else {
            nuclLen = getStart() - getStop() + 1;

        }
        return nuclLen / 3;
    }

}
