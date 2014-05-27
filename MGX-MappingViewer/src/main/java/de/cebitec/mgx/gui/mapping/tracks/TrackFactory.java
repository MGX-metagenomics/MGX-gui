/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping.tracks;

import de.cebitec.mgx.gui.datamodel.MappedSequence;
import java.util.Collection;

/**
 *
 * @author sjaenick
 */
public class TrackFactory {

    public static void createTracks(Iterable<MappedSequence> mappings, Collection<Track> tracks) {
        tracks.clear(); 
        for (MappedSequence ms : mappings) {
            boolean placed = false;
            for (Track t : tracks) {
                if (!placed) {
                    placed = t.add(ms);
                    if (placed) {
                        break;
                    }
                }
            }
            if (!placed) {
                Track t = new Track();
                tracks.add(t);
                placed = t.add(ms);
            }
        }
    }

}
