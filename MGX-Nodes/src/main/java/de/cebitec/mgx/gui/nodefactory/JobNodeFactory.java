package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.exception.MGXLoggedoutException;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.ToolI;
import de.cebitec.mgx.gui.nodes.JobNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 *
 * @author sjaenick
 */
public class JobNodeFactory extends MGXNodeFactoryBase<JobI> {

    public JobNodeFactory(MGXMasterI master) {
        super(master);
    }

    @Override
    protected boolean addKeys(List<JobI> toPopulate) {
        List<JobI> tmp = new ArrayList<>();
        try {
            Iterator<SeqRunI> iter = getMaster().SeqRun().fetchall();
            while (iter != null && iter.hasNext()) {
                if (Thread.interrupted()) {
                    getMaster().log(Level.INFO, "interrupted in NF");
                    toPopulate.clear();
                    return true;
                }
                SeqRunI sr = iter.next();

                try {
                    for (JobI j : sr.getMaster().Job().BySeqRun(sr)) {
                        if (j.getTool() == null) {
                            // trigger fetch of tool
                            ToolI t = sr.getMaster().Tool().ByJob(j);
                        }
                        //j.addPropertyChangeListener(this);
                        tmp.add(j);
                    }
                } catch (MGXLoggedoutException lex) {
                    toPopulate.clear();
                    return true;
                } catch (MGXException ex) {
                    // silently ignore exception here, since it might
                    // be cause by an intermediate refresh while a 
                    // deletion of one of the objects is in progress
                }
            }
            toPopulate.addAll(tmp);
            Collections.sort(toPopulate);
        } catch (MGXLoggedoutException ex) {
            toPopulate.clear();
            return true;
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
        }
        return true;

    }

    @Override
    protected Node createNodeFor(JobI job) {
        return new JobNode(job);
    }

    public void destroy() {
    }

}
