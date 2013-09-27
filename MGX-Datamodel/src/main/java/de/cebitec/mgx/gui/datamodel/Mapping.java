package de.cebitec.mgx.gui.datamodel;

import java.awt.datatransfer.DataFlavor;

/**
 *
 * @author sjaenick
 */
public class Mapping extends Identifiable {

    public static final DataFlavor DATA_FLAVOR = new DataFlavor(Mapping.class, "Mapping");
    private long reference_id;
    private long seqrun_id;
    private long job_id;

    public Mapping() {
        super(DATA_FLAVOR);
    }

    public long getReferenceID() {
        return reference_id;
    }

    public void setReferenceID(long reference_id) {
        this.reference_id = reference_id;
    }

    public long getSeqrunID() {
        return seqrun_id;
    }

    public void setSeqrunID(long seqrun_id) {
        this.seqrun_id = seqrun_id;
    }

    public long getJobID() {
        return job_id;
    }

    public void setJobID(long job_id) {
        this.job_id = job_id;
    }
}
