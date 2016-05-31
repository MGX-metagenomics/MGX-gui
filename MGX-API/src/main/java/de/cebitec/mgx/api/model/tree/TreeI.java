/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.cebitec.mgx.api.model.tree;

import de.cebitec.mgx.api.misc.Visualizable;
import de.cebitec.mgx.api.model.AttributeI;
import java.util.Collection;

/**
 *
 * @author sj
 */
public interface TreeI<T> extends Visualizable {
    
//    public MGXMasterI getMaster();

    NodeI<T> createRootNode(AttributeI attr, T content);

    Collection<NodeI<T>> getLeaves();

    Collection<NodeI<T>> getNodes();

    NodeI<T> getRoot();

    boolean isEmpty();

    public int size();
    
}
