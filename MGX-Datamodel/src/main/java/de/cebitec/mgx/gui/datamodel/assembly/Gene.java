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
    private final int min;
    private final int max;
    private final int coverage;

    public Gene(MGXMasterI m, long id, long ctgId, int start, int stop, int coverage) {
        super(m);
        setId(id);
        this.ctgId = ctgId;
        this.start = start;
        this.stop = stop;
        this.coverage = coverage;
        this.min = min(start, stop);
        this.max = max(start, stop);
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
    public boolean isFwdStrand() {
        return stop > start;
    }

    /**
     * @return 1, 2, 3, -1, -2, -3 depending on the reading frame of the gene
     */
    @Override
    public int getFrame() {
        int frame;

        if (getStart() < getStop()) { // forward strand
            frame = (getStart()) % 3 + 1;
        } else {
            frame = (getStop()) % 3 - 3;
        }
        return frame;
    }

    @Override
    public final int getMax() {
        return max;
    }

    @Override
    public final int getMin() {
        return min;
    }

    @Override
    public int compareTo(GeneI o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private static int min(final int a, final int b) {
        return (a <= b) ? a : b;
    }

    private static int max(final int a, final int b) {
        return (a <= b) ? b : a;
    }

}
