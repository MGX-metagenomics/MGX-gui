package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.exception.MGXLoggedoutException;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.ToolI;
import de.cebitec.mgx.gui.nodes.JobNode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import javax.swing.Timer;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.NodeListener;
import org.openide.util.Exceptions;

/**
 *
 * @author sjaenick
 */
public class JobNodeFactory extends MGXNodeFactoryBase<JobI> implements NodeListener {

    private final Timer timer;

    public JobNodeFactory(MGXMasterI master) {
        super(master);
        timer = new Timer(1000 * 10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                refresh(false);
                if (!refreshing) {
                    refreshing = true;
                    refresh(true);
                    refreshing = false;
                }
            }
        });
        timer.start();
    }

    @Override
    protected boolean addKeys(List<JobI> toPopulate) {
        List<JobI> tmp = new ArrayList<>();
        try {
            Iterator<SeqRunI> iter = getMaster().SeqRun().fetchall();
            while (iter != null && iter.hasNext()) {
                if (Thread.interrupted()) {
                    getMaster().log(Level.INFO, "interrupted in NF");
                    return true;
                }
                SeqRunI sr = iter.next();

                try {
                    for (JobI j : sr.getMaster().Job().BySeqRun(sr)) {
                        ToolI t = sr.getMaster().Tool().ByJob(j);
                        tmp.add(j);
                    }
                } catch (MGXException ex) {
                    // silently ignore exception here, since it might
                    // be cause by an intermediate refresh while a 
                    // deletion of one of the objects is in progress
                }
            }
            toPopulate.addAll(tmp);
            Collections.sort(toPopulate);
        } catch (MGXLoggedoutException ex) {
            toPopulate.clear();
            return true;
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
        }
        return true;

    }

    @Override
    protected Node createNodeForKey(JobI key) {
        JobNode node = new JobNode(key, Children.LEAF);
        node.addNodeListener(this);
        return node;
    }

//    public final void refreshChildren() {
////        if (EventQueue.isDispatchThread()) {
////            NonEDT.invoke(new Runnable() {
////
////                @Override
////                public void run() {
////                    refreshChildren();
////                }
////            });
////            return;
////        }
//
//        refresh(true);
//    }
//
//    @Override
//    public void childrenAdded(NodeMemberEvent ev) {
//        refresh(true);
//    }
//
//    @Override
//    public void childrenRemoved(NodeMemberEvent ev) {
//        refresh(true);
//    }
//
//    @Override
//    public void childrenReordered(NodeReorderEvent ev) {
//    }
//
//    @Override
//    public void nodeDestroyed(NodeEvent ev) { 
//        ev.getNode().removeNodeListener(this);
//        refresh(true);
//    }
//
//    @Override
//    public void propertyChange(PropertyChangeEvent evt) {
//        //refresh(true);
//    }
    public void destroy() {
        timer.stop();
    }
}
