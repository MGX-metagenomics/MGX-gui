/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.datamodel.assembly;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.assembly.GeneI;

/**
 *
 * @author sj
 */
public class Gene extends GeneI {

    private final long ctgId;
    private final int start;
    private final int stop;
    private final int coverage;

    public Gene(MGXMasterI m, long id, long ctgId, int start, int stop, int coverage) {
        super(m);
        setId(id);
        this.ctgId = ctgId;
        this.start = start;
        this.stop = stop;
        this.coverage = coverage;
    }

    @Override
    public long getContigId() {
        return ctgId;
    }

    @Override
    public int getCoverage() {
        return coverage;
    }

    @Override
    public int getStart() {
        return start;
    }

    @Override
    public int getStop() {
        return stop;
    }

    @Override
    public int compareTo(GeneI o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
