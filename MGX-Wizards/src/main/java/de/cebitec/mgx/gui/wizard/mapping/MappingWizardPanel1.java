/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.wizard.mapping;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.Mapping;
import de.cebitec.mgx.gui.datamodel.Reference;
import de.cebitec.mgx.gui.datamodel.SeqRun;
import de.cebitec.mgx.gui.wizard.analysis.workers.MappingRetriever;
import de.cebitec.mgx.gui.wizard.analysis.workers.SeqRunRetriever;
import de.cebitec.mgx.gui.wizard.mapping.MappingVisualPanel1.MappingEntry;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.openide.WizardDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;

public class MappingWizardPanel1 implements ActionListener, WizardDescriptor.FinishablePanel<WizardDescriptor>, ListSelectionListener {

    private MappingVisualPanel1 component;
    private MGXMaster master;
    private Iterator<SeqRun> seqRunsIter;
    private WizardDescriptor model = null;
    private final static String NO_MAPPINGS_AVAILABLE = "No Mappings available";
    private boolean isValid = false;
    private final EventListenerList listeners = new EventListenerList();

    public MappingWizardPanel1(MGXMaster master) {
        this.master = master;
        retrieveSeqruns();
    }

    private void retrieveSeqruns() {
        try {
            SeqRunRetriever retriever = new SeqRunRetriever(master);
            retriever.execute();
            seqRunsIter = retriever.get();
        } catch (InterruptedException | ExecutionException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public MappingVisualPanel1 getComponent() {
        if (component == null) {
            component = new MappingVisualPanel1(seqRunsIter);
            component.addActionListenerToCombobox(this);
            component.addListSelectionListener(this);
        }
        return component;
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

    @Override
    public void readSettings(WizardDescriptor wiz) {
        model = wiz;
    }

    @Override
    public void storeSettings(WizardDescriptor wiz) {
        model = wiz;
        model.putProperty(MappingVisualPanel1.PROP_MAPPING, component.getMappings());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case MappingVisualPanel1.SEQRUN_COMBOBOX_COMMAND:
                model.getNotificationLineSupport().clearMessages();
                this.isValid = false;
                SeqRun seqrun = component.getSeqRun();
                MappingRetriever mappingRetriever = new MappingRetriever(master);
                mappingRetriever.execute();

                List<MappingVisualPanel1.MappingEntry> mappings = new ArrayList<>();
                try {
                    Iterator<Mapping> mappingIter = mappingRetriever.get();
                    while (mappingIter.hasNext()) {
                        Mapping mapping = mappingIter.next();
                        if (mapping.getSeqrunID() == seqrun.getId()) {
                            Reference reference = fetchReference(mapping.getReferenceID());
                            mappings.add(component.new MappingEntry(reference, mapping));
                        }
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }
                component.setMappings(mappings);
                if (mappings.isEmpty()) {
                    model.getNotificationLineSupport().setInformationMessage(NO_MAPPINGS_AVAILABLE);
                }
                break;
        }
    }

    private Reference fetchReference(final long referenceId) {
        SwingWorker<Reference, Void> worker = new SwingWorker<Reference, Void>() {
            @Override
            protected Reference doInBackground() throws Exception {
                return master.Reference().fetch(referenceId);
            }
        };
        worker.execute();
        try {
            return worker.get();
        } catch (InterruptedException | ExecutionException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        component.revalidate();
        if (component.getSeqRun() != null && component.getMappings() != null) {
            isValid = true;
        } else {
            isValid = false;
        }
        fireChangeEvent(this, !isValid, isValid);
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
    public boolean isFinishPanel() {
        return true;
    }
}
