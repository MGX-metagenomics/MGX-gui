/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.SequenceI;
import de.cebitec.mgx.testutils.TestMaster;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

/**
 *
 * @author sj
 */
public class SequenceAccessTest {

    public SequenceAccessTest() {
    }

    @Test
    public void testFetch_SeqRunI_String() throws Exception {
        System.out.println("testFetch_SeqRunI_String");
        MGXMasterI master = TestMaster.getRO();
        SeqRunI seqrun = master.SeqRun().fetch(49);
        assertNotNull(seqrun);
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            SequenceI seq = master.Sequence().fetch(seqrun, "FI5LW4G01AM15A");
            assertNotNull(seq);
        }
        start = System.currentTimeMillis() - start;
        System.err.println("duration in ms: " + start);
    }

    @Test
    public void fetchValidID() {
        System.out.println("fetchValidID");
        MGXMasterI master = TestMaster.getRO();
        SequenceI seq = null;
        try {
            seq = master.Sequence().fetch(2109905);
        } catch (MGXException ex) {
            fail(ex.getMessage());
        }
        assertNotNull(seq);
        assertEquals("FI5LW4G01DZDXZ", seq.getName());
    }

    @Test
    public void fetchInvalidID() {
        System.out.println("fetchInvalidID");
        MGXMasterI master = TestMaster.getRO();
        SequenceI seq = null;
        try {
            seq = master.Sequence().fetch(1);
        } catch (MGXException ex) {
            if (ex.getMessage().contains("No object of type Sequence for ID 1")) {
                // ok
            } else {
                fail(ex.getMessage());
            }
        }
        assertNull(seq);
    }
}
