package de.cebitec.mgx.gui.cache;

import de.cebitec.gpms.core.GPMSException;
import de.cebitec.gpms.rest.GPMSClientI;
import de.cebitec.gpms.rest.RESTMembershipI;
import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.restgpms.GPMS;
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
        GPMSClientI gpms = new GPMS("MyServer", serverURI);
        if (!gpms.login("mgx_unittestRO", "gut-isM5iNt")) {
            fail();
        }

        Iterator<RESTMembershipI> mbr = null;
        try {
            mbr = gpms.getMemberships();
        } catch (GPMSException ex) {
            fail(ex.getMessage());
        }
        Assert.assertNotNull(mbr);

        while (mbr.hasNext()) {
            RESTMembershipI m = mbr.next();
            if ("MGX".equals(m.getProject().getProjectClass().getName()) && ("MGX_Unittest".equals(m.getProject().getName()))) {
                MGXDTOMaster dtomaster = null;
                try {
                    dtomaster = new MGXDTOMaster(gpms.createMaster(m));
                } catch (GPMSException ex) {
                    fail(ex.getMessage());
                }
                masterRO = new MGXMaster(dtomaster);
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
        GPMSClientI gpms = new GPMS("MyServer", serverURI);
        if (!gpms.login("mgx_unittestRW", "hL0amo3oLae")) {
            return null;
        }
        Iterator<RESTMembershipI> mbr = null;
        try {
            mbr = gpms.getMemberships();
        } catch (GPMSException ex) {
            fail(ex.getMessage());
        }
        Assert.assertNotNull(mbr);

        while (mbr.hasNext()) {
            RESTMembershipI m = mbr.next();
            if ("MGX".equals(m.getProject().getProjectClass().getName()) && ("MGX_Unittest".equals(m.getProject().getName()))) {
                MGXDTOMaster dtomaster = null;
                try {
                    dtomaster = new MGXDTOMaster(gpms.createMaster(m));
                } catch (GPMSException ex) {
                    fail(ex.getMessage());
                }
                master = new MGXMaster(dtomaster);
                break;
            }
        }

        return master;
    }

    public static MGXMasterI getPrivate() {
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
        GPMSClientI gpms = new GPMS("MyServer", serverURI);
        if (!gpms.login(p.getProperty("username"), p.getProperty("password"))) {
            return null;
        }
        Iterator<RESTMembershipI> mbr = null;
        try {
            mbr = gpms.getMemberships();
        } catch (GPMSException ex) {
            fail(ex.getMessage());
        }
        Assert.assertNotNull(mbr);

        while (mbr.hasNext()) {
            RESTMembershipI m = mbr.next();
            if ("MGX".equals(m.getProject().getProjectClass().getName()) && ("MGX_Biogas_MT".equals(m.getProject().getName()))) {
                MGXDTOMaster dtomaster = null;
                try {
                    dtomaster = new MGXDTOMaster(gpms.createMaster(m));
                } catch (GPMSException ex) {
                    fail(ex.getMessage());
                }
                master = new MGXMaster(dtomaster);
                break;
            }
        }

        return master;
    }
}
