package de.cebitec.mgx.gui.wizard.configurations.action;

import de.cebitec.mgx.gui.datamodel.Tool;
import de.cebitec.mgx.gui.datamodel.misc.ToolType;
import de.cebitec.mgx.gui.wizard.configurations.start.ToolViewController;
import de.cebitec.mgx.gui.wizard.configurations.utilities.Util;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
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

    private HashMap<ToolType, Map<Long, Tool>> tools = new HashMap<>();
    private WizardDescriptor wiz;
    private boolean isDelete;
    ToolType toolType = null;

    /**
     * Konstruktor um die Tools zu verarbeiten.
     *
     * @param lGlobalTools Tools vom Server.
     * @param lProjectTools Tools vom Projekt.
     */
    public ToolViewStartUp(List<Tool> lGlobalTools, List<Tool> lProjectTools) {

        Map<Long, Tool> globalTools = new HashMap<>();
        for (Tool tool : lGlobalTools) {
            globalTools.put(tool.getId(), tool);
        }
        tools.put(ToolType.GLOBAL, globalTools);

        Map<Long, Tool> projectTools = new HashMap<>();
        for (Tool tool : lProjectTools) {
            projectTools.put(tool.getId(), tool);
        }
        tools.put(ToolType.PROJECT, globalTools);
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

    public boolean isDelete() {
        return isDelete;
    }

    public ToolType getLastToolType() {
        return toolType;
    }

    /**
     * Startet die Anzeige fuer die verschiedenen Tools.
     *
     * @return Tool.
     */
    public Tool startWizardTools() {

        if (DialogDisplayer.getDefault().notify(wiz) != WizardDescriptor.FINISH_OPTION) {
            return null;
        }

        if (wiz.getProperty("DELETE") != null) {
            Tool tool = tools.get(toolType).get((Long) wiz.getProperty("DELETE"));
            isDelete = true;
            return tool;
        }

        toolType = (ToolType) wiz.getProperty("TOOLTYPE");
        Tool selectedTool = null;

        switch (toolType) {
            case GLOBAL:
                selectedTool = tools.get(toolType).get((Long) wiz.getProperty("TOOL"));
                break;
            case PROJECT:
                selectedTool = tools.get(toolType).get((Long) wiz.getProperty("TOOL"));
                break;
            case USER_PROVIDED:
                String[] stringArray = ((String) (wiz.getProperty("TOOL"))).split(";");
                String url = stringArray[1];
                Tool tool = new Tool();
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
                tool.setUrl(url);
                tool.setVersion(Float.parseFloat((String) (wiz.getProperty("LOCALVERSION"))));
                selectedTool = tool;
                break;
            default:
                assert false;
        }

        return selectedTool;
    }
}
