/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.activate;

import de.cebitec.gpms.core.MembershipI;
import de.cebitec.gpms.nodesupport.GPMSProjectSupportI;
import de.cebitec.gpms.rest.RESTMasterI;
import de.cebitec.mgx.api.MGX2MasterI;
import de.cebitec.mgx.gui.controller.MGX2Master;
import de.cebitec.mgx.gui.nodes.ProjectNode;
import org.openide.nodes.Node;

/**
 *
 * @author sj
 */
public class MGX2ProjectSupport implements GPMSProjectSupportI {

    @Override
    public boolean isSupported(MembershipI mbr) {
        return "MGX-2".equals(mbr.getProject().getProjectClass().getName());
    }

    @Override
    public Node createProjectNode(RESTMasterI restMaster) {
        if ("MGX-2".equals(restMaster.getProject().getProjectClass().getName())) {
            MGX2MasterI master = new MGX2Master(restMaster);
            return new ProjectNode(master);
        }
        return null;
    }
}
