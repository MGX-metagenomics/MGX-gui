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
import de.cebitec.mgx.api.model.JobState;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.gui.util.TestMaster;
import java.util.Map;
import java.util.Set;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sjaenick
 */
public class AttributeTypeAccessTest {

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testFetch() throws MGXException {
        System.out.println("fetch");
        MGXMasterI master = TestMaster.getRO();
        AttributeTypeI aType = master.AttributeType().fetch(6);
        assertNotNull(aType);
        assertEquals("Bergey_class", aType.getName());
    }

    @Test
    public void testByRun() throws MGXException {
        System.out.println("testByRun");
        MGXMasterI master = TestMaster.getRO();
        SeqRunI run = master.SeqRun().fetch(1);
        assertEquals("dataset1", run.getName());
        Map<JobI, Set<AttributeTypeI>> map = master.SeqRun().getJobsAndAttributeTypes(run);
        assertNotNull(map);
        assertEquals(10, map.size());

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
        assertEquals(2, numCorrect);

    }
}
