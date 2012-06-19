
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.wizard.configurations.action;

//~--- non-JDK imports --------------------------------------------------------
import de.cebitec.mgx.gui.datamodel.DirEntry;
import de.cebitec.mgx.gui.datamodel.JobParameter;
import de.cebitec.mgx.gui.datamodel.Tool;
import de.cebitec.mgx.gui.wizard.configurations.data.impl.ConfigItem;
import de.cebitec.mgx.gui.wizard.configurations.data.impl.Node;
import de.cebitec.mgx.gui.wizard.configurations.data.impl.Store;
import de.cebitec.mgx.gui.wizard.configurations.menu.MenuController;
import de.cebitec.mgx.gui.wizard.configurations.menu.MenuView;
import de.cebitec.mgx.gui.wizard.configurations.progressScreen.ProgressBar;
import de.cebitec.mgx.gui.wizard.configurations.start.ToolViewController;
import de.cebitec.mgx.gui.wizard.configurations.summary.MenuSummaryController;
import de.cebitec.mgx.gui.wizard.configurations.utilities.ActionCommands;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.*;
import java.text.MessageFormat;
import java.util.*;
import java.util.logging.Logger;
import javax.swing.*;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.WizardDescriptor;

//An example action demonstrating how the wizard could be called from within
//your code. You can move the code below wherever you need, or register an action:
//@ActionID(category = "test2",
//id = "de.wizard.view.ViewWizardAction")
//@ActionRegistration(displayName = "Open View Wizard")
//@ActionReference(path = "Menu/Tools")
public final class MenuAction implements ActionListener {

    private final static Logger LOGGER =
            Logger.getLogger(MenuAction.class.getName());
    /**
     * Der Store in dem die konfigurierbaren Nodes enthalten sind.
     */
    private Store store;
    /**
     * In diesem Parser werden die XML Dateien bearbeitet.
     */
    //private Parser parser;
    private JButton button;
    private HashMap<String, Tool> tools;
    private List<Tool> globalTools;
    private List<Tool> projectTools;

    /**
     * Der Konstruktor, der für die initialisation des Parsers verantwortlich
     * ist.
     */
    public MenuAction() {
        //parser = Lookup.getDefault().lookup(Parser.class);
    }
    boolean showTools;

    public void startFileChooser(List<DirEntry> entries) {
//        FileChooserTest test = new FileChooserTest(entries);
    }

    public void setTools(Collection<Tool> lGlobalTools, List<Tool> lProjectTools) {
        showTools = true;

        tools = new HashMap<String, Tool>();

        projectTools = lProjectTools;
        // store = Transform.getFromJobParameterNodeStore(lParameter);
        globalTools = new ArrayList<>();

        for (Tool tool : lGlobalTools) {

            tools.put("Global-Tools (Server)" + ";" + tool.getId(), tool);
            globalTools.add(tool);

        }
        for (Tool tool : lProjectTools) {

            tools.put("Project-Tools" + ";" + tool.getId(), tool);
        }
    }

    public void setStore(Store lStore) {
        LOGGER.info("setStore");
        showTools = false;
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
//                jobParameter.setChoices(configItem.getChoice().getChoices());
            }
        }



    }

    /**
     * Sobald der Wizard gestartet wird, wird diese Methode aufgerufen.
     *
     *
     * @param e Das Event zum starten des Wizards
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        if (showTools) {
            showTools();
        } else {
            showConfigurations();
        }
    }

    private void showTools() {
        List<WizardDescriptor.Panel<WizardDescriptor>> panels =
                new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();

        panels.add(new ToolViewController(globalTools, projectTools));


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

        Object[] optionButtons = {WizardDescriptor.PREVIOUS_OPTION, WizardDescriptor.FINISH_OPTION,
            WizardDescriptor.CANCEL_OPTION};

        wiz.setOptions(optionButtons);
        Object[] objects = wiz.getOptions();
        JButton button = (JButton) objects[1];
        button.setText("Next >");
        button.setMnemonic(KeyEvent.VK_N);
        wiz.setTitleFormat(new MessageFormat("{0}"));
        wiz.setTitle("Configurations");

        this.wiz = wiz;
    }
    int status = 0;
    public static final int cancel = 0;
    public static final int finish = 1;
    public static final int again = 2;
    private List<DirEntry> entries;

    private void showConfigurations() {
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
        setAllDefaultbutton.setToolTipText("Sets available default values.");
        setAllDefaultbutton.setActionCommand(ActionCommands.Default);


        JButton chooseToolButton = new JButton("Choose a Tool");
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
                        JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
                if (value == JOptionPane.YES_OPTION) {
                    status = again;
                    wiz.doCancelClick();
                } else if (value == JOptionPane.NO_OPTION) {
                }
            }
        });

        Object[] optionButtons = {chooseToolButton, setAllDefaultbutton,
            WizardDescriptor.PREVIOUS_OPTION, WizardDescriptor.NEXT_OPTION,
            WizardDescriptor.FINISH_OPTION, WizardDescriptor.CANCEL_OPTION};

        wiz.setOptions(optionButtons);
        wiz.setTitleFormat(new MessageFormat("{0}"));
        wiz.setTitle("Configurations");

        Object[] objects = wiz.getOptions();
        button = new JButton();

        Action previousClickAction = new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                wiz.doPreviousClick();
            }
        };
        button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F2"),
                "PreviousKey");
        button.getActionMap().put("PreviousKey",
                previousClickAction);
        //  startWizardConfigurations(wiz);
        this.wiz = wiz;

    }
    WizardDescriptor wiz;
    ProgressBar bar;

    public Tool startWizardTools() {
        LOGGER.info("Tools start.");

        if (DialogDisplayer.getDefault().notify(wiz)
                == WizardDescriptor.FINISH_OPTION) {
//            bar = new ProgressBar("", 200, 100);
//            ProgressScreen progress = new ProgressScreen();
            if (((String) (wiz.getProperty("TOOL"))).startsWith("Local-Tools")) {
                LOGGER.info("Local-Tools");
                String[] stringArray = ((String) (wiz.getProperty("TOOL"))).split(";");
                String url = stringArray[1];
                Tool tool = new Tool();
                LOGGER.info("Local-Tools Path: " + url);
                tool.setXMLFile(readFile(url));
                tool.setAuthor((String) (wiz.getProperty("LOCALAUTHOR")));
                tool.setDescription((String) (wiz.getProperty("LOCALDESCRIPTION")));
                tool.setName((String) (wiz.getProperty("LOCALNAME")));
                tool.setId(-1);
                tool.setUrl(url);
                tool.setVersion(Float.parseFloat((String) (wiz.getProperty("LOCALVERSION"))));
                return tool;
            }
            Tool tool = tools.get(wiz.getProperty("TOOL"));
            LOGGER.info("TOOLName " + tool.getName());
            LOGGER.info("TOOLAuthor " + tool.getAuthor());
            LOGGER.info("TOOLDescription " + tool.getDescription());
            LOGGER.info("TOOLId " + tool.getId());
            return tool;
        }
        return null;
    }

    public Store startWizardConfigurations() {
        LOGGER.info("Configurations start");
        bar.dispose();
        Object object = DialogDisplayer.getDefault().notify(wiz);
        if (object
                == WizardDescriptor.FINISH_OPTION) {
            status = finish;
            removeNodesAndConfigItems(wiz);
            return store;
        }
        return null;
    }

    public int getStatus() {
        return status;
    }

    private String readFile(String path) {

        // Erzeuge ein File-Objekt
        File file = new File(path);
        String content = "";
        try {
            // FileReader zum Lesen aus Datei
            FileReader fr = new FileReader(file);

            // Der String, der am Ende ausgegeben wird


            // char-Array als Puffer fuer das Lesen. Die
            // Laenge ergibt sich aus der Groesse der Datei
            char[] temp = new char[(int) file.length()];

            // Lesevorgang
            fr.read(temp);

            // Umwandlung des char-Arrays in einen String
            content = new String(temp);

            //Ausgabe des Strings
//          System.out.println(content);

            // Ressourcen freigeben
            fr.close();
        } catch (FileNotFoundException e1) {
            // die Datei existiert nicht
            System.err.println("File not Found: "
                    + file);
        } catch (IOException e2) {
            // andere IOExceptions abfangen.
            e2.printStackTrace();
        }
        return content;
    }

    /**
     * Nach dem Beenden des Wizards werden, alle Antworten in den Store zu den
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

                store.getNode(nodeId).getConfigItem(configItemName).setAnswer(localProperty);
            }
        }
        store.deleteEmptyNodes();
        test(lWiz);
    }

    private void test(WizardDescriptor wiz) {
        LOGGER.info("StoreSize After: "
                + Integer.toString(store.storeSize()));

        ArrayList<ArrayList<String>> propertys =
                new ArrayList<ArrayList<String>>();


        HashMap<String, HashMap<String, String>> nodeMap = store.getAllAnswers();

        for (String nodeKey : nodeMap.keySet()) {

            ArrayList<String> property = new ArrayList<String>();

            HashMap<String, String> map = nodeMap.get(nodeKey);

            for (String s : map.keySet()) {
                property.add(map.get(s));
            }

            DialogDisplayer.getDefault().notify(
                    new NotifyDescriptor.Message(
                    store.getNode(nodeKey).getDisplayName() + " "
                    + property));
        }
    }

    /**
     * @param entries the entries to set
     */
    public void setEntries(List<DirEntry> entries) {
        this.entries = entries;
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
