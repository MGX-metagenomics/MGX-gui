/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping.tracks;

import de.cebitec.mgx.api.model.MappedSequenceI;
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

    public static synchronized Track addTrack(Collection<Track> tracks, MappedSequenceI ms) {
        Track t = new Track();
        tracks.add(t);
        t.add(ms);
        return t;
    }

    public static void createTracks(int minIdentity, Collection<MappedSequenceI> mappings, Collection<Track> tracks) {
        tracks.clear();
        boolean placed;
        Track last = null;
        for (MappedSequenceI ms : mappings) {
            if (ms.getIdentity() >= minIdentity) {
                placed = false;
                // check last track first as a quick check;
                // major speedup, but suboptimal layout
                //if (last != null && last.canAdd(ms)) {
                for (Track t : tracks) {
                    if (!placed) {
                        placed = t.tryAdd(ms);
                        if (placed) {
                            break;
                        }
                    }
                }
                //}
                if (!placed) {
                    last = addTrack(tracks, ms);
                }
            }
        }
    }

    public static void createTracks2(Iterable<MappedSequenceI> mappings, final List<Track> tracks) {
        tracks.clear();
//        tracks.add(new Track());
        for (MappedSequenceI ms : mappings) {
            FindTrack ft = new FindTrack(0, tracks.size(), tracks.toArray(new Track[]{}), ms);
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

        private final Track[] tracks;
        private final MappedSequenceI ms;
        private final int from, to;

        private final static int THRESHOLD = 10;

        public FindTrack(int from, int to, Track[] tracks, MappedSequenceI ms) {
            this.tracks = tracks;
            this.ms = ms;
            this.from = from;
            this.to = to;
        }

        @Override
        protected Track compute() {
            int len = to - from + 1; //tracks.size();

            if (len <= THRESHOLD) {
                for (int i = from; i < to; i++) {
                    Track t = tracks[i];
                    if (t.canAdd(ms)) {
                        return t;
                    }
                }
                //System.err.println("analyzed "+len);
                return null;
            } else {

                int mid = len / 2;
//                List<Track> subList1 = tracks.subList(0, mid);
//                List<Track> subList2 = tracks.subList(mid, len);
//                assert subList1.size() + subList2.size() == len;

                //System.err.println("processing lists "+ subList1.size() + " and " + subList2.size());
                FindTrack left = new FindTrack(0, mid, tracks, ms);
                left.fork();

                FindTrack right = new FindTrack(mid, len, tracks, ms);
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
