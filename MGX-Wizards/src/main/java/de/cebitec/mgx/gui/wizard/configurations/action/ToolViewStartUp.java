package de.cebitec.mgx.gui.wizard.configurations.action;

import de.cebitec.mgx.gui.datamodel.Tool;
import de.cebitec.mgx.gui.datamodel.misc.ToolType;
import de.cebitec.mgx.gui.wizard.configurations.start.ToolViewController;
import de.cebitec.mgx.gui.wizard.configurations.utilities.ActionCommands;
import de.cebitec.mgx.gui.wizard.configurations.utilities.Util;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
     * Speichert alle Tools. Schluessel ist hierbei der ToolTyp. Er gibt eine Map
     * von Projekt Tools und Server Tools
     * Die innere Map hat die Id des Tools als Schluessels und als Value das 
     * Tool. 
     */
    private HashMap<ToolType, Map<Long, Tool>> tools;
    
    /**
     * Der Descriptor fuer die Anzeige der Tools;
     */
    private WizardDescriptor wiz;
    
    /**
     * Flag um festzustellen, ob das Tool geloescht werden soll oder nicht.
     */
    private boolean isDelete;
    
    /**
     * 
     * Der Typ des Tools, welches ausgesucht wurde.
     */
    private ToolType toolType;

    /**
     * Konstruktor um die Tools zu verarbeiten.
     *
     * @param lGlobalTools Tools vom Server.
     * @param lProjectTools Tools vom Projekt.
     */
    public ToolViewStartUp(Iterator<Tool> lGlobalTools, Iterator<Tool> lProjectTools) {
        toolType = null;
        tools = new HashMap<>();
        Map<Long, Tool> globalTools = new HashMap<>();
        while (lGlobalTools.hasNext()) {
            Tool tool = lGlobalTools.next();
            globalTools.put(tool.getId(), tool);
        }
        tools.put(ToolType.GLOBAL, globalTools);

        Map<Long, Tool> projectTools = new HashMap<>();
        while (lProjectTools.hasNext()) {
            Tool tool = lProjectTools.next();
            projectTools.put(tool.getId(), tool);
        }
        tools.put(ToolType.PROJECT, projectTools);
    }

    /**
     * Initialisiert die Ansicht fuer die einzelnen Tools.
     */
    protected void initializeToolsView() {
        isDelete = false;
        List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<>();

        panels.add(new ToolViewController(tools));

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

        wiz = new WizardDescriptor(new WizardDescriptor.ArrayIterator<>(panels));

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
    }

    /**
     * Getter fuer Flag, ob das Tool geloescht werden soll.
     * @return boolean
     */
    protected boolean isDelete() {
        return isDelete;
    }

    /**
     * Gibt den Typ des Zuletzt ausgesuchten Tools wieder.
     * 
     * @return Der Typ des Tools.
     */
    public ToolType getLastToolType() {
        return toolType;
    }

    /**
     * Startet die Anzeige fuer die verschiedenen Tools.
     *
     * @return Tool.
     */
    public Tool startWizardTools() {

        if (DialogDisplayer.getDefault().notify(wiz) != 
                WizardDescriptor.FINISH_OPTION) {
            return null;
        }

        if (wiz.getProperty(ActionCommands.ToolDelete) != null) {
            Tool tool = tools.get(ToolType.PROJECT)
                    .get((Long) wiz.getProperty(ActionCommands.ToolDelete));
            isDelete = true;
            return tool;
        }

        toolType = (ToolType) wiz.getProperty(ActionCommands.ToolType);
        Tool selectedTool = null;

        switch (toolType) {
            case GLOBAL:
                selectedTool = tools.get(toolType)
                        .get((Long) wiz.getProperty(ActionCommands.Tool));
                break;
            case PROJECT:
                selectedTool = tools.get(toolType)
                        .get((Long) wiz.getProperty(ActionCommands.Tool));
                break;
            case USER_PROVIDED:
                String url = (String) wiz
                        .getProperty(ActionCommands.Tool);
                Tool tool = new Tool();
                String fileContent = null;
                try {
                    fileContent = Util.readFile(url);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
                tool.setXMLFile(fileContent);
                tool.setAuthor((String) (wiz
                        .getProperty(ActionCommands.LocalToolAuthor)));
                tool.setDescription((String) (wiz
                        .getProperty(ActionCommands.LocalToolDescription)));
                tool.setName((String) (wiz
                        .getProperty(ActionCommands.LocalToolName)));
                tool.setUrl(url);
                tool.setVersion(Float.parseFloat((String) (wiz
                        .getProperty(ActionCommands.LocalToolVersion))));
                selectedTool = tool;
                break;
            default:
                assert false;
        }

        return selectedTool;
    }
}
