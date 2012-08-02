package de.cebitec.mgx.gui.wizard.configurations.start;

import de.cebitec.mgx.gui.datamodel.Tool;
import de.cebitec.mgx.gui.datamodel.misc.ToolType;
import de.cebitec.mgx.gui.wizard.configurations.messages.Messages;
import de.cebitec.mgx.gui.wizard.configurations.utilities.ActionCommands;
import de.cebitec.mgx.gui.wizard.configurations.utilities.AlphabetSorter;
import de.cebitec.mgx.gui.wizard.configurations.utilities.Util;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.event.*;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;

/**
 * Controller fuer die einzelnen Aufrufe aus der View (Start Panel fuer die
 * Auswahl an Tools).
 *
 *
 * @author belmann
 */
public class ToolViewController implements WizardDescriptor.Panel<WizardDescriptor>,
        ChangeListener, ActionListener, PropertyChangeListener {

    private final static Logger LOGGER =
            Logger.getLogger(ToolViewController.class.getName());
    /**
     * Hier werden die Namen der Authoren der Tools aus dem Server
     * abgespeichert.
     */
    private ArrayList<String> authorGlobal;
    /**
     * Hier werden die Namen der Authoren der Tools aus dem Projekt
     * abgespeichert.
     */
    private ArrayList<String> authorProject;
    /**
     * Hier werden die Beschreibungen der Tools aus dem Server abgespeichert.
     */
    private ArrayList<String> descriptionGlobal;
    /**
     * Hier werden die Versionsnummern der Tools aus dem Server abgespeichert.
     */
    private ArrayList<String> versionGlobal;
    /**
     * Hier werden die Beschreibungen der Tools aus dem Projekt abgespeichert.
     */
    private final ArrayList<String> descriptionProject;
    /**
     * Hier werden die Versionsnummern der Tools aus dem Project abgespeichert.
     */
    private final ArrayList<String> versionProject;
    /**
     * Hier werden die Namen der Tools aus dem Server abgespeichert.
     */
    private final ArrayList<String> nameGlobal;
    /**
     * Hier werden die Namen der Tools aus dem Projekt abgespeichert.
     */
    private final ArrayList<String> nameProject;
    /**
     * StartView stellt die View des Panels dar.
     */
    private ToolView viewComponent;
    /**
     * Eine Liste aller Tools aus dem Server.
     */
    private List<Tool> global;
    /**
     * Eine Liste aller Tools aus dem Projekt.
     */
    private List<Tool> project;
    /**
     * Das Model fuer die Applikation.
     */
    private WizardDescriptor model;
    /**
     * Gibt an, ob in der View 'Next' oder 'Finish' betaetigt werden kann.
     */
    private boolean isValid = false;
    /**
     * Damit ein WizardDescriptor sich bei einem Wizard-Panel registrieren kann,
     * schreibt das Interface WizardDescriptor.Panel die beiden Methoden
     * addChangeListener und removeChangeListener vor. Diese Methoden fügen
     * dieser Liste die jeweiligen Listener hinzu.
     */
    private final EventListenerList listeners = new EventListenerList();
    private JButton deleteButton;

    /**
     * Der Konstruktor initialisiert alle noetigen Datenstrukturen fuer die
     * View.
     *
     * @param lGlobal Tools auf dem Server.
     * @param lProject Tools im Projekt.
     */
    //public ToolViewController(List<Tool> lGlobal, List<Tool> lProject) {
    public ToolViewController(Map<ToolType, Map<Long, Tool>> tools) {
        AlphabetSorter sorter = new AlphabetSorter();

        List<Tool> lGlobal = new ArrayList<>();
        lGlobal.addAll(tools.get(ToolType.GLOBAL).values());
        Collections.sort(lGlobal, sorter);

        List<Tool> lProject = new ArrayList<>();
        lProject.addAll(tools.get(ToolType.PROJECT).values());
        Collections.sort(lProject, sorter);

        global = lGlobal;
        project = lProject;

        isDelete = false;
        descriptionGlobal = new ArrayList<>();
        authorGlobal = new ArrayList<>();
        authorProject = new ArrayList<>();
        descriptionProject = new ArrayList<>();
        versionGlobal = new ArrayList<>();
        versionProject = new ArrayList<>();
        nameGlobal = new ArrayList<>();
        nameProject = new ArrayList<>();

        for (Tool tool : lGlobal) {
            nameGlobal.add(tool.getName());
            authorGlobal.add(tool.getAuthor());
            descriptionGlobal.add(tool.getDescription());
            versionGlobal.add(Float.toString(tool.getVersion()));
        }

        for (Tool tool : lProject) {
            nameProject.add(tool.getName());
            authorProject.add(tool.getAuthor());
            descriptionProject.add(tool.getDescription());
            versionProject.add(Float.toString(tool.getVersion()));

        }

    }

    /**
     * Gibt die View Komponente wieder.
     *
     * @return View.
     */
    @Override
    public ToolView getComponent() {
        if (viewComponent == null) {
            viewComponent = new ToolView(authorGlobal, authorProject,
                    descriptionGlobal, descriptionProject,
                    versionGlobal, versionProject,
                    nameGlobal, nameProject, this);
        }
        return viewComponent;
    }

    @Override
    public HelpCtx getHelp() {
        return HelpCtx.DEFAULT_HELP;
    }

    /**
     * Erzeugt das Model.
     *
     * @param settings Model.
     */
    @Override
    public void readSettings(WizardDescriptor settings) {
        model = settings;

        Object[] objects = model.getOptions();
        deleteButton = ((JButton) objects[0]);
        LOGGER.info(deleteButton.getName());
        deleteButton.setActionCommand("DELETE");
        deleteButton.addActionListener(this);
//        deleteButton.setEnabled(true);
//        if (hasDefault) {
//            allDefaultButton.setEnabled(true);
//        } else {
//            allDefaultButton.setEnabled(false);
//        }

        getComponent().addPropertyChangeListener(this);
    }

    /**
     * Speichert Daten im Model, sobald die View beendet wird.
     *
     * @param settings Model
     */
    @Override
    public void storeSettings(WizardDescriptor settings) {
//        if (settings.getValue() == WizardDescriptor.FINISH_OPTION) {
        deleteButton.removeActionListener(this);
        if (isDelete) {
            model.putProperty("DELETE",
                    getComponent().getToolType() + ";"
                    + project.get(getComponent().getCurrentRow()).getId());
        }
        if (getComponent().getToolType() == ToolType.USER_PROVIDED) {

            settings.putProperty("TOOL"
//                    ActionCommands.ToolType
                            , 
//                    getComponent().
//                    getToolType() + ";"
//                    + 
                    getComponent().getFileFieldText());
            settings.putProperty(ActionCommands.LocalToolName, getComponent().
                    getNameText());
            settings.putProperty(ActionCommands.LocalToolAuthor, getComponent().
                    getAuthorText());
            settings.putProperty(ActionCommands.LocalToolDescription,
                    getComponent().
                    getDescriptionText());
            settings.putProperty(ActionCommands.LocalToolVersion,
                    getComponent().
                    getVersionText());
            settings.putProperty("TOOLTYPE", ToolType.USER_PROVIDED);         
        } else if (getComponent().getToolType() == ToolType.GLOBAL) {
            settings.putProperty("TOOL", 
//                    getComponent().
//                    getToolType() + ";"
//                    +
                    global.get(getComponent().getCurrentRow()).getId());
            settings.putProperty("TOOLTYPE", ToolType.GLOBAL);
        } else if (getComponent().getToolType() == ToolType.PROJECT) {
            settings.putProperty(
//                    ActionCommands.ToolType
                    "TOOL",
//                    getComponent().getToolType() + ";"
//                    + 
                    project.get(getComponent().getCurrentRow()).getId());
            settings.putProperty("TOOLTYPE", ToolType.PROJECT);
            
        }
//        }
    }

    /**
     * Gibt an ob das Panel korrekte Eingaben enthaelt.
     *
     * @return
     */
    @Override
    public boolean isValid() {
        return isValid;
    }

    /**
     * Setzt ganz nachdem welche Art von Tools der Benutzer sehen moechte
     * (Lokal,Server oder im Projekt) die entsprechende Tabelle.
     *
     * @param e Event.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("DELETE") && !getComponent().
                getFileFieldText().isEmpty()) {
            Object[] options = {"Yes",
                "No",};
            int value = JOptionPane.showOptionDialog(null,
                    "Do you really want to delete the Tool: " + getComponent().
                    getFileFieldText(), "",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null,
                    options, options[0]);
            if (value == JOptionPane.YES_OPTION) {
                isDelete = true;
                model.doFinishClick();

            } else if (value == JOptionPane.NO_OPTION) {
            }
        }
    }
    private boolean isDelete = false;

    /**
     * Fuegt einen Listener hinzu, der darauf achtet, ob sich etwas aendert.
     *
     * @param l Listener
     */
    @Override
    public void addChangeListener(ChangeListener l) {
        listeners.add(ChangeListener.class, l);
    }

    /**
     * Sobald sich derzeit eingegebenen Parameter ändern, wird in dieser Methode
     * überprüft, ob diese so akzeptiert erden.
     *
     * @param lEvt Änderung in der View.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        boolean oldState = isValid;

        isValid = checkValidity(evt);

        fireChangeEvent(this, oldState, isValid);
    }

    /**
     * Entfernt einen Listener, der darauf achtet, ob sich etwas in der View
     * aendert.
     *
     * @param l Listener
     */
    @Override
    public void removeChangeListener(ChangeListener l) {
        listeners.remove(ChangeListener.class, l);
    }

    /**
     * Feuert ein Event, wenn sich etwas in dem Formular veraendert.
     *
     * @param lSource Quelle
     * @param oldState alte Status
     * @param newState neuer Status
     */
    private void fireChangeEvent(Object lSource,
            boolean oldState, boolean newState) {
        if (oldState != newState) {
            ChangeEvent ev = new ChangeEvent(lSource);
            for (ChangeListener listener :
                    listeners.getListeners(ChangeListener.class)) {

                listener.stateChanged(ev);
            }
        }
    }

    /**
     * Ueberprueft, ob alle noetigen Textfelder ausfefuellt sind.
     *
     * @param evt Event.
     * @return Ob das Panel korrekt ausgefuellt ist.
     */
    private boolean checkValidity(PropertyChangeEvent evt) {
        if (!getComponent().getFileFieldText().isEmpty()
                && getComponent().getToolType() == ToolType.PROJECT) {
            deleteButton.setEnabled(true);
        } else {
            deleteButton.setEnabled(false);
        }

        if (getComponent().getToolType() == ToolType.USER_PROVIDED) {
            if (getComponent().getFileFieldText().isEmpty()) {

                model.getNotificationLineSupport().
                        setInformationMessage(
                        Messages.getInformation(Messages.ToolChoose));
                return false;

            } else if (getComponent().getNameText().isEmpty()) {

                model.getNotificationLineSupport().
                        setWarningMessage(
                        Messages.getInformation(Messages.ToolName));

                return false;
            } else if (exists(getComponent().getNameText())) {
                model.getNotificationLineSupport().
                        setWarningMessage(
                        Messages.getInformation(Messages.ToolNameExists));
                return false;
            } else if (getComponent().getAuthorText().isEmpty()) {

                model.getNotificationLineSupport().
                        setWarningMessage(
                        Messages.getInformation(Messages.ToolAuthor));
                return false;
            } else if (getComponent().getDescriptionText().isEmpty()) {

                model.getNotificationLineSupport().
                        setWarningMessage(
                        Messages.getInformation(Messages.ToolDescription));
                return false;

            } else if (getComponent().getVersionText().isEmpty()
                    || !Util.checkFloatFormat(getComponent().
                    getVersionText())) {
                model.getNotificationLineSupport().
                        setWarningMessage(
                        Messages.getInformation(Messages.ToolVersion));
                return false;
            } else {
                model.getNotificationLineSupport().setWarningMessage(null);
            }
        } else {
            if (getComponent().getFileFieldText().isEmpty()) {
                model.getNotificationLineSupport().
                        setInformationMessage(
                        Messages.getInformation(Messages.ToolChoose));
                return false;
            } else {
                model.getNotificationLineSupport().setInformationMessage(null);
                return true;
            }
        }
        return true;
    }

    private boolean exists(String nameText) {
        return (nameProject.contains(nameText) || nameGlobal.contains(nameText));
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() instanceof JTabbedPane) {
            viewComponent.setToolsToChoose(viewComponent.getToolType());
        }
    }
}
