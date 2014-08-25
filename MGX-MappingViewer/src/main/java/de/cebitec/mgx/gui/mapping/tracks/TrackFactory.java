/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping.tracks;

import de.cebitec.mgx.api.model.MappedSequenceI;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.openide.util.Exceptions;

/**
 *
 * @author sjaenick
 */
public class TrackFactory {

    private final static ExecutorService pool = Executors.newCachedThreadPool();

    public static synchronized Track addTrack(Collection<Track> tracks, MappedSequenceI ms) {
        Track t = new Track(pool);
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
        for (MappedSequenceI ms : mappings) {
            //System.err.println("numTracks "+ tracks.size());
            FindTrack ft = new FindTrack(0, tracks.size(), tracks.toArray(new Track[]{}), ms);
            pool.submit(ft);

            Track t = null;
            try {
                t = ft.get();
            } catch (InterruptedException | ExecutionException ex) {
                Exceptions.printStackTrace(ex);
            }
            //Track t = pool.invoke(ft);
            if (t != null) {
                t.add(ms);
            } else {
                t = new Track(pool);
                tracks.add(t);
                t.add(ms);
            }
        }
    }

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
