/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.datamodel.assembly;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.assembly.ContigI;

/**
 *
 * @author sj
 */
public class Contig extends ContigI {
    
    private final long binId;
    private final String name;

    public Contig(MGXMasterI m, long id, long binId, String name) {
        super(m);
        setId(id);
        this.binId = binId;
        this.name = name;
    }
    
    @Override
    public long getBinId() {
        return binId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int compareTo(ContigI o) {
        return this.name.compareTo(o.getName());
    }

}
