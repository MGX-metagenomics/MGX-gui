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
public abstract class ContigI extends Identifiable<ContigI> {

    public static final DataFlavor DATA_FLAVOR = new DataFlavor(ContigI.class, "ContigI");

    public ContigI(MGXMasterI m) {
        super(m, DATA_FLAVOR);
    }
    
    public abstract String getName();

    public abstract long getBinId();

}
