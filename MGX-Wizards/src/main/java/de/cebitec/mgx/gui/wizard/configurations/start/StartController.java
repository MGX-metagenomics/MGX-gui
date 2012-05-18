/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.wizard.configurations.start;

import de.cebitec.mgx.gui.datamodel.Tool;
import de.cebitec.mgx.gui.wizard.configurations.menu.MenuView;
import de.cebitec.mgx.gui.wizard.configurations.utilities.ActionCommands;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.event.*;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;

/**
 *
 * @author belmann
 */
public class StartController implements WizardDescriptor.Panel<WizardDescriptor>,
        ActionListener, PropertyChangeListener {

    private ArrayList<String> globalNames;
    private ArrayList<String> authorGlobal;
    private ArrayList<String> authorProject;
    private ArrayList<String> descriptionGlobal;
    private ArrayList<String> versionGlobal;
    private final ArrayList<String> descriptionProject;
    private final ArrayList<String> versionProject;
    private final ArrayList<String> nameGlobal;
    private final ArrayList<String> nameProject;
    private StartView viewComponent;
    private ArrayList<String> version;
    private List<Tool> global;
    private List<Tool> project;

    public StartController(List<Tool> lGlobal, List<Tool> lProject) {

        global = lGlobal;
        project = lProject;

        descriptionGlobal = new ArrayList<>();
        authorGlobal = new ArrayList<>();
        authorProject = new ArrayList<>();
        descriptionProject = new ArrayList<>();
        versionGlobal = new ArrayList<>();
        versionProject = new ArrayList<>();
        nameGlobal = new ArrayList<>();
        nameProject = new ArrayList<>();



        for (Tool tool : lGlobal) {
            nameGlobal.add(tool.getAuthor());
            authorGlobal.add(tool.getAuthor());
            descriptionGlobal.add(tool.getDescription());
            versionGlobal.add(Float.toString(tool.getVersion()));

        }
        for (Tool tool : lProject) {
            nameProject.add(tool.getAuthor());
            authorProject.add(tool.getAuthor());
            descriptionProject.add(tool.getDescription());
            versionProject.add(Float.toString(tool.getVersion()));

        }
        getComponent().addPropertyChangeListener(this);
    }

    @Override
    public StartView getComponent() {
        if (viewComponent == null) {
            viewComponent = new StartView(authorGlobal, authorProject, descriptionGlobal,
                    descriptionProject, versionGlobal, versionProject,
                    nameGlobal, nameProject, this);
        }
        return viewComponent;
    }

    @Override
    public HelpCtx getHelp() {
        return HelpCtx.DEFAULT_HELP;
    }
    WizardDescriptor model;

    @Override
    public void readSettings(WizardDescriptor settings) {
        model = settings;
    }

    @Override
    public void storeSettings(WizardDescriptor settings) {
        settings.putProperty("TOOL", getComponent().getToolLocation() + ";" + getComponent().getFileFieldText());

    }
    boolean isValid = false;

    @Override
    public boolean isValid() {
        return isValid;
    }
    private final static Logger LOGGER =
            Logger.getLogger(MenuView.class.getName());

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(ActionCommands.toolBox)) {
            if (viewComponent.getToolLocation().equals(ActionCommands.Project)) {
                viewComponent.setToolsToChoose(viewComponent.project);
            } else if (viewComponent.getToolLocation().equals(ActionCommands.Global)) {

                viewComponent.setToolsToChoose(viewComponent.global);
            } else {
                viewComponent.setToolsToChoose(viewComponent.local);
            }
        }
    }

    @Override
    public void addChangeListener(ChangeListener l) {
        listeners.add(ChangeListener.class, l);
    }
    /**
     * Damit ein WizardDescriptor sich bei einem Wizard-Panel registrieren kann,
     * schreibt das Interface WizardDescriptor.Panel die beiden Methoden
     * addChangeListener und removeChangeListener vor. Diese Methoden fügen
     * dieser Liste die jeweiligen Listener hinzu.
     */
    private final EventListenerList listeners = new EventListenerList();

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        boolean oldState = isValid;

        isValid = !getComponent().getFileFieldText().isEmpty();
        fireChangeEvent(this, oldState, isValid);

    }

    @Override
    public void removeChangeListener(ChangeListener l) {
        listeners.remove(ChangeListener.class, l);
    }

    private void fireChangeEvent(Object lSource, boolean oldState, boolean newState) {
        if (oldState != newState) {

            ChangeEvent ev = new ChangeEvent(lSource);
            for (ChangeListener listener :
                    listeners.getListeners(ChangeListener.class)) {
                {
                    listener.stateChanged(ev);
                }

            }
        }
    }
}
