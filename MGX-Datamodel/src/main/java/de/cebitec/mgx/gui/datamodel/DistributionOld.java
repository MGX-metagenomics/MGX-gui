//package de.cebitec.mgx.gui.datamodel;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.Map.Entry;
//
///**
// *
// * @author sjaenick
// */
//public class Distribution {
//
//    Map<Attribute, ? extends Number> dist = null;
//    Attribute[] sortOrder;
//
//    public Distribution(Map<Attribute, ? extends Number> dist) {
//        this.dist = dist;
//    }
//
//    public List<Pair<Attribute, ? extends Number>> getSorted() {
//        List<Pair<Attribute, ? extends Number>> ret = new ArrayList<>();
//
//        if (sortOrder == null) {
//            for (Entry<Attribute, ? extends Number> e : dist.entrySet()) {
//                ret.add(new Pair<>(e.getKey(), e.getValue()));
//            }
//        } else {
//            for (Attribute attr : sortOrder) {
//                Number n = dist.get(attr);
//                if (n != null) {
//                    ret.add(new Pair<>(attr, n));
//                }
//            }
//        }
//
//        return ret;
//    }
//
//    public Map<Attribute, ? extends Number> getMap() {
//        return dist;
//    }
//
//    public void setSortOrder(Attribute[] sortOrder) {
//        this.sortOrder = sortOrder;
//    }
//    
//    public Attribute[] getSortOrder() {
//        return sortOrder;
//    }
//}
