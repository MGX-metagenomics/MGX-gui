package de.cebitec.mgx.gui.util;

import de.cebitec.gpms.core.MembershipI;
import de.cebitec.gpms.rest.GPMSClientI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.restgpms.GPMS;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import static org.junit.Assert.fail;

/**
 *
 * @author sj
 */
public class TestMaster {

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
        if (!gpms.login("mgx_unittest", "gut-isM5iNt")) {
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

    public static MGXMaster get2() {
        MGXMaster master = null;

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
        if (!gpms.login("sjaenick", "XXX")) {
            return null;
        }
        for (MembershipI m : gpms.getMemberships()) {
            if ("MGX".equals(m.getProject().getProjectClass().getName()) && ("MGX_Viersen".equals(m.getProject().getName()))) {
                MGXDTOMaster dtomaster = new MGXDTOMaster(gpms, m);
                master = new MGXMaster(dtomaster);
                break;
            }
        }

        assert master != null;
        return master;
    }
}
