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

    private static ForkJoinPool pool = new ForkJoinPool();

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
            FindTrack ft = new FindTrack(0, tracks.size() - 1, tracks, ms);
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
        private final int from;
        private final int to;

        private final static int THRESHOLD = 1;

        public FindTrack(int from, int to, List<Track> tracks, MappedSequence ms) {
            this.from = from;
            this.to = to;
            this.tracks = tracks;
            this.ms = ms;
        }

        @Override
        protected Track compute() {
            int len = to - from + 1;

            if (len <= THRESHOLD) {
                List<Track> subList = tracks.subList(from, to);
                for (Track t : subList) {
                    if (t.canAdd(ms)) {
                        return t;
                    }
                }
                return null;
            }

            int mid = len / 2;

//            System.err.println(from + "-" + mid);
//            System.err.println((mid + 1) + "-" + to);
            if (mid > from && mid < to) {
                FindTrack left = new FindTrack(from, mid, tracks, ms);
                left.fork();

                FindTrack right = new FindTrack(mid + 1, to, tracks, ms);
                Track tr = right.compute();

                Track tl = left.join();

                if (tl != null) {
                    return tl;
                }
                return tr;
            }

            System.err.println("len " + len);
            System.err.println("from " + from);
            System.err.println("to " + to);
            System.err.println("mid " + mid);
            assert false;

            System.err.println("first " + from + "-" + mid);
            System.err.println("second " + (mid + 1) + "-" + to);

            return null;

        }

    }
}
