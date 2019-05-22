/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.datamodel.assembly;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.assembly.BinI;

/**
 *
 * @author sj
 */
public class Bin extends BinI {

    private final String name;
    private final long asmId;

    public Bin(MGXMasterI m, long id, String name, long asmId) {
        super(m);
        setId(id);
        this.name = name;
        this.asmId = asmId;
    }

    @Override
    public long getAssemblyId() {
        return asmId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int compareTo(BinI o) {
        return this.name.compareTo(o.getName());
    }
}
