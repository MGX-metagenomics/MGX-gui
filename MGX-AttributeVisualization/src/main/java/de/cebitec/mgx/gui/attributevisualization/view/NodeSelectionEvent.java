/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.attributevisualization.view;

import org.openide.nodes.Node;

/**
 *
 * @author sjaenick
 */
public class NodeSelectionEvent {
    
    private final Object source;
    private final Node[] selectedNodes;

    public NodeSelectionEvent(Object source, Node[] selectedNodes) {
        this.source = source;
        this.selectedNodes = selectedNodes;
    }

    public Object getSource() {
        return source;
    }

    public Node[] getSelectedNodes() {
        return selectedNodes;
    }
    
}
