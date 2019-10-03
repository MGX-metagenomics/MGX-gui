package de.cebitec.mgx.api.visualization.filter;

import de.cebitec.mgx.api.groups.GroupI;
import de.cebitec.mgx.api.misc.Pair;
import java.util.List;

/**
 *
 * @author sjaenick
 */
public final class VisFilterSupport {

    private VisFilterSupport() {
    }

    public static <T, U, V> VisFilterI<T, V> append(VisFilterI<T, U> first, VisFilterI<U, V> second) {
        return new VFCombinedImpl<>(first, second);
    }
    
    private final static class VFCombinedImpl<T, U, V> implements VisFilterI<T, V> {

    VisFilterI<T, U> first;
    VisFilterI<U, V> second;

    public VFCombinedImpl(VisFilterI<T, U> first, VisFilterI<U, V> second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public List<Pair<GroupI, V>> filter(List<Pair<GroupI, T>> dists) {
        return second.filter(first.filter(dists));
    }
}
}
