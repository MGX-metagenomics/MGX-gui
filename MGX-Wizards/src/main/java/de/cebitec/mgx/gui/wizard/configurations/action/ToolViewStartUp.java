/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.wizard.configurations.action;

import de.cebitec.mgx.gui.datamodel.Tool;
import de.cebitec.mgx.gui.wizard.configurations.start.ToolViewController;
import de.cebitec.mgx.gui.wizard.configurations.utilities.ActionCommands;
import de.cebitec.mgx.gui.wizard.configurations.utilities.Util;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JComponent;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.util.Exceptions;

/**
 * Klasse die die verschiedenen Tools anzeigt.
 *
 *
 * @author pbelmann
 */
public class ToolViewStartUp {

    /**
     * Alle Tools werden hier abgespeichert.
     */
    private HashMap<String, Tool> tools;
    /**
     * Alle Tools die auf dem Server liegen.
     */
    private List<Tool> globalTools;
    /**
     * Alle Tools die sich im Projekt befinden.
     */
    private List<Tool> projectTools;
    /**
     * Logger fuer die Ausgaben.
     */
    private final static Logger LOGGER =
            Logger.getLogger(ToolViewStartUp.class.getName());
    /**
     * WizardDescriptor fuer das Menu.
     */
    private WizardDescriptor wiz;

    /**
     * Konstruktor um die Tools zu verarbeiten.
     *
     * @param lGlobalTools Tools vom Server.
     * @param lProjectTools Tools vom Projekt.
     */
    public ToolViewStartUp(Collection<Tool> lGlobalTools,
            List<Tool> lProjectTools) {
        isDelete = false;
        tools = new HashMap<String, Tool>();
        projectTools = lProjectTools;
        globalTools = new ArrayList<>();

        for (Tool tool : lGlobalTools) {

            tools.put(ActionCommands.Global + ";" + tool.getId(), tool);
            globalTools.add(tool);

        }
        for (Tool tool : lProjectTools) {

            tools.put(ActionCommands.Project + ";" + tool.getId(), tool);
        }
    }

    /**
     * Initialisiert die Ansicht fuer die einzelnen Tools.
     */
    protected void initializeToolsView() {
        isDelete = false;
        List<WizardDescriptor.Panel<WizardDescriptor>> panels =
                new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();

        panels.add(new ToolViewController(globalTools, projectTools));


        for (int i = 0; i < panels.size(); i++) {
            Component c = panels.get(i).getComponent();

            if (c instanceof JComponent) {
                JComponent jc = (JComponent) c;
                jc.putClientProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE,
                        true);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED,
                        true);
            }
        }

        final WizardDescriptor wiz =
                new WizardDescriptor(
                new WizardDescriptor.ArrayIterator<WizardDescriptor>(panels));

        JButton delete = new JButton("Delete");
        delete.setMnemonic(KeyEvent.VK_D);

        Object[] optionButtons = {delete, WizardDescriptor.PREVIOUS_OPTION,
            WizardDescriptor.FINISH_OPTION,
            WizardDescriptor.CANCEL_OPTION};

        wiz.setOptions(optionButtons);
        Object[] objects = wiz.getOptions();
        JButton buttonNext = (JButton) objects[2];
        buttonNext.setText("Next >");
        buttonNext.setMnemonic(KeyEvent.VK_N);
        JButton buttonCancel = (JButton) objects[3];
        buttonCancel.setMnemonic(KeyEvent.VK_C);



        wiz.setTitleFormat(new MessageFormat("{0}"));
        wiz.setTitle("Tool Overview");
        this.wiz = wiz;
    }
    private boolean isDelete;

    public boolean isDelete() {
        return isDelete;

    }
    String toolType = null;

    public String getLastToolType() {
        if (toolType == null) {
            return "";
        } else {
            return toolType;
        }
    }

    /**
     * Startet die Anzeige fuer die verschiedenen Tools.
     *
     * @return Tool.
     */
    public Tool startWizardTools() {
        LOGGER.info("Tools start.");

        if (DialogDisplayer.getDefault().notify(wiz)
                == WizardDescriptor.FINISH_OPTION) {
            String[] temp = ((String) wiz.getProperty("TOOL")).split(";");
            toolType = temp[0];
            if (wiz.getProperty("DELETE") != null) {
                Tool tool = tools.get(wiz.getProperty("DELETE"));
                isDelete = true;
                return tool;

            } else if (((String) (wiz.getProperty("TOOL"))).startsWith("Local-Tools")) {
                LOGGER.info("Local-Tools");
              
                String[] stringArray =
                        ((String) (wiz.getProperty("TOOL"))).split(";");
                String url = stringArray[1];
                Tool tool = new Tool();
                LOGGER.info("Local-Tools Path: " + url);
                String fileContent = null;
                try {
                    fileContent = Util.readFile(url);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
                tool.setXMLFile(fileContent);
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
}
