package de.cebitec.mgx.gui.datamodel.misc;

import de.cebitec.mgx.gui.datamodel.Attribute;
import de.cebitec.mgx.gui.datamodel.MGXMasterI;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author sj
 */
public class Matrix implements Map<Pair<Attribute, Attribute>, Number> {

    private final Map<Pair<Attribute, Attribute>, Number> _data;
    private long totalClassifiedElements = -1;

    public Matrix(Map<Pair<Attribute, Attribute>, Number> data) {
        this._data = data;
    }

    public Set<Attribute> getFirstAttributes() {
        Set<Attribute> ret = new HashSet<>(_data.size());
        for (Pair<Attribute, Attribute> p : _data.keySet()) {
            ret.add(p.getFirst());
        }
        return ret;
    }

    public Set<Attribute> getSecondAttributes() {
        Set<Attribute> ret = new HashSet<>(_data.size());
        for (Pair<Attribute, Attribute> p : _data.keySet()) {
            ret.add(p.getSecond());
        }
        return ret;
    }

    public long getTotalClassifiedElements() {
        if (totalClassifiedElements == -1) {
            totalClassifiedElements = 0;
            for (Number n : _data.values()) {
                totalClassifiedElements += n.longValue();
            }
        }
        return totalClassifiedElements;
    }

    @Override
    public int size() {
        return _data.size();
    }

    @Override
    public boolean isEmpty() {
        return _data.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return _data.containsKey((Pair<Attribute, Attribute>) key);
    }

    @Override
    public boolean containsValue(Object value) {
        return _data.containsValue(value);
    }

    @Override
    public Number get(Object key) {
        return _data.get(key);
    }

    @Override
    public Number put(Pair<Attribute, Attribute> key, Number value) {
        return _data.put(key, value);
    }

    @Override
    public void clear() {
        _data.clear();
    }

    @Override
    public Set<Pair<Attribute, Attribute>> keySet() {
        return _data.keySet();
    }

    @Override
    public Collection<Number> values() {
        return _data.values();
    }

    @Override
    public Set<Entry<Pair<Attribute, Attribute>, Number>> entrySet() {
        return _data.entrySet();
    }

    @Override
    public Number remove(Object key) {
        return _data.remove(key);
    }

    @Override
    public void putAll(Map<? extends Pair<Attribute, Attribute>, ? extends Number> m) {
        _data.putAll(m);
    }
}
