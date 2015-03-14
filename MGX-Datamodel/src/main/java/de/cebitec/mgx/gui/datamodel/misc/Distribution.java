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
public class Distribution implements DistributionI<Long> {

    private final MGXMasterI master;
    private final Map<AttributeI, Long> _data = new HashMap<>();
    private final Set<AttributeI> keys = new LinkedHashSet<>();
    private final long numElements;

    public Distribution(MGXMasterI master, Map<AttributeI, Long> data) {
        this(master, data, data.keySet());
    }

    /*
     * only attributes present in order and data will appear in the distribution
     */
    public Distribution(MGXMasterI master, Map<AttributeI, Long> data, Collection<AttributeI> order) {
        this.master = master;
        long total = 0;
        for (AttributeI attr : order) {
            if (data.containsKey(attr)) {
                Long n = data.get(attr);
                total += n;
                keys.add(attr);
                _data.put(attr, n);
            }
        }
        numElements = total;
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
            return keySet().contains(a);
        }
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        for (Entry<AttributeI, Long> e : _data.entrySet()) {
            if (e.getValue().equals(value)) {
                return keySet().contains(e.getKey());
            }
        }
        return false;
    }

    @Override
    public Long get(Object key) {
        if (key instanceof AttributeI) {
            AttributeI a = (AttributeI) key;
            if (keySet().contains(a)) {
                return _data.get(a);
            }
        }
        return null;
    }

    @Override
    public Long put(AttributeI key, Long value) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public Long remove(Object key) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void putAll(Map<? extends AttributeI, ? extends Long> m) {
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
    public Collection<Long> values() {
        Collection<Long> ret = new ArrayList<>();
        for (AttributeI attr : keySet()) {
            ret.add(_data.get(attr));
        }
        return ret;
    }

    @Override
    public Set<Entry<AttributeI, Long>> entrySet() {
        Set<Entry<AttributeI, Long>> ret = new LinkedHashSet<>();
        for (AttributeI attr : keySet()) {
            Map.Entry<AttributeI, Long> e = new AbstractMap.SimpleImmutableEntry<>(attr, _data.get(attr));
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
        final Distribution other = (Distribution) obj;
        if (!Objects.equals(this.master, other.master)) {
            return false;
        }
        if (!Objects.equals(this._data, other._data)) {
            return false;
        }
        if (!Objects.equals(this.keys, other.keys)) {
            return false;
        }
        if (this.numElements != other.numElements) {
            return false;
        }
        return true;
    }

    @Override
    public Class<Long> getEntryType() {
        return Long.class;
    }

}
