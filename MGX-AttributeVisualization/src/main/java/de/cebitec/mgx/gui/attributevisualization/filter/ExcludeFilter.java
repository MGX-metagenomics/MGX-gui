
package de.cebitec.mgx.gui.attributevisualization.filter;

import de.cebitec.mgx.gui.datamodel.Attribute;
import de.cebitec.mgx.gui.datamodel.misc.Distribution;
import de.cebitec.mgx.gui.datamodel.misc.Pair;
import de.cebitec.mgx.gui.groups.VisualizationGroup;
import java.util.List;
import java.util.Set;

/**
 *
 * @author sj
 */
public class ExcludeFilter implements VisFilterI<Distribution> {
    
    private final Set<Attribute> blacklist;

    public ExcludeFilter(Set<Attribute> blacklist) {
        this.blacklist = blacklist;
    }

    @Override
    public List<Pair<VisualizationGroup, Distribution>> filter(List<Pair<VisualizationGroup, Distribution>> in) {
        for (Pair<VisualizationGroup, Distribution> p : in) {
            Distribution d = p.getSecond();
            for (Attribute x : blacklist) {
                d.remove(x);
            }
        }
        return in;
    }
    
}
