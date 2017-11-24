/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping.tracks;

import de.cebitec.mgx.api.model.MappedSequenceI;
import de.cebitec.mgx.gui.pool.MGXPool;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.openide.util.Exceptions;

/**
 *
 * @author sjaenick
 */
public class TrackFactory {

    private static MGXPool pool = null;

    private TrackFactory() {
    }

    public static Track addTrack(Collection<Track> tracks, MappedSequenceI ms) {
        Track t = new Track(1);
        tracks.add(t);
        t.add(ms);
        return t;
    }

//    private final static LayouterI layouter = new Layouter();
    private final static int MAX_TRACK_NUM = 200;

//    public static synchronized boolean createTracks(List<MappedSequenceI> sortedMappings, Collection<TrackI> tracks) {
//        int trackCnt = 0;
//        tracks.clear();
//        layouter.clear();
//        boolean allPlaced = true;
//        for (MappedSequenceI ms : sortedMappings) {
//
//            TrackI curTrack = layouter.getTrack(ms.getMin() - 1);
//
//            if (curTrack != null) {
//                layouter.remove(curTrack);
//                curTrack.add(ms);
//                layouter.add(curTrack);
//            } else {
//                if (trackCnt <= MAX_TRACK_NUM) {
//                    Track t = new Track(++trackCnt);
//                    t.add(ms);
//                    layouter.add(t);
//                    tracks.add(t);
//                } else {
//                    allPlaced = false;
//                }
//            }
//        }
//        return allPlaced;
//    }
    public static void createTracksMultiThreaded(Iterator<MappedSequenceI> mappings, Collection<Track> tracks) {
        tracks.clear();
        List<MappedSequenceI> all = new ArrayList<>();
        while (mappings != null && mappings.hasNext()) {
            all.add(mappings.next());
        }
        // sort by min position, ascending
        Collections.sort(all, new Comparator<MappedSequenceI>() {
            @Override
            public int compare(MappedSequenceI o1, MappedSequenceI o2) {
                return Integer.compare(o1.getMin(), o2.getMin());
            }
        });
        TrackHandler handler = new TrackHandler(1, pool);
        if (pool == null) {
            pool = MGXPool.getInstance();
        }
        pool.execute(handler);
//        for (int i =0; i<5;i++) {
//            System.err.println(all.get(i).getMin());
//        }
        for (MappedSequenceI ms : all) {
            handler.add(ms);
        }
//        handler.finish();

        handler.getTracks(tracks);
        System.err.println("Solution has " + tracks.size() + " tracks for " + all.size() + " mappings");
    }
    
    public static boolean createTracks(List<MappedSequenceI> mappings, Collection<TrackI> tracks) {
        return createTracks(mappings, tracks, TrackI.DEFAULT_PADDING);
    }

    public static boolean createTracks(List<MappedSequenceI> mappings, Collection<TrackI> tracks, int trackPadding) {
        tracks.clear();
        int trackCnt = 0;
        boolean placed;
        for (MappedSequenceI ms : mappings) {
            placed = false;
            // check last track first as a quick check;
            // major speedup, but suboptimal layout
            for (TrackI t : tracks) {
                if (!placed) {
                    placed = t.tryAdd(ms);
                    if (placed) {
                        break;
                    }
                }
            }
            if (!placed && trackCnt < MAX_TRACK_NUM) {
                Track t = new Track(++trackCnt, trackPadding);
                tracks.add(t);
                t.add(ms);
            }
        }
        
        return true;
    }

//    public static void createTracks2(Iterable<MappedSequenceI> mappings, final List<Track> tracks) {
//        tracks.clear();
//        for (MappedSequenceI ms : mappings) {
//            //System.err.println("numTracks "+ tracks.size());
//            FindTrack ft = new FindTrack(0, tracks.size(), tracks.toArray(new Track[]{}), ms);
//            pool.submit(ft);
//
//            Track t = null;
//            try {
//                t = ft.get();
//            } catch (InterruptedException | ExecutionException ex) {
//                Exceptions.printStackTrace(ex);
//            }
//            //Track t = pool.invoke(ft);
//            if (t != null) {
//                t.add(ms);
//            } else {
//                t = new Track();
//                tracks.add(t);
//                t.add(ms);
//            }
//        }
//    }
    private static abstract class Find<T> implements Runnable, Future<T> {

        private T result = null;
        private boolean cancelled = false;
        private boolean done = false;
        private final CountDownLatch latch = new CountDownLatch(1);

        public abstract T compute();

        @Override
        public void run() {
            result = compute();
            done = true;
            latch.countDown();
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            if (!done) {
                latch.countDown();
            }
            cancelled = true;
            if (done) {
                return false;
            }
            return true;
        }

        @Override
        public boolean isCancelled() {
            return cancelled;
        }

        @Override
        public boolean isDone() {
            return done || cancelled;
        }

        @Override
        public T get() throws InterruptedException, ExecutionException {
            latch.await();
            assert done;
            return result;
        }

        @Override
        public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            assert done;
            return result;
        }

    }

    private static class FindTrack extends Find<Track> {

        private final Track[] tracks;
        private final MappedSequenceI ms;
        private final int from, to;

        private final static int THRESHOLD = 5;

        public FindTrack(int from, int to, Track[] tracks, MappedSequenceI ms) {
            this.tracks = tracks;
            this.ms = ms;
            this.from = from;
            this.to = to;
        }

        @Override
        public Track compute() {
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
                //System.err.println("fork");
                int mid = len / 2;
                FindTrack left = new FindTrack(0, mid, tracks, ms);
                //left.fork();
                if (pool == null) {
                    pool = MGXPool.getInstance();
                }
                pool.submit(left);

                FindTrack right = new FindTrack(mid, len, tracks, ms);
                //Track tr = right.compute();
                pool.submit(right);

                Track tl = null;
                try {
                    tl = left.get();
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }

                if (tl != null) {
                    return tl;
                }
                try {
                    return right.get();
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }
                return null;
            }

        }

    }
}
