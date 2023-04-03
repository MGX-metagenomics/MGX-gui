/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.actions;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.MGXReferenceI;
import de.cebitec.mgx.api.model.MappingI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.gui.mapping.MappingCtx;
import de.cebitec.mgx.gui.mapping.viewer.MappingViewerTopComponent;
import de.cebitec.mgx.gui.wizard.mapping.MappingWizardWizardAction;
import java.awt.event.ActionEvent;
import java.io.Serial;
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

    @Serial
    private static final long serialVersionUID = 1L;
    
    private Iterator<MappingI> mappings;
    private boolean hasData = false;

    public OpenMappingBySeqRun() {
        super();
        final SeqRunI run = Utilities.actionsGlobalContext().lookup(SeqRunI.class);

        if (run == null) {
            return;
        }

        try {
            mappings = run.getMaster().Mapping().BySeqRun(run);
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
        }
        hasData = mappings != null && mappings.hasNext();
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
                    if (job.getSeqruns() == null || job.getSeqruns().length == 0) {
                        Iterator<SeqRunI> iter = master.SeqRun().ByJob(job);
                        List<SeqRunI> runs = new ArrayList<>();
                        while (iter != null && iter.hasNext()) {
                            runs.add(iter.next());
                        }
                        job.setSeqruns(runs.toArray(new SeqRunI[]{}));
                    }
                    if (job.getTool() == null) {
                        job.setTool(master.Tool().ByJob(job));
                    }
                    MGXReferenceI ref = master.Reference().fetch(m.getReferenceID());
                    ctxs.add(new MappingCtx(m, ref, job));
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
                    MappingViewerTopComponent component = new MappingViewerTopComponent(selectedMapping);
                    //component.createView(selectedMapping);
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
