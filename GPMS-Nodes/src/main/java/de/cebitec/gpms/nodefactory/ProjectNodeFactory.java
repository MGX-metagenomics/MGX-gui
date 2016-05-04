package de.cebitec.gpms.nodefactory;

import de.cebitec.gpms.core.GPMSException;
import de.cebitec.gpms.core.MembershipI;
import de.cebitec.gpms.nodesupport.GPMSNodeSupport;
import de.cebitec.gpms.rest.GPMSClientI;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.nodes.NodeEvent;
import org.openide.nodes.NodeListener;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.NodeReorderEvent;
import org.openide.util.Exceptions;

/**
 *
 * @author sj
 */
public class ProjectNodeFactory extends ChildFactory<MembershipI> implements NodeListener {

    private final GPMSClientI gpmsclient;
    private final List<MembershipI> tmpData = new ArrayList<>();
    private final static Comparator<MembershipI> sortByName = new SortProjects();

    public ProjectNodeFactory(GPMSClientI gpms) {
        this.gpmsclient = gpms;
        gpmsclient.addPropertyChangeListener(this);
    }

    @Override
    protected synchronized boolean createKeys(List<MembershipI> list) {
        if (!gpmsclient.loggedIn()) {
            return true;
        }
        Iterator<MembershipI> iter = null;
        try {
            iter = gpmsclient.getMemberships();
        } catch (GPMSException ex) {
            Exceptions.printStackTrace(ex);
            return true;
        }
        tmpData.clear();
        while (iter != null && iter.hasNext()) {
            MembershipI m = iter.next();
//            if ("MGX".equals(m.getProject().getProjectClass().getName())) {
//                tmpData.add(m);
//            }
            if (GPMSNodeSupport.isSupported(m)) {
                tmpData.add(m);
            }
        }
        Collections.sort(tmpData, sortByName);
        list.addAll(tmpData);
        return true;
    }

    @Override
    protected Node createNodeForKey(MembershipI m) {
        Node node = null;
        if (GPMSNodeSupport.isSupported(m)) {
            node = GPMSNodeSupport.createProjectNode(gpmsclient.createMaster(m));
            if (node != null) {
                node.addNodeListener(this);
            }
        }
        return node;
    }

    @Override
    public void childrenAdded(NodeMemberEvent ev) {
        // this.refresh(true);
    }

    @Override
    public void childrenRemoved(NodeMemberEvent ev) {
        // this.refresh(true);
    }

    @Override
    public void childrenReordered(NodeReorderEvent ev) {
        //this.refresh(true);
    }

    @Override
    public void nodeDestroyed(NodeEvent ev) {
        refresh(true);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource().equals(gpmsclient) && GPMSClientI.PROP_LOGGEDIN.equals(evt.getPropertyName())) {
            refresh(true);
        }
    }

    private final static class SortProjects implements Comparator<MembershipI> {

        @Override
        public int compare(MembershipI m1, MembershipI m2) {
            // sort by project name
            return m1.getProject().getName().toLowerCase().compareTo(m2.getProject().getName().toLowerCase());
        }

    }
}
