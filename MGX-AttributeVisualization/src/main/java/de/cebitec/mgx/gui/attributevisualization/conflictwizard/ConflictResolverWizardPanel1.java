package de.cebitec.mgx.gui.attributevisualization.conflictwizard;

import de.cebitec.mgx.gui.datamodel.Job;
import de.cebitec.mgx.gui.datamodel.Pair;
import de.cebitec.mgx.gui.datamodel.SeqRun;
import de.cebitec.mgx.gui.datamodel.Tool;
import de.cebitec.mgx.gui.groups.VisualizationGroup;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;

public class ConflictResolverWizardPanel1 implements WizardDescriptor.Panel<WizardDescriptor>, PropertyChangeListener {

    private VisualizationGroup vg;
    private SeqRun run;
    private Map<Tool, Job> toolmap;
    private final EventListenerList listeners = new EventListenerList();

    public ConflictResolverWizardPanel1(VisualizationGroup vg, SeqRun run, Map<Tool, Job> map) {
        this.vg = vg;
        this.run = run;
        this.toolmap = map;
    }
    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private ConflictResolverVisualPanel1 component;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public ConflictResolverVisualPanel1 getComponent() {
        if (component == null) {
            component = new ConflictResolverVisualPanel1();
            component.setVisualizationGroup(vg);
            component.setSeqRun(run);
            component.setJobMap(toolmap);
            component.addPropertyChangeListener(this);
        }
        return component;
    }

    @Override
    public HelpCtx getHelp() {
        // Show no Help button for this panel:
        return HelpCtx.DEFAULT_HELP;
        // If you have context help:
        // return new HelpCtx("help.key.here");
    }

    @Override
    public boolean isValid() {
        // If it is always OK to press Next or Finish, then:
        //return true;
        return getComponent().getSelectedJob() != null;
        // If it depends on some condition (form filled out...) and
        // this condition changes (last form field filled in...) then
        // use ChangeSupport to implement add/removeChangeListener below.
        // WizardDescriptor.ERROR/WARNING/INFORMATION_MESSAGE will also be useful.

    }

    public Pair<VisualizationGroup, Pair<SeqRun, Job>> getSelection() {
        return new Pair<>(vg, new Pair<>(run, getComponent().getSelectedJob()));
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
    public void readSettings(WizardDescriptor wiz) {
        // use wiz.getProperty to retrieve previous panel state
    }

    @Override
    public void storeSettings(WizardDescriptor wiz) {
        // use wiz.putProperty to remember current panel state
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        fireChangeEvent(this, false, isValid());
    }
}
