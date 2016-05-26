/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.model;

import de.cebitec.mgx.api.MGXMasterI;
import java.awt.datatransfer.DataFlavor;

/**
 *
 * @author sj
 */
public abstract class HabitatI extends Identifiable<HabitatI> {

    //
    public static final DataFlavor DATA_FLAVOR = new DataFlavor(HabitatI.class, "HabitatI");

    public HabitatI(MGXMasterI m) {
        super(m, DATA_FLAVOR);
    }

    public abstract int getAltitude();

    public abstract String getBiome();

    public abstract String getDescription();

    public abstract double getLatitude();

    public abstract double getLongitude();

    public abstract String getName();

    public abstract HabitatI setAltitude(int altitude);

    public abstract HabitatI setBiome(String biome);

    public abstract HabitatI setDescription(String description);

    public abstract HabitatI setLatitude(double latitude);

    public abstract HabitatI setLongitude(double longitude);

    public abstract HabitatI setName(String name);
    
}
