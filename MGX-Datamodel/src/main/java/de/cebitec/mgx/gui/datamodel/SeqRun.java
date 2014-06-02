package de.cebitec.mgx.gui.datamodel;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.TermI;

/**
 *
 * @author sjaenick
 */
public class SeqRun extends SeqRunI {

    protected TermI sequencing_technology;
    protected TermI sequencing_method;
    protected String name;
    protected boolean submitted_to_insdc;
    protected String database_accession;
    protected long extract_id;
    protected long numSequences = 0;

    public SeqRun(MGXMasterI m) {
        super(m);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public SeqRun setName(String n) {
        name = n;
        return this;
    }

    @Override
    public SeqRun setDNAExtractId(long extractId) {
        extract_id = extractId;
        return this;
    }

    @Override
    public long getExtractId() {
        return extract_id;
    }

    @Override
    public String getAccession() {
        return database_accession;
    }

    @Override
    public SeqRun setAccession(String database_accession) {
        this.database_accession = database_accession;
        return this;
    }

    @Override
    public TermI getSequencingMethod() {
        return sequencing_method;
    }

    public SeqRun setSequencingMethod(TermI sequencing_method) {
        this.sequencing_method = sequencing_method;
        return this;
    }

    @Override
    public TermI getSequencingTechnology() {
        return sequencing_technology;
    }

    public SeqRun setSequencingTechnology(TermI sequencing_technology) {
        this.sequencing_technology = sequencing_technology;
        return this;
    }

    @Override
    public boolean getSubmittedToINSDC() {
        return submitted_to_insdc;
    }

    @Override
    public SeqRun setSubmittedToINSDC(Boolean submitted_to_insdc) {
        this.submitted_to_insdc = submitted_to_insdc;
        return this;
    }

    @Override
    public long getNumSequences() {
        return numSequences;
    }

    @Override
    public void setNumSequences(long numSequences) {
        this.numSequences = numSequences;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash = (int) (31 * hash + this.id);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof SeqRunI)) {
            return false;
        }

        if (this == object) {
            return true;
        }
        SeqRunI other = (SeqRunI) object;
        if ((this.id == INVALID_IDENTIFIER && other.getId() != INVALID_IDENTIFIER) || (this.id != INVALID_IDENTIFIER && this.id != other.getId())) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(SeqRunI o) {
        return getName().compareTo(o.getName());
    }
 
}
