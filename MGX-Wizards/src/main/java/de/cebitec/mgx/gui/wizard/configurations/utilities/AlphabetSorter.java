package de.cebitec.mgx.gui.wizard.configurations.utilities;

import de.cebitec.mgx.gui.datamodel.Tool;
import java.util.Comparator;

/**
 * AlphabetSorter sortiert eine Liste nach dem Alphabet.
 *
 *
 * @author pbelmann
 */
public class AlphabetSorter implements Comparator<Tool> {

    /**
     * vergleicht zwischen zwei Objekten.
     *
     * @param a Objekt
     * @param b Objekt
     * @return groesser, kleiner oder gleich.
     */
    @Override
    public int compare(Tool a, Tool b) {

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
