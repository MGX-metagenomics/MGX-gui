/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.common;

import de.cebitec.mgx.api.groups.ReplicateGroupI;
import de.cebitec.mgx.api.groups.ReplicateI;
import de.cebitec.mgx.api.groups.VGroupManagerI;
import java.awt.Color;

/**
 *
 * @author sjaenick
 */
public class Replicate extends VisualizationGroup implements ReplicateI {

    private final ReplicateGroupI group;

    Replicate(ReplicateGroupI group, VGroupManagerI vgmgr, int id, String groupName, Color color) {
        super(ReplicateI.REPLICATE_DATA_FLAVOR, vgmgr, id, groupName, color);
        this.group = group;
    }

    @Override
    public final void setName(String name) {
        //super.setName(name);
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

}
