/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.server;

import de.cebitec.gpms.rest.GPMSClientI;
import de.cebitec.mgx.restgpms.GPMSClient;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.NbPreferences;

/**
 *
 * @author sj
 */
public class ServerFactory {

    public static List<GPMSClientI> getServers() {
        List<GPMSClientI> ret = new ArrayList<>();
        // let's assume 20 servers will be enough for now..
        for (int i = 0; i < 19; i++) {
            String siteName = NbPreferences.forModule(ServerFactory.class).get("server" + i, null);
            String siteUri = NbPreferences.forModule(ServerFactory.class).get("uri" + i, null);
            if (siteName != null && siteUri != null) {
                GPMSClientI c = new GPMSClient(siteName, siteUri);
                ret.add(c);
            }
        }

        // add default site
        if (ret.isEmpty()) {
            ret.add(new GPMSClient("CeBiTec", "https://mgx.cebitec.uni-bielefeld.de/MGX-maven-web/webresources/"));
            ret.add(new GPMSClient("JLU", "https://mgx.computational.bio.uni-giessen.de/MGX-maven-web/webresources/"));
        }
        return ret;
    }

    public static void addServer(GPMSClientI client) {
        if (client != null) {
            List<GPMSClientI> servers = getServers();
            for (GPMSClientI c : servers) {
                if (c.getServerName().equals(client.getServerName())) {
                    throw new RuntimeException("A server named " + client.getServerName() + " is already configured.");
                }
                if (c.getBaseURI().equals(client.getBaseURI())) {
                    throw new RuntimeException("This server is already present as " + c.getServerName());
                }
            }
            int i = 0;
            while (true) {
                String siteName = NbPreferences.forModule(ServerFactory.class).get("server" + i, null);
                String siteUri = NbPreferences.forModule(ServerFactory.class).get("uri" + i, null);
                if (siteName == null && siteUri == null) {
                    NbPreferences.forModule(ServerFactory.class).put("server" + i, client.getServerName());
                    NbPreferences.forModule(ServerFactory.class).put("uri" + i, client.getBaseURI());
                    return;
                }
                i++;
            }
        }
    }

    public static void removeServer(String serverName) {
        if (serverName != null) {
            for (int i = 0; i < 19; i++) {
                String siteName = NbPreferences.forModule(ServerFactory.class).get("server" + i, null);
                String siteUri = NbPreferences.forModule(ServerFactory.class).get("uri" + i, null);
                if (siteName != null && siteUri != null && siteName.equals(serverName)) {
                    NbPreferences.forModule(ServerFactory.class).remove("server" + i);
                    NbPreferences.forModule(ServerFactory.class).remove("uri" + i);
                    return;
                }
            }
        }
    }
}
