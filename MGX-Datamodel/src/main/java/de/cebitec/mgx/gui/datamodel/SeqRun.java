package de.cebitec.mgx.gui.datamodel;

/**
 *
 * @author sjaenick
 */
public class SeqRun extends ModelBase {

    protected String sequencing_technology;
    protected String sequencing_method;
    protected Boolean submitted_to_insdc;
    protected String database_accession;
    protected long extract_id;
    protected long numSequences = 0;

    public SeqRun setDNAExtractId(long extractId) {
        extract_id = extractId;
        return this;
    }

    public long getExtractId() {
        return extract_id;
    }

    public String getAccession() {
        return database_accession;
    }

    public SeqRun setAccession(String database_accession) {
        this.database_accession = database_accession;
        return this;
    }

    public String getSequencingMethod() {
        return sequencing_method;
    }

    public SeqRun setSequencingMethod(String sequencing_method) {
        this.sequencing_method = sequencing_method;
        return this;
    }

    public String getSequencingTechnology() {
        return sequencing_technology;
    }

    public SeqRun setSequencingTechnology(String sequencing_technology) {
        this.sequencing_technology = sequencing_technology;
        return this;
    }

    public Boolean getSubmittedToINSDC() {
        return submitted_to_insdc;
    }

    public SeqRun setSubmittedToINSDC(Boolean submitted_to_insdc) {
        this.submitted_to_insdc = submitted_to_insdc;
        return this;
    }

    public long getNumSequences() {
        return numSequences;
    }

    public void setNumSequences(long numSequences) {
        this.numSequences = numSequences;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash = (int) ((int) 31 * hash + this.id);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof SeqRun)) {
            return false;
        }
        SeqRun other = (SeqRun) object;
        if ((this.id == INVALID_IDENTIFIER && other.id != INVALID_IDENTIFIER) || (this.id != INVALID_IDENTIFIER && this.id != other.id)) {
            return false;
        }
        return true;
    }
}
