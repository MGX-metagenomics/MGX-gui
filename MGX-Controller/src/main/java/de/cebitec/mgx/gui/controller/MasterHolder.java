/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.exception.MGXLoggedoutException;
import de.cebitec.mgx.client.MGXDTOMaster;

/**
 *
 * @author sj
 */
public abstract class MasterHolder {

    private final MGXMasterI master;
    private final MGXDTOMaster dtomaster;

    public MasterHolder(MGXMasterI master, MGXDTOMaster dtomaster) throws MGXException {
        this.master = master;
        this.dtomaster = dtomaster;
        if (master.isDeleted() || dtomaster.isClosed()) {
            throw new MGXLoggedoutException("You are disconnected.");
        }
    }

    protected final MGXDTOMaster getDTOmaster() {
        if (dtomaster.isClosed()) {
            throw new MGXLoggedoutException("You are disconnected.");
        }
        return dtomaster;
    }

    protected final MGXMasterI getMaster() {
        return master;
    }
}
