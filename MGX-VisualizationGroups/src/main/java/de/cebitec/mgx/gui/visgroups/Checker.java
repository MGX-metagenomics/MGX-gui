package de.cebitec.mgx.gui.visgroups;

import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.Identifiable;
import de.cebitec.mgx.api.model.tree.NodeI;
import de.cebitec.mgx.api.model.tree.TreeI;
import java.util.Set;
import java.util.logging.Logger;

/**
 *
 * @author sj
 */
public class Checker {
    
    //
    // Consistency checks to be applied to validate TreeI operations
    //

    public static void sanityCheck(final Set<AttributeI> map) {
        for (AttributeI a : map) {
            if (a.getParentID() != Identifiable.INVALID_IDENTIFIER) {
                boolean hasParent = false;
                for (AttributeI b : map) {
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

    public static <T> void checkTree(TreeI<T> tree) {
        assert tree.getRoot() != null;

        for (NodeI<T> node : tree.getNodes()) {
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
