/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.cache;

import de.cebitec.gpms.core.GPMSException;
import de.cebitec.gpms.core.MembershipI;
import de.cebitec.gpms.rest.GPMSClientI;
import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.MGXReferenceI;
import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.restgpms.GPMSClient;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author sj
 */
public class IntervalTest {

    public IntervalTest() {
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
    public void testEquals() throws MGXException {
        System.out.println("equals");
        MGXMasterI master = get();
        MGXReferenceI ref = master.Reference().fetch(4);
        Cache<String> cache = CacheFactory.createSequenceCache(master, ref);
        assertNotNull(cache);

        Interval i1 = new Interval(cache.getSegmentSize(), 0);
        Interval i2 = new Interval(cache.getSegmentSize(), 0);
        Interval i3 = new Interval(cache.getSegmentSize(), 1);
        assertEquals(i1, i2);
        assertNotEquals(i1, i3);
    }

    @Test
    public void testHashCode() throws MGXException {
        System.out.println("hashCode");
        MGXMasterI master = get();
        MGXReferenceI ref = master.Reference().fetch(4);
        Cache<String> cache = CacheFactory.createSequenceCache(master, ref);
        assertNotNull(cache);

        Interval i1 = new Interval(cache.getSegmentSize(), 0);
        Interval i2 = new Interval(cache.getSegmentSize(), 0);
        Interval i3 = new Interval(cache.getSegmentSize(), 1);
        assertEquals(i1.hashCode(), i2.hashCode());
        assertNotEquals(i1.hashCode(), i3.hashCode());
    }

    public static MGXMasterI get() {
        MGXMasterI master = null;

        String serverURI = "https://mgx.cebitec.uni-bielefeld.de/MGX-maven-web/webresources/";

        String config = System.getProperty("user.home") + "/.m2/mgx.junit";
        File f = new File(config);
        if (f.exists() && f.canRead()) {
            Properties p = new Properties();
            try {
                p.load(new FileInputStream(f));
                serverURI = p.getProperty("testserver");
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
        GPMSClientI gpms = new GPMSClient("MyServer", serverURI);
        try {
            gpms.login("mgx_unittestRO", "gut-isM5iNt");
        } catch (GPMSException ex) {
            fail(ex.getMessage());
        }
        Iterator<MembershipI> mIter = null;
        try {
            mIter = gpms.getMemberships();
        } catch (GPMSException ex) {
            fail(ex.getMessage());
        }
        while (mIter.hasNext()) {
            MembershipI m = mIter.next();
            if ("MGX".equals(m.getProject().getProjectClass().getName()) && ("MGX_Unittest".equals(m.getProject().getName()))) {
                master = new MGXMaster(gpms.createMaster(m));
                break;
            }
        }

        assert master != null;
        return master;
    }
}
