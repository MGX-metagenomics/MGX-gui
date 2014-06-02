package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.JobI;
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
    public void testFetch() {
        System.out.println("fetch");
        MGXMaster m = TestMaster.getRO();
        SeqRunI sr1 = m.SeqRun().fetch(1);
        assertNotNull(sr1);
        assertNotNull(sr1.getMaster());
    }

    @Test
    public void testgetJobsAndAttributeTypes() {
        System.out.println("getJobsAndAttributeTypes");
        MGXMaster m = TestMaster.getRO();
        SeqRunI sr1 = m.SeqRun().fetch(1);
        Map<JobI, Set<AttributeTypeI>> data = m.SeqRun().getJobsAndAttributeTypes(sr1);
        assertNotNull(data);
        assertEquals(10, data.size());
        for (JobI j : data.keySet()) {
            assertNotNull(j.getSeqrun());
        }
    }

    @Test
    public void testEquality() {
        System.out.println("equals");
        MGXMaster m = TestMaster.getRO();
        SeqRunI sr1 = m.SeqRun().fetch(1);
        SeqRunI sr2 = m.SeqRun().fetch(1);
        assertNotNull(sr1);
        assertNotNull(sr2);
        assertEquals(sr1, sr2);
    }

}
