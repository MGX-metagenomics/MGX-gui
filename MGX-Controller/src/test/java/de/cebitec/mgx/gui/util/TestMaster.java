package de.cebitec.mgx.gui.util;

import de.cebitec.gpms.core.GPMSException;
import de.cebitec.gpms.core.MembershipI;
import de.cebitec.gpms.rest.GPMSClientI;
import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.restgpms.GPMSClient;
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
        GPMSClient gpms = new GPMSClient("MyServer", serverURI, false);
        try {
            gpms.login("mgx_unittestRO", "gut-isM5iNt");
        } catch (GPMSException ex) {
            fail(ex.getMessage());
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
                masterRO = new MGXMaster(gpms.createMaster(m));
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
        GPMSClientI gpms = new GPMSClient("MyServer", serverURI, false);
        try {
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
                master = new MGXMaster(gpms.createMaster(m));
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
        GPMSClientI gpms = new GPMSClient("MyServer", serverURI);
        try {
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
                master = new MGXMaster(gpms.createMaster(m));
                break;
            }
        }

        return master;
    }
}
