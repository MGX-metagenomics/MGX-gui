/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping.tracks;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import org.openide.util.Exceptions;

/**
 *
 * @author sj
 */
public class Layouter extends TreeSet<TrackI> implements LayouterI {

    private transient TreeMap<TrackI, Object> map;

    @SuppressWarnings("unchecked")
    public Layouter() {
        super(new Comparator<TrackI>() {
            @Override
            public int compare(TrackI o1, TrackI o2) {
                int ret = Integer.compare(o1.getMax(), o2.getMax());
                return ret != 0 ? ret : Integer.compare(o1.getId(), o2.getId());
            }
        });

        try {
            Field f = TreeSet.class.getDeclaredField("m");
            f.setAccessible(true);
            map = (TreeMap<TrackI, Object>) f.get(this);
            f.setAccessible(false);
        } catch (NoSuchFieldException | SecurityException | IllegalAccessException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private final DummyTrack dummy = new DummyTrack();

    @Override
    public final TrackI getTrack(int minPos) {
        dummy.setMax(minPos);

        // avoid new treeset instance created in TreeSet#headSet
        SortedSet<TrackI> candidates = map.headMap(dummy, false).navigableKeySet();

        TrackI ret = null;
        for (TrackI t : candidates) {
            if (ret == null || t.getId() < ret.getId()) {
                ret = t;
            }
        }
        return ret;
    }

}
