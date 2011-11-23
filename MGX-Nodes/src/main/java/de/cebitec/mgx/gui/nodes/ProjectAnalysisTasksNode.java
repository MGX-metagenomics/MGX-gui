package de.cebitec.mgx.gui.nodes;

import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Lookup;

/**
 *
 * @author sj
 */
public class ProjectAnalysisTasksNode extends AbstractNode {

    public ProjectAnalysisTasksNode(Children children, Lookup lookup) {
        super(children, lookup);
        setDisplayName("Analysis Tasks");
    }
}
