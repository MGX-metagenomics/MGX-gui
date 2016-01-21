/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.attributevisualization.view;

/**
 *
 * @author sjaenick
 */
public interface NodeSelectionProvider {

    public void addNodeSelectionListener(NodeSelectionListener nsl);

    public void removeNodeSelectionListener(NodeSelectionListener nsl);
}
