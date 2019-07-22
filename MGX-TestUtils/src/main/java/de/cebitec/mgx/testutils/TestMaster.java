package de.cebitec.mgx.testutils;

import de.cebitec.gpms.core.GPMSException;
import de.cebitec.gpms.core.MembershipI;
import de.cebitec.gpms.rest.GPMSClientFactory;
import de.cebitec.gpms.rest.GPMSClientI;
import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.gui.controller.MGXMaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;
import org.junit.Assert;
import static org.junit.Assert.fail;

/**
 *
 * @author sj
 */
public class TestMaster {

    private static MGXMasterI masterRO = null;

    public static MGXMasterI getRO() {
        if (masterRO != null) {
            return masterRO;
        }

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
        Assert.assertNotNull(mbr);

        while (mbr.hasNext()) {
            MembershipI m = mbr.next();
            if ("MGX".equals(m.getProject().getProjectClass().getName()) && ("MGX_Unittest".equals(m.getProject().getName()))) {
                try {
                    masterRO = new MGXMaster(gpms.createMaster(m));
                } catch (GPMSException ex) {
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

        String serverURI = "https://mgx.cebitec.uni-bielefeld.de/MGX-maven-web/webresources/";

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
        Assert.assertNotNull(mbr);

        while (mbr.hasNext()) {
            MembershipI m = mbr.next();
            if ("MGX".equals(m.getProject().getProjectClass().getName()) && ("MGX_Unittest".equals(m.getProject().getName()))) {
                try {
                    master = new MGXMaster(gpms.createMaster(m));
                } catch (GPMSException ex) {
                    fail(ex.getMessage());
                }
                break;
            }
        }

        return master;
    }

    public static MGXMasterI getPrivate(String targetProject) {
        MGXMasterI master = null;

        String serverURI = "https://mgx.cebitec.uni-bielefeld.de/MGX-maven-web/webresources/";

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
        Assert.assertNotNull(mbr);

        while (mbr.hasNext()) {
            MembershipI m = mbr.next();
            if ("MGX".equals(m.getProject().getProjectClass().getName()) && (targetProject.equals(m.getProject().getName()))) {
                try {
                    master = new MGXMaster(gpms.createMaster(m));
                } catch (GPMSException ex) {
                    fail(ex.getMessage());
                }
                break;
            }
        }

        return master;
    }
}
