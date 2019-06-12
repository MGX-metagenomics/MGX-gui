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
    private final float gc;
    private final int length_bp;

    public Contig(MGXMasterI m, long id, long binId, float gc, int length) {
        super(m);
        setId(id);
        this.binId = binId;
        this.gc = gc;
        this.length_bp = length;
    }

    @Override
    public long getBinId() {
        return binId;
    }

    @Override
    public float getGC() {
        return gc;
    }

    @Override
    public int getLength() {
        return length_bp;
    }

    @Override
    public int compareTo(ContigI o) {
        return Long.compare(id, o.getId());
    }

}
