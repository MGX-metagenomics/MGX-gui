/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.attributevisualization.ui;

import de.cebitec.mgx.api.groups.VGroupManagerI;
import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.gui.common.VGroupManager;
import de.cebitec.mgx.gui.nodes.VgMgrNode;
import de.cebitec.mgx.gui.nodes.VizGroupNode;
import java.awt.GraphicsEnvironment;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Assume;
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
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());
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
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());
        VGroupManagerI vgmgr = VGroupManager.getInstance();
        for (VisualizationGroupI vGrp : vgmgr.getActiveVisualizationGroups()) {
            //vgmgr.removeVisualizationGroup(vGrp);
            vGrp.close();
        }
        VisualizationGroupTopComponent tc = new VisualizationGroupTopComponent();
        ExplorerManager em = tc.getExplorerManager();
        Node rootContext = em.getRootContext();
        Node[] nodes = rootContext.getChildren().getNodes();
        assertNotNull(nodes);
        assertEquals("A single visualization group should have been created automatically", 1, nodes.length);
        Node grpNode = nodes[0];
        assertTrue(grpNode.getParentNode() == rootContext);
        assertTrue(grpNode instanceof VizGroupNode);
    }
}
