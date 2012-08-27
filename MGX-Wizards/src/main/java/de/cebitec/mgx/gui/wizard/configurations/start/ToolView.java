package de.cebitec.mgx.gui.wizard.configurations.start;

import de.cebitec.mgx.gui.datamodel.misc.ToolType;
import de.cebitec.mgx.gui.wizard.configurations.renderer.CellRenderer;
import de.cebitec.mgx.gui.wizard.configurations.renderer.MultiLineTableHeaderRenderer;
import de.cebitec.mgx.gui.wizard.configurations.utilities.ActionCommands;
import de.cebitec.mgx.gui.wizard.configurations.utilities.XMLFileFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

/**
 * Diese Klasse zeigt das erste Panel im Wizard, wo die Tools angezeigt werden.
 *
 *
 * @author belmann
 */
public class ToolView extends JPanel implements DocumentListener {

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
     * ChangeListener um zu erkennen, was in der ComboBox gewählt wurde.
     */
    private ChangeListener listener;

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
    private GridBagConstraints constraintsForAllPanels;
    /**
     * AllInOnePanel in das alle möglichen Panels eingegeben werden.
     */
    private JTabbedPane allInOnePanel;
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
     * Das Namensfeld fuer das lokale Tool.
     */
    private JTextField nameField;
    /**
     * Das Authorfeld fuer das lokale Tool.
     */
    private JTextField authorField;
    /**
     * Das Feld fuer die Beschreibung des lokalen Tools.
     */
    private JTextArea descriptionField;
    /**
     * Die Versionsnummer fuer das lokale Tool.
     */
    private JTextField versionField;
    /**
     * Die Indizes der Checkboxen in der Tabelle.
     */
    private ArrayList<Integer> checkBoxTableIndizes;
    /**
     * Array, in dem gespeichert wird, ob die Zeile ein Tool enthaelt oder
     * nicht.
     */
    private final ArrayList<Boolean> containsTool;
    /**
     * CurrentRow fuer die Zeile des derzeit ausgewaehlten tools.
     */
    private int currentRow = 0;

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
     * @param lChangeListener ActionListener für die ComboBox.
     * @param lDocumentListener DocumentListener fuer das JTextField.
     */
    protected ToolView(ArrayList<String> lAuthorGlobal,
            ArrayList<String> lAuthorProject,
            ArrayList<String> lDescriptionGlobal,
            ArrayList<String> lDescriptionProject,
            ArrayList<String> lVersionGlobal,
            ArrayList<String> lVersionProject,
            ArrayList<String> lNameGlobal, ArrayList<String> lNameProject,
            ChangeListener lChangeListener) {


        fileChooser = new JFileChooser();
        listener = lChangeListener;
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

        containsTool = new ArrayList<>();
        checkBoxTableIndizes = new ArrayList<>();
        for (String name : lNameProject) {
            for (int i = 0; i < nameGlobal.size(); i++) {
                if (nameGlobal.get(i).equals(name)) {
                    checkBoxTableIndizes.add(i);
                }
            }
        }

        for (int i = 0; i < nameGlobal.size(); i++) {

            if (checkBoxTableIndizes.contains(i)) {
                containsTool.add(Boolean.TRUE);
            } else {
                containsTool.add(Boolean.FALSE);
            }
        }
        initComponent();
    }

    /**
     * Getter für die gewählten Ort, wo die Tools geladen werden.
     *
     * @return Ort der Tools.
     */
    protected ToolType getToolType() {
        switch (allInOnePanel.getTitleAt(allInOnePanel.getSelectedIndex())) {
            case ActionCommands.Project:
                return ToolType.PROJECT;
            case ActionCommands.Global:
                return ToolType.GLOBAL;
            case ActionCommands.Local:
                return ToolType.USER_PROVIDED;
            default:
                assert false;
        }

        // not reached
        assert false;
        return ToolType.USER_PROVIDED;
    }

    /**
     * Initialisiert die Komponenten der View.
     */
    private void initComponent() {

        setLayout(new GridBagLayout());

        constraintsForAllPanels = new GridBagConstraints();
        constraintsForAllPanels.fill = GridBagConstraints.BOTH;
        constraintsForAllPanels.anchor = GridBagConstraints.CENTER;
        constraintsForAllPanels.gridy = 0;

        String[] options = {ActionCommands.Project, ActionCommands.Global,
            ActionCommands.Local};

        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BorderLayout());

        JPanel questionToolBox = new JPanel();
        questionToolBox.setLayout(new GridBagLayout());
        GridBagConstraints questionToolBoxConstraints =
                new GridBagConstraints();
        questionToolBoxConstraints.insets = new Insets(5, 0, 5, 0);
        questionToolBoxConstraints.gridy = 0;
        Font font = new Font(Font.DIALOG, Font.BOLD, 13);
        JLabel label = new JLabel("Select the location from "
                + "where you want to load your tools.");
        label.setFont(font);
        questionToolBox.add(label,
                questionToolBoxConstraints);
        questionToolBoxConstraints.gridy = 1;

        northPanel.add(questionToolBox);
//        add(northPanel, constraintsForAllPanels);

        JPanel buttonCenterPanel = new JPanel();
        String[][] dataGlobal = GetDataForTableGlobal();
        String[] columnsProject = {"Name", "Author", "Description", "Version"
        };
        String[] columnsGlobal = {"Name", "Author", "Description", "Version"
        };


        initializeGlobalJTable(dataGlobal, columnsGlobal);

        String[][] dataProject = new String[nameProject.size()][4];
        GetDataForTableProject(dataProject);
        JPanel borderLayoutPanel = initializeProjectJTable(dataProject, columnsProject);
        JPanel southPanel = assemblePanels(buttonCenterPanel, borderLayoutPanel);
        add(southPanel, constraintsForAllPanels);
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
        String[][] dataGlobal = new String[nameGlobal.size()][5];
        for (int i = 0; i < nameGlobal.size(); i++) {
            for (int j = 0; j < 5; j++) {
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
                    case 4:
                        dataGlobal[i][j] = containsTool.get(i).toString();
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
        allInOnePanel.add(ActionCommands.Project, scrollPane);
        allInOnePanel.addChangeListener(listener);
        constraintsForAllPanels.gridy = 1;
        constraintsForAllPanels.insets = new Insets(10, 0, 10, 0);
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
        buttonCenterPanel.setLayout(new BoxLayout(buttonCenterPanel,
                BoxLayout.PAGE_AXIS));

        buttonCenterPanel.add(Box.createVerticalGlue());
        JPanel temp = inputLocalToolForm();
        buttonCenterPanel.add(temp, BorderLayout.NORTH);
        buttonCenterPanel.add(Box.createVerticalGlue());

        JScrollPane globalPane = new JScrollPane(globalTable);
        allInOnePanel.add(ActionCommands.Global, globalPane);
        allInOnePanel.add(ActionCommands.Local, buttonCenterPanel);

        borderLayoutPanel.add(allInOnePanel, BorderLayout.CENTER);
        constraintsForAllPanels.weighty = 2.3;
        constraintsForAllPanels.weightx = 2.3;
        add(borderLayoutPanel, constraintsForAllPanels);
        constraintsForAllPanels.weighty = 0;
        constraintsForAllPanels.weightx = 0;
        toolField = new JTextField();
        Font font = new Font(Font.DIALOG, Font.BOLD, 13);
        toolField.setFont(font);
        toolField.getDocument().addDocumentListener(this);
        toolField.setText("");
        toolField.setEditable(false);
        toolField.setFocusable(false);
        toolField.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.LINE_AXIS));
        southPanel.add(Box.createVerticalGlue());
        JPanel toolFieldLabelPanel = new JPanel();
        toolFieldLabelPanel.setComponentOrientation(
                ComponentOrientation.LEFT_TO_RIGHT);
        toolFieldLabelPanel.setLayout(new BorderLayout());
        toolFieldLabelPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel label = new JLabel("Selected tool:", SwingConstants.LEFT);
        label.setFont(font);
        label.setHorizontalAlignment(SwingConstants.LEFT);
        label.setHorizontalTextPosition(SwingConstants.LEFT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        toolFieldLabelPanel.add(label, BorderLayout.LINE_START);

        toolFieldLabelPanel.add(toolField, BorderLayout.CENTER);
        southPanel.add(toolFieldLabelPanel);
        southPanel.add(Box.createVerticalGlue());
        constraintsForAllPanels.gridy = 2;
        constraintsForAllPanels.insets = new Insets(0, 0, 0, 0);
        return southPanel;
    }

    /**
     * Erstellt das Formular fuer die lokalen Tools.
     *
     * @return JPanel des Formulars.
     */
    private JPanel inputLocalToolForm() {
        GridBagConstraints con = new GridBagConstraints();
        JPanel panel = new JPanel(new GridBagLayout());
        int width = 50;

        con.gridy = 0;
        con.gridx = 0;
        JLabel lab = new JLabel("", JLabel.RIGHT);
        lab.setLabelFor(chooseFile);
        panel.add(lab, con);

        con.gridx = 1;
        panel.add(chooseFile, con);

        con.gridy = 1;
        nameField = new JTextField();
        nameField.setColumns(width);
        nameField.getDocument().addDocumentListener(this);

        lab = new JLabel("Name:", JLabel.RIGHT);
        lab.setLabelFor(nameField);

        con.gridx = 0;
        con.anchor = GridBagConstraints.EAST;
        panel.add(lab, con);
        con.insets = new Insets(10, 5, 10, 5);

        con.gridx = 1;
        con.anchor = GridBagConstraints.CENTER;
        panel.add(nameField, con);

        con.gridy = 2;
        authorField = new JTextField();
        authorField.setColumns(width);
        authorField.getDocument().addDocumentListener(this);

        lab = new JLabel("Author:", JLabel.RIGHT);
        lab.setLabelFor(authorField);

        con.gridx = 0;
        con.anchor = GridBagConstraints.EAST;
        panel.add(lab, con);

        con.gridx = 1;
        con.anchor = GridBagConstraints.CENTER;
        panel.add(authorField, con);

        con.gridy = 3;
        descriptionField = new JTextArea();
        descriptionField.setWrapStyleWord(true);
        descriptionField.setLineWrap(true);
        descriptionField.getDocument().addDocumentListener(this);
        JScrollPane pane = new JScrollPane(descriptionField);
        pane.setPreferredSize(new Dimension(405, 70));
        lab = new JLabel("Description:", JLabel.RIGHT);
        lab.setLabelFor(pane);

        con.gridx = 0;
        con.anchor = GridBagConstraints.NORTHEAST;
        panel.add(lab, con);

        con.anchor = GridBagConstraints.CENTER;
        con.gridx = 1;
        panel.add(pane, con);

        con.gridy = 4;
        versionField = new JTextField();
        versionField.getDocument().addDocumentListener(this);
        versionField.setColumns(width);

        lab = new JLabel("Version:", JLabel.RIGHT);
        lab.setLabelFor(versionField);

        con.gridx = 0;
        con.anchor = GridBagConstraints.EAST;
        panel.add(lab, con);

        con.gridx = 1;
        con.anchor = GridBagConstraints.CENTER;
        panel.add(versionField, con);

        return panel;
    }

    /**
     * Gibt den Namen des Tools wieder.
     *
     * @return Namen des Tools.
     */
    public String getNameText() {
        return nameField.getText();
    }

    /**
     * Gibt den Namen des Authors wieder.
     *
     * @return Name des Authors.
     */
    public String getAuthorText() {
        return authorField.getText();
    }

    /**
     * Gibt die Beschreibung des Tools wieder.
     *
     * @return Beschreibung des Tools.
     */
    public String getDescriptionText() {
        return descriptionField.getText();
    }

    /**
     * Gibt die Versionsnummer des Tools zurueck.
     *
     * @return
     */
    public String getVersionText() {
        return versionField.getText();
    }

    /**
     * Initialisiert Datenmodell für die JTable, welche alle Tools vom Server
     * enthält.
     *
     * @param dataGlobal Datenmodell
     * @param columns Spaltennamen.
     */
    private void initializeGlobalJTable(String[][] dataGlobal,
            String[] columns) {
        DefaultTableModel dtm = new DefaultTableModel(dataGlobal, columns);
        globalTable = new JTable(dtm) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        globalTable.addComponentListener(new ComponentListener() {
            @Override
            public void componentHidden(ComponentEvent e) {
            }

            @Override
            public void componentMoved(ComponentEvent e) {
            }

            @Override
            public void componentResized(ComponentEvent e) {
                calculateAndSetRowHeigth(globalTable, global);
            }

            @Override
            public void componentShown(ComponentEvent e) {
                calculateAndSetRowHeigth(globalTable, global);
            }
        });
        globalTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        globalTable.setSelectionModel(new DefaultListSelectionModel() {
            @Override
            public boolean isSelectedIndex(int index) {
                boolean isSelected;

                if (checkBoxTableIndizes.contains(index)) {
                    isSelected = false;
                } else {
                    isSelected = super.isSelectedIndex(index);
                }
                return isSelected;
            }
        });
        globalTable.setCellSelectionEnabled(false);
        globalTable.setRowSelectionAllowed(true);
        globalTable.getTableHeader().setFocusable(false);
        globalTable.getTableHeader().setResizingAllowed(false);
        globalTable.getTableHeader().setReorderingAllowed(false);
        globalTable.repaint();
        globalTable.getColumnModel().getColumn(0).setCellRenderer(
                new CellRenderer(checkBoxTableIndizes));
        globalTable.getColumnModel().getColumn(1).setCellRenderer(
                new CellRenderer(checkBoxTableIndizes));
        globalTable.getColumnModel().getColumn(2).setCellRenderer(
                new CellRenderer(checkBoxTableIndizes));
        globalTable.getColumnModel().getColumn(3).setCellRenderer(
                new CellRenderer(checkBoxTableIndizes));
        globalTable.getTableHeader().setDefaultRenderer(
                new MultiLineTableHeaderRenderer());
        TableColumn col = globalTable.getColumnModel().getColumn(3);
        int versionWidth = 50;

        col.setMaxWidth(versionWidth);
        col.setMinWidth(versionWidth);
        col.setWidth(versionWidth);
        col.setPreferredWidth(versionWidth);

        globalTable.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
                    @Override
                    public void valueChanged(ListSelectionEvent e) {
                        if (globalTable.getSelectedRow() != -1
                                && !checkBoxTableIndizes.contains(
                                globalTable.getSelectedRow())) {
                            firePropertyChange(null, 0, 1);
                            String tool = (String) globalTable.getValueAt(
                                    globalTable.getSelectedRow(), 0);
                            toolField.setText(tool);
                            toolField.setToolTipText(tool);
                            currentRow = globalTable.getSelectedRow();
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
    private JPanel initializeProjectJTable(String[][] dataProject,
            String[] columns) {
        DefaultTableModel dtm = new DefaultTableModel(dataProject, columns);
        projectTable = new JTable(dtm) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false; //Disallow the editing of any cell
            }
        };

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

        TableColumn col = projectTable.getColumnModel().getColumn(3);
        int width = 50;

        col.setMaxWidth(width);
        col.setMinWidth(width);
        col.setWidth(width);
        col.setPreferredWidth(width);
        projectTable.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
                    @Override
                    public void valueChanged(ListSelectionEvent e) {
                        if (projectTable.getSelectedRow() != -1) {
                            firePropertyChange(null, 0, 1);
                            String tool = (String) projectTable.getValueAt(
                                    projectTable.getSelectedRow(), 0);
                            toolField.setText(tool);
                            toolField.setToolTipText(tool);
                            currentRow = projectTable.getSelectedRow();
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
                calculateAndSetRowHeigth(projectTable, project);
            }

            @Override
            public void componentShown(ComponentEvent e) {
                calculateAndSetRowHeigth(projectTable, project);
            }
        });
        JPanel borderLayoutPanel = new JPanel();
        borderLayoutPanel.setLayout(new BorderLayout());
        allInOnePanel = new JTabbedPane();
        scrollPane = new JScrollPane(projectTable);
        scrollPane.getVerticalScrollBar().addAdjustmentListener(
                new AdjustmentListener() {
                    @Override
                    public void adjustmentValueChanged(AdjustmentEvent e) {
                        projectTable.repaint();
                        globalTable.repaint();
                    }
                });
        return borderLayoutPanel;
    }

    /**
     *
     * Malt den FileChooser neu, sobald der Benutzer den Scrollbalken betätigt.
     *
     * @param lComponent Der allgemeine Container, wo sich der Scrollbalken
     * befindet.
     *
     * @param lChooser FileChooser auf dem repaint aufgerufen werden soll.
     */
    private void repaintFileChooser(Component lComponent,
            final JFileChooser lChooser) {
        if (lComponent instanceof JScrollPane) {
            JScrollPane localScrollPane = (JScrollPane) lComponent;

            localScrollPane.getHorizontalScrollBar().addAdjustmentListener(
                    new AdjustmentListener() {
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
        Font fontFields = new Font(Font.DIALOG, Font.BOLD, 14);
        for (int columnIndex = 0; columnIndex < columnsCounter; columnIndex++) {

            int width = table.getColumnModel().getColumn(columnIndex).
                    getWidth();
            for (int row = 0; row < table.getRowCount(); row++) {
                JTextArea area = new JTextArea();
                if (columnIndex == 0) {
                    area.setFont(fontFields);
                }
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
                    localRowHeight[row] = height;
                } else {

                    if (height < localRowHeight[row]) {
                        table.setRowHeight(row, localRowHeight[row]);
                    } else {
                        table.setRowHeight(row, height);
                        localRowHeight[row] = height;
                    }
                }
            }
        }

        if (tableSort == project) {
            rowsHeightProject = localRowHeight;
        } else {
            rowsHeightGlobal = localRowHeight;
        }

    }

    /**
     * Setzt die Tools, welche angezeigt werden müssen.
     *
     * @param toolLocation Ort, wo die Tools sich befindet.
     */
    protected void setToolsToChoose(ToolType toolLocation) {
        toolField.setText("");
        this.firePropertyChange("insert", 0, 1);
        constraintsForAllPanels.weighty = 2.3;
        constraintsForAllPanels.weightx = 2.3;
        constraintsForAllPanels.gridy = 1;
        switch (toolLocation) {
            case PROJECT:
                projectTable.clearSelection();
                globalTable.clearSelection();
                break;
            case GLOBAL:
                globalTable.clearSelection();
                projectTable.clearSelection();
                break;
            case USER_PROVIDED:
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

    /**
     * Feuert ein Event, wenn etwas in das Formular fuer das lokale Tool
     * eingegeben wird.
     *
     * @param e Event.
     */
    @Override
    public void insertUpdate(DocumentEvent e) {
        if (this.getToolType() == ToolType.USER_PROVIDED) {
            this.firePropertyChange(null, 0, 1);
        }
    }

    /**
     * Feuert ein Event, wenn etwas aus dem Formular des lokalen Tools geloescht
     * wird.
     *
     * @param e Event.
     */
    @Override
    public void removeUpdate(DocumentEvent e) {
        if (this.getToolType() == ToolType.USER_PROVIDED) {
            this.firePropertyChange(null, 0, 1);
        }
    }

    /**
     * Feuert ein Event, wenn etwas veraendert wird in dem Formular.
     *
     * @param e Event.
     */
    @Override
    public void changedUpdate(DocumentEvent e) {
    }

    /**
     * Gibt die derzeitig ausgewaehlte Zeile wieder.
     *
     * @return the currentRow
     */
    public int getCurrentRow() {
        return currentRow;
    }
}
