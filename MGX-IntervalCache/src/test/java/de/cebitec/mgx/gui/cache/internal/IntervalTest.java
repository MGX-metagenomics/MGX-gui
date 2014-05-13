/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.cache.internal;

import de.cebitec.gpms.core.MembershipI;
import de.cebitec.gpms.rest.GPMSClientI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.gui.cache.Cache;
import de.cebitec.mgx.gui.cache.CacheFactory;
import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.Reference;
import de.cebitec.mgx.restgpms.GPMS;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
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
    public void testEquals() {
        System.out.println("equals");
        MGXMaster master = get();
        Reference ref = master.Reference().fetch(4);
        Cache<String> cache = CacheFactory.createSequenceCache(master, ref);
        assertNotNull(cache);

        Interval<String> i1 = new Interval<>(cache, 0, 1);
        Interval<String> i2 = new Interval<>(cache, 0, 1);
        Interval<String> i3 = new Interval<>(cache, 0, 2);
        assertEquals(i1, i2);
        assertNotEquals(i1, i3);
    }

    @Test
    public void testHashCode() {
        System.out.println("hashCode");
        MGXMaster master = get();
        Reference ref = master.Reference().fetch(4);
        Cache<String> cache = CacheFactory.createSequenceCache(master, ref);
        assertNotNull(cache);

        Interval<String> i1 = new Interval<>(cache, 0, 1);
        Interval<String> i2 = new Interval<>(cache, 0, 1);
        Interval<String> i3 = new Interval<>(cache, 0, 2);
        assertEquals(i1.hashCode(), i2.hashCode());
        assertNotEquals(i1.hashCode(), i3.hashCode());
    }

    public static MGXMaster get() {
        MGXMaster master = null;

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
        GPMSClientI gpms = new GPMS("MyServer", serverURI);
        if (!gpms.login("mgx_unittestRO", "gut-isM5iNt")) {
            fail();
        }
        for (MembershipI m : gpms.getMemberships()) {
            if ("MGX".equals(m.getProject().getProjectClass().getName()) && ("MGX_Unittest".equals(m.getProject().getName()))) {
                MGXDTOMaster dtomaster = new MGXDTOMaster(gpms, m);
                master = new MGXMaster(dtomaster);
                break;
            }
        }

        assert master != null;
        return master;
    }
}
