/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.cebitec.mgx.api.model;

import de.cebitec.mgx.api.MGXMasterI;
import java.awt.datatransfer.DataFlavor;
import java.util.Date;

/**
 *
 * @author sj
 */
public abstract class SampleI extends Identifiable<SampleI> {
    //
    public static final DataFlavor DATA_FLAVOR = new DataFlavor(SampleI.class, "SampleI");

    public SampleI(MGXMasterI m) {
        super(m, DATA_FLAVOR);
    }

    public abstract SampleI setHabitatId(long habId);

    public abstract long getHabitatId();

    public abstract Date getCollectionDate();

    public abstract SampleI setCollectionDate(Date collectiondate);

    public abstract String getMaterial();

    public abstract SampleI setMaterial(String material);

    public abstract Double getTemperature();

    public abstract SampleI setTemperature(double temperature);

    public abstract Integer getVolume();

    public abstract SampleI setVolume(int volume);

    public abstract String getVolumeUnit();

    public abstract SampleI setVolumeUnit(String volume_unit);
    
}
