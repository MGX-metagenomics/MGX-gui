package de.cebitec.mgx.gui.wizard.configurations.action;

import de.cebitec.mgx.gui.datamodel.MGXFile;
import de.cebitec.mgx.gui.datamodel.Tool;
import de.cebitec.mgx.gui.datamodel.misc.ToolType;
import de.cebitec.mgx.gui.wizard.configurations.data.impl.Store;
import de.cebitec.mgx.gui.wizard.configurations.progressscreen.ProgressBar;
import de.cebitec.mgx.gui.wizard.configurations.utilities.MenuStatus;
import java.util.List;

/**
 * Startet die Anzeige fuer die Tools als auch die Anzeige fuer die Parameter.
 *
 *
 * @author pbelmann
 */
public class WizardController {

    /**
     * Fortschrittsanzeige bei einer Verbindung mit dem Server.
     */
    private ProgressBar bar;
    /**
     * Das StartUp fuer die Anzeige der Parameter.
     */
    private MenuViewStartUp configurationStartUp;
   
    /**
     * Controller fuer die Anzeige der aller vorhandenen Tools.
     */
    private ToolViewStartUp toolStartUp;

    /**
     * Flag, ob das Tool geloescht werden soll oder nicht.
     */
    private boolean isDelete;
    
    
    /**
     * Startet die Anzeige fuer die Auswahl der Tools.
     *
     * @param lGlobalTools Tools die auf dem Server liegen.
     * @param lProjectTools Tools die sich im Projekt befinden.
     * @return Das ausgewaehlte Tool.
     */
    public Tool startToolViewStartUp(List<Tool> lGlobalTools, List<Tool> lProjectTools) {
        isDelete = false;
        toolStartUp = new ToolViewStartUp(lGlobalTools, lProjectTools);
        toolStartUp.initializeToolsView();
        Tool tool = toolStartUp.startWizardTools();
        isDelete = toolStartUp.isDelete();
        return tool;
    }

    public boolean isToolDelete() {
        return isDelete;
    }

    public ToolType getLastToolType() {
        return toolStartUp.getLastToolType();
    }

    /**
     *
     * Started die Anzeige fuer die Eingabe der Parameter.
     *
     * @param store Der Store der die moeglichen Parameter beinhaltet.
     * @param entries Eintraege der Dateien in dem Projekt.
     * @return Der Store mit den eingegebenen Parametern.
     */
    public Store startParameterConfiguration(Store store,
            List<MGXFile> entries, String toolName) {
        configurationStartUp = new MenuViewStartUp(store, entries, toolName);
        configurationStartUp.initializeWizard();
        return configurationStartUp.startWizardConfigurations();
    }

    /**
     * Gibt den Status des Menues wieder. Moeglicher Status: 0 Bei Cancel. 1 Bei
     * finish. 2 Bei nochmaliger Anzeige der Tools.
     *
     * @return Status
     */
    public MenuStatus getStatus() {
        if (configurationStartUp != null) {
            return configurationStartUp.getStatus();
        } else {
            return MenuStatus.RUNNING;
        }
    }
}
