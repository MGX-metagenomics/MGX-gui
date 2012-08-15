package de.cebitec.mgx.gui.jobmonitor;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.Job;
import de.cebitec.mgx.gui.nodes.JobNode;
import java.util.List;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sjaenick
 */
public class JobChildren extends Children.Keys<Job> {
    
    private List<Job> jobs;
    private MGXMaster m;

    public JobChildren(MGXMaster m, List<Job> jobs) {
        this.m = m;
        this.jobs = jobs;
        setKeys(jobs);
    }
    
    @Override
    protected Node[] createNodes(Job t) {
        return new Node[] { new JobNode(Children.LEAF, Lookups.fixed(m, t), t)};
    }
    
}
