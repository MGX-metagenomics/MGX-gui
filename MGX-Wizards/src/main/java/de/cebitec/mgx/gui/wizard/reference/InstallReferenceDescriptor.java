package de.cebitec.mgx.gui.wizard.reference;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.Reference;
import java.awt.Dimension;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import org.openide.WizardDescriptor;
import org.openide.WizardDescriptor.ArrayIterator;
import org.openide.WizardDescriptor.Panel;
import org.openide.util.Exceptions;
import org.openide.util.Utilities;

/**
 *
 * @author sjaenick
 */
public class InstallReferenceDescriptor extends WizardDescriptor {

    private InstallReferenceWizardPanel1 p1 = new InstallReferenceWizardPanel1();
    public static final String PROP_REFERENCE = "propRef";

    public InstallReferenceDescriptor() {
        List<Panel<WizardDescriptor>> panels = new ArrayList<>();
        panels.add(p1);
        this.setPanelsAndSettings(new ArrayIterator<>(panels), this);
        this.setTitleFormat(new MessageFormat("{0}"));
        this.setTitle("Reference sequence wizard");
        putProperty(WizardDescriptor.PROP_CONTENT_DATA, new String[]{p1.getName()});
        putProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, Boolean.TRUE);
        putProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, Boolean.TRUE);
        putProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, Boolean.TRUE);
        setData();

    }

    public Reference getSelectedReference() {
        return (Reference) getProperty(PROP_REFERENCE);
    }

    private void setData() {
        final MGXMaster m = Utilities.actionsGlobalContext().lookup(MGXMaster.class);
        SwingWorker<Void, Void> sw = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                Set<Reference> refs = new TreeSet<>();
                Set<String> projRefNames = new HashSet<>();
                java.util.Iterator<Reference> projrefiter = m.Reference().fetchall();
                while (projrefiter.hasNext()) {
                    projRefNames.add(projrefiter.next().getName());
                }
                java.util.Iterator<Reference> refiter = m.Reference().listGlobalReferences();
                while (refiter.hasNext()) {
                    Reference r = refiter.next();
                    if (!projRefNames.contains(r.getName())) {
                        refs.add(r);
                    }
                }
                p1.setReferences(refs);
                return null;
            }
        };
        sw.execute();
        try {
            sw.get();
        } catch (InterruptedException | ExecutionException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
