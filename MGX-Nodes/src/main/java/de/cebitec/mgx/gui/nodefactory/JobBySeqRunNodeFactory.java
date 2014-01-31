package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.Job;
import de.cebitec.mgx.gui.datamodel.SeqRun;
import de.cebitec.mgx.gui.datamodel.Tool;
import de.cebitec.mgx.gui.nodes.JobNode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.List;
import javax.swing.Timer;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author sjaenick
 */
public class JobBySeqRunNodeFactory extends JobNodeFactory {

    private final SeqRun run;

    public JobBySeqRunNodeFactory(SeqRun run) {
        super();
        this.run = run;
        Timer timer = new Timer(1000 * 10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshChildren();
            }
        });
        timer.start();
    }

    @Override
    protected boolean createKeys(List<Job> toPopulate) {
        MGXMaster master = (MGXMaster) run.getMaster();
        for (Job j : master.Job().BySeqRun(run)) {
            j.setSeqrun(run);
            Tool t = master.Tool().ByJob(j.getId());
            j.setTool(t);
            toPopulate.add(j);
        }
        Collections.sort(toPopulate);
        return true;
    }

    @Override
    protected Node createNodeForKey(Job key) {
        JobNode node = new JobNode((MGXMaster) key.getMaster(), key, Children.LEAF);
        node.addNodeListener(this);
        return node;
    }
}
