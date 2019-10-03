package de.cebitec.mgx.gui.seqexporter;

import de.cebitec.mgx.api.groups.GroupI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.model.AttributeI;
import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;

public final class ExportSeqWizardIterator<T extends Number, U> implements WizardDescriptor.Iterator<WizardDescriptor> {

    private int index = 0;
    private final List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<>();
    private final ExportSeqWizardPanel1<T> p1;
    private final ExportSeqWizardPanel2<U> p2;

    public ExportSeqWizardIterator(GroupI<U> vgroup, DistributionI<T> dist) {
        p1 = new ExportSeqWizardPanel1<>();
        p1.setDistribution(dist);
        panels.add(p1);
        p2 = new ExportSeqWizardPanel2<>();
        p2.setGroup(vgroup);
        panels.add(p2);

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

    private List<WizardDescriptor.Panel<WizardDescriptor>> getPanels() {
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
        return index == 0 && !p1.getSelectedAttributes().isEmpty();
    }

    @Override
    public boolean hasPrevious() {
        return index == 1;
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

    File getSelectedFile() {
        return p2.getSelectedFile();
    }

    boolean hasQuality() {
        return p2.hasQuality();
    }

    Set<AttributeI> getSelectedAttributes() {
        return p1.getSelectedAttributes();
    }
}
