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
public interface NodeProviderI {
 
    public Node getRootNode();
    
    public Node[] getSelectedNodes();
}
