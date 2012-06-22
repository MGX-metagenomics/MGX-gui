package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.upload.SeqUploader;
import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.DNAExtract;
import de.cebitec.mgx.gui.datamodel.SeqRun;
import de.cebitec.mgx.gui.nodefactory.SeqRunNodeFactory;
import de.cebitec.mgx.gui.taskview.MGXTask;
import de.cebitec.mgx.gui.taskview.TaskManager;
import de.cebitec.mgx.gui.wizard.extract.DNAExtractWizardDescriptor;
import de.cebitec.mgx.gui.wizard.seqrun.SeqRunWizardDescriptor;
import de.cebitec.mgx.sequence.SeqReaderFactory;
import de.cebitec.mgx.sequence.SeqReaderI;
import de.cebitec.mgx.sequence.SeqStoreException;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.SwingWorker;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.WizardDescriptor;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sj
 */
public class DNAExtractNode extends MGXNodeBase<DNAExtract> {

    private SeqRunNodeFactory snf = null;

    public DNAExtractNode(MGXMaster m, DNAExtract d) {
        this(d, new SeqRunNodeFactory(m, d));
        master = m;
        setDisplayName(d.getMethod());
    }

    private DNAExtractNode(DNAExtract d, SeqRunNodeFactory snf) {
        super(Children.create(snf, true), Lookups.singleton(d), d);
        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/DNAExtract.png");
        this.snf = snf;
    }

    @Override
    public boolean canDestroy() {
        return true;
    }

    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{new EditDNAExtract(), new DeleteDNAExtract(), new AddSeqRun()};
    }

    private class EditDNAExtract extends AbstractAction {

        public EditDNAExtract() {
            putValue(NAME, "Edit");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            DNAExtract extract = getLookup().lookup(DNAExtract.class);
            DNAExtractWizardDescriptor wd = new DNAExtractWizardDescriptor(extract);
            Dialog dialog = DialogDisplayer.getDefault().createDialog(wd);
            dialog.setVisible(true);
            dialog.toFront();
            boolean cancelled = wd.getValue() != WizardDescriptor.FINISH_OPTION;
            if (!cancelled) {
                final String oldDisplayName = extract.getMethod();
                final DNAExtract updatedExtract = wd.getDNAExtract();
                SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

                    @Override
                    protected Void doInBackground() throws Exception {
                        getMaster().DNAExtract().update(updatedExtract);
                        return null;
                    }

                    @Override
                    protected void done() {
                        fireDisplayNameChange(oldDisplayName, updatedExtract.getMethod());
                        super.done();
                    }
                };
                worker.execute();
            }
        }
    }

    private class DeleteDNAExtract extends AbstractAction {

        public DeleteDNAExtract() {
            putValue(NAME, "Delete");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            final DNAExtract dna = getLookup().lookup(DNAExtract.class);
            NotifyDescriptor d = new NotifyDescriptor("Really delete DNA extract " + dna.getMethod() + "?",
                    "Delete DNA extract",
                    NotifyDescriptor.YES_NO_OPTION,
                    NotifyDescriptor.QUESTION_MESSAGE,
                    null,
                    null);
            Object ret = DialogDisplayer.getDefault().notify(d);
            if (NotifyDescriptor.YES_OPTION.equals(ret)) {
                MGXTask deleteTask = new MGXTask() {

                    @Override
                    public void process() {
                        setStatus("Deleting..");
                        getMaster().DNAExtract().delete(dna.getId());
                    }

                    @Override
                    public void finished() {
                        super.finished();
                        fireNodeDestroyed();
                    }
                };

                TaskManager.getInstance().addTask("Delete " + dna.getMethod(), deleteTask);

            }
        }
    }

    private class AddSeqRun extends AbstractAction {

        public AddSeqRun() {
            putValue(NAME, "Add sequencing run");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            final SeqRunWizardDescriptor wd = new SeqRunWizardDescriptor(getMaster());
            Dialog dialog = DialogDisplayer.getDefault().createDialog(wd);
            dialog.setVisible(true);
            dialog.toFront();
            boolean cancelled = wd.getValue() != WizardDescriptor.FINISH_OPTION;
            if (!cancelled) {
                DNAExtract extract = getLookup().lookup(DNAExtract.class);
                final SeqRun seqrun = wd.getSeqRun();
                seqrun.setDNAExtractId(extract.getId());

                SwingWorker<Void, Exception> sw = new SwingWorker<Void, Exception>() {

                    @Override
                    protected Void doInBackground() throws Exception {

                        getMaster().SeqRun().create(seqrun);

                        // create a sequence reader
                        String canonicalPath = null;
                        SeqReaderI reader = null;
                        try {
                            canonicalPath = wd.getSequenceFile().getCanonicalPath();
                            reader = SeqReaderFactory.getReader(canonicalPath);
                        } catch (IOException | SeqStoreException ex) {
                            getMaster().SeqRun().delete(seqrun.getId());
                            snf.refreshChildren();
                            publish(ex);
                            return null;
                        }
                        final SeqUploader uploader = getMaster().Sequence().createUploader(seqrun.getId(), reader);
                        MGXTask run = new MGXTask() {

                            @Override
                            public void process() {
                                boolean success = uploader.upload();
                                if (!success) {
                                    publish(new MGXClientException(uploader.getErrorMessage()));
                                }
                            }

                            @Override
                            public void finished() {
                                snf.refreshChildren();
                            }

                            @Override
                            public void failed() {
                                getMaster().SeqRun().delete(seqrun.getId());
                                snf.refreshChildren();
                            }

                            @Override
                            public void propertyChange(PropertyChangeEvent pce) {
                                if (pce.getPropertyName().equals(SeqUploader.NUM_SEQUENCES)) {
                                    setStatus(String.format("%1$d sequences sent", pce.getNewValue()));
                                    seqrun.setNumSequences((Long)pce.getNewValue());
                                }
                            }
                        };
                        uploader.addPropertyChangeListener(run);

                        TaskManager.getInstance().addTask("Upload " + canonicalPath, run);
                        return null;
                    }

                    @Override
                    protected void process(List<Exception> chunks) {
                        StringBuilder sb = new StringBuilder();
                        for (Exception e : chunks) {
                            sb.append(e.getMessage());
                        }
                        NotifyDescriptor nd = new NotifyDescriptor(sb.toString(),
                                "Error",
                                NotifyDescriptor.OK_CANCEL_OPTION,
                                NotifyDescriptor.ERROR_MESSAGE,
                                null,
                                null);
                        Object ret = DialogDisplayer.getDefault().notify(nd);
                    }
                };
                sw.execute();
            }
        }
    }
}
