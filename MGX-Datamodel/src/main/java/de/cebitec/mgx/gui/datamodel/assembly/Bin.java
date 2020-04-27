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
    private final float completeness, contamination;
    private final long n50;
    private final long totalBp;
    private final String taxonomy;
    private final int predicted_cds;
    private final long asmId;
    private final int num_contigs;

    public Bin(MGXMasterI m, long id, String name, float completeness, float contamination, long n50, long totalBp, String taxonomy, int numPredictedCDS, int numContigs, long asmId) {
        super(m);
        setId(id);
        this.name = name;
        this.completeness = completeness;
        this.contamination = contamination;
        this.n50 = n50;
        this.totalBp = totalBp;
        this.taxonomy = taxonomy;
        this.predicted_cds = numPredictedCDS;
        this.num_contigs = numContigs;
        this.asmId = asmId;
    }

    @Override
    public float getCompleteness() {
        return completeness;
    }

    @Override
    public float getContamination() {
        return contamination;
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
    public long getTotalSize() {
        return totalBp;
    }

    @Override
    public int getNumContigs() {
        return num_contigs;
    }

    @Override
    public int compareTo(BinI o) {
        if (getName().startsWith("Bin ") && o.getName().startsWith("Bin ")) {
            long myId = Long.parseLong(getName().substring(4));
            long otherId = Long.parseLong(o.getName().substring(4));
            return Long.compare(myId, otherId);
        }
        return name.compareTo(o.getName());
    }

    @Override
    public String toString() {
        return "Bin{" + "name=" + name + '}';
    }
}
