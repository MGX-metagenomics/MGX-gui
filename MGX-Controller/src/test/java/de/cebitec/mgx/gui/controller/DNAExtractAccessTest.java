/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.DNAExtractI;
import de.cebitec.mgx.testutils.TestMaster;
import java.util.Iterator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

/**
 *
 * @author sj
 */
public class DNAExtractAccessTest {

    @Test
    public void testFetchall() throws Exception {
        System.out.println("fetchall");
        MGXMasterI master = TestMaster.getRO();
        Iterator<DNAExtractI> iter = master.DNAExtract().fetchall();
        assertNotNull(iter);
        int cnt = 0;
        while (iter.hasNext()) {
            DNAExtractI ex = iter.next();
            assertNotNull(ex);
            cnt++;
        }
        assertEquals(1, cnt);
    }

    @Test
    public void testFetch() {
        System.out.println("fetchValid");
        MGXMasterI master = TestMaster.getRO();
        try {
            DNAExtractI ex = master.DNAExtract().fetch(48);
            assertNotNull(ex);
        } catch (MGXException ex1) {
            fail(ex1.getMessage());
        }
    }

    @Test
    public void testEquals() throws MGXException {
        System.out.println("testEquals");
        MGXMasterI master = TestMaster.getRO();
        DNAExtractI d1 = master.DNAExtract().fetch(48);
        DNAExtractI d2 = master.DNAExtract().fetch(48);
        assertNotNull(d1);
        assertNotNull(d2);
        assertNotSame(d1, d2);
        assertEquals(d1, d2);
    }

    @Test
    public void testFetchInvalid() {
        System.out.println("fetchInvalid");
        MGXMasterI master = TestMaster.getRO();
        try {
            DNAExtractI ex = master.DNAExtract().fetch(42);
            assertNull(ex);
        } catch (MGXException ex1) {
            if (ex1.getMessage().contains("No object of type DNAExtract")) {
                return; // ok
            }
            fail(ex1.getMessage());
        }
    }
}
