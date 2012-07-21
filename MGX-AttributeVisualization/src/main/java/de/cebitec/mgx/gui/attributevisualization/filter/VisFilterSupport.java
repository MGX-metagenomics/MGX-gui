package de.cebitec.mgx.gui.attributevisualization.filter;

import de.cebitec.mgx.gui.groups.VisualizationGroup;
import de.cebitec.mgx.gui.datamodel.misc.Pair;
import java.util.List;

/**
 *
 * @author sjaenick
 */
public final class VisFilterSupport {

    private VisFilterSupport() {
    }

    public static <V, T, U> VisFilterI append(VisFilterI<T> first, VisFilterI<T> second) {
        return new VFCombinedImpl(first, second);
    }
    
    private final static class VFCombinedImpl<T> implements VisFilterI<T> {

    VisFilterI<T> first;
    VisFilterI<T> second;

    public VFCombinedImpl(VisFilterI<T> first, VisFilterI<T> second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public List<Pair<VisualizationGroup, T>> filter(List<Pair<VisualizationGroup, T>> dists) {
        return second.filter(first.filter(dists));
    }
}
}
