/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.gui.datamodel.MappedSequence;
import de.cebitec.mgx.gui.util.TestMaster;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;
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
public class MappingAccessTest {

    public MappingAccessTest() {
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
    public void testMappedSeqs() throws Exception {
        System.out.println("testMappedSeqs");
        MGXMaster master = TestMaster.getRO();
        UUID uuid = master.Mapping().openMapping(30);
        assertNotNull(uuid);
        int numMappedReads = 0;
        Iterator<MappedSequence> iter = master.Mapping().byReferenceInterval(uuid, 566470, 566480);
        assertNotNull(iter);

        while (iter.hasNext()) {
            MappedSequence ms = iter.next();
            //System.err.println(ms.getSeqId());
            numMappedReads++;

        }
        master.Mapping().closeMapping(uuid);
        assertEquals(3, numMappedReads);
    }

    @Test
    public void testMappedSeqs2() throws Exception {
        System.out.println("testMappedSeqs2");
        MGXMaster master = TestMaster.getRO();
        UUID uuid = master.Mapping().openMapping(30);
        int numMappedReads = 0;
        assertNotNull(uuid);
        Iterator<MappedSequence> iter = master.Mapping().byReferenceInterval(uuid, 566470, 566480);
        assertNotNull(iter);

        SortedSet<MappedSequence> set = new TreeSet<>();

        while (iter.hasNext()) {
            MappedSequence ms = iter.next();
            assertNotNull(ms);
            //System.err.println("got "+ms);
            assertFalse(set.contains(ms));
            //System.err.println("adding "+ms);
            set.add(ms);
            assertTrue(set.contains(ms));
            numMappedReads++;
        }

        master.Mapping().closeMapping(uuid);

        assertEquals(3, numMappedReads);
        assertEquals(3, set.size());

        for (long l : new long[]{53748, 3436, 26467}) {
            boolean present = false;
            for (MappedSequence ms : set) {
                if (ms.getSeqId() == l) {
                    present = true;
                }
            }
            assertTrue("expected seqid " + l + " not in result", present);
        }
    }
}
