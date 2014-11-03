package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.ToolI;
import java.awt.EventQueue;
import java.util.Collections;
import java.util.List;
import org.openide.util.Exceptions;

/**
 *
 * @author sjaenick
 */
public class JobBySeqRunNodeFactory extends JobNodeFactory {

    private final SeqRunI run;

    public JobBySeqRunNodeFactory(MGXMasterI master, SeqRunI run) {
        super(master);
        this.run = run;
    }

    @Override
    protected boolean createKeys(List<JobI> toPopulate) {
        System.err.println(Thread.currentThread().getId() + " createKeys() on EDT? " + EventQueue.isDispatchThread());
        try {
            for (JobI j : master.Job().BySeqRun(run)) {
                //j.setSeqrun(run);
                ToolI t = master.Tool().ByJob(j);
                j.setTool(t);
                toPopulate.add(j);
            }
            Collections.sort(toPopulate);
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
        }
        return true;
    }
}
