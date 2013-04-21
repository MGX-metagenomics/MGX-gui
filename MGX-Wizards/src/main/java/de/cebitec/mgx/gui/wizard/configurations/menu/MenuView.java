package de.cebitec.mgx.gui.wizard.configurations.menu;

import de.cebitec.mgx.gui.datamodel.MGXFile;
import de.cebitec.mgx.gui.wizard.configurations.messages.Messages;
import de.cebitec.mgx.gui.wizard.configurations.renderer.MenuComboBoxRenderer;
import de.cebitec.mgx.gui.wizard.configurations.utilities.ActionCommands;
import de.cebitec.mgx.gui.wizard.configurations.utilities.ProjectFileSystemView;
import de.cebitec.mgx.gui.wizard.configurations.utilities.Types;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * Diese Klasse ist für sämtliche Panels außer der Summary verantwortlich.
 *
 * @author belmann
 */
public final class MenuView extends JPanel implements DocumentListener {

    /**
     * Der Name des Panels bzw. Node.
     */
    private String displayName;
    /**
     * HashMap um zum passenden ConfigItem den passenden JFileChooser
     * aufzurufen.
     */
    private HashMap<Integer, JFileChooser> chooser;
    /**
     * FileField zum anzeigen des DateiPfads.
     *
     */
    private JTextField fileField;
    /**
     * Komponentent wie JTextFields, JRadioGroups und andere, werden in dieser
     * ArrayList abgespeichert.
     *
     */
    private ArrayList allComponentsList;
    /**
     * Die Default Werte werden in dieser ArrayList abgespeichert.
     */
    private ArrayList<String> defaultValues;
    /**
     * JLabel für die einzelnen Aufforderungen an den Benutzer etwas einzugeben.
     *
     */
    private JLabel messageLabel;
    /**
     * Der ActionListener für die Buttons.
     */
    private ActionListener listener;
    
    /**
     * Dateien die sich im Projekt befinden.
     */
    private Iterator<MGXFile> entries;

    /**
     * Konstruktor für die Übergabe der
     *
     *
     * @param lFieldCounter Gibt an, welche Nummer das Panel bzw der Node hat.
     * @param lDisplayName Der Name des Nodes.
     * @param lUserNames Der vom Benutzer übergebene Name des Nodes.
     * @param lUserDescriptions Die Beschreibungen des Benutzers.
     * @param lConfigTypes Die Typen der ConfigItems.
     * @param lUserChoicesValues Die Auswahlwerte bei einer ComboBox.
     * @param lUserChoicesDescriptions Die Beschreibungen der Werte einer
     * ComboBox.
     * @param lDefaultValues Die Default Werte der ConfigItems.
     * @param lMandatoryComponentsCounter Die Anzahl der Pflichtfelder.
     * @param lListener
     * @param docListener
     */
    protected MenuView(int lFieldCounter, String lDisplayName,
            ArrayList<String> lUserNames, ArrayList<String> lUserDescriptions,
            ArrayList<String> lConfigTypes,
            ArrayList<ArrayList<String>> lUserChoicesValues,
            ArrayList<ArrayList<String>> lUserChoicesDescriptions,
            ArrayList<String> lDefaultValues, int lMandatoryComponentsCounter,
            ActionListener lListener,
            Iterator<MGXFile> lEntries) {

        this.entries = lEntries;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        listener = lListener;
        displayName = lDisplayName;
        allComponentsList = new ArrayList();
        defaultValues = lDefaultValues;
        radioButtons = new HashMap<>();

        setMinimumSize(new Dimension(650, 380));
        setPreferredSize(new Dimension(650, 380));
        setMaximumSize(new Dimension(650, 380));

        setUpPanelForm(lConfigTypes, lUserDescriptions, lUserNames,
                lUserChoicesDescriptions, lUserChoicesValues,
                lMandatoryComponentsCounter, lFieldCounter);
    }

    /**
     * Gibt den Namen der View zurück.
     *
     * @return Namen der View.
     */
    @Override
    public String getName() {
        return displayName;
    }

    /**
     * Hier werden alle Panels außer dem endgültigen ScrollPane zusammengefügt.
     * Diese Methode soll nur von der InitMethode aufgerufen werden.
     *
     * @param lComponentDescriptionPanel Panel in dem die Beschreibung des
     * Benutzers steht, sowie die eigentliche Componente.
     * @param lUserNames Name des ConfigItems.
     * @param configIndex Index des ConfigItems in dem Node.
     * @param lDescriptionPanel Das Panel in dem die Beschreiubng des Benutzers
     * steht.
     * @param lDescriptionLabel Das Label in dem die Beschreiubng des Benutzers
     * steht.
     * @param lStartLabel Ist startLable 0, wird eine Message an den Benutzer
     * angegeben, bei 1 nicht.
     * @param lComponentConstraints Constraints für Grid Bag mit der Komponente.
     * @param lComponentPanel Das Panel in dem die Komponente eingefügt wird.
     * @param lMandatoryComponentsCounter Anzahl an Pflichtfeldern.
     * @param lAllInOneConstraints Constraints für Grid Bag, wo alle Panels
     * eingefügt werden.
     * @param lAddToConfigIndex Falls ein Mandatory oder Optional Label kommt,
     * wird entsprechend dieser Integer gesetzt und dann in Constraints
     * eingefügt.
     * @param lRightComponentPanel Enthält die Komponente als auch die
     * Beschreiubng.
     * @param lOptionalLabel Das Label zeigt die Überschrift an, ob das
     * ConfigItem optional ist oder nicht.
     * @param lFieldCounter Gibt die Anzahl an Feldern an.
     */
    private int assemblePanels(JPanel lComponentDescriptionPanel,
            ArrayList<String> lUserNames, int configIndex,
            JPanel lDescriptionPanel, JLabel lDescriptionLabel,
            int lStartLabel, GridBagConstraints lComponentConstraints,
            JPanel lComponentPanel, int lMandatoryComponentsCounter,
            GridBagConstraints lAllInOneConstraints, int lAddToConfigIndex,
            JPanel lRightComponentPanel, JLabel lOptionalLabel, int lFieldCounter) {
        Font font = new Font(Font.DIALOG, Font.BOLD, 14);

        lComponentDescriptionPanel.setBorder(
                BorderFactory.createTitledBorder(null, lUserNames.get(configIndex),
                TitledBorder.CENTER, TitledBorder.DEFAULT_JUSTIFICATION, font,
                Color.BLACK));

        lDescriptionPanel.add(lDescriptionLabel);
        lComponentConstraints.fill = GridBagConstraints.BOTH;
        if (lStartLabel == 0) {
            lComponentConstraints.gridy = lStartLabel;
            lStartLabel++;
            lComponentConstraints.anchor = GridBagConstraints.LINE_START;
            lComponentDescriptionPanel.add(messageLabel, lComponentConstraints);
        }

        lComponentConstraints.gridy = lStartLabel;
        lStartLabel++;
        lComponentConstraints.anchor = GridBagConstraints.WEST;
        lComponentConstraints.weightx = 20;
        lComponentConstraints.weighty = 20;
        lComponentDescriptionPanel.add(lComponentPanel, lComponentConstraints);
        lComponentConstraints.gridy = lStartLabel;
        lComponentDescriptionPanel.add(lDescriptionPanel, lComponentConstraints);
        lAllInOneConstraints.anchor = GridBagConstraints.WEST;
        if (configIndex != 0 && configIndex == lMandatoryComponentsCounter
                && lMandatoryComponentsCounter != lUserNames.size()) {

            lAllInOneConstraints.gridx = 0;
            lAllInOneConstraints.gridy = configIndex + 1;
            lAllInOneConstraints.insets = new Insets(10, 0, 10, 0);
            lAddToConfigIndex++;
            lRightComponentPanel.add(lOptionalLabel, lAllInOneConstraints);
            lAllInOneConstraints.insets = new Insets(0, 0, 0, 0);
            lAllInOneConstraints.gridx = 1;
        }

        lAllInOneConstraints.gridy = configIndex + 1 + lAddToConfigIndex;
        lAllInOneConstraints.gridx = 1;
        lRightComponentPanel.add(lComponentDescriptionPanel, lAllInOneConstraints);

        JLabel numberLabel = new JLabel(lFieldCounter + "."
                + Integer.toString(configIndex + 1));
        numberLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        numberLabel.setHorizontalAlignment(SwingConstants.CENTER);
        Font numberFont = new Font(Font.DIALOG, Font.BOLD, 16);
        numberLabel.setFont(numberFont);

        lAllInOneConstraints.gridx = 0;
        lRightComponentPanel.add(numberLabel, lAllInOneConstraints);
        lAllInOneConstraints.gridx = 1;

        return lStartLabel;
    }

    /**
     * Fügt das Panel mit allen Komponenten in ein ScrollPane ein.
     *
     * @param lAllInOnePanel Diesem Panel wird das entgültige ScrollPane
     * hinzugefügt.
     * @param lRightComponentPanel Das Panel enthält das Panel mit den
     * Beschreibungen und Komponenten.
     */
    private void assemblePanelsAndScrollPane(JPanel lAllInOnePanel,
            JPanel lRightComponentPanel) {
        lAllInOnePanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        lAllInOnePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        lAllInOnePanel.setAlignmentY(Component.CENTER_ALIGNMENT);

        JScrollPane scrollPane = new JScrollPane(lRightComponentPanel);
        scrollPane.setHorizontalScrollBarPolicy(
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        scrollPane.setAlignmentY(Component.CENTER_ALIGNMENT);
        scrollPane.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        scrollPane.getVerticalScrollBar().addAdjustmentListener(
                new AdjustmentListener() {

                    @Override
                    public void adjustmentValueChanged(AdjustmentEvent e) {
                        repaint();

                    }
                });

        scrollPane.getHorizontalScrollBar().addAdjustmentListener(
                new AdjustmentListener() {

                    @Override
                    public void adjustmentValueChanged(AdjustmentEvent e) {
                        repaint();
                    }
                });

        lAllInOnePanel.add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

    /**
     * Konstruktor für die Übergabe der
     *
     *
     * @param lFieldCounter Gibt an, welche Nummer das Panel bzw der Node hat.
     * @param lUserNames Der vom Benutzer übergebene Name des Nodes.
     * @param lUserDescriptions Die Beschreibungen des Benutzers.
     * @param lConfigTypes Die Typen der ConfigItems.
     * @param lUserChoicesValues Die Auswahlwerte bei einer ComboBox.
     * @param lUserChoicesDescriptions Die Beschreibungen der Werte einer
     * ComboBox.
     * @param lMandatoryComponentsCounter Die Anzahl der Pflichtfelder.
     * @param lListener
     * @param docListener
     */
    private void setUpPanelForm(ArrayList<String> lConfigTypes,
            ArrayList<String> lUserDescriptions, ArrayList<String> lUserNames,
            ArrayList<ArrayList<String>> lUserChoiceDescriptions,
            ArrayList<ArrayList<String>> lUserChoiceValues,
            int lMandatoryComponentsCounter, int lFieldCounter) {

        int choiceIndex = 0;
        JPanel componentDescriptionPanel;


        JPanel allInOnePanel = new JPanel();
        allInOnePanel.setLayout(new BoxLayout(allInOnePanel,
                BoxLayout.PAGE_AXIS));

        JPanel rightComponentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints allInOneConstraints = new GridBagConstraints();
        allInOneConstraints.fill = GridBagConstraints.HORIZONTAL;
        allInOneConstraints.gridx = 0;

        GridBagConstraints componentConstraints = new GridBagConstraints();
        componentConstraints.gridx = 0;
        int startLabel = 1;


        int choicesCounter = 0;
        JPanel componentPanel;
        JPanel descriptionPanel;
        String defaultValue;
        int addToConfigIndex = 0;
        JLabel mandatoryLabel = new JLabel("<html><u>Required "
                + "Fields:</u></html>");
        JLabel optionalLabel = new JLabel("<html><u>Optional "
                + "Fields:</u></html>");

        allInOneConstraints.gridy = 0;
        allInOneConstraints.insets = new Insets(10, 0, 10, 0);
        Font fontFields = new Font(Font.DIALOG, Font.BOLD, 14);

        mandatoryLabel.setFont(fontFields);
        optionalLabel.setFont(fontFields);

        chooser = new HashMap<>();

        if (lMandatoryComponentsCounter == lUserNames.size()
                || lMandatoryComponentsCounter != 0) {

            rightComponentPanel.add(mandatoryLabel, allInOneConstraints);

        } else {

            rightComponentPanel.add(optionalLabel, allInOneConstraints);

        }
        allInOneConstraints.insets = new Insets(0, 0, 0, 0);
        Font orderFont = new Font(Font.DIALOG, Font.ITALIC, 14);

        for (int configIndex = 0; configIndex < lUserNames.size();
                configIndex++) {

            if (defaultValues.get(configIndex) == null) {
                defaultValue = null;
            } else {
                defaultValue = defaultValues.get(configIndex);
            }


            componentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            descriptionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            componentDescriptionPanel = new JPanel(new GridBagLayout());

            String initialText = "<html><b><i>Description: <br></i></b>";

            JLabel descriptionLabel = new JLabel(initialText
                    + "<html><table><td width=400>"
                    + lUserDescriptions.get(configIndex) + "</td></table></html>");



            if (lConfigTypes.get(configIndex).equals(Types.Boolean)) {

                initializeRadioButtons(componentPanel, configIndex,
                        lMandatoryComponentsCounter, defaultValue);

            } else if (lConfigTypes.get(configIndex).equals(Types.File)) {

                initializeFileChooser(componentPanel, orderFont, configIndex);
                startLabel = 0;

            } else if (lConfigTypes.get(configIndex).equals(Types.Selection)
                    || lConfigTypes.get(configIndex).equals(Types.Enumeration)) {

                initializeComboBox(lUserChoiceValues, choiceIndex,
                        lUserChoiceDescriptions, defaultValue,
                        choicesCounter, componentPanel, orderFont, configIndex);

                choiceIndex++;
                startLabel = 0;

            } else {
                initializeTextFieldsAreas(lConfigTypes, configIndex, orderFont,
                        componentPanel, defaultValue);
                startLabel = 0;
            }
            startLabel = assemblePanels(componentDescriptionPanel, lUserNames,
                    configIndex, descriptionPanel, descriptionLabel, startLabel,
                    componentConstraints, componentPanel,
                    lMandatoryComponentsCounter, allInOneConstraints,
                    addToConfigIndex, rightComponentPanel, optionalLabel,
                    lFieldCounter);
        }

        assemblePanelsAndScrollPane(allInOnePanel, rightComponentPanel);
        add(allInOnePanel, BorderLayout.CENTER);
    }

    /**
     * Initialisiert die FileChooser, damit der User Dateien auswählen kann.
     *
     *
     * @param lComponentPanel Panel bei dem die eigentliche
     * Komponente(JTextField, RadioGroup...) hinzu gefügt wird.
     * @param lMessageFont Die Font für die Nachricht an den Benutzer.
     *
     * @param lConfigIndex Index des Configs in dem Node
     */
    private void initializeFileChooser(JPanel lComponentPanel, Font lMessageFont,
            int lConfigIndex) {
        final JButton openButton;
        final JButton removeButton;
        fileField = new JTextField();
        fileField.setFocusable(false);
        allComponentsList.add(fileField);
        fileField.setEditable(false);
        fileField.setColumns(20);

        Boolean old = UIManager.getBoolean("FileChooser.readOnly");
        UIManager.put("FileChooser.readOnly", Boolean.TRUE);
        JFileChooser fileChooser =
                new JFileChooser(new ProjectFileSystemView(entries));
        UIManager.put("FileChooser.readOnly", old);
        textFieldFileChooser(fileChooser, fileChooser);


        chooser.put(lConfigIndex, fileChooser);
        repaintFileChooser(fileChooser, fileChooser);
        fileChooser.setAutoscrolls(false);
        openButton = new JButton("Open");
        removeButton = new JButton("Remove Selection");


        openButton.setActionCommand(ActionCommands.FileOpen + ":"
                + lConfigIndex);
        openButton.addActionListener(listener);
        removeButton.addActionListener(listener);
        removeButton.setActionCommand(ActionCommands.FileClear + ":"
                + lConfigIndex);
        lComponentPanel.add(openButton);

        lComponentPanel.add(fileField);
        lComponentPanel.add(removeButton);

        messageLabel = new JLabel("<html><table><td width=400>"
                + Messages.getInformation(Messages.File) + "</td></table></html>");
        messageLabel.setFont(lMessageFont);
    }

    /**
     * Initialisiert die JTextFields für ConfigByte, ConfigSByte, ConfigInteger,
     * ConfigLong, ConfigULong, sowie JTextAreas für ConfigString.
     *
     *
     * @param lConfigTypes Die Typen der ConfigItems.
     * @param lConfigIndex Der Index der ConfigItems
     * @param lMessageFont Der Font der Nachrichten bzw Aufforderungen an den
     * Benutzer.
     * @param lComponentPanel Panel bei dem die eigentliche
     * Komponente(JTextField, RadioGroup...) hinzu gefügt wird.
     * @param lDefaultValue Der Default Wert für dieses ConfigItem.
     */
    private void initializeTextFieldsAreas(ArrayList<String> lConfigTypes,
            int lConfigIndex, Font lMessageFont, JPanel lComponentPanel,
            String lDefaultValue) {

        JButton defaultButton;
        if (lConfigTypes.get(lConfigIndex).equals(Types.Byte)) {
            messageLabel = new JLabel("<html><table><td width=400>"
                    + Messages.getInformation(Messages.Byte)
                    + "</td></table></html>");

        } else if (lConfigTypes.get(lConfigIndex).equals(Types.Double)) {
            messageLabel = new JLabel("<html><table><td width=400>"
                    + Messages.getInformation(Messages.Double)
                    + "</td></table></html>");

        } else if (lConfigTypes.get(lConfigIndex).equals(Types.Integer)) {
            messageLabel = new JLabel("<html><table><td width=400>"
                    + Messages.getInformation(Messages.Integer)
                    + "</td></table></html>");

        } else if (lConfigTypes.get(lConfigIndex).equals(Types.Long)) {
            messageLabel = new JLabel("<html><table><td width=400>"
                    + Messages.getInformation(Messages.Long)
                    + "</td></table></html>");

        } else if (lConfigTypes.get(lConfigIndex).equals(Types.ULong)) {
            messageLabel = new JLabel("<html><table><td width=400>"
                    + Messages.getInformation(Messages.ULong)
                    + "</td></table></html>");

        } else if (lConfigTypes.get(lConfigIndex).equals(Types.SByte)) {
            messageLabel = new JLabel("<html><table><td width=400>"
                    + Messages.getInformation(Messages.SByte)
                    + "</td></table></html>");

        } else if (lConfigTypes.get(lConfigIndex).equals(Types.String)) {
            messageLabel = new JLabel("<html><table><td width=400>"
                    + Messages.getInformation(Messages.String)
                    + "</td></table></html>");
        }
        messageLabel.setFont(lMessageFont);
        if (lConfigTypes.get(lConfigIndex).equals(Types.String)) {
            JTextArea area = new JTextArea();
            area.setWrapStyleWord(true);
            area.setLineWrap(true);

            JScrollPane pane = new JScrollPane(area);
            pane.setSize(new Dimension(340, 130));
            pane.setPreferredSize(new Dimension(340, 130));
            pane.setMinimumSize(new Dimension(340, 130));
            pane.setMaximumSize(new Dimension(340, 130));

            allComponentsList.add(area);
            area.getDocument().addDocumentListener(this);
            lComponentPanel.add(pane);
            if (!lDefaultValue.isEmpty()) {
                area.setText(lDefaultValue);
                area.setToolTipText("Default: " + lDefaultValue);
                defaultButton = new JButton("Reset Default");
                defaultButton.setToolTipText("Default: " + lDefaultValue);
                defaultButton.setActionCommand(ActionCommands.Default + ":"
                        + Integer.toString(lConfigIndex));
                defaultButton.addActionListener(listener);
                lComponentPanel.add(defaultButton);
            }
        } else {
            JTextField field = new JTextField();
            allComponentsList.add(field);
            field.setColumns(15);
            field.getDocument().addDocumentListener(this);
            lComponentPanel.add(field);
            if (!lDefaultValue.isEmpty()) {
                field.setText(lDefaultValue);
                field.setToolTipText("Default: " + lDefaultValue);
                defaultButton = new JButton("Reset Default");
                defaultButton.setToolTipText("Default: " + lDefaultValue);
                defaultButton.setActionCommand(ActionCommands.Default + ":"
                        + Integer.toString(lConfigIndex));
                defaultButton.addActionListener(listener);
                lComponentPanel.add(defaultButton);
            }
        }
    }

    private void textFieldFileChooser(Component lComponent,
            final JFileChooser lChooser) {
        if (lComponent instanceof JTextField) {
            ((JTextField) lComponent).setEditable(false);

        } else if (lComponent instanceof Container) {
            Container container = (Container) lComponent;
            for (int i = 0; i < container.getComponentCount(); i++) {
                Component child = container.getComponent(i);
                textFieldFileChooser(child, lChooser);
            }
        }
    }

    /**
     *
     * Initialisiert die Enumerations und Selections.
     *
     * @param lChoiceIndex Der Index für die ArrayList von Auswahlwerten inder
     * lUserChoicesValues und l UserChoicesDescriptions
     * @param lDefaultValue Der Defaultwert des ConfigItems
     * @param lChoicesCounter Der Counter für die Elemente einer ComboBox.
     * @param lComponentPanel Das Panel zum hinzufügen der Elemente.
     * @param lMessageFont Die Font für die einzelnen Aufforderungen des
     * Benutzers.
     * @param lUserChoicesValues Die Auswahlwerte bei einer ComboBox.
     * @param lUserChoicesDescriptions Die Beschreibungen der Werte einer
     * ComboBox.
     */
    private void initializeComboBox(ArrayList<ArrayList<String>> lUserChoiceValues,
            int lChoiceIndex, ArrayList<ArrayList<String>> lUserChoiceDescriptions,
            String lDefaultValue, int lChoicesCounter, JPanel lComponentPanel,
            Font lMessageFont, int lConfigIndex) {
        JComboBox comboBox;

        String[] choicesValues = new String[lUserChoiceValues.get(lChoiceIndex).size() + 1];
        String[] choicesDescriptions = new String[lUserChoiceValues.get(lChoiceIndex).size() + 1];
        choicesValues[0] = "";
        choicesDescriptions[0] = "";
        String toolTip = "";
        int selectedIndex = 0;
        for (int j = 0; j < lUserChoiceValues.get(lChoiceIndex).size(); j++) {
            choicesValues[j + 1] = lUserChoiceValues.get(lChoiceIndex).get(j);
            choicesDescriptions[j + 1] = "("+lUserChoiceDescriptions.get(lChoiceIndex).get(j)+")";
            if (!lDefaultValue.isEmpty()) {

                toolTip = "Default: " + lDefaultValue;

                String choiceValue = lUserChoiceValues.get(lChoiceIndex).get(j);
                if (choiceValue.equals(lDefaultValue)) {
                    selectedIndex = j + 1;
                    choicesValues[selectedIndex] = choicesValues[selectedIndex]
                            + " (Default)";
                }
            } else {
                selectedIndex = 0;
            }
        }

        comboBox = new JComboBox(choicesValues);
        comboBox.setSize(250, 25);
        comboBox.setPreferredSize(new Dimension(250, 25));
        comboBox.setMaximumSize(new Dimension(250, 25));
        comboBox.setRenderer(new MenuComboBoxRenderer(choicesDescriptions));
        comboBox.setActionCommand(ActionCommands.Box);
        comboBox.addActionListener(listener);
        allComponentsList.add(comboBox);
        lChoicesCounter++;
        lComponentPanel.add(comboBox);
        if (!lDefaultValue.isEmpty()) {
            comboBox.setToolTipText(toolTip);
            comboBox.setSelectedIndex(selectedIndex);
            JButton defaultButton = new JButton("Reset Default");
            defaultButton.setToolTipText("Default: " + lDefaultValue);
            defaultButton.setActionCommand(ActionCommands.Default + ":"
                    + Integer.toString(lConfigIndex));
            defaultButton.addActionListener(listener);
            lComponentPanel.add(defaultButton);
        }
        messageLabel = new JLabel("<html><table><td width=400>"
                + Messages.getInformation(Messages.Enumeration)
                + "</td></table></html>");
        messageLabel.setFont(lMessageFont);
    }

    /**
     *
     * Erstellt das Panel mit den RadioButtons
     *
     * @param lComponentPanel Das Panel zum hinzufügen der Elemente.
     * @param lConfigIndex Der Index des ConfigItems.
     * @param lMandatoryComponentsCounter Anzahl der Pflichtfelder für diesen
     * Node.
     * @param lDefaultValue Der Defaultwert für dieses ConfigItem.
     */
    private void initializeRadioButtons(JPanel lComponentPanel, int lConfigIndex,
            int lMandatoryComponentsCounter, String lDefaultValue) {

        ArrayList<JRadioButton> radioGroup = new ArrayList<>();
        JRadioButton radioTrue;
        JRadioButton radioFalse;
        JRadioButton radioNothing;
        radioTrue = new JRadioButton("Yes");
        radioTrue.setActionCommand(ActionCommands.RadioTrue);
        radioTrue.addActionListener(listener);
        radioGroup.add(radioTrue);
        radioFalse = new JRadioButton("No");
        radioFalse.setActionCommand(ActionCommands.RadioFalse);
        radioFalse.addActionListener(listener);
        radioGroup.add(radioFalse);
        ButtonGroup radios = new ButtonGroup();
        radios.add(radioTrue);
        radios.add(radioFalse);

        radioButtons.put(lConfigIndex, radioGroup);
        allComponentsList.add(radios);

        lComponentPanel.add(radioTrue);
        lComponentPanel.add(radioFalse);
        radioNothing = new JRadioButton("Nothing");
        radioNothing.setActionCommand(ActionCommands.RadioNothing);


        if (lConfigIndex >= lMandatoryComponentsCounter) {
            radios.add(radioNothing);
            lComponentPanel.add(radioNothing);
        }
        if (!lDefaultValue.isEmpty()) {
            if (lDefaultValue.equals("True")) {
                radioTrue.setSelected(true);
                radioTrue.setText("Yes (default)");
            } else {
                radioFalse.setSelected(true);
                radioFalse.setText("No (default)");
            }
        }
    }
    HashMap<Integer, ArrayList<JRadioButton>> radioButtons;

    /**
     * Gibt die eingegebenen Parameters des Benutzers wieder.
     *
     * @param lIndex
     * @return Die Antwort in einem String.
     */
    protected String getInput(int lIndex) {
        Object selected = allComponentsList.get(lIndex);
        
        if (selected instanceof ButtonGroup) {
            ButtonGroup buttonGroup = ((ButtonGroup) selected);
            if (buttonGroup.getSelection() == null || 
                    buttonGroup.getSelection().getActionCommand().
                    equals(ActionCommands.RadioNothing)) {
                return "";
            }
            return buttonGroup.getSelection().getActionCommand().toString();
        } else if (selected instanceof JComboBox) {
            JComboBox comboBox = ((JComboBox) selected);
            return comboBox.getSelectedItem().toString();
        } else if (selected instanceof JTextArea) {
            return ((JTextArea) selected).getText().trim();
        } else if (selected instanceof JTextField) {
            return ((JTextField) allComponentsList.get(lIndex)).getText().trim();
        } else {
            assert false;
        }
        
        // not reached
        return "";
    }

    /**
     * Öffnet und verwaltet den FileChooser, sobald der Benutzer den open Button
     * anklickt.
     *
     * @param lIndex Index des FileFields
     */
    protected void chooseFile(int lIndex) {

        if (allComponentsList.get(lIndex) instanceof JTextField) {
            int returnVal;
            JTextField filedField = ((JTextField) allComponentsList.get(lIndex));

            Boolean old = UIManager.getBoolean("FileChooser.readOnly");
            UIManager.put("FileChooser.readOnly", Boolean.TRUE);
            JFileChooser fileChooser = chooser.get(lIndex);
            UIManager.put("FileChooser.readOnly", old);

            if (filedField.getText().isEmpty()) {

                UIManager.put("FileChooser.readOnly", Boolean.TRUE);
                fileChooser = new JFileChooser(new ProjectFileSystemView(entries));
                UIManager.put("FileChooser.readOnly", old);
                repaintFileChooser(fileChooser, fileChooser);
                returnVal = fileChooser.showOpenDialog(this);

            } else {
                returnVal = fileChooser.showOpenDialog(this);
            }

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                ((JTextField) allComponentsList.get(lIndex)).setText(
                        file.getPath() + "." + "\n");
                ((JTextField) allComponentsList.get(lIndex)).setToolTipText(
                        file.getPath());
                firePropertyChange(null, 0, 1);
                chooser.put(lIndex, fileChooser);
            }
        }
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
            JScrollPane scrollPane = (JScrollPane) lComponent;

            scrollPane.getHorizontalScrollBar().addAdjustmentListener(
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
     * Resetet den FileChooser und das dazugehörige TextField
     *
     * @param lIndex Index des FileFields
     *
     */
    public void resetFileFieldText(int lIndex) {
        if (allComponentsList.get(lIndex) instanceof JTextField) {

            ((JTextField) allComponentsList.get(lIndex)).setText("");
            ((JTextField) allComponentsList.get(lIndex)).setToolTipText("");

            chooser.get(lIndex).cancelSelection();
        }
    }

    /**
     * Insert Update, für das eingeben in ein JTextField. Es wird der Controller
     * mit einem PropertyChangeEvent informiert, um zu überprüfen, ob das
     * eingegebene korrekt ist.
     *
     * @param lE DocumentEvent
     */
    @Override
    public void insertUpdate(DocumentEvent lE) {
        this.firePropertyChange(null, 0, 1);
    }

    /**
     * removeUpdate für das Löschen in einem JTextfield. Es wird der Controller
     * mit einem PropertyChangeEvent informiert, um zu überprüfen, ob das
     * eingegebene korrekt ist.
     *
     * @param lE DocumentEvent
     */
    @Override
    public void removeUpdate(DocumentEvent lE) {
        this.firePropertyChange(null, 0, 1);
    }

    /**
     * ChangeUpdate für das verändern bzw. ersetzen von Symbolen in einem
     * JTextfield. Es wird der Controller mit einem PropertyChangeEvent
     * informiert, um zu überprüfen, ob das eingegebene korrekt ist.
     *
     * @param lE DocumentEvent
     */
    @Override
    public void changedUpdate(DocumentEvent lE) {
        this.firePropertyChange(null, 0, 1);
    }

    /**
     * Setzt den Default für den Index in einer ComponentsArrayList.
     *
     * @param lIndex Index des ConfigItems.
     */
    protected void setDefault(int lIndex) {
        if (allComponentsList.get(lIndex) instanceof JTextField) {
            ((JTextField) allComponentsList.get(lIndex)).setText(
                    defaultValues.get(lIndex));
        } else if (allComponentsList.get(lIndex) instanceof JTextArea) {
            ((JTextArea) allComponentsList.get(lIndex)).setText(
                    defaultValues.get(lIndex));
        } else if (allComponentsList.get(lIndex) instanceof ButtonGroup) {

            ((ButtonGroup) allComponentsList.get(lIndex)).clearSelection();

            if (defaultValues.get(lIndex).equalsIgnoreCase(
                    ActionCommands.RadioTrue)) {

                radioButtons.get(lIndex).get(0).setSelected(true);

            } else if (defaultValues.get(lIndex).equalsIgnoreCase(
                    ActionCommands.RadioFalse)) {

                radioButtons.get(lIndex).get(1).setSelected(true);

            }
        } else if (allComponentsList.get(lIndex) instanceof JComboBox) {
            ((JComboBox) allComponentsList.get(lIndex)).setSelectedItem(
                    defaultValues.get(lIndex));
        }
    }
}
