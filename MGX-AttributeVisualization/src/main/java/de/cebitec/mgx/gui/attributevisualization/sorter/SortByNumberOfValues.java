package de.cebitec.mgx.gui.attributevisualization.sorter;

import de.cebitec.mgx.api.model.AttributeTypeI;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author sjaenick
 */
public class SortByNumberOfValues<T> implements Comparator<AttributeTypeI> {

    Map<AttributeTypeI, Set<T>> map;

    public void setMap(Map<AttributeTypeI, Set<T>> data) {
        map = data;
    }

    @Override
    public int compare(AttributeTypeI o1, AttributeTypeI o2) {
        return map != null
                ? Integer.compare(map.get(o1).size(), map.get(o2).size())
                : 0;
    }
}
