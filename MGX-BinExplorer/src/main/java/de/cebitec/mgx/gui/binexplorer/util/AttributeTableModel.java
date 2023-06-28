/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.binexplorer.util;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.RegionI;
import de.cebitec.mgx.api.model.assembly.AssembledRegionI;
import de.cebitec.mgx.api.model.assembly.GeneObservationI;
import de.cebitec.mgx.gui.binexplorer.internal.ContigViewController;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serial;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import org.apache.commons.math3.util.FastMath;
import org.openide.util.Exceptions;

/**
 *
 * @author sj
 */
public class AttributeTableModel extends DefaultTableModel implements PropertyChangeListener {

    @Serial
    private static final long serialVersionUID = 1L;

    private final ContigViewController vc;
    private final List<GeneObservationI> gobsList = new ArrayList<>();

    public AttributeTableModel(ContigViewController vc) {
        vc.addPropertyChangeListener(this);
        this.vc = vc;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(ContigViewController.FEATURE_SELECTED)
                || evt.getPropertyName().equals(ContigViewController.NAVIGATE_TO_REGION)) {
            AssembledRegionI region = vc.getSelectedRegion();
            update(region);
        }
    }

    public void clear() {
        gobsList.clear();
        super.setRowCount(0);
    }

    private final static List<String> taxOrder = new ArrayList<String>() {
        {
            add("NCBI_NO_RANK");
            add("NCBI_SUPERKINGDOM");
            add("NCBI_PHYLUM");
            add("NCBI_CLASS");
            add("NCBI_ORDER");
            add("NCBI_FAMILY");
            add("NCBI_GENUS");
            add("NCBI_SPECIES");
        }
    };

    private synchronized void update(RegionI gene) {
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

            Collections.sort(gobsList, new Comparator<GeneObservationI>() {
                @Override
                public int compare(GeneObservationI t1, GeneObservationI t2) {

                    if ((t1.getAttributeTypeName().startsWith("NCBI")) && (t2.getAttributeTypeName().startsWith("NCBI"))) {
                        // sort by rank
                        int i1 = taxOrder.indexOf(t1.getAttributeTypeName());
                        int i2 = taxOrder.indexOf(t2.getAttributeTypeName());
                        return Integer.compare(i1, i2);
                    }

                    if ((t1.getAttributeTypeName().startsWith("NCBI")) && (!t2.getAttributeTypeName().startsWith("NCBI"))) {
                        return -1;
                    }

                    if ((!t1.getAttributeTypeName().startsWith("NCBI")) && (t2.getAttributeTypeName().startsWith("NCBI"))) {
                        return 1;
                    }

                    int ret = t1.getAttributeTypeName().compareTo(t2.getAttributeTypeName());

                    // for equal attribute types, sort by query range
                    if (ret == 0) {
                        int min1 = FastMath.min(t1.getStart(), t1.getStop());
                        int min2 = FastMath.min(t2.getStart(), t2.getStop());
                        return Integer.compare(min1, min2);
                    }

                    return ret;
                }
            });
            super.setRowCount(gobsList.size());

        } catch (MGXException ex) {
            gobsList.clear();
            super.setRowCount(0);
            Exceptions.printStackTrace(ex);
        }
        
        fireTableDataChanged();
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (row >= gobsList.size()) {
            return null;
        }
        GeneObservationI gobs = gobsList.get(row);
        switch (column) {
            case 0:
                return gobs.getAttributeTypeName();
            case 1:
                return gobs.getAttributeName();
            case 2:
                return gobs.getStart();
            case 3:
                return gobs.getStop();
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
                return "Query coverage";
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
    public Class<?> getColumnClass(int column) {
        switch (column) {
            case 2:
            case 3:
                return Integer.class;
            case 4:
                return GeneObservationI.class;
        }

        return super.getColumnClass(column);
    }

}
