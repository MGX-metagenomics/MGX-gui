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
    private final int numSubregions;
    private final int coverage;

    public Contig(MGXMasterI m, long id, String name, long binId, float gc, int length, int coverage, int numSubregions) {
        super(m);
        setId(id);
        this.name = name;
        this.binId = binId;
        this.gc = gc;
        this.length_bp = length;
        this.coverage = coverage;
        this.numSubregions = numSubregions;
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

//    @Override
//    public float getPurine() {
//        try {
//            SequenceI dnaSequence = getMaster().Contig().getDNASequence(this);
//            String seq = dnaSequence.getSequence();
//            float purine = 0;
//            float pyrimidine = 0;
//            for (int i = 0; i< seq.length(); i++) {
//                char c = seq.charAt(i);
//                switch (c) {
//                    case 'A':
//                    case 'G':
//                        purine++;
//                        break;
//                    default:
//                        pyrimidine++;
//                }
//            }
//            return purine / pyrimidine;
//        } catch (MGXException ex) {
//            Logger.getLogger(Contig.class.getName()).log(Level.SEVERE, null, ex);
//            return 0;
//        }
//    }

    @Override
    public int getLength() {
        return length_bp;
    }

    @Override
    public int getCoverage() {
        return coverage;
    }

    @Override
    public int getPredictedSubregions() {
        return numSubregions;
    }

    @Override
    public int compareTo(ContigI o) {
        return Long.compare(id, o.getId());
    }

}
