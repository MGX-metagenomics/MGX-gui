package de.cebitec.mgx.gui.attributevisualization.filter;

import de.cebitec.mgx.gui.attributevisualization.Pair;
import de.cebitec.mgx.gui.attributevisualization.data.Distribution;
import de.cebitec.mgx.gui.attributevisualization.data.VisualizationGroup;
import java.util.List;

/**
 *
 * @author sjaenick
 */
public interface VisFilter {
    
    public List<Pair<VisualizationGroup, Distribution>> filter(List<Pair<VisualizationGroup, Distribution>> dists);
    
}
