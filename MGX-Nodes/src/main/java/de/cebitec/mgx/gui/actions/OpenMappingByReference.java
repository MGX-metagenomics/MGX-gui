/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.actions;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.Job;
import de.cebitec.mgx.gui.datamodel.Mapping;
import de.cebitec.mgx.gui.datamodel.Reference;
import de.cebitec.mgx.gui.mapping.MappingCtx;
import de.cebitec.mgx.gui.mapping.viewer.TopComponentViewer;
import de.cebitec.mgx.gui.swingutils.NonEDT;
import de.cebitec.mgx.gui.wizard.mapping.MappingWizardWizardAction;
import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import org.openide.util.Exceptions;
import org.openide.util.Utilities;

/**
 *
 * @author sj
 */
public class OpenMappingByReference extends OpenMappingBase {

    private boolean hasData = false;

    public OpenMappingByReference() {
        super();
        final MGXMaster m = Utilities.actionsGlobalContext().lookup(MGXMaster.class);
        final Reference ref = Utilities.actionsGlobalContext().lookup(Reference.class);

        if (m == null || ref == null) {
            return;
        }
        NonEDT.invokeAndWait(new Runnable() {

            @Override
            public void run() {
                Iterator<Mapping> mappings = m.Mapping().ByReference(ref.getId());
                hasData = mappings.hasNext();
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        final MGXMaster master = Utilities.actionsGlobalContext().lookup(MGXMaster.class);
        final Reference ref = Utilities.actionsGlobalContext().lookup(Reference.class);

        SwingWorker<List<MappingCtx>, Void> worker = new SwingWorker<List<MappingCtx>, Void>() {

            @Override
            protected List<MappingCtx> doInBackground() throws Exception {
                List<MappingCtx> ctxs = new LinkedList<>();
                Iterator<Mapping> mappings = master.Mapping().ByReference(ref.getId());
                while (mappings.hasNext()) {
                    Mapping m = mappings.next();

                    Job job = master.Job().fetch(m.getJobID());
                    if (job.getSeqrun() == null) {
                        job.setSeqrun(master.SeqRun().fetch(m.getSeqrunID()));
                    }
                    if (job.getTool() == null) {
                        job.setTool(master.Tool().ByJob(job.getId()));
                    }
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
