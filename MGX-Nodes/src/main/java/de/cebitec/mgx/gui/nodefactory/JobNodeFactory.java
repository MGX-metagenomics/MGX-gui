package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.ToolI;
import de.cebitec.mgx.gui.nodes.JobNode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import javax.swing.Timer;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.NodeEvent;
import org.openide.nodes.NodeListener;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.NodeReorderEvent;
import org.openide.util.Exceptions;

/**
 *
 * @author sjaenick
 */
public class JobNodeFactory extends ChildFactory<JobI> implements NodeListener {

    protected final MGXMasterI master;
    private final Timer timer;

    public JobNodeFactory(MGXMasterI master) {
        timer = new Timer(1000 * 10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //refreshChildren();
                refresh(false);
            }
        });
        timer.start();
        this.master = master;
    }

    @Override
    protected boolean createKeys(List<JobI> toPopulate) {
        List<JobI> tmp = new ArrayList<>();
        try {
            Iterator<SeqRunI> iter = master.SeqRun().fetchall();
            while (iter != null && iter.hasNext()) {
                if (Thread.interrupted()) { 
                    master.log(Level.INFO, "interrupted in NF");
                    return true;
                }
                SeqRunI sr = iter.next();
                for (JobI j : master.Job().BySeqRun(sr)) {
                    ToolI t = master.Tool().ByJob(j);
                    j.setTool(t);
                    tmp.add(j);
                }
            }
            toPopulate.addAll(tmp);
            Collections.sort(toPopulate);
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
        }
        return true;

    }

    @Override
    protected Node createNodeForKey(JobI key) {
        JobNode node = new JobNode(key.getMaster(), key, Children.LEAF);
        node.addNodeListener(this);
        return node;
    }

    public final void refreshChildren() {
//        if (EventQueue.isDispatchThread()) {
//            NonEDT.invoke(new Runnable() {
//
//                @Override
//                public void run() {
//                    refreshChildren();
//                }
//            });
//            return;
//        }

        refresh(true);
    }

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
        ev.getNode().removeNodeListener(this);
        refresh(true);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        //refresh(true);
    }
    
    public void destroy() {
        timer.stop();
    }
}
