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

    public static <V, T, U> VisFilterI append(VisFilterI first, VisFilterI second) {
        return new VFCombinedImpl(first, second);
    }
    
    private final static class VFCombinedImpl implements VisFilterI {

    VisFilterI first;
    VisFilterI second;

    public VFCombinedImpl(VisFilterI first, VisFilterI second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public List<Pair<VisualizationGroup, Distribution>> filter(List<Pair<VisualizationGroup, Distribution>> dists) {
        return second.filter(first.filter(dists));
    }
}
}
