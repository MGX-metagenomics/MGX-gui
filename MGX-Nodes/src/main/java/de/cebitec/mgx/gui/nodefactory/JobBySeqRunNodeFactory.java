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
            for (JobI j : master.Job().BySeqRun(run)) {
                if (Thread.interrupted()) {
                    master.log(Level.INFO, "interrupted in NF");
                    return true;
                }
                ToolI t = master.Tool().ByJob(j);
                j.setTool(t);
                tmp.add(j);
            }
            toPopulate.addAll(tmp);
            Collections.sort(toPopulate);
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
        }
        return true;
    }
}
