package de.cebitec.mgx.gui.attributevisualization.filter;

import de.cebitec.gpms.core.MembershipI;
import de.cebitec.gpms.rest.GPMSClientI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.restgpms.GPMS;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;
import static org.junit.Assert.fail;

/**
 *
 * @author sjaenick
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
        Iterator<MembershipI> mIter = gpms.getMemberships();
        while (mIter.hasNext()) {
            MembershipI m = mIter.next();
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

        String serverURI = "http://scooter.cebitec.uni-bielefeld.de:8080/MGX-maven-web/webresources/";
        String username = null;
        String password = null;

        Properties p = new Properties();
        String config = System.getProperty("user.home") + "/.m2/mgx.private";
        File f = new File(config);
        if (f.exists() && f.canRead()) {
            try {
                p.load(new FileInputStream(f));
                serverURI = p.getProperty("testserver");
                password = p.getProperty("password");
                username = p.getProperty("username");
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
        GPMSClientI gpms = new GPMS("MyServer", serverURI);
        if (!gpms.login(username, password)) {
            return null;
        }

        Iterator<MembershipI> mIter = gpms.getMemberships();
        while (mIter.hasNext()) {
            MembershipI m = mIter.next();
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
