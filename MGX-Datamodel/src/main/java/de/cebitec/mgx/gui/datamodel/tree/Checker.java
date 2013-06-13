package de.cebitec.mgx.gui.datamodel.tree;

import de.cebitec.mgx.gui.datamodel.Attribute;
import de.cebitec.mgx.gui.datamodel.Identifiable;
import java.util.Set;

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
}
