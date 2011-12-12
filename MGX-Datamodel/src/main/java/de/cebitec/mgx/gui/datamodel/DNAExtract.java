package de.cebitec.mgx.gui.datamodel;

import java.util.Collection;

/**
 *
 * @author sjaenick
 */
public class DNAExtract extends ModelBase {

    protected Collection<SeqRun> seqruns;
    protected Sample sample;
    protected String method;
    protected String protocol;
    protected String fivePrimer;
    protected String threePrimer;
    protected String targetGene;
    protected String targetFragment;
    protected String description;

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

    public String getMethod() {
        return method;
    }

    public DNAExtract setMethod(String method) {
        this.method = method;
        return this;
    }

    public String getFivePrimer() {
        return fivePrimer;
    }

    public DNAExtract setFivePrimer(String fivePrimer) {
        this.fivePrimer = fivePrimer;
        return this;
    }

    public String getProtocol() {
        return protocol;
    }

    public DNAExtract setProtocol(String protocol) {
        this.protocol = protocol;
        return this;
    }

    public String getTargetFragment() {
        return targetFragment;
    }

    public DNAExtract setTargetFragment(String targetFragment) {
        this.targetFragment = targetFragment;
        return this;
    }

    public String getTargetGene() {
        return targetGene;
    }

    public DNAExtract setTargetGene(String targetGene) {
        this.targetGene = targetGene;
        return this;
    }

    public String getThreePrimer() {
        return threePrimer;
    }

    public DNAExtract setThreePrimer(String threePrimer) {
        this.threePrimer = threePrimer;
        return this;
    }
    
    public String getDescription() {
        return description;
    }

    public DNAExtract setDescription(String description) {
        this.description = description;
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
