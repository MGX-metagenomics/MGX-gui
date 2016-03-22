package de.cebitec.mgx.gui.datamodel;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.DNAExtractI;

/**
 *
 * @author sjaenick
 */
public class DNAExtract extends DNAExtractI {

    protected String name;
    protected String method;
    protected String protocol;
    protected String fivePrimer;
    protected String threePrimer;
    protected String targetGene;
    protected String targetFragment;
    protected String description;
    protected long sample_id;

    public DNAExtract(MGXMasterI m) {
        super(m);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public DNAExtract setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public DNAExtract setSampleId(long sampleId) {
        sample_id = sampleId;
        return this;
    }

    @Override
    public long getSampleId() {
        return sample_id;
    }

    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public DNAExtract setMethod(String method) {
        this.method = method;
        return this;
    }

    @Override
    public String getFivePrimer() {
        return fivePrimer;
    }

    @Override
    public DNAExtract setFivePrimer(String fivePrimer) {
        this.fivePrimer = fivePrimer;
        return this;
    }

    @Override
    public String getProtocol() {
        return protocol;
    }

    @Override
    public DNAExtract setProtocol(String protocol) {
        this.protocol = protocol;
        return this;
    }

    @Override
    public String getTargetFragment() {
        return targetFragment;
    }

    @Override
    public DNAExtract setTargetFragment(String targetFragment) {
        this.targetFragment = targetFragment;
        return this;
    }

    @Override
    public String getTargetGene() {
        return targetGene;
    }

    @Override
    public DNAExtract setTargetGene(String targetGene) {
        this.targetGene = targetGene;
        return this;
    }

    @Override
    public String getThreePrimer() {
        return threePrimer;
    }

    @Override
    public DNAExtract setThreePrimer(String threePrimer) {
        this.threePrimer = threePrimer;
        return this;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public DNAExtract setDescription(String description) {
        this.description = description;
        return this;
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
    public int compareTo(DNAExtractI o) {
        return name.compareTo(o.getName());
    }
}
