/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.api.groups.VGroupManagerI;
import de.cebitec.mgx.gui.nodefactory.VisualizationGroupSet;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sjaenick
 */
public final class VgMgrNode extends AbstractNode {

    public VgMgrNode(VGroupManagerI vgmgr) {
        super(Children.create(new VisualizationGroupSet(vgmgr), false), Lookups.singleton(vgmgr));
        super.setName("Visualization group manager");
        super.setDisplayName("Visualization group manager");
    }

}
