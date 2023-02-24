package de.cebitec.mgx.gui.datamodel;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.HabitatI;

/**
 *
 * @author sjaenick
 */
public class Habitat extends HabitatI {

    protected String name;
    /*
     * GPS location of habitat
     */
    protected double latitude;
    protected double longitude;
    protected String description;
    protected String biome;

    public Habitat(MGXMasterI m) {
        super(m);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Habitat setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public double getLatitude() {
        return latitude;
    }

    @Override
    public Habitat setLatitude(double latitude) {
        this.latitude = latitude;
        return this;
    }

    @Override
    public double getLongitude() {
        return longitude;
    }

    @Override
    public Habitat setLongitude(double longitude) {
        this.longitude = longitude;
        return this;
    }

    @Override
    public String getBiome() {
        return biome;
    }

    @Override
    public Habitat setBiome(String biome) {
        this.biome = biome;
        return this;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Habitat setDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Habitat)) {
            return false;
        }
        Habitat other = (Habitat) object;
        if ((this.id == INVALID_IDENTIFIER && other.id != INVALID_IDENTIFIER) || (this.id != INVALID_IDENTIFIER && this.id != other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(HabitatI o) {
        return name.compareTo(o.getName());
    }
}
