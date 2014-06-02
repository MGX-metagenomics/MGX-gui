package de.cebitec.mgx.gui.datamodel;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.MappingI;
import java.awt.datatransfer.DataFlavor;

/**
 *
 * @author sjaenick
 */
public class Mapping extends MappingI {

    public static final DataFlavor DATA_FLAVOR = new DataFlavor(MappingI.class, "MappingI");
    private long reference_id;
    private long seqrun_id;
    private long job_id;

    public Mapping(MGXMasterI m) {
        super(m, DATA_FLAVOR);
    }

    @Override
    public long getReferenceID() {
        return reference_id;
    }

    @Override
    public void setReferenceID(long reference_id) {
        this.reference_id = reference_id;
    }

    @Override
    public long getSeqrunID() {
        return seqrun_id;
    }

    @Override
    public void setSeqrunID(long seqrun_id) {
        this.seqrun_id = seqrun_id;
    }

    @Override
    public long getJobID() {
        return job_id;
    }

    @Override
    public void setJobID(long job_id) {
        this.job_id = job_id;
    }

    @Override
    public int compareTo(MappingI o) {
        return Long.compare(job_id, o.getJobID());
    }
}
