package de.cebitec.mgx.gui.datamodel;

import java.util.Collection;
import java.util.HashSet;

/**
 *
 * @author sjaenick
 */
public class Habitat extends ModelBase {

    protected String name;
    /* GPS location of habitat */
    protected double latitude;
    protected double longitude;
    protected String description;
    protected int altitude;
    protected String biome;
//    protected Collection<Sample> samples = new HashSet<Sample>();
//
//    public Collection<Sample> getSamples() {
//        return samples;
//    }
//
//    public Habitat addSample(Sample s) {
//        getSamples().add(s);
//        s.setHabitat(this);
//        return this;
//    }
//
//    public Habitat setSamples(Collection<Sample> samples) {
//        this.samples = samples;
//        return this;
//    }

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
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Habitat)) {
            return false;
        }
        Habitat other = (Habitat) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
}
