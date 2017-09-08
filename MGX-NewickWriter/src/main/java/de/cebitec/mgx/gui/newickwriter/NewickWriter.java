/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.newickwriter;

import de.cebitec.mgx.api.model.tree.NodeI;
import de.cebitec.mgx.api.model.tree.TreeI;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

/**
 *
 * @author sj
 */
public class NewickWriter {

    public static <T> void toNewick(Writer w, TreeI<T> tree, boolean includeContent) throws IOException {
        NodeI<T> root = tree.getRoot();
        appendNode(w, root, includeContent);
        w.write(";");
    }

    private static <T> void appendNode(Writer w, NodeI<T> node, boolean includeContent) throws IOException {
        if (node.hasChildren()) {
            w.write("(");
            join(w, node.getChildren(), includeContent);
            w.write(")");
        }
        w.write(node.getAttribute().getValue());
        if (includeContent) {
            w.write(":");
            w.write(node.getContent().toString());
        }
    }

    private static <T> void join(Writer w, Iterable< NodeI<T>> pColl, boolean includeContent) throws IOException {
        Iterator<NodeI<T>> oIter;
        if (pColl == null || (!(oIter = pColl.iterator()).hasNext())) {
            return;
        }
        appendNode(w, oIter.next(), includeContent);
        while (oIter.hasNext()) {
            w.write(",");
            appendNode(w, oIter.next(), includeContent);
        }
    }

}
