/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.groups;

import de.cebitec.mgx.api.model.SeqRunI;
import java.awt.datatransfer.DataFlavor;

/**
 *
 * @author sjaenick
 */
public interface ReplicateI extends GroupI<SeqRunI> {

    public static final DataFlavor REPLICATE_DATA_FLAVOR = new DataFlavor(ReplicateI.class, "ReplicateI");

    public ReplicateGroupI getReplicateGroup();

}
