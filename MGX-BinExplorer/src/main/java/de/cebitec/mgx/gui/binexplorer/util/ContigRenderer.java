/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.binexplorer.util;

import de.cebitec.mgx.api.model.assembly.ContigI;
import java.awt.Component;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;

/**
 *
 * @author sj
 */
public class ContigRenderer extends JLabel implements ListCellRenderer<ContigI> {

    private final static DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);

    public ContigRenderer() {
        super();
        super.setOpaque(true);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends ContigI> list, ContigI contig,
            int index, boolean isSelected, boolean cellHasFocus) {

        this.setText(contig.getName() + " (" + formatter.format(contig.getLength()) + ")");

        if (isSelected) {
            this.setForeground(UIManager.getColor("List.selectionForeground"));
            this.setBackground(UIManager.getColor("List.selectionBackground"));
        } else {
            this.setForeground(UIManager.getColor("List.foreground"));
            this.setBackground(UIManager.getColor("List.background"));
        }

        return this;
    }

}
