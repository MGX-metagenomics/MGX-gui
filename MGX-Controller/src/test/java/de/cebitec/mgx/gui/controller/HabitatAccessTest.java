/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.HabitatI;
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
public class HabitatAccessTest {

    public HabitatAccessTest() {
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
    public void testFetch() throws Exception {
        System.out.println("fetch");
        MGXMasterI master = TestMaster.getRO();
        HabitatI h = master.Habitat().fetch(1);
        assertNotNull(h);
    }

    @Test
    public void testFetchall() throws Exception {
        System.out.println("fetchall");
        MGXMasterI master = TestMaster.getRO();
        Iterator<HabitatI> iter = master.Habitat().fetchall();
        int cnt = 0;
        while (iter.hasNext()) {
            HabitatI h = iter.next();
            assertNotNull(h);
            cnt++;
        }
        assertEquals(2, cnt);
    }

    @Test
    public void testEquals() throws Exception {
        System.out.println("testEquals");
        MGXMasterI master = TestMaster.getRO();
        HabitatI h1 = master.Habitat().fetch(1);
        HabitatI h2 = master.Habitat().fetch(1);
        assertNotNull(h1);
        assertNotNull(h2);
        assertNotSame(h1, h2);
        assertEquals(h1, h2);
    }

}
