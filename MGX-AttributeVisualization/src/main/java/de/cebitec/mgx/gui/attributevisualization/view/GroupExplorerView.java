/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.attributevisualization.view;

import de.cebitec.mgx.gui.attributevisualization.GroupFrameBase;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;
import java.io.Serial;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.Node;
import org.openide.nodes.NodeAdapter;
import org.openide.nodes.NodeEvent;
import org.openide.nodes.NodeMemberEvent;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author sjaenick
 */
public class GroupExplorerView<T extends GroupFrameBase> extends JScrollPane { //implements MouseListener {

    @Serial
    private static final long serialVersionUID = 1L;
    
    private final Map<T, Node> componentToNodeMap = new HashMap<>();
    private T selectedComponent = null;
    private transient Node root;
    private transient ExplorerManager exmngr;
    private final NodeMapperI<T> mapper;
    //
    private final InstanceContent content = new InstanceContent();
    private final Lookup lookup = new AbstractLookup(content);
    //
    private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

    public GroupExplorerView(NodeMapperI<T> mapper) {
        //addMouseListener(this);
        super.setViewportView(panel);
        super.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        super.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        this.mapper = mapper;
    }

    @Override
    public void addNotify() {
        super.addNotify();
        exmngr = ExplorerManager.find(this);
        root = exmngr.getRootContext();

        // set up initial node set representation
        Node[] kids = root.getChildren().getNodes(true);
        for (Node n : kids) {
            addNode(n);
        }
        panel.repaint();

        // listen for subsequent changes
        root.addNodeListener(new NodeAdapter() {

            @Override
            public void childrenRemoved(NodeMemberEvent ev) {
                for (Node rmNode : ev.getDelta()) {
                    delNode(rmNode);
                }
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        panel.repaint();
                    }

                });
            }

            @Override
            public void childrenAdded(final NodeMemberEvent ev) {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        for (Node newNode : ev.getDelta()) {
                            addNode(newNode);
                        }
                        panel.repaint();
                    }
                });
            }
        });

        panel.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                // clicked somewhere onto empty panel space
                // clear lookup and set to root node
                if (e.getSource() == panel) {
                    content.set(Collections.emptyList(), null);
                    content.add(root);
                    for (Object o : root.getLookup().lookupAll(Object.class)) {
                        content.add(o);
                    }
                }
            }

        });

    }

    private void delNode(Node oldNode) {
        T component = null;
        for (Entry<T, Node> e : componentToNodeMap.entrySet()) {
            if (e.getValue().equals(oldNode)) {
                component = e.getKey();
                break;
            }
        }
        if (component != null) {
            panel.remove(component);
            componentToNodeMap.remove(component);
            if (component == selectedComponent) {
                selectedComponent = null;
            }
            try {
                component.setSelected(false);
            } catch (PropertyVetoException ex) {
                Exceptions.printStackTrace(ex);
            }
            component.dispose();
            //panel.repaint();
        }
    }

    private void addNode(final Node newNode) {
        final T comp = mapper.getComponent(newNode);
        componentToNodeMap.put(comp, newNode);

        newNode.addNodeListener(new NodeAdapter() {

            @Override
            public void nodeDestroyed(NodeEvent ev) {
                panel.remove(comp);
                componentToNodeMap.remove(comp);
                if (comp == selectedComponent) {
                    selectedComponent = null;
                }
                ev.getNode().removeNodeListener(this);
                panel.repaint();
            }

            @Override
            public void childrenAdded(NodeMemberEvent ev) {
                System.err.println("children added to node " + newNode.getName());
            }

        });

        comp.addNodeSelectionListener(new NodeSelectionListener() {

            @Override
            public void handleNodeSelection(final NodeSelectionEvent nse) {
                Node[] nodes = nse.getSelectedNodes();
                content.set(Collections.emptyList(), null); // clear content
                for (Node n : nodes) {
                    content.add(n);
                    for (Object o : n.getLookup().lookupAll(Object.class)) {
                        content.add(o);
                    }
                }
            }
        });
        panel.add(comp);
        panel.repaint();
    }

    @Override
    public void removeNotify() {
        exmngr = null;
        super.removeNotify();
    }

//    private void setSelectedComponent(T c) {
//        this.selectedComponent = c;
//    }
//
//    private boolean isUnderRoot(Node node) {
//        while (node != null) {
//            if (node.equals(root)) {
//                return true;
//            }
//            node = node.getParentNode();
//        }
//        return false;
//    }
//    @Override
//    @SuppressWarnings("unchecked")
//    public void mouseClicked(MouseEvent ev) {
//        T newSelection = null;
//        System.err.println("Clicked!!");
//
//        Object source = ev.getSource();
//        Set<Node> nodes = new HashSet<>();
//
//        nodes.add(root);
//
//        if (source != this) {
//            T c = (T) source;
//            if (!componentToNodeMap.containsKey(c)) {
//                assert false;
//            }
//            newSelection = c;
//            nodes.addAll(Arrays.asList(c.getSelectedNodes()));
//        }
//
//        for (Node n : nodes) {
//            for (Object o : n.getLookup().lookupAll(Object.class)) {
//                System.err.println("  lkp: " + o);
//            }
//        }
//
//        //if (newSelection != curSelection) {
//        try {
//            setSelectedComponent(newSelection);
//            exmngr.setSelectedNodes(nodes.toArray(new Node[]{}));
//        } catch (PropertyVetoException ex) {
//            Exceptions.printStackTrace(ex);
//        }
//        //}
//
//        panel.repaint();
//    }
//    private void showPopupMenu(MouseEvent evt) {
//        Node[] n = exmngr.getSelectedNodes();
//        if (n.length == 1) {
//            Action[] a = n[0].getActions(true);
//            JPopupMenu popup = Utilities.actionsToPopup(a, this);
//            popup.show(this, evt.getX(), evt.getY());
//        }
//    }
    public Lookup getLookup() {
        return lookup;
    }
}
