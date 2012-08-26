package de.cebitec.mgx.gui.attributevisualization.filter;

import de.cebitec.mgx.gui.datamodel.Attribute;
import de.cebitec.mgx.gui.datamodel.misc.Distribution;
import de.cebitec.mgx.gui.datamodel.misc.Pair;
import de.cebitec.mgx.gui.groups.VisualizationGroup;
import java.util.List;
import java.util.Map.Entry;

/**
 *
 * @author sjaenick
 */
public class ToFractionFilter implements VisFilterI<Distribution> {

    @Override
    public List<Pair<VisualizationGroup, Distribution>> filter(List<Pair<VisualizationGroup, Distribution>> dists) {
        for (Pair<VisualizationGroup, Distribution> pair : dists) {
            convertSingleDistribution(pair.getSecond());
        }
        return dists;
    }

    private Distribution convertSingleDistribution(Distribution dist) {

        // sum up
        long total = dist.getTotalClassifiedElements();
        System.out.println("got total of "+total);

        for (Entry<Attribute, Number> e : dist.entrySet()) {
            //e.setValue((double)e.getValue().longValue() / total);
            dist.put(e.getKey(), (double)e.getValue().longValue() / total);
        }
        
        return dist;
    }
}
