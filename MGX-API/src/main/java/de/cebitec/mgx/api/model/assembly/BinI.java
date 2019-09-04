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
public abstract class BinI extends Identifiable<BinI> {

    public static final DataFlavor DATA_FLAVOR = new DataFlavor(BinI.class, "BinI");

    public BinI(MGXMasterI m) {
        super(m, DATA_FLAVOR);
    }

    public abstract long getAssemblyId();

    public abstract String getName();

    public abstract float getCompleteness();

    public abstract float getContamination();
    
    public abstract long getTotalSize();
    
    public abstract int getNumContigs();

    public abstract long getN50();

    public abstract String getTaxonomy();
    
    public abstract int getPredictedCDS();
}
