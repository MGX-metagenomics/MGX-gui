/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.actions;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.access.datatransfer.TransferBaseI;
import de.cebitec.mgx.api.access.datatransfer.UploadBaseI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.DNAExtractI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.gui.controller.RBAC;
import de.cebitec.mgx.gui.nodefactory.SeqRunNodeFactory;
import de.cebitec.mgx.gui.taskview.MGXTask;
import de.cebitec.mgx.gui.taskview.TaskManager;
import de.cebitec.mgx.gui.wizard.seqrun.SeqRunWizardDescriptor;
import de.cebitec.mgx.sequence.DNASequenceI;
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
import javax.swing.SwingWorker;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.WizardDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.Utilities;

/**
 *
 * @author sjaenick
 */
public class AddSeqRun extends AbstractAction {

    private final SeqRunNodeFactory snf;

    public AddSeqRun(final SeqRunNodeFactory snf) {
        this.snf = snf;
        putValue(NAME, "Add sequencing run");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final MGXMasterI m = Utilities.actionsGlobalContext().lookup(MGXMasterI.class);
        final SeqRunWizardDescriptor wd = new SeqRunWizardDescriptor();
        Dialog dialog = DialogDisplayer.getDefault().createDialog(wd);
        dialog.setVisible(true);
        dialog.toFront();
        boolean cancelled = wd.getValue() != WizardDescriptor.FINISH_OPTION;
        if (!cancelled) {
            final DNAExtractI extract = Utilities.actionsGlobalContext().lookup(DNAExtractI.class);
            //final SeqRunI seqrun = wd.getSeqRun(extract.getMaster());
            //seqrun.setDNAExtractId(extract.getId());
            SwingWorker<SeqRunI, Exception> sw = new SwingWorker<SeqRunI, Exception>() {
                @Override
                protected SeqRunI doInBackground() throws MGXException {
                    final SeqRunI seqrun = m.SeqRun().create(extract, wd.getSeqRunName(), wd.getSequencingMethod(), wd.getSequencingTechnology(), wd.getSubmittedToINSDC(), wd.getAccession());
                    // create a sequence reader
                    String canonicalPath;
                    SeqReaderI<DNASequenceI> reader;
                    try {
                        canonicalPath = wd.getSequenceFile().getCanonicalPath();
                        reader = SeqReaderFactory.getReader(canonicalPath);
                    } catch (IOException | SeqStoreException ex) {
                        m.SeqRun().delete(seqrun);
                        snf.refreshChildren();
                        publish(ex);
                        return null;
                    }
                    final UploadBaseI uploader = m.Sequence().createUploader(seqrun, reader);
                    MGXTask run = new MGXTask("Upload " + canonicalPath) {
                        @Override
                        public boolean process() {
                            boolean success = uploader.upload();
                            if (!success) {
                                publish(new MGXException(uploader.getErrorMessage()));
                            }
                            seqrun.setNumSequences(uploader.getNumElementsSent());
                            return success;
                        }

                        @Override
                        public void finished() {
                            super.finished();
                            snf.refreshChildren();
                        }

                        @Override
                        public void failed() {
                            MGXMasterI m = Utilities.actionsGlobalContext().lookup(MGXMasterI.class);
                            try {
                                m.SeqRun().delete(seqrun);
                            } catch (MGXException ex) {
                                Exceptions.printStackTrace(ex);
                            }
                            snf.refreshChildren();
                            super.failed();
                        }

                        @Override
                        public void propertyChange(PropertyChangeEvent pce) {
                            if (pce.getPropertyName().equals(TransferBaseI.NUM_ELEMENTS_TRANSFERRED)) {
                                setStatus(String.format("%1$d sequences sent", pce.getNewValue()));
                                //seqrun.setNumSequences((Long) pce.getNewValue());
                            } else {
                                super.propertyChange(pce);
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
                    NotifyDescriptor nd = new NotifyDescriptor(sb.toString(), "Error", NotifyDescriptor.OK_CANCEL_OPTION, NotifyDescriptor.ERROR_MESSAGE, null, null);
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
        return super.isEnabled() && RBAC.isUser();
    }

}
