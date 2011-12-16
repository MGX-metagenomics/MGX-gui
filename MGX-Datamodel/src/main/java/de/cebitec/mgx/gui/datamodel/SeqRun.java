package de.cebitec.mgx.gui.datamodel;

import java.util.Collection;

/**
 *
 * @author sjaenick
 */
public class SeqRun extends ModelBase {

    protected DNAExtract dnaextract;
    protected String sequencing_technology;
    protected String sequencing_method;
    protected Boolean submitted_to_insdc;
    protected String database_accession;

    public DNAExtract getExtract() {
        return dnaextract;
    }

    public SeqRun setExtract(DNAExtract dnaextract) {
        this.dnaextract = dnaextract;
        return this;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof SeqRun)) {
            return false;
        }
        SeqRun other = (SeqRun) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
}
