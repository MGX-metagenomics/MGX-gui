package de.cebitec.mgx.gui.attributevisualization.filter;

import de.cebitec.mgx.gui.attributevisualization.data.VisualizationGroup;
import de.cebitec.mgx.gui.datamodel.Pair;
import java.util.List;

/**
 *
 * @author sjaenick
 */
public interface VisFilterI<T> {
    
    public List<Pair<VisualizationGroup, T>> filter(List<Pair<VisualizationGroup, T>> dists);
    
}
