package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.Job;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sjaenick
 */
public class JobNode extends MGXNodeBase<Job>  {

    public JobNode(MGXMaster m, Job job, Children c) {
        super(Children.LEAF, Lookups.fixed(m, job), job);
        setDisplayName(job.getTool().getName());
    }
    
}
