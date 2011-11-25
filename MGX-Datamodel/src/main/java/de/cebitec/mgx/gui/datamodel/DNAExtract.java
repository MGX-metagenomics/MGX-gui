package de.cebitec.mgx.gui.datamodel;

import java.util.Collection;

/**
 *
 * @author sjaenick
 */
public class DNAExtract extends Identifiable {

    protected Collection<SeqRun> seqruns;
    //
    protected Sample sample;

    public Sample getSample() {
        return sample;
    }

    public void setSample(Sample sample) {
        this.sample = sample;
    }

    public Collection<SeqRun> getSeqruns() {
        return seqruns;
    }

    public void setSeqruns(Collection<SeqRun> seqruns) {
        this.seqruns = seqruns;
    }

    public void addSeqRun(SeqRun s) {
        getSeqruns().add(s);
        s.setExtract(this);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof DNAExtract)) {
            return false;
        }
        DNAExtract other = (DNAExtract) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
}
