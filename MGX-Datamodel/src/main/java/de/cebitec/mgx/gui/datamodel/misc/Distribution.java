package de.cebitec.mgx.gui.datamodel.misc;

import de.cebitec.mgx.gui.datamodel.Attribute;
import java.util.*;

/**
 *
 * @author sjaenick
 */
public class Distribution implements Map<Attribute, Number> {

    private final Map<Attribute, ? extends Number> _data;
    private Set<Attribute> keys = new LinkedHashSet<>();
    private final Map<Attribute, Number> filtered = new HashMap<>();
    private long totalClassifiedElements = -1;

    public Distribution(Map<Attribute, ? extends Number> data, long total) {
        this._data = data;
        keys.addAll(_data.keySet());
        filtered.putAll(_data);
        totalClassifiedElements = total;
    }

    public long getTotalClassifiedElements() {
        assert totalClassifiedElements != -1;
        return totalClassifiedElements;
    }

    @Override
    public int size() {
        return keys.size();
    }

    @Override
    public boolean isEmpty() {
        return keys.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return filtered.containsKey((Attribute) key);
    }

    @Override
    public boolean containsValue(Object value) {
        return filtered.containsValue(value);
    }

    @Override
    public Number get(Object key) {
        return filtered.get(key);
    }

    @Override
    public Number put(Attribute key, Number value) {
        keys.add(key);
        return filtered.put(key, value);
    }

    @Override
    public Number remove(Object key) {
        keys.remove(key);
        return filtered.remove(key);
    }

    @Override
    public void putAll(Map<? extends Attribute, ? extends Number> m) {
        keys.addAll(m.keySet());
        filtered.putAll(m);
    }

    @Override
    public void clear() {
        keys.clear();
        filtered.clear();
    }

    @Override
    public Set<Attribute> keySet() {
        return keys;
    }

    @Override
    public Collection<Number> values() {
        return filtered.values();
    }

    @Override
    public Set<Entry<Attribute, Number>> entrySet() {
        Set<Entry<Attribute, Number>> ret = new LinkedHashSet<>();
        for (Attribute a : keys) {
            ret.add(new AbstractMap.SimpleEntry<>(a, filtered.get(a)));
        }
        return ret;
    }

    public void setOrder(List<Attribute> o) {
        List<Attribute> present = new ArrayList<>();
        for (Attribute a : o) {
            if (filtered.containsKey(a)) {
                present.add(a);
            }
        }
        keys.clear(); keys.addAll(present);
    }

    public void reset() {
        keys.clear();
        filtered.clear();
        keys.addAll(_data.keySet());
        filtered.putAll(_data);
    }
}
