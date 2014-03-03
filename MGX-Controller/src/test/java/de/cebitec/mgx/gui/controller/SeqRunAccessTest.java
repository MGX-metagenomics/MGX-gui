package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.gui.datamodel.AttributeType;
import de.cebitec.mgx.gui.datamodel.Job;
import de.cebitec.mgx.gui.datamodel.SeqRun;
import de.cebitec.mgx.gui.util.TestMaster;
import java.util.List;
import java.util.Map;
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
public class SeqRunAccessTest {

    public SeqRunAccessTest() {
    }

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
    public void testgetJobsAndAttributeTypes() {
        System.out.println("getJobsAndAttributeTypes");
        MGXMaster m = TestMaster.getRO();
        SeqRun sr1 = m.SeqRun().fetch(1);
        Map<Job, List<AttributeType>> data = m.SeqRun().getJobsAndAttributeTypes(sr1);
        assertNotNull(data);
        assertEquals(9, data.size());
        for (Job j : data.keySet()) {
            assertNotNull(j.getSeqrun());
        }
    }

    @Test
    public void testEquality() {
        System.out.println("equals");
        MGXMaster m = TestMaster.getRO();
        SeqRun sr1 = m.SeqRun().fetch(1);
        SeqRun sr2 = m.SeqRun().fetch(1);
        assertNotNull(sr1);
        assertNotNull(sr2);
        assertEquals(sr1, sr2);
    }

}
