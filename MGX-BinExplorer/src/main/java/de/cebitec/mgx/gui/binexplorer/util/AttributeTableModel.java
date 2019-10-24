/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.binexplorer.util;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.assembly.GeneI;
import de.cebitec.mgx.api.model.assembly.GeneObservationI;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import javax.swing.table.DefaultTableModel;
import org.openide.util.Exceptions;

/**
 *
 * @author sj
 */
public class AttributeTableModel extends DefaultTableModel {

    private final List<GeneObservationI> gobsList = new ArrayList<>();

    public AttributeTableModel() {
    }

    public synchronized void update(GeneI gene) {
        gobsList.clear();
        if (gene == null) {
            super.setRowCount(0);
            return;
        }
        MGXMasterI master = gene.getMaster();
        try {
            Iterator<GeneObservationI> iter = master.GeneObservation().ByGene(gene);
            while (iter != null && iter.hasNext()) {
                GeneObservationI gobs = iter.next();
                gobsList.add(gobs);
            }
            super.setRowCount(gobsList.size());

        } catch (MGXException ex) {
            gobsList.clear();
            super.setRowCount(0);
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        GeneObservationI gobs = gobsList.get(row);
        switch (column) {
            case 0:
                return gobs.getAttributeTypeName();
            case 1:
                return gobs.getAttributeName();
            case 2:
                return NumberFormat.getInstance(Locale.US).format(gobs.getStart());
            case 3:
                return NumberFormat.getInstance(Locale.US).format(gobs.getStop());
            case 4:
                return gobs;
        }
        return null;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "Attribute type";
            case 1:
                return "Attribute";
            case 2:
                return "Start";
            case 3:
                return "Stop";
            case 4:
                return "";
        }
        return ""; // not reached
    }

    @Override
    public int getColumnCount() {
        return 5;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 4) {
            return GeneObservationI.class;
        }
        return super.getColumnClass(columnIndex);
    }
    
}
