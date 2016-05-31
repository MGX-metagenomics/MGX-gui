/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.activate;

import de.cebitec.gpms.nodesupport.GPMSNodeSupport;
import de.cebitec.gpms.nodesupport.GPMSProjectSupportI;
import org.openide.modules.ModuleInstall;

public class Installer extends ModuleInstall {
    
    private final GPMSProjectSupportI supp = new MGXProjectSupport();
    
    @Override
    public void restored() {
        GPMSNodeSupport.register(supp);
    }
    
    @Override
    public void uninstalled() {
        GPMSNodeSupport.unregister(supp);
    }
    
}