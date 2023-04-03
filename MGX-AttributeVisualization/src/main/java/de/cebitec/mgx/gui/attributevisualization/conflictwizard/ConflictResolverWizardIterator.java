package de.cebitec.mgx.gui.attributevisualization.conflictwizard;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.groups.GroupI;
import de.cebitec.mgx.api.misc.AttributeRank;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.misc.Triple;
import de.cebitec.mgx.api.model.JobI;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.WizardDescriptor.Panel;
import org.openide.util.Exceptions;

public final class ConflictResolverWizardIterator implements WizardDescriptor.Iterator<WizardDescriptor> {

    // Example of invoking this wizard:
    // @ActionID(category="...", id="...")
    // @ActionRegistration(displayName="...")
    // @ActionReference(path="Menu/...")
    // public static ActionListener run() {
    //     return new ActionListener() {
    //         @Override public void actionPerformed(ActionEvent e) {
    //             WizardDescriptor wiz = new WizardDescriptor(new ConflictResolverWizardIterator());
    //             // {0} will be replaced by WizardDescriptor.Panel.getComponent().getName()
    //             // {1} will be replaced by WizardDescriptor.Iterator.name()
    //             wiz.setTitleFormat(new MessageFormat("{0} ({1})"));
    //             wiz.setTitle("...dialog title...");
    //             if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) {
    //                 ...do something...
    //             }
    //         }
    //     };
    // }
    private int index;
    private List<WizardDescriptor.Panel<WizardDescriptor>> panels;
    private final List<GroupI> groups;

    public ConflictResolverWizardIterator(List<GroupI> groups) {
        this.groups = groups;
    }

    public List<Pair<GroupI, Triple<AttributeRank, Object, JobI>>> getSelection() {
        List<Pair<GroupI, Triple<AttributeRank, Object, JobI>>> l = new ArrayList<>();
        for (Panel<WizardDescriptor> p : panels) {
            ConflictResolverWizardPanel1 tmp = (ConflictResolverWizardPanel1) p;
            Pair<GroupI, Triple<AttributeRank, Object, JobI>> selection = tmp.getSelection();
            l.add(selection);
        }
        return l;
    }

    @SuppressWarnings("unchecked")
    private List<WizardDescriptor.Panel<WizardDescriptor>> getPanels() {
        if (panels == null) {
            panels = new ArrayList<>();

            for (final GroupI<?> vg : groups) {
                
                //
                // the job objects don't have the corresponding tool instance set
                // here, so we need to fetch them separately
                //
                for (final Triple<AttributeRank, ?, Set<JobI>> e : (List<Triple<AttributeRank, ?, Set<JobI>>>)vg.getConflicts()) {
                    final Set<JobI> jobs = e.getThird();
                    try {
                        for (final JobI job : jobs) {
                            if (job.getTool() == null) {
                                // trigger tool fetch
                                job.getMaster().Tool().ByJob(job);
                            }
                        }
                    } catch (MGXException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                    WizardDescriptor.Panel panel = new ConflictResolverWizardPanel1(vg, e.getFirst(), e.getSecond(), e.getThird());
                    panels.add(panel);
                }
            }

            String[] steps = new String[panels.size()];
            for (int i = 0; i < panels.size(); i++) {
                Component c = panels.get(i).getComponent();
                // Default step name to component name of panel.
                steps[i] = c.getName();
                if (c instanceof JComponent) { // assume Swing components
                    JComponent jc = (JComponent) c;
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, i);
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, steps);
                    jc.putClientProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, true);
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, true);
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, true);
                }
            }
        }
        return panels;
    }

    @Override
    public WizardDescriptor.Panel<WizardDescriptor> current() {
        return getPanels().get(index);
    }

    @Override
    public String name() {
        return index + 1 + ". from " + getPanels().size();
    }

    @Override
    public boolean hasNext() {
        return index < getPanels().size() - 1;
    }

    @Override
    public boolean hasPrevious() {
        return index > 0;
    }

    @Override
    public void nextPanel() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        index++;
    }

    @Override
    public void previousPanel() {
        if (!hasPrevious()) {
            throw new NoSuchElementException();
        }
        index--;
    }

    // If nothing unusual changes in the middle of the wizard, simply:
    @Override
    public void addChangeListener(ChangeListener l) {
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
    }
    // If something changes dynamically (besides moving between panels), e.g.
    // the number of panels changes in response to user input, then use
    // ChangeSupport to implement add/removeChangeListener and call fireChange
    // when needed
}
