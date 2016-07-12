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
import de.cebitec.mgx.gui.util.TestMaster;
import java.util.Arrays;
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
public class SequenceAccessTest {

    public SequenceAccessTest() {
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
    public void testFetch_SeqRunI_String() throws Exception {
        System.out.println("testFetch_SeqRunI_String");
        MGXMasterI master = TestMaster.getRO();
        SeqRunI seqrun = master.SeqRun().fetch(1);
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
    public void testFetchByIDsListPerformance() {
        System.out.println("testFetchByIDsListPerformance");
        MGXMasterI master = TestMaster.getRO();
        long[] ids = new long[59482];
        for (int i = 0; i < 59482; i++) {
            ids[i] = i + 1;
        }

        int from = 0;
        int size = 10_000;
        long[] chunk;

        while (from < ids.length) {
            chunk = Arrays.copyOfRange(ids, from, Math.min(from + size, ids.length));

            long start = System.currentTimeMillis();
            Iterator<SequenceI> iter = null;
            try {
                iter = master.Sequence().fetchByIds(chunk);
            } catch (MGXException ex) {
                fail(ex.getMessage());
            }
            assertNotNull(iter);
            System.err.println("  fetched interval " + chunk[0] + "-" + chunk[chunk.length - 1]+ " in "+ (System.currentTimeMillis()-start)+ " ms");
            int numRes = 0;
            while (iter.hasNext()) {
                iter.next();
                numRes++;
            }
            assertEquals(chunk.length, numRes);
            System.err.println("  processed interval " + chunk[0] + "-" + chunk[chunk.length - 1]+ " in "+ (System.currentTimeMillis()-start)+ " ms");

            from += size;
        }
    }
}
