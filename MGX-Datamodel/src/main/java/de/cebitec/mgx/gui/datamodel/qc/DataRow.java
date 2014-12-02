/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.datamodel.qc;

import de.cebitec.mgx.api.model.qc.DataRowI;

/**
 *
 * @author sj
 */
public class DataRow implements DataRowI {

    private final String name;
    private final float[] data;

    public DataRow(String name, float[] data) {
        this.name = name;
        this.data = data;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public float[] getData() {
        return data;
    }
}
