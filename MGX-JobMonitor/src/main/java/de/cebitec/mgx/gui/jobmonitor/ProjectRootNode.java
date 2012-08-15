
package de.cebitec.mgx.gui.jobmonitor;

import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;

/**
 *
 * @author sjaenick
 */
public class ProjectRootNode extends AbstractNode {
    
    public ProjectRootNode(String projName, Children children) {
        super(children);
        setDisplayName(projName);
    }
}