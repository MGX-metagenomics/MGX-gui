package de.cebitec.mgx.gui.wizard.seqrun;

import de.cebitec.mgx.gui.datamodel.SeqRun;
import de.cebitec.mgx.gui.datamodel.Term;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import org.openide.NotificationLineSupport;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;

public class SeqRunWizardPanel1 implements WizardDescriptor.Panel<WizardDescriptor>, PropertyChangeListener {

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private SeqRunVisualPanel1 component;
    private WizardDescriptor model = null;
    private boolean isValid = true;
    private final EventListenerList listeners = new EventListenerList();
    private Collection<SeqRun> allRuns;

    @Override
    public SeqRunVisualPanel1 getComponent() {
        if (component == null) {
            component = new SeqRunVisualPanel1();
            component.putClientProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, Boolean.TRUE);
            component.putClientProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, Boolean.TRUE);
            component.putClientProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, Boolean.TRUE);
            component.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, Integer.valueOf(0));
        }
        return component;
    }

    public String getName() {
        return "Sequencing run";
    }

    @Override
    public HelpCtx getHelp() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    public boolean isValid() {
        return isValid;
    }

    @Override
    public final void addChangeListener(ChangeListener l) {
        listeners.add(ChangeListener.class, l);
    }

    @Override
    public final void removeChangeListener(ChangeListener l) {
        listeners.remove(ChangeListener.class, l);
    }

    protected final void fireChangeEvent(Object src, boolean old, boolean newState) {
        if (old != newState) {
            ChangeEvent ev = new ChangeEvent(src);
            for (ChangeListener cl : listeners.getListeners(ChangeListener.class)) {
                cl.stateChanged(ev);
            }
        }
    }

    @Override
    public void readSettings(WizardDescriptor settings) {
        model = settings;
        SeqRunVisualPanel1 c = getComponent();
        c.addPropertyChangeListener(this);
    }

    @Override
    public void storeSettings(WizardDescriptor settings) {
        model = settings;
        SeqRunVisualPanel1 c = getComponent();
        model.putProperty(SeqRunVisualPanel1.PROP_NAME, c.getRunName());
        model.putProperty(SeqRunVisualPanel1.PROP_PLATFORM, c.getPlatform());
        model.putProperty(SeqRunVisualPanel1.PROP_METHOD, c.getMethod());
        model.putProperty(SeqRunVisualPanel1.PROP_SUBMITTED, c.getSubmittedState());
        model.putProperty(SeqRunVisualPanel1.PROP_ACCESSION, c.getAccession());
    }

    public void setProperties(WizardDescriptor settings) {
        model = settings;
        SeqRunVisualPanel1 c = getComponent();
        c.setRunName((String) model.getProperty(SeqRunVisualPanel1.PROP_NAME));
        c.setPlatform((Term) model.getProperty(SeqRunVisualPanel1.PROP_PLATFORM));
        c.setMethod((Term) model.getProperty(SeqRunVisualPanel1.PROP_METHOD));
        c.setSubmittedState((Boolean) model.getProperty(SeqRunVisualPanel1.PROP_SUBMITTED));
        c.setAccession((String) model.getProperty(SeqRunVisualPanel1.PROP_ACCESSION));
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        boolean oldState = isValid;
        isValid = checkValidity();
        fireChangeEvent(this, oldState, isValid);
    }

    private boolean checkValidity() {
        isValid = true;
        NotificationLineSupport nls = model.getNotificationLineSupport();
        nls.setErrorMessage("");

        SeqRunVisualPanel1 c = getComponent();

        if (c.getRunName() == null || "".equals(c.getRunName())) {
            model.getNotificationLineSupport().setErrorMessage("Please enter run name.");
            isValid = false;
        }
        boolean alreadyExists = checkDuplicate(c.getRunName());
        if (alreadyExists) {
            model.getNotificationLineSupport().setErrorMessage("Sequencing run with same name already exists.");
            isValid = false;
        } 

        // if it's submitted, we require the accession
        if (c.getSubmittedState()) {
            String accession = c.getAccession();
            if (accession == null || "".equals(accession)) {
                model.getNotificationLineSupport().setErrorMessage("Please enter accession.");
                isValid = false;
            }
        } 
        if(isValid){
             model.getNotificationLineSupport().clearMessages();
        }
        return isValid;
    }

    public void setMethods(Term[] terms) {
        getComponent().setMethods(terms);
    }

    public void setPlatforms(Term[] terms) {
        getComponent().setPlatforms(terms);
    }

    public void setSeqRuns(Iterator<SeqRun> runs) {
        allRuns = new ArrayList<>();
        while (runs.hasNext()) {
            allRuns.add(runs.next());
        }
    }

    private boolean checkDuplicate(String name) {
        boolean alreadyExists = false;
        if (model.getProperty(SeqRunWizardDescriptor.INVOCATION_MODE).equals(SeqRunWizardDescriptor.CREATE_MODE)) {
            for (SeqRun seqrun : allRuns) {
                if (seqrun.getName().equals(name)) {
                    alreadyExists = true;
                }
            }
        } else if(model.getProperty(SeqRunWizardDescriptor.INVOCATION_MODE).equals(SeqRunWizardDescriptor.EDIT_MODE)){
            for (SeqRun seqrun : allRuns) {
                if (seqrun.getName().equals(name) && !seqrun.getName().equals(model.getProperty(SeqRunVisualPanel1.PROP_NAME))) {
                    alreadyExists = true;
                }
            }
        }
        return alreadyExists;
    }
}
