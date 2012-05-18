/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.wizard.configurations.renderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Die Klasse ist für die Bearbeitung der Zelleninhalte verantwortlich.
 */
public class CellRenderer extends DefaultTableCellRenderer {

   /**
    * Zelleninhalte bestehen aus JTextArea.
    */
   private JTextArea area;

   /**
    * Der Konstruktor initialisiert die JTextArea.
    *
    */
   public CellRenderer() {
	area = new JTextArea();
	area.setLineWrap(true);
	area.setWrapStyleWord(true);
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
	Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	if (column == 0) {
	   Font fontFields = new Font(Font.DIALOG, Font.BOLD, 14);
	   area.setFont(fontFields);
	}


	//neu:
	if (isSelected) {
area.setBackground(Color.orange);
	   
//	   setForeground(table.getSelectionForeground());
//	   setBackground(table.getSelectionBackground());
	} else {
//	   setBackground(table.getBackground());
//	   setForeground(table.getForeground());
area.setBackground(Color.WHITE);

	}

//	if (hasFocus) {
//	   setBorder(UIManager.getBorder("Table.focusCellHigh lightBorder"));
//	   
//	}
	area.setText(value.toString());
//	   area.setForeground(c.getForeground());
//	   area.setBackground(c.getBackground());




	return area;
   }
}
