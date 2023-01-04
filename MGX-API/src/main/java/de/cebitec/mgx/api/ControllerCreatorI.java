/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api;

import de.cebitec.gpms.rest.RESTMasterI;

/**
 *
 * @author sj
 */
public interface ControllerCreatorI {

    public MGXMasterI createController(RESTMasterI gpmsClient);

}
