/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping.tracks;

import de.cebitec.mgx.gui.datamodel.MappedSequence;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

/**
 *
 * @author sjaenick
 */
public class TrackFactory {

    public static List<Track> createTracks(SortedSet<MappedSequence> mappings) {
        List<Track> tracks = new ArrayList<>();
        for (MappedSequence ms : mappings) {
            boolean placed = false;
            for (Track t : tracks) {
                if (!placed) {
                    placed = t.add(ms);
                }
            }
            if (!placed) {
                Track t = new Track();
                tracks.add(t);
                placed = t.add(ms);
            }
            assert placed;
        }
        return tracks;
    }

}
