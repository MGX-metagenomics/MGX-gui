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
import de.cebitec.mgx.gui.controller.RBAC;
import de.cebitec.mgx.gui.nodefactory.MGXNodeFactoryBase;
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
import java.text.DecimalFormat;
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

    private final MGXNodeFactoryBase parent;

    public AddSeqRun(final MGXNodeFactoryBase snf) {
        this.parent = snf;
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
                protected SeqRunI doInBackground() throws MGXException {
                    final SeqRunI seqrun = m.SeqRun().create(extract, wd.getSeqRunName(), wd.getSequencingMethod(), wd.getSequencingTechnology(), wd.getSubmittedToINSDC(), wd.getAccession());
                    // create a sequence reader
                    String canonicalPath;
                    SeqReaderI<? extends DNASequenceI> reader;
                    try {
                        canonicalPath = wd.getSequenceFile().getCanonicalPath();
                        reader = SeqReaderFactory.<DNASequenceI>getReader(canonicalPath);
                    } catch (IOException | SeqStoreException ex) {
                        m.SeqRun().delete(seqrun);
                        parent.refreshChildren();
                        extract.modified();
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
                            return success;
                        }

                        @Override
                        public void finished() {
                            super.finished();
                            parent.refreshChildren();
                            extract.modified();
                            //seqrun.modified();

                            if (wd.runDefaultTools()) {
                                // FIXME
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
                            parent.refreshChildren();
                            extract.modified();
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
