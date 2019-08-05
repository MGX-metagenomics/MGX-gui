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

    private final String name;
    private final long binId;
    private final float gc;
    private final int length_bp;
    private final int numCDS;
    private final int coverage;

    public Contig(MGXMasterI m, long id, String name, long binId, float gc, int length, int coverage, int numCDS) {
        super(m);
        setId(id);
        this.name = name;
        this.binId = binId;
        this.gc = gc;
        this.length_bp = length;
        this.coverage = coverage;
        this.numCDS = numCDS;
    }

    @Override
    public String getName() {
        return name;
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
    public int getCoverage() {
        return coverage;
    }

    @Override
    public int getPredictedCDS() {
        return numCDS;
    }

    @Override
    public int compareTo(ContigI o) {
        return Long.compare(id, o.getId());
    }

}
