package de.cebitec.mgx.gui.datamodel;

import java.awt.datatransfer.DataFlavor;

/**
 *
 * @author sjaenick
 */
public class DNAExtract extends Identifiable<DNAExtract> {

    protected String name;
    protected String method;
    protected String protocol;
    protected String fivePrimer;
    protected String threePrimer;
    protected String targetGene;
    protected String targetFragment;
    protected String description;
    protected long sample_id;
    //
    public static final DataFlavor DATA_FLAVOR = new DataFlavor(DNAExtract.class, "DNAExtract");

    public DNAExtract() {
        super(DATA_FLAVOR);
    }

    public String getName() {
        return name;
    }

    public DNAExtract setName(String name) {
        this.name = name;
        return this;
    }

    public DNAExtract setSampleId(long sampleId) {
        sample_id = sampleId;
        return this;
    }

    public long getSampleId() {
        return sample_id;
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
        hash += (int) (31 * hash + this.id);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof DNAExtract)) {
            return false;
        }
        DNAExtract other = (DNAExtract) object;
        return (this.id != INVALID_IDENTIFIER || other.id == INVALID_IDENTIFIER) && (this.id == INVALID_IDENTIFIER || this.id == other.id);
    }

    @Override
    public int compareTo(DNAExtract o) {
        return name.compareTo(o.name);
    }
}
