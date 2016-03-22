package de.cebitec.mgx.gui.datamodel;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.SampleI;
import java.util.Date;

/**
 *
 * @author sjaenick
 */
public class Sample extends SampleI {

    protected Date collectiondate;
    protected String material;
    protected double temperature;
    protected int volume;
    protected String volume_unit;
    protected long habitat_id;

    public Sample(MGXMasterI m) {
        super(m);
    }

    @Override
    public long getHabitatId() {
        return habitat_id;
    }

    @Override
    public Date getCollectionDate() {
        return collectiondate;
    }

    @Override
    public String getMaterial() {
        return material;
    }

    @Override
    public Sample setHabitatId(long habId) {
        habitat_id = habId;
        return this;
    }

    @Override
    public Sample setCollectionDate(Date collectiondate) {
        this.collectiondate = collectiondate;
        return this;
    }

    @Override
    public Sample setMaterial(String material) {
        this.material = material;
        return this;
    }

    @Override
    public Double getTemperature() {
        return temperature;
    }

    @Override
    public Sample setTemperature(double temperature) {
        this.temperature = temperature;
        return this;
    }

    @Override
    public Integer getVolume() {
        return volume;
    }

    @Override
    public Sample setVolume(int volume) {
        this.volume = volume;
        return this;
    }

    @Override
    public String getVolumeUnit() {
        return volume_unit;
    }

    @Override
    public Sample setVolumeUnit(String volume_unit) {
        this.volume_unit = volume_unit;
        return this;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof SampleI)) {
            return false;
        }
        SampleI other = (SampleI) object;
        if ((this.id == INVALID_IDENTIFIER && other.getId() != INVALID_IDENTIFIER) || (this.id != INVALID_IDENTIFIER && this.id != other.getId())) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(SampleI o) {
        return getCollectionDate().compareTo(o.getCollectionDate());
    }

}
