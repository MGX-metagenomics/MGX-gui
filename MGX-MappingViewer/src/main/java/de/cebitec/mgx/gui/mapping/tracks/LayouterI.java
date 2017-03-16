/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping.tracks;

/**
 *
 * @author sj
 */
public interface LayouterI {

    TrackI getTrack(int minPos);

    void clear();

    boolean add(TrackI t);

    boolean remove(Object t);

    TrackI first();

    TrackI last();
}
