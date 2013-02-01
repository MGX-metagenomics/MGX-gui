package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.client.datatransfer.SeqUploader;
import de.cebitec.mgx.client.datatransfer.UploadBase;
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.controller.RBAC;
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
import java.util.concurrent.ExecutionException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.SwingWorker;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.WizardDescriptor;
import org.openide.nodes.Children;
import org.openide.util.Exceptions;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sj
 */
public class DNAExtractNode extends MGXNodeBase<DNAExtract> {

    private SeqRunNodeFactory snf = null;

    public DNAExtractNode(MGXMaster m, DNAExtract d) {
        this(m, d, new SeqRunNodeFactory(m, d));
    }

    private DNAExtractNode(MGXMaster m, DNAExtract d, SeqRunNodeFactory snf) {
        super(Children.create(snf, true), Lookups.fixed(m, d), d);
        master = m;
        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/DNAExtract.png");
        setShortDescription(getToolTipText(d));
        setDisplayName(d.getName());
        this.snf = snf;
    }

    private String getToolTipText(DNAExtract d) {
        return new StringBuilder("<html><b>DNA extract: </b>")
                .append(d.getName())
                .append("<br><hr><br>")
                .append("type: ").append(d.getMethod()).append("<br>")
                .append("protocol: ").append(d.getProtocol() != null ? d.getProtocol() : "")
                .append("</html>").toString();
    }

    @Override
    public boolean canDestroy() {
        return true;
    }

    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{new EditDNAExtract(), new DeleteDNAExtract(), new AddSeqRun()};
    }

    @Override
    public void updateModified() {
        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/DNAExtract.png");
        setShortDescription(getToolTipText(getContent()));
        setDisplayName(getContent().getName());
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
                final MGXMaster m = Utilities.actionsGlobalContext().lookup(MGXMaster.class);
                SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        m.DNAExtract().update(updatedExtract);
                        return null;
                    }

                    @Override
                    protected void done() {
                        try {
                            get();
                        } catch (InterruptedException | ExecutionException ex) {
                            Exceptions.printStackTrace(ex);
                        }
//                        setShortDescription(getToolTipText(updatedExtract));
//                        setDisplayName(updatedExtract.getName());
//                        fireDisplayNameChange(oldDisplayName, updatedExtract.getMethod());
                        super.done();
                    }
                };
                worker.execute();
            }
        }

        @Override
        public boolean isEnabled() {
            return (super.isEnabled() && RBAC.isUser());
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
                final MGXMaster m = Utilities.actionsGlobalContext().lookup(MGXMaster.class);
                MGXTask deleteTask = new MGXTask("Delete " + dna.getName()) {
                    @Override
                    public void process() {
                        setStatus("Deleting..");
                        m.DNAExtract().delete(dna);
                    }

                    @Override
                    public void finished() {
                        super.finished();
                        fireNodeDestroyed();
                    }

                    @Override
                    public boolean isDeterminate() {
                        return false;
                    }

                    @Override
                    public int getProgress() {
                        return MGXTask.PROGRESS_UNKNOWN;
                    }
                };

                TaskManager.getInstance().addTask(deleteTask);

            }
        }

        @Override
        public boolean isEnabled() {
            return (super.isEnabled() && RBAC.isUser());
        }
    }

    private class AddSeqRun extends AbstractAction {

        public AddSeqRun() {
            putValue(NAME, "Add sequencing run");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            final SeqRunWizardDescriptor wd = new SeqRunWizardDescriptor();
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
                    protected Void doInBackground() {
                        MGXMaster m = Utilities.actionsGlobalContext().lookup(MGXMaster.class);
                        m.SeqRun().create(seqrun);

                        // create a sequence reader
                        String canonicalPath = null;
                        SeqReaderI reader = null;
                        try {
                            canonicalPath = wd.getSequenceFile().getCanonicalPath();
                            reader = SeqReaderFactory.getReader(canonicalPath);
                        } catch (IOException | SeqStoreException ex) {
                            m.SeqRun().delete(seqrun);
                            snf.refreshChildren();
                            publish(ex);
                            return null;
                        }
                        final SeqUploader uploader = m.Sequence().createUploader(seqrun.getId(), reader);
                        MGXTask run = new MGXTask("Upload " + canonicalPath) {
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
                                MGXMaster m = Utilities.actionsGlobalContext().lookup(MGXMaster.class);
                                m.SeqRun().delete(seqrun);
                                snf.refreshChildren();
                                super.failed();
                            }

                            @Override
                            public boolean isDeterminate() {
                                return false;
                            }

                            @Override
                            public int getProgress() {
                                return MGXTask.PROGRESS_UNKNOWN;
                            }

                            @Override
                            public void propertyChange(PropertyChangeEvent pce) {
                                if (pce.getPropertyName().equals(UploadBase.NUM_ELEMENTS_SENT)) {
                                    setStatus(String.format("%1$d sequences sent", pce.getNewValue()));
                                    seqrun.setNumSequences((Long) pce.getNewValue());
                                }
                            }
                        };
                        uploader.addPropertyChangeListener(run);

                        TaskManager.getInstance().addTask(run);
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
                        DialogDisplayer.getDefault().notify(nd);
                    }

                    @Override
                    protected void done() {
                        try {
                            get();
                        } catch (InterruptedException | ExecutionException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                        super.done();
                    }
                };
                sw.execute();
            }
        }

        @Override
        public boolean isEnabled() {
            return (super.isEnabled() && RBAC.isUser());
        }
    }
}
