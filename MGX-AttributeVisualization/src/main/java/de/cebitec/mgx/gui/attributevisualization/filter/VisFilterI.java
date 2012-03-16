package de.cebitec.mgx.gui.attributevisualization.filter;

import de.cebitec.mgx.gui.datamodel.Pair;
import de.cebitec.mgx.gui.datamodel.Distribution;
import de.cebitec.mgx.gui.attributevisualization.data.VisualizationGroup;
import java.util.List;

/**
 *
 * @author sjaenick
 */
public interface VisFilterI {
    
    public List<Pair<VisualizationGroup, Distribution>> filter(List<Pair<VisualizationGroup, Distribution>> dists);
    
}
