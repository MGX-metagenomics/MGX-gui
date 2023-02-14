/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.common.JobState;
import de.cebitec.mgx.testutils.TestMaster;
import java.util.Map;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

/**
 *
 * @author sjaenick
 */
public class AttributeTypeAccessTest {

    @Test
    public void testFetch() throws MGXException {
        System.out.println("fetch");
        MGXMasterI master = TestMaster.getRO();
        AttributeTypeI aType = master.AttributeType().fetch(6);
        assertNotNull(aType);
        assertEquals("NCBI_FAMILY", aType.getName());
    }

    @Test
    public void testByRun() throws MGXException {
        System.out.println("testByRun");
        MGXMasterI master = TestMaster.getRO();
        SeqRunI run = master.SeqRun().fetch(49);
        assertEquals("dataset1", run.getName());
        Map<JobI, Set<AttributeTypeI>> map = master.SeqRun().getJobsAndAttributeTypes(run);
        assertNotNull(map);
        assertEquals(3, map.size());

        int numCorrect = 0;
        for (JobI j : map.keySet()) {
            if (j.getStatus() != JobState.FINISHED) {
            } else {
                for (AttributeTypeI at : map.get(j)) {
                    if ("NCBI_SUPERKINGDOM".equals(at.getName())) {
                        numCorrect++;
                    }
                }
            }
        }
        assertEquals(1, numCorrect);

    }
}
