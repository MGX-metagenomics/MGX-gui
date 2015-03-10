//package de.cebitec.mgx.gui.datamodel.misc;
//
//import de.cebitec.mgx.api.MGXMasterI;
//import de.cebitec.mgx.api.misc.DistributionI;
//import de.cebitec.mgx.api.model.AttributeI;
//import java.util.AbstractMap;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.LinkedHashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.Objects;
//import java.util.Set;
//
///**
// *
// * @author sjaenick
// */
//public class Distribution implements DistributionI {
//
//    protected final MGXMasterI master;
//    private final Map<AttributeI, ? extends Number> _data;
//    private final Set<AttributeI> keys = new LinkedHashSet<>();
//    private final Map<AttributeI, Number> filtered = new HashMap<>();
//    private long totalClassifiedElements = -1;
//
//    public Distribution(Map<AttributeI, ? extends Number> data, long total, MGXMasterI m) {
//        this(data, data.keySet(), total, m);
//    }
//
//    public Distribution(Map<AttributeI, ? extends Number> data, Collection<AttributeI> keysOrdered, long total, MGXMasterI m) {
//        this._data = data;
//        keys.addAll(_data.keySet());
//        filtered.putAll(_data);
//        totalClassifiedElements = total;
//        master = m;
//    }
//
//    @Override
//    public MGXMasterI getMaster() {
//        return master;
//    }
//
//    @Override
//    public long getTotalClassifiedElements() {
//        assert totalClassifiedElements != -1;
//        return totalClassifiedElements;
//    }
//
//    @Override
//    public int size() {
//        return keys.size();
//    }
//
//    @Override
//    public boolean isEmpty() {
//        return keys.isEmpty();
//    }
//
//    @Override
//    public boolean containsKey(Object key) {
//        return filtered.containsKey((AttributeI) key);
//    }
//
//    @Override
//    public boolean containsValue(Object value) {
//        return filtered.containsValue(value);
//    }
//
//    @Override
//    public Number get(Object key) {
//        return filtered.get(key);
//    }
//
//    @Override
//    public Number put(AttributeI key, Number value) {
//        keys.add(key);
//        return filtered.put(key, value);
//    }
//
//    @Override
//    public Number remove(Object key) {
//        keys.remove(key);
//        return filtered.remove(key);
//    }
//
//    @Override
//    public void putAll(Map<? extends AttributeI, ? extends Number> m) {
//        keys.addAll(m.keySet());
//        filtered.putAll(m);
//    }
//
//    @Override
//    public void clear() {
//        keys.clear();
//        filtered.clear();
//        totalClassifiedElements = -1;
//    }
//
//    @Override
//    public Set<AttributeI> keySet() {
//        return keys;
//    }
//
//    @Override
//    public Collection<Number> values() {
//        return filtered.values();
//    }
//
//    @Override
//    public Set<Entry<AttributeI, Number>> entrySet() {
//        Set<Entry<AttributeI, Number>> ret = new LinkedHashSet<>();
//        for (AttributeI a : keys) {
//            ret.add(new AbstractMap.SimpleEntry<>(a, filtered.get(a)));
//        }
//        return ret;
//    }
//
////    @Override
////    public void setOrder(List<AttributeI> o) {
////        List<AttributeI> present = new ArrayList<>();
////        for (AttributeI a : o) {
////            if (filtered.containsKey(a)) {
////                present.add(a);
////            }
////        }
////        keys.clear(); keys.addAll(present);
////    }
////    @Override
////    public void reset() {
////        keys.clear();
////        filtered.clear();
////        keys.addAll(_data.keySet());
////        filtered.putAll(_data);
////    }
//    @Override
//    public int hashCode() {
//        int hash = 5;
//        hash = 71 * hash + Objects.hashCode(this.master);
//        hash = 71 * hash + (int) (this.totalClassifiedElements ^ (this.totalClassifiedElements >>> 32));
//        return hash;
//    }
//
//    @Override
//    public boolean equals(Object obj) {
//        if (obj == null) {
//            return false;
//        }
//        if (getClass() != obj.getClass()) {
//            return false;
//        }
//        final Distribution other = (Distribution) obj;
//        if (!Objects.equals(this.master, other.master)) {
//            return false;
//        }
//        if (!Objects.equals(this._data, other._data)) {
//            return false;
//        }
//        if (!Objects.equals(this.keys, other.keys)) {
//            return false;
//        }
//        if (!Objects.equals(this.filtered, other.filtered)) {
//            return false;
//        }
//        if (this.totalClassifiedElements != other.totalClassifiedElements) {
//            return false;
//        }
//        return true;
//    }
//
//}
