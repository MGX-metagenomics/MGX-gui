package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.gui.datamodel.Job;
import org.openide.nodes.Children;
import org.openide.util.Lookup;

/**
 *
 * @author sjaenick
 */
public class JobNode extends MGXNodeBase<Job>  {

    public JobNode(Children children, Lookup lookup, Job data) {
        super(children, lookup, data);
        setDisplayName(data.getTool().getName());
    }
    
}
