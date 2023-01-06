/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.api.groups.VGroupManagerI;
import de.cebitec.mgx.gui.nodes.VgMgrNode;
import de.cebitec.mgx.gui.nodes.VizGroupNode;
import de.cebitec.mgx.gui.visgroups.VGroupManager;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.openide.nodes.Node;

/**
 *
 * @author sjaenick
 */
public class VisualizationGroupSetTest {
    
    @Test
    public void testParentNode() {
        System.out.println("testParentNode");
        VGroupManagerI vgmgr = VGroupManager.getTestInstance();
        VgMgrNode mgrNode = new VgMgrNode(vgmgr);
        //
        // add group
        vgmgr.createVisualizationGroup();
        //
        Node[] children = mgrNode.getChildren().getNodes();
        assertNotNull(children);
        assertEquals(1, children.length);
        Node grpNode = children[0];
        assertTrue(grpNode instanceof VizGroupNode);
        assertTrue(mgrNode.getChildren().getNodeAt(0) == grpNode);
        assertEquals(mgrNode, grpNode.getParentNode());
    }
    
}
