/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.model.assembly;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.Identifiable;
import java.awt.datatransfer.DataFlavor;

/**
 *
 * @author sj
 */
public abstract class GeneCoverageI extends Identifiable<GeneCoverageI> {

    public static final DataFlavor DATA_FLAVOR = new DataFlavor(GeneCoverageI.class, "GeneCoverageI");

    public GeneCoverageI(MGXMasterI m) {
        super(m, DATA_FLAVOR);
    }

    public abstract long getGeneId();

    public abstract int getCoverage();
    
    public abstract long getRunId();


}
