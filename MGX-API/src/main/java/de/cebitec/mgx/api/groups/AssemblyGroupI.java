/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.groups;

import de.cebitec.mgx.api.model.assembly.AssembledSeqRunI;
import java.awt.datatransfer.DataFlavor;

/**
 *
 * @author sj
 */
public interface AssemblyGroupI extends GroupI<AssembledSeqRunI> {

    public static final DataFlavor ASMGROUP_DATA_FLAVOR = new DataFlavor(AssemblyGroupI.class, "AssemblyGroupI");
    //private String managedState = OBJECT_MANAGED;
    //
    public static final String ASMGROUP_ACTIVATED = "asmgroup_activated";
    public static final String ASMGROUP_ATTRTYPE_CHANGED = "asmgAttrTypeChange";
    public static final String ASMGROUP_CHANGED = "asmgroup_changed";
    public static final String ASMGROUP_DEACTIVATED = "asmgroup_deactivated";
    public static final String ASMGROUP_HAS_DIST = "asmgroup_HasDist";
    public static final String ASMGROUP_RENAMED = "asmgroup_renamed";

}
