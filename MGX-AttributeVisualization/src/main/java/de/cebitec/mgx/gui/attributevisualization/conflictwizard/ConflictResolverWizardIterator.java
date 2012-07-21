/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.attributevisualization.conflictwizard;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.Job;
import de.cebitec.mgx.gui.datamodel.misc.Pair;
import de.cebitec.mgx.gui.datamodel.SeqRun;
import de.cebitec.mgx.gui.datamodel.Tool;
import de.cebitec.mgx.gui.groups.VisualizationGroup;
import java.awt.Component;
import java.util.Map.Entry;
import java.util.*;
import java.util.concurrent.ExecutionException;
import javax.swing.JComponent;
import javax.swing.SwingWorker;
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
    private List<VisualizationGroup> groups;

    public ConflictResolverWizardIterator(List<VisualizationGroup> groups) {
        this.groups = groups;
    }

//    public void setGroups(List<VisualizationGroup> g) {
//        groups = g;
//    }
    public List<Pair<VisualizationGroup, Pair<SeqRun, Job>>> getSelection() {
        List<Pair<VisualizationGroup, Pair<SeqRun, Job>>> l = new ArrayList<>();
        for (Panel<WizardDescriptor> p : panels) {
            ConflictResolverWizardPanel1 tmp = (ConflictResolverWizardPanel1) p;
            l.add(tmp.getSelection());
        }
        return l;
    }

    private List<WizardDescriptor.Panel<WizardDescriptor>> getPanels() {
        if (panels == null) {
            panels = new ArrayList<>();

            for (final VisualizationGroup vg : groups) {
                for (final Entry<SeqRun, List<Job>> e : vg.getConflicts().entrySet()) {

                    final List<Job> jobs = e.getValue();

                    SwingWorker<Map<Tool, Job>, Void> sw = new SwingWorker<Map<Tool, Job>, Void>() {

                        @Override
                        protected Map<Tool, Job> doInBackground() throws Exception {
                            Map<Tool, Job> map = new HashMap<>();
                            for (Job j : jobs) {
                                MGXMaster master = (MGXMaster) j.getMaster();
                                Tool t = master.Tool().ByJob(j.getId());
                                map.put(t, j);
                            }
                            return map;
                        }
                    };
                    sw.execute();
                    Map<Tool, Job> map = null;
                    try {
                        map = sw.get();
                    } catch (InterruptedException | ExecutionException ex) {
                        Exceptions.printStackTrace(ex);
                    }

                    WizardDescriptor.Panel panel = new ConflictResolverWizardPanel1(vg, e.getKey(), map);
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
