package de.cebitec.mgx.gui.attributevisualization.data;

import de.cebitec.mgx.gui.attributevisualization.Pair;
import de.cebitec.mgx.gui.datamodel.Attribute;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author sjaenick
 */
public class Distribution {

    Map<Attribute, Number> dist = null;
    List<Attribute> sortOrder = null;

    public Distribution(Map<Attribute, Number> dist) {
        this.dist = dist;
    }

    public List<Pair<Attribute, Number>> get() {
        List<Pair<Attribute, Number>> ret = new ArrayList<Pair<Attribute, Number>>();

        if (sortOrder == null) {
            System.err.println("using defined sort order");
            for (Entry<Attribute, Number> e : dist.entrySet()) {
                ret.add(new Pair<Attribute, Number>(e.getKey(), e.getValue()));
            }
        } else {
            for (Attribute attr : sortOrder) {
                Number n = dist.get(attr);
                if (n != null) {
                    ret.add(new Pair<Attribute, Number>(attr, n));
                }
            }
        }

        return ret;
    }

    public Map<Attribute, Number> getMap() {
        return dist;
    }

    public void setSortOrder(List<Attribute> sortOrder) {
        this.sortOrder = sortOrder;
    }
    
    public List<Attribute> getSortOrder() {
        return sortOrder;
    }
}
