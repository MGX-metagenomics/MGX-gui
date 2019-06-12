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
    private final float completeness;
    private final long n50;
    private final String taxonomy;
    private final int predicted_cds;
    private final long asmId;

    public Bin(MGXMasterI m, long id, String name, float completeness, long n50, String taxonomy, int numPredictedCDS, long asmId) {
        super(m);
        setId(id);
        this.name = name;
        this.completeness = completeness;
        this.n50 = n50;
        this.taxonomy = taxonomy;
        this.predicted_cds = numPredictedCDS;
        this.asmId = asmId;
    }

    @Override
    public float getCompleteness() {
        return completeness;
    }

    @Override
    public long getN50() {
        return n50;
    }

    @Override
    public String getTaxonomy() {
        return taxonomy;
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
    public int getPredictedCDS() {
        return predicted_cds;
    }

    @Override
    public int compareTo(BinI o) {
        return this.name.compareTo(o.getName());
    }
}
