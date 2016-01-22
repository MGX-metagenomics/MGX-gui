///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package de.cebitec.mgx.gui.nodefactory;
//
//import de.cebitec.mgx.gui.nodeactions.RemoveReplicateAction;
//import de.cebitec.mgx.api.groups.ReplicateI;
//import de.cebitec.mgx.api.groups.VisualizationGroupI;
//import de.cebitec.mgx.gui.nodes.ReplicateNode;
//import java.awt.event.ActionEvent;
//import java.beans.PropertyChangeEvent;
//import java.util.Collection;
//import javax.swing.AbstractAction;
//import javax.swing.Action;
//import org.openide.nodes.FilterNode;
//import org.openide.nodes.NodeEvent;
//import org.openide.nodes.NodeListener;
//import org.openide.nodes.NodeMemberEvent;
//import org.openide.nodes.NodeReorderEvent;
//import org.openide.util.lookup.Lookups;
//
///**
// *
// * @author sjaenick
// */
//public class ReplicateFilterNode extends FilterNode implements NodeListener {
//
//    private final ReplicateNode n;
//    private final ReplicateNodeFactory nf;
//
//    public ReplicateFilterNode(ReplicateNode node, ReplicateNodeFactory nodef) {
//        super(node, Children.LEAF, Lookups.singleton(node.getContent()));
//        disableDelegation(DELEGATE_SET_DISPLAY_NAME + DELEGATE_GET_ACTIONS);
//        n = node;
//        nf = nodef;
//        node.addNodeListener(this);
//    }
//
//    @Override
//    public Action[] getActions(boolean context) {
//        return new Action[]{new RemoveReplicateAction()};
//    }
//
//    @Override
//    public void childrenAdded(NodeMemberEvent ev) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    @Override
//    public void childrenRemoved(NodeMemberEvent ev) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    @Override
//    public void childrenReordered(NodeReorderEvent ev) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    @Override
//    public void nodeDestroyed(NodeEvent ev) {
//        ReplicateI repl = getLookup().lookup(ReplicateI.class);
//        repl.getReplicateGroup().remove(repl);
//    }
//
//    @Override
//    public void propertyChange(PropertyChangeEvent evt) {
//    }
//
//
//}
