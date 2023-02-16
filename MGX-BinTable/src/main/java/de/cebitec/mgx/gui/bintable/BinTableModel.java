package de.cebitec.mgx.gui.bintable;

import de.cebitec.mgx.api.model.assembly.BinI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author sj
 */
public class BinTableModel extends DefaultTableModel {

    private final List<BinI> data = new ArrayList<>();

    public void clear() {
        data.clear();
        super.setRowCount(0);
    }

    public void addAll(Collection<? extends BinI> coll) {
        for (BinI b : coll) {
            if (!data.contains(b)) {
                data.add(b);
            }
        }
        super.setRowCount(data.size());
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (row >= data.size()) {
            return null;
        }
        BinI bin = data.get(row);
        switch (column) {
            case 0:
                return bin.getName();
            case 1:
                return bin.getTaxonomy();
            case 2:
                return bin.getNumContigs();
            case 3:
                return bin.getTotalSize();
            case 4:
                return bin.getN50();
            case 5:
                return bin.getPredictedCDS();
            case 6:
                return bin.getCompleteness();
            case 7:
                return bin.getContamination();
        }
        return null;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "Bin name";
            case 1:
                return "Taxonomy";
            case 2:
                return "# Contigs";
            case 3:
                return "Assembled bp";
            case 4:
                return "N50";
            case 5:
                return "CDS";
            case 6:
                return "Completeness";
            case 7:
                return "Contamination";
        }
        return ""; // not reached
    }

    @Override
    public Class<?> getColumnClass(int column) {
        switch (column) {
            case 0:
            case 1:
                return String.class;
            case 2:
                return Integer.class;
            case 3:
                return Long.class;
            case 4:
            case 5:
                return Integer.class;
            case 6:
            case 7:
                return Float.class;
        }
        return super.getColumnClass(column);
    }

    @Override
    public int getColumnCount() {
        return 8;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}
