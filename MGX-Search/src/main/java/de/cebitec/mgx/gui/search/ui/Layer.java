
package de.cebitec.mgx.gui.search.ui;

import de.cebitec.mgx.api.model.ObservationI;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.util.FastMath;

/**
 *
 * @author sj
 */
public class Layer {
    
    private final List<ObservationI> observations = new ArrayList<>();
    private int lastX = 0;
    
    public boolean add(ObservationI o) {
        int min = FastMath.min(o.getStart(), o.getStop());
        if (min < lastX) {
            return false;
        }
        observations.add(o);
        lastX = FastMath.max(o.getStart(), o.getStop()) + 3; // update and add some extra space
        return true;
    }
    
    public List<ObservationI> getObservations() {
        return observations;
    }
    
}
