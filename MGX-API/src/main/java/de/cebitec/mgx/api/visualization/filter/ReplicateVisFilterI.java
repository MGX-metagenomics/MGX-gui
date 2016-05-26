package de.cebitec.mgx.api.visualization.filter;

import de.cebitec.mgx.api.groups.ReplicateGroupI;
import de.cebitec.mgx.api.misc.Triple;
import java.util.List;

/**
 *
 * @author sjaenick
 */
public interface ReplicateVisFilterI<T,U>{
    
    public List<Triple<ReplicateGroupI, T, T>> filter(List<Triple<ReplicateGroupI, U, U>> in);
    
}
