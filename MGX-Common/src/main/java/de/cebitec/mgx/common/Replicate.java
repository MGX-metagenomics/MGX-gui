/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.common;

import de.cebitec.mgx.api.groups.ReplicateGroupI;
import de.cebitec.mgx.api.groups.ReplicateI;
import de.cebitec.mgx.api.groups.VGroupManagerI;
import de.cebitec.mgx.api.groups.VisualizationGroupI;
import java.awt.Color;
import java.awt.datatransfer.DataFlavor;

/**
 *
 * @author sjaenick
 */
public class Replicate extends VisualizationGroup implements ReplicateI {

    private final ReplicateGroupI group;
    private final int replicateNum;

    Replicate(ReplicateGroupI group, VGroupManagerI vgmgr, int id, String replName, Color color) {
        super(vgmgr, id, replName, color);
        this.group = group;
        this.replicateNum = group.getNextReplicateNum();
    }

    @Override
    public final void setName(String name) {
        //super.setName(name);
    }

    @Override
    public String getDisplayName() {
        return group.getName() + " Replicate " + replicateNum;
    }

    @Override
    public boolean isActive() {
        return group.isActive() && super.isActive();
    }

    @Override
    public void setActive(boolean is_active) {
        group.setActive(is_active);
        super.setActive(is_active);
    }

    @Override
    public final ReplicateGroupI getReplicateGroup() {
        return group;
    }
    
       @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{VisualizationGroupI.VISGROUP_DATA_FLAVOR, ReplicateI.REPLICATE_DATA_FLAVOR};
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor != null && (flavor.equals(VisualizationGroupI.VISGROUP_DATA_FLAVOR) || flavor.equals(ReplicateI.REPLICATE_DATA_FLAVOR));
    }

    @Override
    public int compareTo(VisualizationGroupI o) {
        return super.compareTo(o);
    }

}
