/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.visgroups;

import de.cebitec.mgx.api.groups.ReplicateGroupI;
import de.cebitec.mgx.api.groups.ReplicateI;
import de.cebitec.mgx.api.groups.VGroupManagerI;
import de.cebitec.mgx.api.groups.VisualizationGroupI;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

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
    public void testAddRemove() {
        System.out.println("testAddRemove");
        VGroupManagerI vgmgr = VGroupManager.getTestInstance();
        assertEquals(0, vgmgr.getAllGroups().size());
        VisualizationGroupI vg = vgmgr.createVisualizationGroup();
        assertNotNull(vg);
        assertEquals(1, vgmgr.getAllGroups().size());
        //vgmgr.removeVisualizationGroup(vg);
        vg.close();
        assertEquals(0, vgmgr.getAllGroups().size());
    }

    @Test
    public void testCreateVGroup() {
        System.out.println("createVGroup");
        VGroupManagerI mgr = VGroupManager.getTestInstance();
        assertTrue(mgr.getReplicateGroups().isEmpty());
        assertTrue(mgr.getAllGroups().isEmpty());

        // create vGrp
        VisualizationGroupI vGrp = mgr.createVisualizationGroup();
        assertNotNull(vGrp);
        assertEquals(1, mgr.getAllGroups().size());
        assertTrue(mgr.getReplicateGroups().isEmpty());

        // removal of vGrp
        //mgr.removeVisualizationGroup(vGrp);
        vGrp.close();
        assertTrue(mgr.getReplicateGroups().isEmpty());
        assertTrue(mgr.getAllGroups().isEmpty());
    }

    @Test
    public void testReplicateGroups() {
        System.out.println("testReplicateGroups");
        VGroupManagerI mgr = VGroupManager.getTestInstance();
        assertNotNull(mgr.getReplicateGroups());
        assertEquals(0, mgr.getReplicateGroups().size());

        // create empty replicategroup
        ReplicateGroupI rGroup = mgr.createReplicateGroup();
        assertNotNull(rGroup);
        assertTrue(rGroup.isActive());
        assertTrue(rGroup.getReplicates().isEmpty());
        assertEquals(1, mgr.getReplicateGroups().size());
        assertTrue(mgr.getReplicateGroups().contains(rGroup));

        // add a replicate
        assertTrue(rGroup.getReplicates().isEmpty());
        assertTrue(mgr.getActiveGroups().isEmpty());
        ReplicateI replicate = mgr.createReplicate(rGroup);
        assertNotNull(replicate);
        assertEquals(replicate.getReplicateGroup(), rGroup);
        assertEquals(1, rGroup.getReplicates().size());
        assertTrue(rGroup.getReplicates().contains(replicate));
//        assertFalse("Empty replicate should not be active", replicate.isActive());
        assertTrue(mgr.getAllGroups().contains(replicate)); // replicate is a Vizgroup, as well

        // remove replicategroup it again
        //mgr.removeReplicateGroup(rGroup);
        rGroup.close();
        assertTrue(mgr.getReplicateGroups().isEmpty());
    }

    @Test
    public void testReplicateGroupNaming() {
        System.out.println("testReplicateGroupNaming");
        VGroupManagerI mgr = VGroupManager.getTestInstance();
        assertNotNull(mgr.getReplicateGroups());
        assertEquals(0, mgr.getReplicateGroups().size());

        // create replicategroup
        ReplicateGroupI rGroup1 = mgr.createReplicateGroup();
        assertNotNull(rGroup1);
        ReplicateGroupI rGroup2 = mgr.createReplicateGroup();
        assertNotNull(rGroup2);
        assertNotEquals(rGroup1.getName(), rGroup2.getName());
        assertNotEquals(rGroup1.getColor(), rGroup2.getColor());

        rGroup1.close();
        rGroup2.close();

//        mgr.removeReplicateGroup(rGroup1);
//        mgr.removeReplicateGroup(rGroup2);
    }

    @Test
    public void testReplicateRemoval() {
        System.out.println("testReplicateRemoval");
        VGroupManagerI mgr = VGroupManager.getTestInstance();
        assertTrue(mgr.getReplicateGroups().isEmpty());
        ReplicateGroupI rGroup = mgr.createReplicateGroup();
        assertTrue(rGroup.isActive());
        ReplicateI r1 = mgr.createReplicate(rGroup);
        ReplicateI r2 = mgr.createReplicate(rGroup);
        assertEquals(2, rGroup.getReplicates().size());
        //
        assertTrue(mgr.getAllGroups().contains(r1));
        assertTrue(mgr.getAllGroups().contains(r2));
        //
        //mgr.removeReplicateGroup(rGroup);
        rGroup.close();
        assertEquals(0, rGroup.getReplicates().size(), "Replicate group must not contain any replicates after group has been removed");
        //
        //
        // removing a replicate group should also remove all replicates contained in it 
        assertFalse(mgr.getAllGroups().contains(r1));
        assertFalse(mgr.getAllGroups().contains(r2));
    }

    @Test
    public void testReplicateNaming() {
        System.out.println("testReplicateNaming");
        VGroupManagerI mgr = VGroupManager.getTestInstance();
        ReplicateGroupI rGroup1 = mgr.createReplicateGroup();
        ReplicateI r1 = mgr.createReplicate(rGroup1);
        ReplicateI r2 = mgr.createReplicate(rGroup1);
        assertEquals("Replicate 1", r1.getName());
        assertNotEquals(r1.getName(), r2.getName());
        //mgr.removeReplicateGroup(rGroup1);
        rGroup1.close();
    }

    @Test
    public void testReplicateGroupRenaming() {
        System.out.println("testReplicateGroupRenaming");
        VGroupManagerI mgr = VGroupManager.getTestInstance();
        ReplicateGroupI rGroup = mgr.createReplicateGroup();
        ReplicateI r1 = mgr.createReplicate(rGroup);
        assertEquals("Replicate Group 1", rGroup.getName());
        assertEquals("Replicate 1", r1.getName());
        assertEquals("Replicate Group 1 Replicate 1", r1.getDisplayName());

        //mgr.removeReplicateGroup(rGroup);
        rGroup.close();
    }
}
