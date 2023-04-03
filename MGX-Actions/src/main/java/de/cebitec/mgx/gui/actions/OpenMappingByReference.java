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
import de.cebitec.mgx.gui.pool.MGXPool;
import de.cebitec.mgx.gui.wizard.mapping.MappingWizardWizardAction;
import java.awt.event.ActionEvent;
import java.io.Serial;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import org.openide.util.Exceptions;
import org.openide.util.Utilities;

/**
 *
 * @author sj
 */
public class OpenMappingByReference extends OpenMappingBase {

    @Serial
    private static final long serialVersionUID = 1L;

    private boolean hasData = false;

    public OpenMappingByReference() {
        super();
        final MGXReferenceI ref = Utilities.actionsGlobalContext().lookup(MGXReferenceI.class);

        if (ref == null) {
            return;
        }

        Iterator<MappingI> mappings = null;
        try {
            mappings = ref.getMaster().Mapping().ByReference(ref);
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
        }
        hasData = mappings != null && mappings.hasNext();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        final MGXReferenceI ref = Utilities.actionsGlobalContext().lookup(MGXReferenceI.class);

        final MGXMasterI master = ref.getMaster();
        Iterator<MappingI> iter;
        try {
            iter = master.Mapping().ByReference(ref);
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
            return;
        }

        List<MappingI> mappings = new ArrayList<>();
        while (iter.hasNext()) {
            final MappingI m = iter.next();
            mappings.add(m);
        }

        final CountDownLatch done = new CountDownLatch(mappings.size());
        final List<MappingCtx> ctxs = new ArrayList<>(mappings.size());

        for (final MappingI m : mappings) {

            MGXPool.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    JobI job = null;
                    try {
                        job = master.Job().fetch(m.getJobID());
                        if (job.getSeqruns() == null) {
                            Iterator<SeqRunI> iter = master.SeqRun().ByJob(job);
                            List<SeqRunI> runs = new ArrayList<>();
                            while (iter != null && iter.hasNext()) {
                                runs.add(iter.next());
                            }
                            job.setSeqruns(runs.toArray(new SeqRunI[]{}));
                        }
                        if (job.getTool() == null) {
                            master.Tool().ByJob(job); // trigger tool fetch
                        }
                        synchronized (ctxs) {
                            ctxs.add(new MappingCtx(m, ref, job));
                        }
                    } catch (MGXException ex) {
                        Exceptions.printStackTrace(ex);
                    } finally {
                        done.countDown();
                    }
                }
            });

        }

        try {
            done.await();
        } catch (InterruptedException ex) {
            Exceptions.printStackTrace(ex);
            return;
        }

        MappingCtx selectedMapping = null;

        if (ctxs.size() == 1) {
            // only one mapping exists, show it..
            selectedMapping = ctxs.get(0);
        } else if (ctxs.size() > 1) {
            // let user choose
            selectedMapping = MappingWizardWizardAction.selectMapping(ctxs);
        } else {
            // should not happen
            assert false;
        }

        if (selectedMapping != null) {
            MappingViewerTopComponent component = new MappingViewerTopComponent(selectedMapping);
            component.open();
        }

    }

    @Override
    public boolean isEnabled() {
        return hasData && super.isEnabled();
    }
}
