package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.ToolI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
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
    protected synchronized boolean createKeys(List<JobI> toPopulate) {
        List<JobI> tmp = new ArrayList<>();
        try {
            for (JobI j : getMaster().Job().BySeqRun(run)) {
                if (Thread.interrupted()) {
                    getMaster().log(Level.INFO, "interrupted in NF");
                    return true;
                }
                
                ToolI t = null;
                try {
                    t = getMaster().Tool().ByJob(j);
                } catch (MGXException ex) {
                    // if a refresh is triggered while a job is being deleted,
                    // this might fail when the job is already gone. silently
                    // ignore this case..
                }

                if (t != null) {
                    j.setTool(t);
                    tmp.add(j);
                }
            }
            toPopulate.addAll(tmp);
            Collections.sort(toPopulate);
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
        }
        return true;
    }
}
