package de.cebitec.mgx.gui.wizard.configurations.start;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import de.cebitec.mgx.gui.wizard.configurations.data.util.XMLFileFilter;
import de.cebitec.mgx.gui.wizard.configurations.renderer.CellRenderer;
import de.cebitec.mgx.gui.wizard.configurations.utilities.ActionCommands;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *Diese Klasse zeigt das erste Panel im Wizard, wo die Tools angezeigt
 * werden.
 * 
 * 
 * @author belmann
 */
public class StartView extends JPanel implements DocumentListener {

   /**
    * Integer für Tools aus dem Projekt.
    */
   public final static int project = 0;
   /**
    * Integer für Tools vom Server.
    *
    */
   public final static int global = 1;
   /**
    * Integer für Tools vom Lokalen System.
    */
   public final static int local = 2;
   /**
    * ActionListener um zu erkennen, was in der ComboBox gewählt wurde.
    */
   private ActionListener listener;
   /**
    * ComboBox um zu bestimmen, ob die Tools vom Server, Projekt oder Lokal
    * hochgeladen werden.
    */
   private JComboBox toolLocationBox;
   /**
    * Author von Tools vom Server.
    */
   private ArrayList<String> authorGlobal;
   /**
    * Author von Tools vom Projekt.
    */
   private ArrayList<String> authorProject;
   /**
    * Beschreibung von Tools vom Server.
    */
   private ArrayList<String> descriptionGlobal;
   /**
    * Versionsnummer von Tools vom Server.
    */
   private ArrayList<String> versionGlobal;
   /**
    * Beschreibung von Tools vom Projekt.
    */
   private final ArrayList<String> descriptionProject;
   /**
    * Versionsnummer von Tools aus dem Projekt.
    */
   private final ArrayList<String> versionProject;
   /**
    * Name von Tools vom Server.
    */
   private final ArrayList<String> nameGlobal;
   /**
    * Name von Tools vom Projekt.
    */
   private final ArrayList<String> nameProject;
   /**
    * Tabelle für Tools vom Server.
    */
   private JTable globalTable;
   /**
    * Tabelle für Tools vom Projekt.
    */
   private JTable projectTable;
   /**
    * ScrollPane in das die Tabellen eingefügt werden.
    */
   private JScrollPane scrollPane;
   /**
    * FileChooser für die Wahl von XML Dateien.
    */
   private JFileChooser fileChooser;
   /**
    * Feld um den Namen der gewählten Tools anzuzeigen.
    */
   private JTextField toolField;
   /**
    * JButton um Dateien auswählen zu können.
    */
   private JButton chooseFile;
   /**
    * GridBagConstraints für das GridBagLayout des kompletten Panels.
    */
   private GridBagConstraints c;
   /**
    * AllInOnePanel in das alle möglichen Panels eingegeben werden.
    */
   private JPanel allInOnePanel;
   /**
    * Array für die Höhe der einzelnen Zeilen in der Tabelle für die Tools vom
    * Projekt.
    */
   private int[] rowsHeightProject;
   /**
    * Array für die Höhe der einzelnen Zeilen in der Tabelle für die Tools vom
    * Server.
    *
    */
   private int[] rowsHeightGlobal;

   /**
    * Konstruktor
    *
    * @param lAuthorGlobal Namen der Tools vom Server.
    * @param lAuthorProject Name der Tools vom Projekt.
    * @param lDescriptionGlobal Beschreibung der Tools vom Server.
    * @param lDescriptionProject Beschreibung der Tools vom Projekt.
    * @param lVersionGlobal Versionsnummern von Tools vom Server.
    * @param lVersionProject Versionsnummern von Tools vom Projekt.
    * @param lNameGlobal Name der Tools vom Server.
    * @param lNameProject Name der Tools vom Projekt.
    * @param lActionListener ActionListener für die ComboBox.
    * @param lDocumentListener DocumentListener fuer das JTextField.
    */
   protected StartView(ArrayList<String> lAuthorGlobal, 
           ArrayList<String> lAuthorProject, ArrayList<String> lDescriptionGlobal, 
           ArrayList<String> lDescriptionProject, ArrayList<String> lVersionGlobal, 
           ArrayList<String> lVersionProject, 
           ArrayList<String> lNameGlobal, ArrayList<String> lNameProject, 
           ActionListener lActionListener) {


	fileChooser = new JFileChooser();
	listener = lActionListener;
	authorGlobal = lAuthorGlobal;
	authorProject = lAuthorProject;
	descriptionGlobal = lDescriptionGlobal;
	descriptionProject = lDescriptionProject;
	versionGlobal = lVersionGlobal;
	versionProject = lVersionProject;
	nameGlobal = lNameGlobal;
	nameProject = lNameProject;
	rowsHeightGlobal = new int[nameGlobal.size()];
	rowsHeightProject = new int[nameProject.size()];
	setMinimumSize(new Dimension(650, 380));
	setPreferredSize(new Dimension(650, 380));
	setMaximumSize(new Dimension(650, 380));
	initComponent();
   }

   /**
    * Getter für die gewählten Ort, wo die Tools geladen werden.
    *
    * @return Ort der Tools.
    */
   protected String getToolLocation() {
	return (String) toolLocationBox.getSelectedItem();
   }

   /**
    * Initialisiert die Komponenten der View.
    */
   private void initComponent() {

	setLayout(new GridBagLayout());

	c = new GridBagConstraints();
	c.fill = GridBagConstraints.BOTH;
	c.anchor = GridBagConstraints.CENTER;
	c.gridy = 0;

	String[] options = {ActionCommands.Project, ActionCommands.Global, ActionCommands.Local};
	toolLocationBox = new JComboBox(options);
	toolLocationBox.setActionCommand(ActionCommands.toolBox);
	toolLocationBox.addActionListener(listener);

	JPanel northPanel = new JPanel();
	northPanel.setLayout(new BorderLayout());

	JPanel questionToolBox = new JPanel();
	questionToolBox.setLayout(new GridBagLayout());
	GridBagConstraints questionToolBoxConstraints = new GridBagConstraints();
	questionToolBoxConstraints.gridy = 0;
	questionToolBox.add(new JLabel("Select the location from where you want to load your Tools."), questionToolBoxConstraints);

	questionToolBoxConstraints.gridy = 1;
	questionToolBox.add(toolLocationBox, questionToolBoxConstraints);

	northPanel.add(questionToolBox);
	add(northPanel, c);

	JPanel buttonCenterPanel = new JPanel();
	String[][] dataGlobal = GetDataForTableGlobal();
	String[] columns = {"Name", "Author", "Description", "Version"};

	initializeGlobalJTable(dataGlobal, columns);

	String[][] dataProject = new String[nameProject.size()][4];
	GetDataForTableProject(dataProject);
	JPanel borderLayoutPanel = initializeProjectJTable(dataProject, columns);
	JPanel southPanel = assemblePanels(buttonCenterPanel, borderLayoutPanel);
	add(southPanel, c);
   }

   /**
    * Erstellt das Datenmodell für die JTable für Tools vom Projekt.
    *
    * @param dataProject Datenmodell
    */
   private void GetDataForTableProject(String[][] dataProject) {
	for (int i = 0; i < nameProject.size(); i++) {
	   for (int j = 0; j < 4; j++) {
		switch (j) {
		   case 0:
			dataProject[i][j] = nameProject.get(i);
			break;
		   case 1:
			dataProject[i][j] = authorProject.get(i);
			break;
		   case 2:
			dataProject[i][j] = descriptionProject.get(i);
			break;
		   case 3:
			dataProject[i][j] = versionProject.get(i);
			break;
		}
	   }
	}
   }

   /**
    * Erstellt das Datenmodell für die JTable für Tools vom Projekt
    *
    * @return Datenmodell für Tools vom Server.
    */
   private String[][] GetDataForTableGlobal() {
	String[][] dataGlobal = new String[nameGlobal.size()][4];
	for (int i = 0; i < nameGlobal.size(); i++) {
	   for (int j = 0; j < 4; j++) {
		switch (j) {
		   case 0:
			dataGlobal[i][j] = nameGlobal.get(i);
			break;
		   case 1:
			dataGlobal[i][j] = authorGlobal.get(i);
			break;
		   case 2:
			dataGlobal[i][j] = descriptionGlobal.get(i);
			break;
		   case 3:
			dataGlobal[i][j] = versionGlobal.get(i);
			break;
		}
	   }
	}
	return dataGlobal;
   }

   /**
    * Fügt alle Panels zusammen.
    *
    * @param buttonCenterPanel Panel für den Button um lokal eine Datei
    * hochladen zu können.
    * @param borderLayoutPanel Panel, wo alle anderen Panels eingefügt werden.
    * @return Panel, welches alle Panels enthält.
    */
   private JPanel assemblePanels(JPanel buttonCenterPanel,
	 JPanel borderLayoutPanel) {
	scrollPane.setBorder(BorderFactory.createLineBorder(Color.black));
	allInOnePanel.add(scrollPane, "scrollPane");
	c.gridy = 1;
	c.insets = new Insets(10, 0, 10, 0);
	chooseFile = new JButton("Choose a local tool.");
	chooseFile.addActionListener(new ActionListener() {

	   @Override
	   public void actionPerformed(ActionEvent e) {
		chooseLocalFile();
	   }
	});
	repaintFileChooser(fileChooser, fileChooser);
	fileChooser.setFileFilter(new XMLFileFilter());
	chooseFile.setAlignmentX(
	    Component.CENTER_ALIGNMENT);
	buttonCenterPanel.setLayout(new BoxLayout(buttonCenterPanel, BoxLayout.PAGE_AXIS));
	buttonCenterPanel.add(Box.createVerticalGlue());
	buttonCenterPanel.add(chooseFile, BorderLayout.CENTER);
	buttonCenterPanel.add(Box.createVerticalGlue());
	allInOnePanel.add(buttonCenterPanel, "buttonPanel");
	borderLayoutPanel.add(allInOnePanel, BorderLayout.CENTER);
	c.weighty = 2.3;
	c.weightx = 2.3;
	add(borderLayoutPanel, c);
	c.weighty = 0;
	c.weightx = 0;
	toolField = new JTextField();
        toolField.getDocument().addDocumentListener(this);
	toolField.setEditable(false);
	toolField.setFocusable(false);
	JPanel southPanel = new JPanel();
	southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.LINE_AXIS));
	southPanel.add(Box.createVerticalGlue());
	JPanel toolFieldLabelPanel = new JPanel();
	toolFieldLabelPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
	toolFieldLabelPanel.setLayout(new BorderLayout());
	toolFieldLabelPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
	JLabel label = new JLabel("Selected tool:", SwingConstants.LEFT);
	label.setHorizontalAlignment(SwingConstants.LEFT);
	label.setHorizontalTextPosition(SwingConstants.LEFT);
	label.setAlignmentX(Component.LEFT_ALIGNMENT);
	label.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
	toolFieldLabelPanel.add(label, BorderLayout.LINE_START);
	toolFieldLabelPanel.add(toolField, BorderLayout.CENTER);
	southPanel.add(toolFieldLabelPanel);
	southPanel.add(Box.createVerticalGlue());
	c.gridy = 2;
	c.insets = new Insets(0, 0, 0, 0);
	return southPanel;
   }

   /**
    * Initialisiert Datenmodell für die JTable, welche alle Tools vom Server
    * enthält.
    *
    * @param dataGlobal Datenmodell
    * @param columns Spaltennamen.
    */
   private void initializeGlobalJTable(String[][] dataGlobal, String[] columns) {
	globalTable = new JTable(dataGlobal, columns);
	globalTable.addComponentListener(new ComponentListener() {

	   @Override
	   public void componentHidden(ComponentEvent e) {
	   }

	   @Override
	   public void componentMoved(ComponentEvent e) {
	   }

	   @Override
	   public void componentResized(ComponentEvent e) {
		calculateAndSetRowHeigth(globalTable,global);
	   }

	   @Override
	   public void componentShown(ComponentEvent e) {
		calculateAndSetRowHeigth(globalTable,global);
	   }
	});
	globalTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        globalTable.setCellSelectionEnabled(false);
	globalTable.setRowSelectionAllowed(true);
	globalTable.getTableHeader().setFocusable(false);
	globalTable.getTableHeader().setResizingAllowed(false);
	globalTable.getTableHeader().setReorderingAllowed(false);
	globalTable.repaint();
	globalTable.getColumnModel().getColumn(0).setCellRenderer(
	    new CellRenderer());
	globalTable.getColumnModel().getColumn(1).setCellRenderer(
	    new CellRenderer());
	globalTable.getColumnModel().getColumn(2).setCellRenderer(
	    new CellRenderer());
	globalTable.getColumnModel().getColumn(3).setCellRenderer(
	    new CellRenderer());

	globalTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

	   @Override
	   public void valueChanged(ListSelectionEvent e) {
		toolField.setText("");
		if (globalTable.getSelectedRow() != -1) {
		   String tool = (String) globalTable.getValueAt(globalTable.getSelectedRow(), 0);
		   toolField.setText(tool);
		   toolField.setToolTipText(tool);
		}
	   }
	});
   }

   /**
    *
    * Initialisiert Datenmodell für die JTable, welche alle Tools vom Projekt
    * enthält.
    *
    * @param dataProject Datenmodell
    * @param columns Spalten
    * @return JPanel mit der Tabelle
    */
   private JPanel initializeProjectJTable(String[][] dataProject, String[] columns) {
	projectTable = new JTable(dataProject, columns);
	projectTable.getColumnModel().getColumn(0).setCellRenderer(
	    new CellRenderer());
	projectTable.getColumnModel().getColumn(1).setCellRenderer(
	    new CellRenderer());
	projectTable.getColumnModel().getColumn(2).setCellRenderer(
	    new CellRenderer());
	projectTable.getColumnModel().getColumn(3).setCellRenderer(
	    new CellRenderer());
	projectTable.setRowSelectionAllowed(true);
	projectTable.getTableHeader().setFocusable(false);
	projectTable.getTableHeader().setResizingAllowed(false);
	projectTable.getTableHeader().setReorderingAllowed(false);
	projectTable.repaint();
	projectTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	projectTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

	   @Override
	   public void valueChanged(ListSelectionEvent e) {
		toolField.setText("");
		if (projectTable.getSelectedRow() != -1) {
		   String tool = (String) projectTable.getValueAt(projectTable.getSelectedRow(), 0);
		   toolField.setText(tool);
		   toolField.setToolTipText(tool);
                   currentID = projectTable.getSelectedRow();
		}
	   }
	});
	projectTable.addComponentListener(new ComponentListener() {

	   @Override
	   public void componentHidden(ComponentEvent e) {
	   }

	   @Override
	   public void componentMoved(ComponentEvent e) {
	   }

	   @Override
	   public void componentResized(ComponentEvent e) {
		calculateAndSetRowHeigth(projectTable,project);
	   }

	   @Override
	   public void componentShown(ComponentEvent e) {
		calculateAndSetRowHeigth(projectTable, project);
	   }
	});
	JPanel borderLayoutPanel = new JPanel();
	borderLayoutPanel.setLayout(new BorderLayout());
	allInOnePanel = new JPanel();
	allInOnePanel.setLayout(new CardLayout());
	scrollPane = new JScrollPane(projectTable);
	scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {

	   @Override
	   public void adjustmentValueChanged(AdjustmentEvent e) {
		projectTable.repaint();
		globalTable.repaint();
	   }
	});
	return borderLayoutPanel;
   }
int currentID = 3;
   /**
    *
    * Malt den FileChooser neu, sobald der Benutzer den Scrollbalken betätigt.
    *
    * @param lComponent Der allgemeine Container, wo sich der Scrollbalken
    * befindet.
    *
    * @param lChooser FileChooser auf dem repaint aufgerufen werden soll.
    */
   private void repaintFileChooser(Component lComponent, final JFileChooser lChooser) {
	if (lComponent instanceof JScrollPane) {
	   JScrollPane localScrollPane = (JScrollPane) lComponent;

	   localScrollPane.getHorizontalScrollBar().addAdjustmentListener(new AdjustmentListener() {

		@Override
		public void adjustmentValueChanged(AdjustmentEvent e) {
		   lChooser.repaint();
		}
	   });

	} else if (lComponent instanceof Container) {
	   Container container = (Container) lComponent;
	   for (int i = 0; i < container.getComponentCount(); i++) {
		Component child = container.getComponent(i);
		repaintFileChooser(child, lChooser);
	   }
	}
   }

   /**
    * Startet den FileChooser.
    */
   private void chooseLocalFile() {
	int returnVal = fileChooser.showOpenDialog(this);
	if (returnVal == JFileChooser.APPROVE_OPTION) {
	   File file = fileChooser.getSelectedFile();
	   toolField.setText("");
	   toolField.setText(file.getPath());
	   toolField.setToolTipText(file.getPath());
	}
   }

   /**
    * Berechnet und setzt die Zeilenhöhe in einer Tabelle.
    * 
    * @param table JTable.
    * @param tableSort Art der Tabelle(Project,Global bzw Server)
    */
   private void calculateAndSetRowHeigth(JTable table, int tableSort) {
	final int columnsCounter = 4;
	int[] localRowHeight;
	if (tableSort == project) {
	   localRowHeight = rowsHeightProject;
	} else {
	   localRowHeight = rowsHeightGlobal;
	}

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
		   rowsHeightGlobal[row] = height;
		} else {

		   if (height < rowsHeightGlobal[row]) {
			table.setRowHeight(row, rowsHeightGlobal[row]);
		   } else {
			table.setRowHeight(row, height);
			rowsHeightGlobal[row] = height;
		   }
		}
	   }
	}

	if (tableSort==project) {
	   rowsHeightProject = localRowHeight;
	} else {
	   rowsHeightGlobal = localRowHeight;
	}

   }
   
   /**
    * Setzt die Tools, welche angezeigt werden müssen.
    * 
    * @param toolLocation  Ort, wo die Tools sich befindet.
    */
   protected void setToolsToChoose(int toolLocation) {
	toolField.setText("");
	c.weighty = 2.3;
	c.weightx = 2.3;
	c.gridy = 1;
	switch (toolLocation) {
	   case project:
		projectTable.clearSelection();
		globalTable.clearSelection();
		scrollPane.remove(chooseFile);
		scrollPane.remove(globalTable);
		scrollPane.setViewportView(projectTable);
		((CardLayout) allInOnePanel.getLayout()).show(allInOnePanel, "scrollPane");
		break;
	   case global:
		globalTable.clearSelection();
		projectTable.clearSelection();
		scrollPane.remove(chooseFile);
		scrollPane.remove(projectTable);
		scrollPane.setViewportView(globalTable);
		((CardLayout) allInOnePanel.getLayout()).show(allInOnePanel, "scrollPane");
		break;
	   case local:
		((CardLayout) allInOnePanel.getLayout()).show(allInOnePanel, "buttonPanel");
		break;
	}
   }

   /**
    * Gibt den Namen des Tools wieder.
    * 
    * @return Name des Tools.
    */
    protected String getFileFieldText() {
       return toolField.getText();
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
      this.firePropertyChange(null, 0, 1);
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
      this.firePropertyChange(null, 0, 1);
      
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
      this.firePropertyChange(null, 0, 1);
      
    }
}
