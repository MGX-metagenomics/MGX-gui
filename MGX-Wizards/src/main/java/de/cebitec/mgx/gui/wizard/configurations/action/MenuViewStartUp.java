/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.wizard.configurations.action;

import de.cebitec.mgx.gui.datamodel.DirEntry;
import de.cebitec.mgx.gui.wizard.configurations.data.impl.ConfigItem;
import de.cebitec.mgx.gui.wizard.configurations.data.impl.Node;
import de.cebitec.mgx.gui.wizard.configurations.data.impl.Store;
import de.cebitec.mgx.gui.wizard.configurations.menu.MenuController;
import de.cebitec.mgx.gui.wizard.configurations.summary.MenuSummaryController;
import de.cebitec.mgx.gui.wizard.configurations.utilities.ActionCommands;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.text.MessageFormat;
import java.util.*;
import java.util.logging.Logger;
import javax.swing.*;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;

/**
 *
 * @author pbelmann
 */
public class MenuViewStartUp {

    /**
     * Der Store in dem die konfigurierbaren Nodes enthalten sind.
     */
    private Store store;
    /**
     * .
     */
    /**
     * Logger fuer die Ausgaben.
     */
    private final static Logger LOGGER =
            Logger.getLogger(MenuViewStartUp.class.getName());
    /**
     * Der WizardDescriptor fuer die Anzeige.
     */
    private WizardDescriptor wiz;
    /**
     * Speichert den Status des Wizards.
     */
    private int status = 0;
    /**
     * Wizard wurde Abgebrochen.
     */
    private static final int cancel = 0;
    /**
     * Wizard wird beendet.
     */
    private static final int finish = 1;
    /**
     * Der Tool Wizard muss nochmal gestartet werden.
     */
    private static final int again = 2;
    /**
     * Dateieintraete in dem Projekt.
     */
    private List<DirEntry> entries;
    /**
     * Der Name des Tools.
     */
    private String toolName;

    /**
     * Konstruktor fuer den Menu start.
     *
     * @param lStore Store mit den moeglichen Paramtern.
     * @param entries Eintraege der Dateien im Projekt.
     */
    protected MenuViewStartUp(Store lStore, List<DirEntry> lEntries, String lToolName) {
        this.entries = lEntries;
        LOGGER.info("setStore");
        toolName = lToolName;
        this.store = lStore;


        Iterator nodeIterator = store.getIterator();
        Map.Entry nodeME;
        String nodeId;
        Iterator configItemIterator;
        while (nodeIterator.hasNext()) {

            nodeME = (Map.Entry) nodeIterator.next();
            nodeId = (String) nodeME.getKey();
            Node node = (Node) nodeME.getValue();
            configItemIterator = (node).getIterator();
            Map.Entry configItemME;


            String configItemName;
            LOGGER.info("NodeId: " + nodeId);
            LOGGER.info(node.getDisplayName());
            while (configItemIterator.hasNext()) {

                configItemME = (Map.Entry) configItemIterator.next();
                ConfigItem configItem = (ConfigItem) configItemME.getValue();
                configItemName = (String) configItemME.getKey();


                LOGGER.info(nodeId + " "
                        + configItemName);
                LOGGER.info(nodeId + " " + configItem.getAnswer());
                LOGGER.info(nodeId + " " + node.getClassName());
                LOGGER.info(nodeId + " " + configItem.getDefaultValue());
                LOGGER.info(nodeId + " " + Boolean.toString(configItem.isOptional()));
                LOGGER.info(nodeId + " " + configItem.getConfigType());
                LOGGER.info(nodeId + " " + configItem.getUserDescription());
                LOGGER.info(nodeId + " " + configItem.getUserName());
            }
        }
    }

    /**
     * Startet den Wizard.
     *
     * @return Den Store, der alle moeglichen Parameter enthaelt.
     */
    protected Store startWizardConfigurations() {
        LOGGER.info("Configurations start");

        Object object = DialogDisplayer.getDefault().notify(wiz);
        if (object
                == WizardDescriptor.FINISH_OPTION) {
            status = finish;
            removeNodesAndConfigItems(wiz);
            return store;
        }
        return null;
    }

    /**
     * Gibt den Status des Menus wieder: Moegliche Eingaben:
     *
     * 0 Bei Cancel. 1 Bei finish. 2 Bei nochmaliger Anzeige der Tools.
     *
     * @return Status
     */
    public int getStatus() {
        return status;
    }

    /**
     * Initialisiert den Wizard und stellt ihn ein.
     */
    protected void initializeWizard() {
        status = 0;
        List<WizardDescriptor.Panel<WizardDescriptor>> panels =
                new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();

        Iterator iterator =
                store.getIterator();

        Map.Entry me;
        int nodeIndex = 1;

        while (iterator.hasNext()) {
            me = (Map.Entry) iterator.next();
            panels.add(
                    new MenuController(nodeIndex,
                    store.getNode((String) me.getKey()), entries));
            nodeIndex++;
        }
        panels.add(new MenuSummaryController(store));


        String[] steps = new String[panels.size()];

        for (int i = 0; i < panels.size(); i++) {
            Component c = panels.get(i).getComponent();

            steps[i] = c.getName();

            if (c instanceof JComponent) {
                JComponent jc = (JComponent) c;
                jc.putClientProperty(
                        WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, i);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, steps);
                jc.putClientProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE,
                        true);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED,
                        true);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_NUMBERED,
                        true);
            }
        }

        final WizardDescriptor wiz =
                new WizardDescriptor(
                new WizardDescriptor.ArrayIterator<WizardDescriptor>(panels));

        JButton setAllDefaultbutton = new JButton("Set default");
        setAllDefaultbutton.setMnemonic(KeyEvent.VK_S);
        setAllDefaultbutton.setToolTipText("Sets available default values.");
        setAllDefaultbutton.setActionCommand(ActionCommands.Default);


        JButton chooseToolButton = new JButton("Choose a Tool");
        chooseToolButton.setMnemonic(KeyEvent.VK_T);
        chooseToolButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Object[] options = {"Yes",
                    "No",};
                int value = JOptionPane.showOptionDialog(null,
                        "Do you really want to close the Configuration window "
                        + "and return back to the tool selection window?\n"
                        + "Already entered parameters will be deleted.", "",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null,
                        options, options[1]);
                if (value == JOptionPane.YES_OPTION) {
                    status = again;
                    wiz.doCancelClick();
                } else if (value == JOptionPane.NO_OPTION) {
                }
            }
        });


        JButton buttonCancel = new JButton("Cancel");
        buttonCancel.setMnemonic(KeyEvent.VK_C);
        buttonCancel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                Object[] options = {"Yes",
                    "No",};
                int value = JOptionPane.showOptionDialog(null,
                        "If you Cancel this Menu, already "
                        + "entered parameters will be deleted.", "",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null,
                        options, options[1]);
                if (value == JOptionPane.YES_OPTION) {

                    wiz.doCancelClick();

                } else {
                }
            }
        });


        Object[] optionButtons = {chooseToolButton, setAllDefaultbutton,
            WizardDescriptor.PREVIOUS_OPTION, WizardDescriptor.NEXT_OPTION,
            WizardDescriptor.FINISH_OPTION, buttonCancel};

        wiz.setOptions(optionButtons);

        Object[] objects = wiz.getOptions();
        JButton buttonNext = (JButton) objects[3];
        LOGGER.info(buttonNext.getName());
        buttonNext.setMnemonic(KeyEvent.VK_N);

        wiz.setTitleFormat(new MessageFormat("{0}"));
        wiz.setTitle("Configuration of the tool: \"" + toolName + "\" ");
        this.wiz = wiz;
    }

    /**
     * Nach dem Beenden des Wizards werden alle Antworten in den Store zu den
     * jeweiligen Nodes und ConfigItems gegeben und dann alle Nodes und
     * configItems entfernt, die keine Antworten des Users enthalten.
     *
     * @param lWiz WizardDescriptor
     */
    private void removeNodesAndConfigItems(WizardDescriptor lWiz) {


        LOGGER.info("StoreSize: " + Integer.toString(store.storeSize()));
        Iterator nodeIterator = store.getIterator();
        Map.Entry nodeME;
        String nodeId;
        Iterator configItemIterator;
        while (nodeIterator.hasNext()) {



            nodeME = (Map.Entry) nodeIterator.next();
            nodeId = (String) nodeME.getKey();
            configItemIterator = ((Node) nodeME.getValue()).getIterator();
            Map.Entry configItemME;

            String localProperty;
            String configItemName;
            String nodeClassName;

            while (configItemIterator.hasNext()) {

                configItemME = (Map.Entry) configItemIterator.next();
                configItemName = (String) configItemME.getKey();
                nodeClassName = store.getNode(nodeId).getClassName();


                localProperty =
                        (String) lWiz.getProperty(
                        nodeId
                        + nodeClassName
                        + configItemName);

                store.getNode(nodeId).getConfigItem(configItemName).
                        setAnswer(localProperty);
            }
        }
        store.deleteEmptyNodes();
    }
}
