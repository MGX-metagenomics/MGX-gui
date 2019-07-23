/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.assembly.access.AssemblyAccessI;
import de.cebitec.mgx.api.model.assembly.access.BinAccessI;
import de.cebitec.mgx.api.model.assembly.access.ContigAccessI;

/**
 *
 * @author sj
 */
public abstract class MGX2MasterI extends MGXMasterI {

    @Override
    public final MGX2MasterI getMaster() {
        return this;
    }

    public abstract AssemblyAccessI Assembly() throws MGXException;

    public abstract BinAccessI Bin() throws MGXException;

    public abstract ContigAccessI Contig() throws MGXException;

}
