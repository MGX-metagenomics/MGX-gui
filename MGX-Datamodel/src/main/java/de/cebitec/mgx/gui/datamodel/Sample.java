package de.cebitec.mgx.gui.datamodel;

import java.util.Date;

/**
 *
 * @author sjaenick
 */
public class Sample extends ModelBase {

    protected Date collectiondate;
    protected String material;
    protected double temperature;
    protected int volume;
    protected String volume_unit;
    protected long habitat_id;
//    protected Collection<DNAExtract> extracts = new HashSet<DNAExtract>();
//    protected Habitat habitat;
//
//    public Habitat getHabitat() {
//        return habitat;
//    }
//
//    public Sample setHabitat(Habitat h) {
//        habitat = h;
//        return this;
//    }

    public Sample setHabitatId(long habId) {
        habitat_id = habId;
        return this;
    }

    public long getHabitatId() {
        return habitat_id;
    }

    public Date getCollectionDate() {
        return collectiondate;
    }

    public Sample setCollectionDate(Date collectiondate) {
        this.collectiondate = collectiondate;
        return this;
    }

    public String getMaterial() {
        return material;
    }

    public Sample setMaterial(String material) {
        this.material = material;
        return this;
    }

    public Double getTemperature() {
        return temperature;
    }

    public Sample setTemperature(double temperature) {
        this.temperature = temperature;
        return this;
    }

    public Integer getVolume() {
        return volume;
    }

    public Sample setVolume(int volume) {
        this.volume = volume;
        return this;
    }

    public String getVolumeUnit() {
        return volume_unit;
    }

    public Sample setVolumeUnit(String volume_unit) {
        this.volume_unit = volume_unit;
        return this;
    }

//    public Collection<DNAExtract> getDNAExtracts() {
//        return extracts;
//    }
//
//    public Sample setDNAExtracts(Collection<DNAExtract> extracts) {
//        this.extracts = extracts;
//        return this;
//    }
//
//    public Sample addDNAExtract(DNAExtract d) {
//        getDNAExtracts().add(d);
//        d.setSample(this);
//        return this;
//    }
    @Override
    public int hashCode() {
        int hash = 0;
        hash = (int) ((int) 31 * hash + this.id);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Sample)) {
            return false;
        }
        Sample other = (Sample) object;
        if ((this.id == INVALID_IDENTIFIER && other.id != INVALID_IDENTIFIER) || (this.id != INVALID_IDENTIFIER && this.id != other.id)) {
            return false;
        }
        return true;
    }
}
