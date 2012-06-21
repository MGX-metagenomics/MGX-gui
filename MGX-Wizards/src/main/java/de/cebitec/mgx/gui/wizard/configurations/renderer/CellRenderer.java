package de.cebitec.mgx.gui.wizard.configurations.renderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Die Klasse ist für die Bearbeitung der Zelleninhalte verantwortlich.
 */
public class CellRenderer extends DefaultTableCellRenderer {

    /**
     * Zelleninhalte bestehen aus JTextArea.
     */
    private JTextArea area= new JTextArea();
    private List<Integer> disableRows = null;

    /**
     * Der Konstruktor initialisiert die JTextArea.
     *
     */
    public CellRenderer() {
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setEditable(false);
        disableRows = new ArrayList<>();
    }
    
    /**
     * Konstruktor fuer die Uebergabe von Zeilen, die ausgeblendet werden soll.
     * 
     * @param lDisableRows 
     */
    public CellRenderer(ArrayList<Integer> lDisableRows) {
        disableRows = lDisableRows;
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
