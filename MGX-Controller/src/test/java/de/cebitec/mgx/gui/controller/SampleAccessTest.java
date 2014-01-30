package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.gui.datamodel.Habitat;
import de.cebitec.mgx.gui.datamodel.Sample;
import de.cebitec.mgx.gui.datamodel.misc.Task;
import de.cebitec.mgx.gui.util.TestMaster;
import java.util.Iterator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sj
 */
public class SampleAccessTest {

    public SampleAccessTest() {
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
        MGXMaster master = TestMaster.getRO();
        Sample s = master.Sample().fetch(1);
        assertNotNull(s);
        assertNotNull(s.getMaster());
    }

    @Test
    public void testByHabitat() {
        System.out.println("ByHabitat");
        MGXMaster master = TestMaster.getRO();
        Habitat h = master.Habitat().fetch(1);
        assertNotNull(h);
        Iterator<Sample> iter = master.Sample().ByHabitat(h.getId());
        int cnt = 0;
        while (iter.hasNext()) {
            Sample s = iter.next();
            assertNotNull(s);
            assertNotNull(s.getMaster());
            cnt++;
        }
        assertEquals(1, cnt);
    }

}
