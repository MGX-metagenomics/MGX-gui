/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.wizard.configurations.utilities;

import de.cebitec.mgx.gui.datamodel.Tool;
import java.util.Comparator;
import java.util.Vector;

/**
 * AlphabetSorter sortiert eine Liste nach dem Alphabet.
 *
 *
 * @author pbelmann
 */
public class AlphabetSorter implements Comparator {

    /**
     * vergleicht zwischen zwei Objekten.
     *
     * @param a Objekt
     * @param b Objekt
     * @return groesser, kleiner oder gleich.
     */
    @Override
    public int compare(Object a1, Object b1) {
        Tool a = (Tool) a1;
        Tool b = (Tool) b1;

        if (a.getName() instanceof String && ((String) a.getName()).length() == 0) {
            a = null;
        }
        if (b.getName() instanceof String && ((String) b.getName()).length() == 0) {
            b = null;
        }
        if (a.getName() == null && b.getName() == null) {
            return 0;
        } else if (a.getName() == null) {
            return 1;
        } else if (b.getName() == null) {
            return -1;
        } else if (a.getName() instanceof Comparable) {

            return (a.getName()).compareToIgnoreCase(b.getName());
        } else {

            return a.getName().toString().compareToIgnoreCase(b.getName().toString());
        }
    }
}
