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
public abstract class SequenceI extends Identifiable<SequenceI> {
    //
    public static final DataFlavor DATA_FLAVOR = new DataFlavor(SequenceI.class, "SequenceI");

    public SequenceI(MGXMasterI m) {
        super(m, DATA_FLAVOR);
    }

    public abstract String getName();

    public abstract void setName(String name);

    public abstract String getSequence();

    public abstract void setSequence(String sequence);

    public abstract int getLength() ;

    public abstract void setLength(int length);
    
}
