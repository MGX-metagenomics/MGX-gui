package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.gui.datamodel.SeqRun;
import de.cebitec.mgx.gui.util.TestMaster;
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