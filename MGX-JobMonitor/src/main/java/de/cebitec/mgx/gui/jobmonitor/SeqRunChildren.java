package de.cebitec.mgx.gui.jobmonitor;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.Job;
import de.cebitec.mgx.gui.datamodel.SeqRun;
import de.cebitec.mgx.gui.nodes.SeqRunNode;
import java.util.List;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author sjaenick
 */
public class SeqRunChildren extends Children.Keys<SeqRun> {

    private java.util.Map<SeqRun, List<Job>> map;
    private MGXMaster m;

    public SeqRunChildren(MGXMaster m, java.util.Map<SeqRun, List<Job>> map) {
        this.m = m;
        this.map = map;
        setKeys(map.keySet());
    }
    
    @Override
    protected Node[] createNodes(SeqRun t) {
        return new Node[] { new SeqRunNode(m, t, new JobChildren(m, map.get(t))) };
    }
    
}
