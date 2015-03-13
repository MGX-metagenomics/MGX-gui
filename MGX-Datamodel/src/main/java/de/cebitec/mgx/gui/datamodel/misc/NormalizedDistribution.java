/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.datamodel.misc;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.model.AttributeI;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author sjaenick
 */
public class NormalizedDistribution implements DistributionI<Double> {

    private final MGXMasterI master;
    private final Map<AttributeI, Double> _data = new HashMap<>();
    private final Set<AttributeI> keys = new LinkedHashSet<>();
    private final long numElements;

    public NormalizedDistribution(MGXMasterI master, Map<AttributeI, Double> data) {
        this(master, data, data.keySet());
    }

    /*
     * only attributes present in data and order will appear in the distribution
     */
    public NormalizedDistribution(MGXMasterI master, Map<AttributeI, Double> data, Collection<AttributeI> order) {
        this.master = master;
        long total = 0;
        for (AttributeI attr : order) {
            Double n = data.get(attr);
            if (n != null) {
                total += n;
                keys.add(attr);
                _data.put(attr, n);
            }

        }
        numElements = total;
    }

    public NormalizedDistribution(MGXMasterI master, Map<AttributeI, Double> data, Collection<AttributeI> order, long totalElem) {
        this.master = master;
        for (AttributeI attr : order) {
            Double n = data.get(attr);
            if (n != null) {
                keys.add(attr);
                _data.put(attr, n);
            }

        }
        numElements = totalElem;
    }

    @Override
    public MGXMasterI getMaster() {
        return master;
    }

    @Override
    public long getTotalClassifiedElements() {
        return numElements;
    }

    @Override
    public int size() {
        return keySet().size();
    }

    @Override
    public boolean isEmpty() {
        return keySet().isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        if (key instanceof AttributeI) {
            AttributeI a = (AttributeI) key;
            return keys.contains(a);
        }
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        for (Entry<AttributeI, Double> e : _data.entrySet()) {
            if (e.getValue().equals(value)) {
                return keys.contains(e.getKey());
            }
        }
        return false;
    }

    @Override
    public Double get(Object key) {
        if (key instanceof AttributeI) {
            AttributeI a = (AttributeI) key;
            if (keys.contains(a)) {
                return _data.get(a);
            }
        }
        return null;
    }

    @Override
    public Double put(AttributeI key, Double value) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public Double remove(Object key) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void putAll(Map<? extends AttributeI, ? extends Double> m) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public Set<AttributeI> keySet() {
        return Collections.unmodifiableSet(keys);
    }

    @Override
    public Collection<Double> values() {
        Collection<Double> ret = new ArrayList<>();
        // maintain order as defined in keys
        for (AttributeI attr : keySet()) {
            ret.add(_data.get(attr));
        }
        return ret;
    }

    @Override
    public Set<Entry<AttributeI, Double>> entrySet() {
        Set<Entry<AttributeI, Double>> ret = new LinkedHashSet<>();

        // maintain order as defined in keys
        for (AttributeI attr : keySet()) {
            Map.Entry<AttributeI, Double> e = new AbstractMap.SimpleImmutableEntry<>(attr, _data.get(attr));
            ret.add(e);
        }
        return ret;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.master);
        hash = 89 * hash + Objects.hashCode(this._data);
        hash = 89 * hash + Objects.hashCode(this.keys);
        hash = 89 * hash + (int) (this.numElements ^ (this.numElements >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final NormalizedDistribution other = (NormalizedDistribution) obj;
        if (!Objects.equals(this.master, other.master)) {
            return false;
        }
        if (!Objects.equals(this._data, other._data)) {
            return false;
        }
        if (!Objects.equals(this.keys, other.keys)) {
            return false;
        }
        return this.numElements == other.numElements;
    }

    @Override
    public Class<Double> getEntryType() {
        return Double.class;
    }

}
