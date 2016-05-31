/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.cebitec.mgx.api.model;

import de.cebitec.mgx.api.MGXMasterI;
import java.awt.datatransfer.DataFlavor;

/**
 *
 * @author sj
 */
public abstract class DNAExtractI extends Identifiable<DNAExtractI> {
    //
    public static final DataFlavor DATA_FLAVOR = new DataFlavor(DNAExtractI.class, "DNAExtractI");

    public DNAExtractI(MGXMasterI m) {
        super(m, DATA_FLAVOR);
    }

    public abstract String getName();

    public abstract DNAExtractI setName(String name);

    public abstract DNAExtractI setSampleId(long sampleId);

    public abstract long getSampleId();

    public abstract String getMethod();

    public abstract DNAExtractI setMethod(String method);

    public abstract String getFivePrimer();

    public abstract DNAExtractI setFivePrimer(String fivePrimer);

    public abstract String getProtocol();

    public abstract DNAExtractI setProtocol(String protocol);

    public abstract String getTargetFragment();

    public abstract DNAExtractI setTargetFragment(String targetFragment);

    public abstract String getTargetGene();

    public abstract DNAExtractI setTargetGene(String targetGene);

    public abstract String getThreePrimer();

    public abstract DNAExtractI setThreePrimer(String threePrimer);

    public abstract String getDescription();

    public abstract DNAExtractI setDescription(String description);

    @Override
    public abstract int compareTo(DNAExtractI o);
    
}
