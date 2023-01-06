/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.HabitatI;
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
public class HabitatAccessTest {

    public HabitatAccessTest() {
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
