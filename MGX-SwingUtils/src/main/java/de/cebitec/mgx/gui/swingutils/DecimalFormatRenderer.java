/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.swingutils;

import java.awt.Component;
import java.io.Serial;
import java.text.DecimalFormat;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author sj
 */
public class DecimalFormatRenderer extends DefaultTableCellRenderer {
    
    @Serial
    private static final long serialVersionUID = 1L;
    
    private static final DecimalFormat FMT = new DecimalFormat("#.####");

    public DecimalFormatRenderer() {
        super();
        super.setHorizontalAlignment(SwingConstants.RIGHT);
        FMT.setMinimumFractionDigits(4);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value instanceof Double) {
            value = FMT.format((Double) value);
        }
        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }
    
}
