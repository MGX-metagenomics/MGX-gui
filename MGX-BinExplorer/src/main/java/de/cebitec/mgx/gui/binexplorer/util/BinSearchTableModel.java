/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.binexplorer.util;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.assembly.BinI;
import de.cebitec.mgx.api.model.assembly.BinSearchResultI;
import java.io.Serial;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import org.openide.util.Exceptions;

/**
 *
 * @author sj
 */
public class BinSearchTableModel extends DefaultTableModel {

    @Serial
    private static final long serialVersionUID = 1L;

    private final List<BinSearchResultI> resList = new ArrayList<>();

    public BinSearchTableModel() {
    }

    public void clear() {
        resList.clear();
        super.setRowCount(0);
    }

    public synchronized void update(BinI bin, String searchTerm) {
        resList.clear();
        if (bin == null || searchTerm == null || searchTerm.isEmpty()) {
            super.setRowCount(0);
            return;
        }
        MGXMasterI master = bin.getMaster();

        try {
            Iterator<BinSearchResultI> iter = master.AssembledRegion().search(bin, searchTerm);
            while (iter != null && iter.hasNext()) {
                BinSearchResultI res = iter.next();
                resList.add(res);
            }

            super.setRowCount(resList.size());

        } catch (MGXException ex) {
            resList.clear();
            super.setRowCount(0);
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        BinSearchResultI obj = resList.get(row);
        switch (column) {
            case 0:
                return obj.getContigName() + "_" + obj.getRegionId();
            case 1:
                return obj.getContigName();
            case 2:
                return obj.getAttributeTypeValue();
            case 3:
                return obj.getAttributeName();
        }
        return null;
    }

    public BinSearchResultI getValue(int row) {
        return resList.get(row);
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "Gene";
            case 1:
                return "Contig";
            case 2:
                return "Type";
            case 3:
                return "Attribute";
        }
        return ""; // not reached
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

}
