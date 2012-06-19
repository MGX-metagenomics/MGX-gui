/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.wizard.configurations.summary;

//~--- JDK imports ------------------------------------------------------------
import de.cebitec.mgx.gui.wizard.configurations.menu.MenuView;
import de.cebitec.mgx.gui.wizard.configurations.renderer.CellRenderer;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Diese Klasse stellt die View der Summary dar. Diese besteht aus einer JTable.
 *
 * @author belmann
 */
public class MenuSummaryView extends JPanel {

   /**
    * Hier werden die vom Benutzer vergebenen Namen für die ConfigItems
    * abgespeichert.
    */
  private ArrayList<ArrayList<String>> configItemsUserNames;
  /**
   * Die Knoten, die abgespeichert werden.
   */ 
  private ArrayList<String> nodes;
  
  /**
   * Die vom Benutzer übergebenen Parameter.
   */
   private ArrayList<String> parameter;
   
   /**
    * Zeilenhöhe.
    */
   private int[] rowsHeight;
   
   /**
    *JTable. 
    */
   private JTable table;

   /**
    * Im Konstruktor werden die Nodes und die Items als Parameter an die
    * Klassenvariablen übergeben.
    *
    * @param lNodes Nodes
    * @param lItems configItems
    */
   protected MenuSummaryView(ArrayList<String> lNodes,
	 ArrayList<ArrayList<String>> lItems) {
	this.setLayout(new BorderLayout());
	this.nodes = lNodes;
	this.configItemsUserNames = lItems;

	int size = 0;

	for (ArrayList<String> localParameter : lItems) {
	   size = size + localParameter.size();
	}
	rowsHeight = new int[size + nodes.size()];
   }

   /**
    * Gibt den Namen des Panels wieder.
    *
    * @return Name
    */
   @Override
   public String getName() {
	return "Summary/Finish";
   }

   /**
    * Initialisiert die View mit der JTable.
    *
    * @param parameter Parameter
    */
   public void initView(ArrayList<ArrayList<String>> parameter) {
	this.setLayout(new BorderLayout());
	setMinimumSize(new Dimension(650, 380));
	setPreferredSize(new Dimension(650, 380));
	setMaximumSize(new Dimension(650, 380));

	String[] columns = {"Field", "Parameter", "Input"};
	int size = 0;

	for (ArrayList<String> localParameter : parameter) {
	   size = size + localParameter.size();
	}

	String[][] data = new String[size + nodes.size()][3];
	int rowCounter = 0;

	for (int i = 0; i < nodes.size(); i++) {
	   data[rowCounter][0] = nodes.get(i);
	   data[rowCounter][1] = "";
	   data[rowCounter][2] = "";

	   for (int j = 0; j < parameter.get(i).size(); j++) {

		rowCounter++;
		data[rowCounter][0] = "";
		data[rowCounter][1] = configItemsUserNames.get(i).get(j);
		data[rowCounter][2] = parameter.get(i).get(j);
	   }

	   rowCounter++;
	}
	JScrollPane scrollPane = setUpJTable(data, columns);

	this.add(scrollPane, BorderLayout.CENTER);
   }

   /**
    * Ist für die Erstellung der JTable verantwortlich.
    *
    * @param data Daten
    * @param columns Spalten
    * @return JScrollPane
    */
   private JScrollPane setUpJTable(String[][] data, String[] columns) {
	table = new JTable(data, columns);
	table.getColumnModel().getColumn(0).setCellRenderer(
	    new CellRenderer());
	table.getColumnModel().getColumn(1).setCellRenderer(
	    new CellRenderer());
	table.getColumnModel().getColumn(2).setCellRenderer(
	    new CellRenderer());
	table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
	table.addComponentListener(new ComponentListener() {

	   @Override
	   public void componentHidden(ComponentEvent e) {
	   }

	   public void componentMoved(ComponentEvent e) {
	   }

	   public void componentResized(ComponentEvent e) {
		calculateAndSetRowHeigth();
	   }

	   public void componentShown(ComponentEvent e) {
		calculateAndSetRowHeigth();
	   }
	});
	table.setFocusable(false);
	table.setCellSelectionEnabled(false);
	table.getTableHeader().setFocusable(false);
	table.getTableHeader().setResizingAllowed(false);
	table.getTableHeader().setReorderingAllowed(false);
	table.repaint();
	JPanel panel = new JPanel();
	panel.add(table);
	JScrollPane scrollPane = new JScrollPane(table);
	scrollPane.getVerticalScrollBar().addAdjustmentListener(
                new AdjustmentListener() {

	   @Override
	   public void adjustmentValueChanged(AdjustmentEvent e) {
		table.repaint();
	   }
	});
	return scrollPane;
   }

   /**
    * Berechnet die Höhe einer Zeile im JTable.
    */
   private void calculateAndSetRowHeigth() {
	final int columnsCounter = 3;

	for (int columnIndex = 0; columnIndex < columnsCounter; columnIndex++) {

	   int width = table.getColumnModel().getColumn(columnIndex).getWidth();
	   for (int row = 0; row < table.getRowCount(); row++) {
		JTextArea area = new JTextArea();
                Font fontFields = new Font(Font.DIALOG, Font.BOLD, 14);
		area.setFont(fontFields);
		area.setLineWrap(true);
		area.setWrapStyleWord(true);
		area.setSize(width, Short.MAX_VALUE);
		area.setText(table.getValueAt(row, columnIndex).toString());

		int height;
		if (area.getPreferredSize().height < 1) {

		   height = 1;

		} else {
		   height = area.getPreferredSize().height;
		}

		if (columnIndex == 0) {
		   table.setRowHeight(row, height);
		   rowsHeight[row] = height;
		} else {

		   if (height < rowsHeight[row]) {
			table.setRowHeight(row, rowsHeight[row]);
		   } else {
			table.setRowHeight(row, height);
			rowsHeight[row] = height;
		   }
		}
	   }
	}
   }
   private final static Logger LOGGER =
	 Logger.getLogger(MenuView.class.getName());

 
}
//~ Formatted by Jindent --- http://www.jindent.com