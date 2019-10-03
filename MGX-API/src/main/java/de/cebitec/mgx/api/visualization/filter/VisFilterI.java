package de.cebitec.mgx.api.visualization.filter;

import de.cebitec.mgx.api.groups.GroupI;
import de.cebitec.mgx.api.misc.Pair;
import java.util.List;

/**
 *
 * @author sjaenick
 */
public interface VisFilterI<T, U> {
    
    public List<Pair<GroupI, U>> filter(List<Pair<GroupI, T>> in);
    
}
