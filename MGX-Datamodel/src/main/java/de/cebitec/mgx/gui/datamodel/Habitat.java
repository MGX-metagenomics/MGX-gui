package de.cebitec.mgx.gui.datamodel;

import java.awt.datatransfer.DataFlavor;

/**
 *
 * @author sjaenick
 */
public class Habitat extends Identifiable implements Comparable<Habitat> {

    protected String name;
    //
    public static final DataFlavor DATA_FLAVOR = new DataFlavor(Habitat.class, "Habitat");
    /*
     * GPS location of habitat
     */
    protected double latitude;
    protected double longitude;
    protected String description;
    protected int altitude;
    protected String biome;

    public Habitat() {
        super(DATA_FLAVOR);
    }

    public String getName() {
        return name;
    }

    public Habitat setName(String name) {
        this.name = name;
        return this;
    }

    public double getLatitude() {
        return latitude;
    }

    public Habitat setLatitude(double latitude) {
        this.latitude = latitude;
        return this;
    }

    public double getLongitude() {
        return longitude;
    }

    public Habitat setLongitude(double longitude) {
        this.longitude = longitude;
        return this;
    }

    public int getAltitude() {
        return altitude;
    }

    public Habitat setAltitude(int altitude) {
        this.altitude = altitude;
        return this;
    }

    public String getBiome() {
        return biome;
    }

    public Habitat setBiome(String biome) {
        this.biome = biome;
        return this;
    }

    public String getDescription() {
        return description;
    }

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
    public int hashCode() {
        int hash = 7;
        hash = (int) ((int) 31 * hash + this.id);
        return hash;
    }

    @Override
    public int compareTo(Habitat o) {
        return name.compareTo(o.name);
    }
}
