
package de.cebitec.mgx.gui.jobmonitor;

import de.cebitec.mgx.gui.controller.MGXMaster;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sjaenick
 */
public class ProjectRootNode extends AbstractNode {
    
    public ProjectRootNode(MGXMaster master, Children children) {
        super(children, Lookups.fixed(master));
        setDisplayName(master.getProject());
    }

    ProjectRootNode(String no_project_selected, Children children) {
        super(children);
        setDisplayName(no_project_selected);
    }
}