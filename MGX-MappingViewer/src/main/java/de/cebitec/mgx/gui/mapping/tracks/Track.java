/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping.tracks;

import de.cebitec.mgx.api.model.MappedSequenceI;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 *
 * @author sj
 */
public class Track {

    private static final ForkJoinPool pool = new ForkJoinPool();
    private final List<MappedSequenceI> content = new LinkedList<>();
    private final int padding = 25; // bp, should be px

    Track() {
    }

    public boolean tryAdd(MappedSequenceI ms) {
//        System.err.println("checking "+content.size());
//        for (MappedSequence m : content) {
//            if (overlaps(m, ms, padding)) {
//                return false;
//            }
//        }
        boolean ovl1 = overlaps(ms);
//        boolean ovl2 = pool.invoke(new CheckOverlap(0, content.size(), content.toArray(new MappedSequence[]{}), ms));
//        assert ovl1 == ovl2;
        if (ovl1) {
            return false;
        }
        content.add(ms);
        return true;
    }

    public void add(MappedSequenceI ms) {
        content.add(ms);
    }

    public boolean canAdd(MappedSequenceI ms) {
        //return pool.invoke(new CheckOverlap(0, content.size(), content.toArray(new MappedSequence[]{}), ms));
        for (MappedSequenceI m : content) {
            if (overlaps(m, ms, padding)) {
                return false;
            }
        }
        return true;
    }

    public Iterator<MappedSequenceI> getSequences() {
        return content.iterator();
    }

    public int size() {
        return content.size();
    }

    private boolean overlaps(MappedSequenceI ms) {
        for (MappedSequenceI m : content) {
            if (overlaps(m, ms, padding)) {
                return true;
            }
        }
        return false;
    }

    private static boolean overlaps(MappedSequenceI ms1, MappedSequenceI ms2, final int pad) {
        int ms2min = ms2.getMin() - pad;
        int ms2max = ms2.getMax() + pad;
        return within(ms1.getMin(), ms2min, ms2max)
                || within(ms1.getMax(), ms2min, ms2max);
    }

    private static boolean within(int pos, int from, int to) {
        return pos >= from && pos <= to;
    }

    public class CheckOverlap extends RecursiveTask<Boolean> {

        private final MappedSequenceI[] data;
        private final MappedSequenceI ms;
        private final int from, to;

        private final static int THRESHOLD = 2;

        public CheckOverlap(int from, int to, MappedSequenceI[] data, MappedSequenceI ms) {
            this.data = data;
            this.ms = ms;
            this.from = from;
            this.to = to;
        }

        @Override
        protected Boolean compute() {
            int len = to - from + 1;

            if (len <= THRESHOLD) {
                System.err.println("  checking idx " + from + " - " + (to - 1) + " out of " + data.length);
                for (int i = from; i < to; i++) {
                    System.err.println("    check idx " + i);
                    MappedSequenceI m = data[i];
                    if (overlaps(m, ms, 25)) {
                        return true;
                    }
                }
                return false;
            } else {

                int mid = (to + from) / 2;
                assert mid < data.length;
                if (len > data.length) {
                    System.err.println("from " + from + " to " + to + " mid " + mid + " datalen " + data.length);
                    assert false;
                }
                //assert len <= data.length;
                CheckOverlap left = new CheckOverlap(from, mid, data, ms);
                left.fork();

                CheckOverlap right = new CheckOverlap(mid, to, data, ms);
                Boolean tr = right.compute();

                Boolean tl = left.join();
                if (tl) {
                    return tl;
                }
                return tr;
            }

        }

    }
}