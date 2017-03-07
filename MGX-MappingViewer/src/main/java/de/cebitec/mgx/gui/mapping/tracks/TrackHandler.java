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
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TransferQueue;
import org.openide.util.Exceptions;

/**
 *
 * @author sj
 */
public class TrackHandler implements Runnable {

    private final MGXPool pool;
    private final TransferQueue<MappedSequenceI> seqs = new LinkedTransferQueue<>();
    private volatile boolean mayFinish = false;
    private final CountDownLatch allDone = new CountDownLatch(1);
    private TrackHandler nextHandler = null;
    private final List<Track> tracks = new ArrayList<>();
    private boolean placeSuccess = false;
    private final static int MAX_TRACKS = 100;
    private final int id;
//    private int numSeqs = 0;

    public TrackHandler(int id, MGXPool pool) {
        this.pool = pool;
        this.id = id;
    }

    void add(MappedSequenceI ms) {
        seqs.add(ms);
    }

    @Override
    public void run() {
//        System.err.println(id + " running..");
        while (!(mayFinish && seqs.isEmpty())) {
            MappedSequenceI ms = null;
            try {
                ms = seqs.poll(5, TimeUnit.MILLISECONDS);
            } catch (InterruptedException ex) {
                Exceptions.printStackTrace(ex);
            }
            if (ms != null) {
                placeSuccess = false;
                for (Track t : tracks) {
                    MappedSequenceI last = t.getLast();

                    // check collision
                    // no need for null check, all tracks will have at least one mapping
                    if (last.getMax() < ms.getMin()) {
                        t.add(ms);
                        placeSuccess = true;
//                        numSeqs++;
                        break;
                    }
                }

                if (!placeSuccess) {
                    // add new track to handler
                    if (tracks.size() < MAX_TRACKS) {
                        Track newTrack = new Track(1);
                        tracks.add(newTrack);
                        newTrack.add(ms);
//                        numSeqs++;
                    } else {
                        if (nextHandler == null) {
                            // this handler has reached the track limit, start a new one
                            nextHandler = new TrackHandler(id + 1, pool);
                            pool.execute(nextHandler);
                        }
                        nextHandler.add(ms);
                    }
                }
            }
        }

//        System.err.println(id + " done, placed " + numSeqs + " mappings");

//        if (nextHandler != null) {
//            nextHandler.finish();
//        }
        allDone.countDown();
    }

    void getTracks(Collection<Track> layout) {
        
          mayFinish = true;
        try {
            allDone.await();
        } catch (InterruptedException ex) {
            Exceptions.printStackTrace(ex);
        }
        
        layout.addAll(tracks);
        if (nextHandler != null) {
            nextHandler.getTracks(layout);
        }
    }

}
