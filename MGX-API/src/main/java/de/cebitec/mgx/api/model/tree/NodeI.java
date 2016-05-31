/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.cebitec.mgx.api.model.tree;

import de.cebitec.mgx.api.model.AttributeI;
import java.util.Set;

/**
 *
 * @author sj
 */
public interface NodeI<T> {

    NodeI<T> addChild(AttributeI attr, T content);

    AttributeI getAttribute();

    Set<NodeI<T>> getChildren();

    T getContent();

    int getDepth();

    long getId();

    NodeI<T> getParent();

    NodeI<T>[] getPath();

    boolean hasChildren();

    boolean isLeaf();

    boolean isRoot();

    void setContent(T value);
    
}
