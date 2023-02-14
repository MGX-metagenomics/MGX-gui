/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.exception.MGXTimeoutException;
import de.cebitec.mgx.api.model.MappedSequenceI;
import de.cebitec.mgx.api.model.MappingI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.testutils.TestMaster;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

/**
 *
 * @author sj
 */
public class MappingAccessTest {

    @Test
    public void testBySeqRun() throws Exception {
        System.out.println("BySeqRun");
        MGXMasterI master = TestMaster.getRO();
        SeqRunI run = master.SeqRun().fetch(49);
        assertNotNull(run);
        Iterator<MappingI> it = master.Mapping().BySeqRun(run);
        assertNotNull(it);
        Set<MappingI> data = new HashSet<>();
        while (it.hasNext()) {
            data.add(it.next());
        }
        assertEquals(1, data.size());
    }

    @Test
    public void testCloseInvalidSession() {
        System.out.println("testCloseInvalidSession");
        MGXMasterI master = TestMaster.getRO();
        try {
            master.Mapping().closeMapping(UUID.randomUUID());
        } catch (MGXTimeoutException mte) {
            // ok
            return;
        } catch (MGXException ex) {
            fail(ex.getMessage());
        }
        fail("Closing a non-existing session should indicate a possible timeout.");
    }

    @Test
    public void testMappedSeqs() throws Exception {
        System.out.println("testMappedSeqs");
        MGXMasterI master = TestMaster.getRO();
        UUID uuid = master.Mapping().openMapping(1);
        assertNotNull(uuid);
        int numMappedReads = 0;
        Iterator<MappedSequenceI> iter = master.Mapping().byReferenceInterval(uuid, 566470, 566480);
        assertNotNull(iter);

        while (iter.hasNext()) {
            MappedSequenceI ms = iter.next();
            //System.err.println(ms.getSeqId());
            numMappedReads++;

        }
        master.Mapping().closeMapping(uuid);
        assertEquals(3, numMappedReads);
    }

    @Test
    public void testMappedSeqs2() throws Exception {
        System.out.println("testMappedSeqs2");
        MGXMasterI master = TestMaster.getRO();
        UUID uuid = master.Mapping().openMapping(1);
        int numMappedReads = 0;
        assertNotNull(uuid);
        Iterator<MappedSequenceI> iter = master.Mapping().byReferenceInterval(uuid, 466020, 566480);
        assertNotNull(iter);

        SortedSet<MappedSequenceI> set = new TreeSet<>();

        while (iter.hasNext()) {
            MappedSequenceI ms = iter.next();
            assertNotNull(ms);
            //System.err.println("got "+ms);
            assertFalse(set.contains(ms));
            System.err.println("adding " + ms);
            set.add(ms);
            assertTrue(set.contains(ms));
            numMappedReads++;
        }

        master.Mapping().closeMapping(uuid);

        assertEquals(17, numMappedReads);
        assertEquals(17, set.size());

        for (long l : new long[]{2148727, 2150896, 2113340}) {
            boolean present = false;
            for (MappedSequenceI ms : set) {
                if (ms.getSeqId() == l) {
                    present = true;
                }
            }
            assertTrue(present, "expected seqid " + l + " not in result");
        }
    }
}
