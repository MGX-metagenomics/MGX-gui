/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.cebitec.mgx.gui.controller;

import de.cebitec.gpms.rest.RESTMasterI;
import de.cebitec.mgx.api.ControllerCreatorI;
import de.cebitec.mgx.api.MGXMasterI;

/**
 *
 * @author sj
 */
public class ControllerCreatorImpl implements ControllerCreatorI {

    @Override
    public MGXMasterI createController(RESTMasterI gpmsClient) {
        return new MGXMaster(gpmsClient);
    }
    
}
