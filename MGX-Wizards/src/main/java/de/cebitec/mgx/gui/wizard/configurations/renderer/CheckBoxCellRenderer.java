package de.cebitec.mgx.gui.wizard.configurations.renderer;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * Rendered die CheckBoxen in einer JTable.
 *
 * @author pbelmann
 */
public class CheckBoxCellRenderer extends JCheckBox implements TableCellRenderer {

    /**
     * Enthaelt die Indizes von Zeilen die ausgeblendet werden sollen.
     */
    private final ArrayList<Integer> disableRows;

    /**
     * Uebergabe der Zeilen, die ausgeblendet werden sollen.
     *
     * @param disableRows
     */
    public CheckBoxCellRenderer(ArrayList<Integer> lDisableRows) {
        disableRows = lDisableRows;
        setHorizontalAlignment(JLabel.CENTER);
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
        if (disableRows != null) {
            boolean setDisableRow = false;
            for (int rowNumber : disableRows) {
                if (rowNumber == row) {
                    setDisableRow = true;
                    setEnabled(false);
                    setSelected(true);
                }

            }
            if (!setDisableRow) {
                setSelected(false);
                this.setEnabled(true);
            }
        }
        if (isSelected) {
            int red = table.getSelectionBackground().getRed();
            int green = table.getSelectionBackground().getGreen();
            int blue = table.getSelectionBackground().getBlue();
            setBackground(new Color(red, green, blue));
        } else {
            setBackground(Color.white);
        }
        return this;
    }
}
