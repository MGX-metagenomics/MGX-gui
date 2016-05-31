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
public abstract class MappingI extends Identifiable<MappingI> {

    public MappingI(MGXMasterI master, DataFlavor df) {
        super(master, df);
    }

    public abstract long getReferenceID();

    public abstract void setReferenceID(long reference_id);

    public abstract long getSeqrunID();

    public abstract void setSeqrunID(long seqrun_id);

    public abstract long getJobID();

    public abstract void setJobID(long job_id);

    @Override
    public abstract int compareTo(MappingI o);
    
}
