
package de.cebitec.mgx.gui.search.ui;

import de.cebitec.mgx.gui.datamodel.Observation;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author sj
 */
public class Layer {
    
    private final List<Observation> observations = new LinkedList<>();
    private int lastX = 0;
    
    public boolean add(Observation o) {
        int min = Math.min(o.getStart(), o.getStop());
        if (min < lastX) {
            return false;
        }
        observations.add(o);
        lastX = Math.max(o.getStart(), o.getStop()) + 3; // update and add some extra space
        return true;
    }
    
    public List<Observation> getObservations() {
        return observations;
    }
    
}
