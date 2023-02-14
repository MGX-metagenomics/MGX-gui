package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.HabitatI;
import de.cebitec.mgx.api.model.SampleI;
import de.cebitec.mgx.testutils.TestMaster;
import java.util.Iterator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import org.junit.jupiter.api.Test;

/**
 *
 * @author sj
 */
public class SampleAccessTest {

    public SampleAccessTest() {
    }

    @Test
    public void testFetch() throws MGXException {
        System.out.println("fetch");
        MGXMasterI master = TestMaster.getRO();
        SampleI s = master.Sample().fetch(1);
        assertNotNull(s);
        assertNotNull(s.getMaster());
    }

    @Test
    public void testEquals() throws MGXException {
        System.out.println("testEquals");
        MGXMasterI master = TestMaster.getRO();
        SampleI s1 = master.Sample().fetch(1);
        SampleI s2 = master.Sample().fetch(1);
        assertNotNull(s1);
        assertNotNull(s2);
        assertNotSame(s1, s2);
        assertEquals(s1, s2);
    }

    @Test
    public void testByHabitat() throws MGXException {
        System.out.println("ByHabitat");
        MGXMasterI master = TestMaster.getRO();
        HabitatI h = master.Habitat().fetch(2);
        assertNotNull(h);
        Iterator<SampleI> iter = master.Sample().ByHabitat(h);
        int cnt = 0;
        while (iter.hasNext()) {
            SampleI s = iter.next();
            assertNotNull(s);
            assertNotNull(s.getMaster());
            cnt++;
        }
        assertEquals(1, cnt);
    }

}
