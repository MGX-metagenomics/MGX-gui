package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.ToolI;
import de.cebitec.mgx.gui.nodes.JobNode;
import de.cebitec.mgx.gui.swingutils.NonEDT;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.swing.Timer;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.NodeEvent;
import org.openide.nodes.NodeListener;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.NodeReorderEvent;

/**
 *
 * @author sjaenick
 */
public class JobNodeFactory extends ChildFactory<JobI> implements NodeListener {

    private MGXMasterI master;

    public JobNodeFactory(MGXMasterI master) {
        this();
        this.master = master;
    }

    protected JobNodeFactory() {
        Timer timer = new Timer(1000 * 10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshChildren();
            }
        });
        timer.start();
    }

    @Override
    protected boolean createKeys(List<JobI> toPopulate) {
        Iterator<SeqRunI> iter = master.SeqRun().fetchall();
        while (iter.hasNext()) {
            SeqRunI sr = iter.next();
            for (JobI j : master.Job().BySeqRun(sr)) {
                j.setSeqrun(sr);
                ToolI t = master.Tool().ByJob(j.getId());
                j.setTool(t);
                toPopulate.add(j);
            }
        }
        Collections.sort(toPopulate);
        return true;
    }

    @Override
    protected Node createNodeForKey(JobI key) {
        JobNode node = new JobNode(master, key, Children.LEAF);
        node.addNodeListener(this);
        return node;
    }

    protected boolean refreshing = false;

    public final void refreshChildren() {

        if (EventQueue.isDispatchThread()) {
            NonEDT.invoke(new Runnable() {

                @Override
                public void run() {
                    refreshChildren();
                }
            });
            return;
        }
        if (!refreshing) {
            refreshing = true;
            //System.err.println("refreshing on EDT? " + EventQueue.isDispatchThread());
            refresh(true);
            refreshing = false;
        }
    }

//    public void refreshChildren() {
//        refresh(true);
//    }
    @Override
    public void childrenAdded(NodeMemberEvent ev) {
        refresh(true);
    }

    @Override
    public void childrenRemoved(NodeMemberEvent ev) {
        refresh(true);
    }

    @Override
    public void childrenReordered(NodeReorderEvent ev) {
    }

    @Override
    public void nodeDestroyed(NodeEvent ev) {
        refresh(true);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        //refresh(true);
    }
}
