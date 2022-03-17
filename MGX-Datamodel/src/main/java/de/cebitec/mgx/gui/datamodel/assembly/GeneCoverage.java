/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.datamodel.assembly;

import de.cebitec.mgx.api.model.assembly.GeneCoverageI;

/**
 *
 * @author sj
 */
public class GeneCoverage extends GeneCoverageI {

    private final long geneId;
    private final long runId;
    private final int coverage;

    public GeneCoverage(long geneId, long runId, int coverage) {
        super();
        this.geneId = geneId;
        this.runId = runId;
        this.coverage = coverage;
    }

    @Override
    public long getRegionId() {
        return geneId;
    }

    @Override
    public long getRunId() {
        return runId;
    }

    @Override
    public int getCoverage() {
        return coverage;
    }

    @Override
    public int compareTo(GeneCoverageI t) {
        return Long.compare(this.runId, t.getRunId());
    }

}
