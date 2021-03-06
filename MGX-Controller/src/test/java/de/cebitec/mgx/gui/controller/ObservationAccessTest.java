/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.misc.BulkObservation;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.SequenceI;
import de.cebitec.mgx.gui.util.TestMaster;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.openide.util.Exceptions;

/**
 *
 * @author sj
 */
public class ObservationAccessTest {

    public ObservationAccessTest() {
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
    public void testCreateBulkRO() {
        System.out.println("testCreateBulkRO");
        MGXMasterI master = TestMaster.getRO();
        List<BulkObservation> obsList = new ArrayList<>();
        SeqRunI run = null;
        AttributeI attr = null;
        try {
            run = master.SeqRun().fetch(1);
            assertNotNull(run);
            attr = master.Attribute().fetch(1);
            assertNotNull(attr);
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
        }
        obsList.add(new BulkObservation(run.getId(), "xx", attr.getId(), 0, 5));
        try {
            master.Observation().createBulk(obsList);
        } catch (MGXException ex) {
            if (ex.getMessage().contains("Resource access denied")) {
                return; // ok
            }
            fail(ex.getMessage());
        }
    }

    @Test
    public void testCreateBulkInvalidReadName() {
        System.out.println("testCreateBulkInvalidReadName");
        MGXMasterI master = TestMaster.getRW();
        List<BulkObservation> obsList = new ArrayList<>();
        SeqRunI run = null;
        AttributeI attr = null;
        try {
            run = master.SeqRun().fetch(1);
            assertNotNull(run);
            attr = master.Attribute().fetch(1);
            assertNotNull(attr);
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
        }
        obsList.add(new BulkObservation(run.getId(), "xx", attr.getId(), 0, 5));
        try {
            master.Observation().createBulk(obsList);
        } catch (MGXException ex) {
            if (ex.getMessage().contains("First failing read was xx with status 0")) {
                return; // ok
            }
            fail(ex.getMessage());
        }
    }

    @Test
    public void testCreateBulk() {
        System.out.println("testCreateBulk");
        MGXMasterI master = TestMaster.getRW();
        List<BulkObservation> obsList = new ArrayList<>();
        SeqRunI run = null;
        AttributeI attr = null;
        try {
            run = master.SeqRun().fetch(1);
            assertNotNull(run);
            attr = master.Attribute().fetch(1);
            assertNotNull(attr);
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
        }
        obsList.add(new BulkObservation(run.getId(), "FI5LW4G01DZDXZ", attr.getId(), 0, 5));
        try {
            master.Observation().createBulk(obsList);
        } catch (MGXException ex) {
            fail(ex.getMessage());
        } finally {
            SequenceI seq;
            try {
                seq = master.Sequence().fetch(1);
                master.Observation().delete(seq, attr, 0, 5);
            } catch (MGXException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
}
