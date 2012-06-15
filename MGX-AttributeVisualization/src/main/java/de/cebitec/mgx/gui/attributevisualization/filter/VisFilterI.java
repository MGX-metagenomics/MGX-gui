package de.cebitec.mgx.gui.attributevisualization.filter;

import de.cebitec.mgx.gui.datamodel.Pair;
import de.cebitec.mgx.gui.groups.VisualizationGroup;
import java.util.List;

/**
 *
 * @author sjaenick
 */
public interface VisFilterI<T> {
    
    public List<Pair<VisualizationGroup, T>> filter(List<Pair<VisualizationGroup, T>> in);
    
}
