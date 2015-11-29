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
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;

/**
 *
 * @author sjaenick
 */
public class VGroupManagerTest {

    /**
     * Test of getInstance method, of class VGroupManager.
     */
    @Test
    public void testGetInstance() {
        System.out.println("getInstance");
        VGroupManagerI result = VGroupManager.getInstance();
        assertNotNull(result);
    }

    @Test
    public void testCreateVGroup() {
        System.out.println("createVGroup");
        VGroupManagerI mgr = VGroupManager.getInstance();
        assertTrue(mgr.getReplicateGroups().isEmpty());
        assertTrue(mgr.getAllVizGroups().isEmpty());
        
        // create vGrp
        VisualizationGroupI vGrp = mgr.createVizGroup();
        assertNotNull(vGrp);
        assertEquals(1, mgr.getAllVizGroups().size());
        assertTrue(mgr.getReplicateGroups().isEmpty());
        
        // removal of vGrp
        mgr.removeVizGroup(vGrp);
        assertTrue(mgr.getReplicateGroups().isEmpty());
        assertTrue(mgr.getAllVizGroups().isEmpty());
    }

    @Test
    public void testReplicateGroups() {
        System.out.println("testReplicateGroups");
        VGroupManagerI mgr = VGroupManager.getInstance();
        assertNotNull(mgr.getReplicateGroups());
        assertEquals(0, mgr.getReplicateGroups().size());
        
        // create empty replicategroup
        ReplicateGroupI rGroup = mgr.createReplicateGroup();
        assertNotNull(rGroup);
        assertTrue(rGroup.isActive());
        assertTrue(rGroup.getReplicates().isEmpty());
        assertEquals(1, mgr.getReplicateGroups().size());
        
        // add a replicate
        assertTrue(rGroup.getReplicates().isEmpty());
        assertTrue(mgr.getActiveVizGroups().isEmpty()); 
        ReplicateI replicate = mgr.createReplicate(rGroup);
        assertNotNull(replicate);
        assertEquals(replicate.getReplicateGroup(), rGroup);
        assertEquals(1, rGroup.getReplicates().size());
        assertTrue(rGroup.getReplicates().contains(replicate));
        assertFalse("Empty replicate should not be active", replicate.isActive());
        assertTrue(mgr.getAllVizGroups().contains(replicate)); // replicate is a Vizgroup, as well
        
        // remove replicategroup it again
        mgr.removeReplicateGroup(rGroup);
        assertTrue(mgr.getReplicateGroups().isEmpty());
        
    }
 
}
