package de.cebitec.mgx.gui.datamodel.tree;

import de.cebitec.mgx.gui.datamodel.Attribute;
import de.cebitec.mgx.gui.datamodel.Identifiable;
import java.util.Set;
import java.util.logging.Logger;

/**
 *
 * @author sj
 */
public class Checker {

    public static void sanityCheck(final Set<Attribute> map) {
        for (Attribute a : map) {
            if (a.getParentID() != Identifiable.INVALID_IDENTIFIER) {
                boolean hasParent = false;
                for (Attribute b : map) {
                    if (a.getParentID() == b.getId()) {
                        hasParent = true;
                    }
                }
                if (!hasParent) {
                    System.err.println("no parent for " + a.getValue());
                    System.err.println("was looking for id " + a.getParentID());
                    assert false;
                }
            }
        }
    }

    public static <T> void checkTree(Tree<T> tree) {
        assert tree.getRoot() != null;

        for (Node<T> node : tree.getNodes()) {
            Logger.getAnonymousLogger().info("checking "+node.getAttribute().getValue());
            if (tree.getRoot().equals(node)) {
                assert node.isRoot();
                assert node.getParent() == null;
                assert node.getAttribute().getParentID() == Identifiable.INVALID_IDENTIFIER;
            } else {
                assert !node.isRoot();
                assert node.getParent() != null;
                assert node.getAttribute().getParentID() != Identifiable.INVALID_IDENTIFIER;
            }

            if (node.getParent() == null && !tree.getRoot().equals(node)) {
                Logger.getAnonymousLogger().info("non-root node " + node.getAttribute().getValue() + " has no parent");
                assert false;
            }
        }
    }
}
