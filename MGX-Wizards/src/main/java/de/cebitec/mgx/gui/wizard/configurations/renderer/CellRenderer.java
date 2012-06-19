/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.wizard.configurations.renderer;

import de.cebitec.mgx.gui.wizard.configurations.menu.MenuView;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.ArrayList;
import java.util.logging.Logger;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Die Klasse ist für die Bearbeitung der Zelleninhalte verantwortlich.
 */
public class CellRenderer extends DefaultTableCellRenderer {

    private final static Logger LOGGER =
            Logger.getLogger(CellRenderer.class.getName());
    /**
     * Zelleninhalte bestehen aus JTextArea.
     */
    private JTextArea area;
    private final ArrayList<Integer> disableRows;

    /**
     * Der Konstruktor initialisiert die JTextArea.
     *
     */
    public CellRenderer() {
        area = new JTextArea();
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setEditable(false);
        disableRows = null;

    }

    
    /**
     * Konstruktor fuer die Uebergabe von Zeilen, die ausgeblendet werden soll.
     * 
     * @param lDisableRows 
     */
    public CellRenderer(ArrayList<Integer> lDisableRows) {

        disableRows = lDisableRows;
        area = new JTextArea();
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setEditable(false);

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
    @Override
    public Component getTableCellRendererComponent(JTable table,
            Object value, boolean isSelected, boolean hasFocus, int row,
            int column) {
        if (column == 0) {
            Font fontFields = new Font(Font.DIALOG, Font.BOLD, 14);
            area.setFont(fontFields);

        }
        if (disableRows != null) {
            boolean setDisableRow = false;
            for (int rowNumber : disableRows) {
                if (rowNumber == row) {
                    this.setEnabled(false);
                    area.setEnabled(false);
                    setDisableRow = true;
                }
            }
            if (!setDisableRow) {
                area.setEnabled(true);

            }
        }
        if (isSelected) {
            int red = table.getSelectionBackground().getRed();
            int green = table.getSelectionBackground().getGreen();
            int blue = table.getSelectionBackground().getBlue();
            area.setBackground(new Color(red, green, blue));
        } else {
            area.setBackground(Color.WHITE);
        }
        area.setText(value.toString());
        return area;
    }
}
