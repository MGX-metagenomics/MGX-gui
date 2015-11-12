/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.datamodel.qc;

import de.cebitec.mgx.api.model.qc.DataRowI;
import de.cebitec.mgx.api.model.qc.QCResultI;

/**
 *
 * @author sj
 */
public class QCResult implements QCResultI {
    
    private final String name;
    private final String description;
    private final DataRowI[] data;

    public QCResult(String name, String description, DataRowI[] data) {
        this.name = name;
        this.description = description;
        this.data = data;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public DataRowI[] getData() {
        return data;
    }

    @Override
    public int compareTo(QCResultI o) {
        return name.compareTo(o.getName());
    }

    @Override
    public String getDescription() {
        return description != null ? description : name;
    }
    
}
