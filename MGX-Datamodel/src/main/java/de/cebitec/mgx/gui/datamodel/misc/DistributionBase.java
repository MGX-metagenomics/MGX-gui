/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.datamodel.misc;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.AttributeTypeI;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author sjaenick
 */
public abstract class DistributionBase<T extends Number> implements DistributionI<T> {

    private final MGXMasterI master;
    private final AttributeTypeI attrType;
    
    // we cant derive this from the sum of values; normalization, filtering..
    private final long totalClassifiedSeqs;
    private final Set<AttributeI> keys = new LinkedHashSet<>();
    private final Map<AttributeI, T> _data = new HashMap<>();

    protected DistributionBase(MGXMasterI master, AttributeTypeI aType, Map<AttributeI, T> data, Collection<AttributeI> order, long total) {
        this.master = master;
        this.attrType = aType;
        this.totalClassifiedSeqs = total;

        for (AttributeI attr : order) {
            if (data.containsKey(attr)) {
                T n = data.get(attr);
                keys.add(attr);
                _data.put(attr, n);
            }
        }
    }

    @Override
    public final MGXMasterI getMaster() {
        return master;
    }
    
    @Override
    public AttributeTypeI getAttributeType() {
        return attrType;
    }

    @Override
    public final long getTotalClassifiedElements() {
        return totalClassifiedSeqs;
    }

    @Override
    public final T get(Object key) {
        if (key instanceof AttributeI) {
            AttributeI a = (AttributeI) key;
            if (keys.contains(a)) {
                return _data.get(a);
            }
        }
        return null;
    }

    @Override
    public final Collection<T> values() {
        Collection<T> ret = new ArrayList<>(keys.size());
        for (AttributeI attr : keys) {
            ret.add(_data.get(attr));
        }
        return ret;
    }

    @Override
    public final boolean containsKey(Object key) {
        return key instanceof AttributeI && keys.contains((AttributeI) key);
    }

    @Override
    public final boolean containsValue(Object value) {
        for (Entry<AttributeI, T> e : _data.entrySet()) {
            if (e.getValue().equals(value)) {
                return keys.contains(e.getKey());
            }
        }
        return false;
    }

    @Override
    public final Set<AttributeI> keySet() {
        return keys;
    }

    @Override
    public final Set<Entry<AttributeI, T>> entrySet() {
        Set<Entry<AttributeI, T>> ret = new LinkedHashSet<>();
        for (AttributeI attr : keys) {
            Map.Entry<AttributeI, T> e = new AbstractMap.SimpleImmutableEntry<>(attr, _data.get(attr));
            ret.add(e);
        }
        return ret; 
    }

    @Override
    public final int size() {
        return keys.size();
    }

    @Override
    public final boolean isEmpty() {
        return keys.isEmpty();
    }

    @Override
    public final T put(AttributeI key, T value) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public final T remove(Object key) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public final void putAll(Map<? extends AttributeI, ? extends T> m) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public final void clear() {
        throw new UnsupportedOperationException("Not supported.");
    }

    protected static long count(Map<?, Long> data) {
        long ret = 0;
        for (Long l : data.values()) {
            ret += l;
        }
        return ret;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.getMaster());
        hash = 89 * hash + Objects.hashCode(this._data);
        hash = 89 * hash + Objects.hashCode(this.keys);
        hash = 89 * hash + (int) (this.getTotalClassifiedElements() ^ (this.getTotalClassifiedElements() >>> 32));
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
        final DistributionBase<?> other = (DistributionBase) obj;
        if (!Objects.equals(this.getEntryType(), other.getEntryType())) {
            return false;
        }
        if (!Objects.equals(this.getMaster(), other.getMaster())) {
            return false;
        }
        if (!Objects.equals(this._data, other._data)) {
            return false;
        }
        if (!Objects.equals(this.keys, other.keys)) {
            return false;
        }
        if (this.getTotalClassifiedElements() != other.getTotalClassifiedElements()) {
            return false;
        }
        return true;
    }
}
