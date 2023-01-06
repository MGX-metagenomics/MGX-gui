/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.attributevisualization.ui;

import de.cebitec.mgx.api.groups.GroupI;
import de.cebitec.mgx.api.groups.VGroupManagerI;
import de.cebitec.mgx.gui.nodes.VgMgrNode;
import de.cebitec.mgx.gui.nodes.VizGroupNode;
import de.cebitec.mgx.gui.visgroups.VGroupManager;
import java.awt.GraphicsEnvironment;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import org.junit.jupiter.api.Test;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.Node;

/**
 *
 * @author sjaenick
 */
public class VisualizationGroupTopComponentTest {
    
   
    @Test
    public void testExplorerManagerRootContext() {
        System.out.println("testExplorerManagerRootContext");
        assumeFalse(GraphicsEnvironment.isHeadless());
        VisualizationGroupTopComponent tc = new VisualizationGroupTopComponent();
        ExplorerManager em = tc.getExplorerManager();
        assertNotNull(em);
        Node rootContext = em.getRootContext();
        assertNotNull(rootContext);
        assertTrue(rootContext instanceof VgMgrNode);
    }
       
    @Test
    public void testExplorerManagerContent() {
        System.out.println("testExplorerManagerContent");
        assumeFalse(GraphicsEnvironment.isHeadless());
        VGroupManagerI vgmgr = VGroupManager.getInstance();
        for (GroupI vGrp : vgmgr.getActiveGroups()) {
            //vgmgr.removeVisualizationGroup(vGrp);
            vGrp.close();
        }
        VisualizationGroupTopComponent tc = new VisualizationGroupTopComponent();
        ExplorerManager em = tc.getExplorerManager();
        Node rootContext = em.getRootContext();
        Node[] nodes = rootContext.getChildren().getNodes();
        assertNotNull(nodes);
        assertEquals(1, nodes.length, "A single visualization group should have been created automatically");
        Node grpNode = nodes[0];
        assertTrue(grpNode.getParentNode() == rootContext);
        assertTrue(grpNode instanceof VizGroupNode);
    }
}
