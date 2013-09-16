package de.cebitec.mgx.gui.attributevisualization.sorter;

import de.cebitec.mgx.gui.datamodel.AttributeType;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author sjaenick
 */
public class SortByNumberOfValues<T> implements Comparator<AttributeType> {

    Map<AttributeType, Set<T>> map;

    public void setMap(Map<AttributeType, Set<T>> data) {
        map = data;
    }

    @Override
    public int compare(AttributeType o1, AttributeType o2) {
        return map != null
                ? Integer.compare(map.get(o1).size(), map.get(o2).size())
                : 0;
    }
}
