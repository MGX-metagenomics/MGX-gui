/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.nodeactions;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.access.datatransfer.TransferBaseI;
import de.cebitec.mgx.api.access.datatransfer.UploadBaseI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.DNAExtractI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.gui.rbac.RBAC;
import de.cebitec.mgx.gui.taskview.MGXTask;
import de.cebitec.mgx.gui.taskview.TaskManager;
import de.cebitec.mgx.gui.wizard.seqrun.SeqRunWizardDescriptor;
import de.cebitec.mgx.seqstorage.AlternatingQReader;
import de.cebitec.mgx.sequence.DNAQualitySequenceI;
import de.cebitec.mgx.sequence.DNASequenceI;
import de.cebitec.mgx.sequence.SeqReaderFactory;
import de.cebitec.mgx.sequence.SeqReaderI;
import de.cebitec.mgx.sequence.SeqStoreException;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.io.Serial;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
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

    @Serial
    private static final long serialVersionUID = 1L;

    public AddSeqRun() {
        super.putValue(NAME, "Add sequencing run");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final SeqRunWizardDescriptor wd = new SeqRunWizardDescriptor();
        Dialog dialog = DialogDisplayer.getDefault().createDialog(wd);
        dialog.setVisible(true);
        dialog.toFront();
        boolean cancelled = wd.getValue() != WizardDescriptor.FINISH_OPTION;
        if (!cancelled) {
            final DNAExtractI extract = Utilities.actionsGlobalContext().lookup(DNAExtractI.class);
            final MGXMasterI m = extract.getMaster();

            SwingWorker<SeqRunI, Exception> sw = new SwingWorker<SeqRunI, Exception>() {
                @Override
                @SuppressWarnings("unchecked")
                protected SeqRunI doInBackground() throws MGXException {
                    final SeqRunI seqrun = m.SeqRun().create(extract, wd.getSeqRunName(), wd.getSequencingMethod(), wd.getSequencingTechnology(), wd.getSubmittedToINSDC(), wd.getIsPaired(), wd.getAccession());
                    // create a sequence reader
                    String canonicalPath;
                    SeqReaderI<? extends DNASequenceI> reader;
                    try {
                        if (seqrun.isPaired()) {
                            canonicalPath = wd.getSequenceFiles()[0].getCanonicalPath();
                            SeqReaderI<DNAQualitySequenceI> reader1 = (SeqReaderI<DNAQualitySequenceI>) SeqReaderFactory.<DNAQualitySequenceI>getReader(canonicalPath);
                            canonicalPath = wd.getSequenceFiles()[1].getCanonicalPath();
                            SeqReaderI<DNAQualitySequenceI> reader2 = (SeqReaderI<DNAQualitySequenceI>) SeqReaderFactory.<DNAQualitySequenceI>getReader(canonicalPath);
                            reader = new AlternatingQReader(reader1, reader2);
                        } else {
                            canonicalPath = wd.getSequenceFiles()[0].getCanonicalPath();
                            reader = SeqReaderFactory.<DNASequenceI>getReader(canonicalPath);
                        }
                    } catch (IOException | SeqStoreException ex) {
                        m.SeqRun().delete(seqrun);
                        publish(ex);
                        extract.childChanged();
                        return null;
                    }
                    final UploadBaseI uploader = m.Sequence().createUploader(seqrun, reader);
                    MGXTask run = new MGXTask("Upload " + seqrun.getName()) {
                        @Override
                        public boolean process() {
                            boolean success = uploader.upload();
                            if (!success) {
                                publish(new MGXException(uploader.getErrorMessage()));
                            }
                            return success;
                        }

                        @Override
                        public void finished() {
                            super.finished();
                            extract.childChanged();

                            if (wd.runDefaultTools()) {
                                try {
                                    m.Job().runDefaultTools(seqrun);
                                } catch (MGXException ex) {
                                    publish(ex);
                                }
                            }
                        }

                        @Override
                        public void failed(String reason) {
                            MGXMasterI m = Utilities.actionsGlobalContext().lookup(MGXMasterI.class);
                            try {
                                m.SeqRun().delete(seqrun);
                            } catch (MGXException ex) {
                                Exceptions.printStackTrace(ex);
                            }
                            extract.childChanged();
                            super.failed(reason);
                        }

                        @Override
                        public void propertyChange(PropertyChangeEvent pce) {
                            if (pce.getPropertyName().equals(TransferBaseI.NUM_ELEMENTS_TRANSFERRED)) {
                                setStatus(NumberFormat.getInstance(Locale.US).format(pce.getNewValue()) + " sequences sent");
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
