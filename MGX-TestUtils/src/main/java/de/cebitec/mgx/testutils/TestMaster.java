package de.cebitec.mgx.testutils;

import de.cebitec.gpms.core.GPMSException;
import de.cebitec.gpms.core.MembershipI;
import de.cebitec.gpms.rest.GPMSClientFactory;
import de.cebitec.gpms.rest.GPMSClientI;
import de.cebitec.mgx.api.MGXControllerFactory;
import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

/**
 *
 * @author sj
 */
public class TestMaster {

    private static MGXMasterI masterRO = null;

    private final static String SERVER = "https://mgx-test.computational.bio.uni-giessen.de/MGX-rest/webresources/";

    public static MGXMasterI getRO() {
        if (masterRO != null) {
            return masterRO;
        }

        String serverURI = SERVER;

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
        GPMSClientI gpms;
        try {
            gpms = GPMSClientFactory.createClient(serverURI, serverURI, false);
            gpms.login("mgx_unittestRO", "gut-isM5iNt");
        } catch (GPMSException ex) {
            fail(ex.getMessage());
            return null;
        }

        Iterator<MembershipI> mbr = null;
        try {
            mbr = gpms.getMemberships();
        } catch (GPMSException ex) {
            fail(ex.getMessage());
        }
        assertNotNull(mbr);

        while (mbr.hasNext()) {
            MembershipI m = mbr.next();
            if ("MGX-2".equals(m.getProject().getProjectClass().getName()) && ("MGX2_Unittest".equals(m.getProject().getName()))) {
                try {
                    masterRO = MGXControllerFactory.createMaster(gpms.createMaster(m));
                } catch (GPMSException | MGXException ex) {
                    fail(ex.getMessage());
                }
                break;
            }
        }

        assert masterRO != null;
        return masterRO;
    }

    public static MGXMasterI getRW() {
        MGXMasterI master = null;

        String serverURI = SERVER;

        Properties p = new Properties();
        String config = System.getProperty("user.home") + "/.m2/mgx.junit";
        File f = new File(config);
        if (f.exists() && f.canRead()) {
            try {
                p.load(new FileInputStream(f));
                serverURI = p.getProperty("testserver");
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
        GPMSClientI gpms;
        try {
            gpms = GPMSClientFactory.createClient("MyServer", serverURI, false);
            gpms.login("mgx_unittestRW", "hL0amo3oLae");
        } catch (GPMSException ex) {
            return null;
        }

        Iterator<MembershipI> mbr = null;
        try {
            mbr = gpms.getMemberships();
        } catch (GPMSException ex) {
            fail(ex.getMessage());
        }
        assertNotNull(mbr);

        while (mbr.hasNext()) {
            MembershipI m = mbr.next();
            if ("MGX-2".equals(m.getProject().getProjectClass().getName()) && ("MGX2_Unittest".equals(m.getProject().getName()))) {
                try {
                    master = MGXControllerFactory.createMaster(gpms.createMaster(m));
                } catch (GPMSException | MGXException ex) {
                    fail(ex.getMessage());
                }
                break;
            }
        }

        return master;
    }

    public static MGXMasterI getPrivate(String targetProject) {
        MGXMasterI master = null;

        String serverURI = SERVER;

        Properties p = new Properties();
        String config = System.getProperty("user.home") + "/.m2/mgx.private";
        File f = new File(config);
        if (f.exists() && f.canRead()) {
            try {
                p.load(new FileInputStream(f));
                serverURI = p.getProperty("testserver");
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
        GPMSClientI gpms;
        try {
            gpms = GPMSClientFactory.createClient("MyServer", serverURI, true);
            gpms.login(p.getProperty("username"), p.getProperty("password"));
        } catch (GPMSException ex) {
            return null;
        }

        Iterator<MembershipI> mbr = null;
        try {
            mbr = gpms.getMemberships();
        } catch (GPMSException ex) {
            fail(ex.getMessage());
        }
        assertNotNull(mbr);

        while (mbr.hasNext()) {
            MembershipI m = mbr.next();
            if ("MGX-2".equals(m.getProject().getProjectClass().getName()) && (targetProject.equals(m.getProject().getName()))) {
                try {
                    master = MGXControllerFactory.createMaster(gpms.createMaster(m));
                } catch (GPMSException | MGXException ex) {
                    fail(ex.getMessage());
                }
                break;
            }
        }

        return master;
    }
}
