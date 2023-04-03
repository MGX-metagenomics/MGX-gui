/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.actions;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.groups.FileType;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.common.JobState;
import de.cebitec.mgx.gui.swingutils.util.FileChooserUtils;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Serial;
import javax.swing.AbstractAction;
import javax.swing.SwingWorker;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Utilities;

/**
 *
 * @author sjaenick
 */
public class SaveToolXML extends AbstractAction {

    @Serial
    private static final long serialVersionUID = 1L;

    public SaveToolXML() {
        putValue(NAME, "Download workflow");
    }

    @Override
    public boolean isEnabled() {
        final JobI job = Utilities.actionsGlobalContext().lookup(JobI.class);
        return super.isEnabled() && job != null && job.getStatus() != JobState.IN_DELETION;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final JobI job = Utilities.actionsGlobalContext().lookup(JobI.class);
        final MGXMasterI m = job.getMaster();
        final String fname = FileChooserUtils.selectNewFilename(new FileType[]{FileType.XML}, job.getTool().getName() + "-" + job.getTool().getVersion());
        if (fname == null || fname.trim().isEmpty()) {
            return;
        }
        SwingWorker<String, Void> sw = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                return m.Tool().getDefinition(job.getTool());
            }

            @Override
            protected void done() {
                super.done();
                try (final BufferedWriter bw = new BufferedWriter(new FileWriter(fname))) {
                    bw.write(get());
                } catch (Exception ex) {
                    NotifyDescriptor nd = new NotifyDescriptor.Message("Error: " + ex.getMessage(), NotifyDescriptor.ERROR_MESSAGE);
                    DialogDisplayer.getDefault().notify(nd);
                    return;
                }
                NotifyDescriptor nd = new NotifyDescriptor.Message("Workflow saved to " + fname, NotifyDescriptor.INFORMATION_MESSAGE);
                DialogDisplayer.getDefault().notify(nd);
            }
        };
        sw.execute();
    }

}
