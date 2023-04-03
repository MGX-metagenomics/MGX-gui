/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.activate;

import de.cebitec.gpms.nodesupport.GPMSNodeSupport;
import de.cebitec.gpms.nodesupport.GPMSProjectSupportI;
import java.io.Serial;
import org.openide.modules.ModuleInstall;

public class Installer extends ModuleInstall {

    @Serial
    private static final long serialVersionUID = 1L;
    
    //private final GPMSProjectSupportI supp = new MGXProjectSupport();
    private final GPMSProjectSupportI supp2 = new MGX2ProjectSupport();

    @Override
    public void restored() {
        //GPMSNodeSupport.register(supp);
        GPMSNodeSupport.register(supp2);
    }

    @Override
    public void uninstalled() {
        //GPMSNodeSupport.unregister(supp);
        GPMSNodeSupport.unregister(supp2);
    }

}
