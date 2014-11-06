/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.actions;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.MGXReferenceI;
import de.cebitec.mgx.api.model.MappingI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.gui.mapping.MappingCtx;
import de.cebitec.mgx.gui.mapping.viewer.TopComponentViewer;
import de.cebitec.mgx.gui.swingutils.NonEDT;
import de.cebitec.mgx.gui.wizard.mapping.MappingWizardWizardAction;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import org.openide.util.Exceptions;
import org.openide.util.Utilities;

/**
 *
 * @author sj
 */
public class OpenMappingBySeqRun extends OpenMappingBase {

    private Iterator<MappingI> mappings;
    private boolean hasData = false;

    public OpenMappingBySeqRun() {
        super();
        final SeqRunI run = Utilities.actionsGlobalContext().lookup(SeqRunI.class);

        if (run == null) {
            return;
        }
        NonEDT.invokeAndWait(new Runnable() {

            @Override
            public void run() {
                mappings = run.getMaster().Mapping().BySeqRun(run);
                hasData = mappings.hasNext();
            }
        });

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        final SeqRunI run = Utilities.actionsGlobalContext().lookup(SeqRunI.class);

        SwingWorker<List<MappingCtx>, Void> worker = new SwingWorker<List<MappingCtx>, Void>() {

            @Override
            protected List<MappingCtx> doInBackground() throws Exception {
                List<MappingCtx> ctxs = new ArrayList<>();
                MGXMasterI master = run.getMaster();
                Iterator<MappingI> mappings = master.Mapping().BySeqRun(run);
                while (mappings.hasNext()) {
                    MappingI m = mappings.next();

                    JobI job = master.Job().fetch(m.getJobID());
                    if (job.getSeqrun() == null) {
                        job.setSeqrun(run);
                    }
                    if (job.getTool() == null) {
                        job.setTool(master.Tool().ByJob(job));
                    }
                    MGXReferenceI ref = master.Reference().fetch(m.getReferenceID());
                    ctxs.add(new MappingCtx(m, ref, job, run));
                }
                return ctxs;
            }

            @Override
            protected void done() {
                List<MappingCtx> ctxs = null;
                try {
                    ctxs = get();
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }
                MappingCtx selectedMapping = null;

                if (ctxs != null && ctxs.size() == 1) {
                    // only one mapping exists, show it..
                    selectedMapping = ctxs.get(0);
                } else if (ctxs != null && ctxs.size() > 1) {
                    // let user choose
                    selectedMapping = MappingWizardWizardAction.selectMapping(ctxs);
                } else {
                    // should not happen
                    assert false;
                }

                if (selectedMapping != null) {
                    TopComponentViewer component = new TopComponentViewer(selectedMapping);
                    component.open();
                }
                super.done();
            }

        };
        worker.execute();
    }

    @Override
    public boolean isEnabled() {
        return hasData && super.isEnabled();
    }
}
