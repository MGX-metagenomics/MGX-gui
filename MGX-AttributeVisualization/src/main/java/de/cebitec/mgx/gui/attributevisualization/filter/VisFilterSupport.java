package de.cebitec.mgx.gui.attributevisualization.filter;

import de.cebitec.mgx.gui.attributevisualization.Pair;
import de.cebitec.mgx.gui.attributevisualization.data.Distribution;
import de.cebitec.mgx.gui.attributevisualization.data.VisualizationGroup;
import java.util.List;

/**
 *
 * @author sjaenick
 */
public final class VisFilterSupport {

    private VisFilterSupport() {
    }

    public static <V, T, U> VisFilter append(VisFilter first, VisFilter second) {
        return new VFCombinedImpl(first, second);
    }
    
    private final static class VFCombinedImpl implements VisFilter {

    VisFilter first;
    VisFilter second;

    public VFCombinedImpl(VisFilter first, VisFilter second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public List<Pair<VisualizationGroup, Distribution>> filter(List<Pair<VisualizationGroup, Distribution>> dists) {
        return second.filter(first.filter(dists));
    }
}
}
