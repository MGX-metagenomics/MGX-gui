package de.cebitec.mgx.gui.datamodel;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

/**
 *
 * @author sjaenick
 */
public class Sample extends Identifiable {

    protected Date collectiondate;
    protected String material;
    protected int temperature;
    protected int volume;
    protected String volume_unit;
    protected Collection<DNAExtract> extracts;
    protected Habitat habitat;

    public Habitat getHabitat() {
        return habitat;
    }

    public Sample setHabitat(Habitat h) {
        habitat = h;
        return this;
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

    public int getTemperature() {
        return temperature;
    }

    public Sample setTemperature(int temperature) {
        this.temperature = temperature;
        return this;
    }

    public int getVolume() {
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

    public Collection<DNAExtract> getDNAExtracts() {
        return extracts;
    }

    public Sample setDNAExtracts(Collection<DNAExtract> extracts) {
        this.extracts = extracts;
        return this;
    }

    public Sample addDNAExtract(DNAExtract d) {
        getDNAExtracts().add(d);
        d.setSample(this);
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
        if (!(object instanceof Sample)) {
            return false;
        }
        Sample other = (Sample) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
}
