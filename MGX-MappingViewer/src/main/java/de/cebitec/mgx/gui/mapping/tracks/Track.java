/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping.tracks;

import de.cebitec.mgx.api.model.MappedSequenceI;
import java.io.Serial;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

/**
 *
 * @author sj
 */
public class Track implements Comparable<TrackI>, TrackI {

    static int COUNT = 0;
    private final int id;
    private final List<MappedSequenceI> content = new LinkedList<>();
    private MappedSequenceI last = null;
    private final int padding; // bp, should be px

    public Track(int id) {
        this(id, DEFAULT_PADDING);
    }

    public Track(int id, int pad) {
        this.id = id;
        this.padding = pad;
    }

    @Override
    public final int getId() {
        return id;
    }

    public final MappedSequenceI getLast() {
        return last;
    }

    @Override
    public final int getMax() {
        return getLast().getMax();
    }

    @Override
    public synchronized boolean tryAdd(MappedSequenceI ms) {
        boolean ovl1 = overlaps(last, ms, padding);
        if (ovl1) {
            return false;
        }
        content.add(ms);
        last = ms;
        return true;
    }

    @Override
    public synchronized void add(MappedSequenceI ms) {
        content.add(ms);
        last = ms;
    }

    public synchronized boolean canAdd(MappedSequenceI ms) {
        boolean ovl1 = overlaps(last, ms, padding);
        if (ovl1) {
            return false;
        }
        return true;
    }

    @Override
    public Iterator<MappedSequenceI> getSequences() {
        return content.iterator();
    }

    @Override
    public final Collection<MappedSequenceI> sequences() {
        return content;
    }

    public final int size() {
        return content.size();
    }

    private boolean overlaps(MappedSequenceI ms) {
        return overlaps(last, ms, padding);
//        Iterator<MappedSequenceI> iter = content.iterator();
//        int pos=0;
//        while (iter.hasNext()) {
//            MappedSequenceI m = iter.next();
//            pos++;
//            if (overlaps(m, ms, padding)) {
//                System.err.println("overlap at idx "+pos+" out of "+content.size());
//                return true;
//            }
//        }
//        return false;
    }

//    private boolean overlapsRev(MappedSequenceI ms) {
//        // slower than forward iteration..
//        Iterator<MappedSequenceI> iter = new ReverseIterator<>(content);
//        while (iter.hasNext()) {
//            MappedSequenceI m = iter.next();
//            if (overlaps(m, ms, padding)) {
//                return true;
//            }
//        }
//        return false;
//    }
    private static boolean overlaps(MappedSequenceI ms1, MappedSequenceI ms2, final int pad) {
        int ms2min = ms2.getMin() - pad;
        int ms2max = ms2.getMax() + pad;
        return within(ms1.getMin(), ms2min, ms2max)
                || within(ms1.getMax(), ms2min, ms2max);
    }

    private static boolean within(int pos, int from, int to) {
        return pos >= from && pos <= to;

    }

    @Override
    public final int compareTo(TrackI o) {
        return Integer.compare(id, o.getId());
    }

    public class CheckOverlap extends RecursiveTask<Boolean> {

        @Serial
        private static final long serialVersionUID = 1L;

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

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + this.id;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Track other = (Track) obj;
        return this.id == other.id;
    }

}
