/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.misc;

/**
 *
 * @author sj
 */
public class BulkObservation {
    
    private final long seqRunId;
    private final String seqName;
    private final long attrId;
    private final int start;
    private final int stop;

    public BulkObservation(long seqRunId, String seqName, long attrId, int start, int stop) {
        this.seqRunId = seqRunId;
        this.seqName = seqName;
        this.attrId = attrId;
        this.start = start;
        this.stop = stop;
    }

    public final long getSeqRunId() {
        return seqRunId;
    }

    public final String getSequenceName() {
        return seqName;
    }

    public final long getAttributeId() {
        return attrId;
    }

    public final int getStart() {
        return start;
    }

    public final int getStop() {
        return stop;
    }
    
}
