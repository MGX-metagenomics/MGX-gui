/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping.tracks;

import de.cebitec.mgx.gui.datamodel.MappedSequence;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 *
 * @author sjaenick
 */
public class TrackFactory {

    private static final ForkJoinPool pool = new ForkJoinPool();

    public static void createTracks(Iterable<MappedSequence> mappings, Collection<Track> tracks) {
        tracks.clear();
        for (MappedSequence ms : mappings) {
            boolean placed = false;
            for (Track t : tracks) {
                if (!placed) {
                    placed = t.tryAdd(ms);
                    if (placed) {
                        break;
                    }
                }
            }
            if (!placed) {
                Track t = new Track();
                tracks.add(t);
                placed = t.tryAdd(ms);
            }
        }
    }

    public static void createTracks2(Iterable<MappedSequence> mappings, final List<Track> tracks) {
        tracks.clear();
        tracks.add(new Track());
        for (MappedSequence ms : mappings) {
            FindTrack ft = new FindTrack(tracks, ms);
            Track t = pool.invoke(ft);
            if (t != null) {
                t.add(ms);
            } else {
                t = new Track();
                tracks.add(t);
                t.add(ms);
            }
        }
    }

    public static class FindTrack extends RecursiveTask<Track> {

        private final List<Track> tracks;
        private final MappedSequence ms;

        private final static int THRESHOLD = 1;

        public FindTrack(List<Track> tracks, MappedSequence ms) {
            this.tracks = tracks;
            this.ms = ms;
        }

        @Override
        protected Track compute() {
            int len = tracks.size();

            if (len <= THRESHOLD) {
                for (Track t : tracks) {
                    if (t.canAdd(ms)) {
                        return t;
                    }
                }
                System.err.println("analyzed "+len);
                return null;
            } else {

                int mid = len / 2;
                List<Track> subList1 = tracks.subList(0, mid);
                List<Track> subList2 = tracks.subList(mid, len);

                assert subList1.size() + subList2.size() == len;
                
                System.err.println("processing lists "+ subList1.size() + " and " + subList2.size());

                FindTrack left = new FindTrack(subList1, ms);
                left.fork();

                FindTrack right = new FindTrack(subList2, ms);
                Track tr = right.compute();

                Track tl = left.join();

                if (tl != null) {
                    return tl;
                }

                return tr;

            }

        }

    }
}
