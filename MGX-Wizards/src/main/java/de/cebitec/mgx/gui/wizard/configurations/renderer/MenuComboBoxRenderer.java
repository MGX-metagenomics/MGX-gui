/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.wizard.configurations.renderer;

import java.awt.Color;
import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 *
 * Renderer um die JComboBox zu konfigurieren.
 *
 * @author belmann
 */
public class MenuComboBoxRenderer extends JLabel implements ListCellRenderer {

    /**
     * Die Beschreibungen der Items.
     */
    private String[] descriptions;

    /**
     * Der Konstruktor übergibt die Beschreibungen der Items an die
     * Klassenvariable.
     *
     * @param lDescriptions
     */
    public MenuComboBoxRenderer(String[] lDescriptions) {
        descriptions = lDescriptions;
        setOpaque(true);
    }

    /**
     * Gibt den Renderer für die einzelnen Zellen in der JComboBox wieder.
     *
     * @param list Liste in der JComboBox.
     * @param value Die Werte in der JComboBox.
     * @param index Der Index eines Objekts in der JComboBox.
     * @param isSelected Selektiert oder nicht.
     * @param cellHasFocus Besitzt den Fokus oder nicht.
     * @return Gibt die Komponente der JComboBox wieder.
     */
    @Override
    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {

        if (isSelected) {

            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        setFont(list.getFont());

        if (-1 < index) {

            String itemText = "<html><table><td width=" + 220
                    + "><b><i>" + value.toString() + "<br></i></b></td></table>";
            String divideText;
            if (index == 0) {

                divideText = "<html><table><td width=" + 220 + "></td></table>";
            } else {
                divideText = " <html><table><td width=" + 220
                        + ">" + "----------------" + "</td></table>";
            }
            String descriptionText = " <html><table><td width=" + 220 + ">"
                    + descriptions[index] + "</td></table>";
            setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.black));
            setText(itemText + divideText + descriptionText);
        } else {
            setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, Color.black));
            setText((value == null) ? "" : value.toString());

        }



        return this;
    }
}