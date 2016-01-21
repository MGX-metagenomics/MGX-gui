/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.api.groups.VGroupManagerI;
import de.cebitec.mgx.common.VGroupManager;
import de.cebitec.mgx.gui.nodes.VgMgrNode;
import de.cebitec.mgx.gui.nodes.VizGroupNode;
import static org.junit.Assert.*;
import org.junit.Test;
import org.openide.nodes.Node;

/**
 *
 * @author sjaenick
 */
public class VisualizationGroupSetTest {
    
    @Test
    public void testParentNode() {
        System.out.println("testParentNode");
        VGroupManagerI vgmgr = VGroupManager.getInstance();
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