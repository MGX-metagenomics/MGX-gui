/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.wizard.configurations.renderer;

import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;

/**
 *
 * Header mit einer Anzeige fuer mehrere Zeilen.
 *
 * @author pbelmann
 */
public class MultiLineTableHeaderRenderer extends JLabel
        implements TableCellRenderer {

    /**
     * Konstruktor, der die einstellungen der Farben des Headers.
     */
    public MultiLineTableHeaderRenderer() {
        this.setOpaque(true);
        this.setForeground(UIManager.getColor("TableHeader.foreground"));
        this.setBackground(UIManager.getColor("TableHeader.background"));
        this.setBorder(UIManager.getBorder("TableHeader.cellBorder"));
        this.setFont(UIManager.getFont("TableHeader.font"));
    }

 /**
     * Gibt die JTable Komponente wieder.
     *
     * @param table Tabelle
     * @param value Objekt
     * @param isSelected selektiert oder nicht.
     * @param hasFocus besitzt den Fokus oder nicht
     * @param row Zeile
     * @param column Spalte
     * @return Komponente
     */
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        this.setText("<html><table><td width=60>" + value.toString()
                + "</td></table></html>");
        return this;
    }
}
