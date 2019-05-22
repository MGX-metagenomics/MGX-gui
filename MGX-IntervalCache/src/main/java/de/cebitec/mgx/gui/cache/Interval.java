package de.cebitec.mgx.gui.cache;

/**
 *
 * @author sjaenick
 */
public class Interval {

    // lower bound is always segment-aligned, 
    // upper bound isn't (but might be, if not last segment)
    private final int from;
    private final int segmentSize;

//    public Interval(Cache<T> cache, int from) {
//        this(cache, from, Math.min(from + cache.getSegmentSize() - 1, cache.getReference().getLength() - 1));
//    }
    public Interval(int segmentSize) {
        this(segmentSize, 0);
    }

    public Interval(int segmentSize, int from) {
        this.segmentSize = segmentSize;
        this.from = from;
    }

    public final int getFrom() {
        return from;
    }

    public final int getTo() {
        return from + segmentSize - 1;
    }

    public final int length() {
        return getTo() - getFrom() + 1;
    }

    public final Interval next(int upto) {
        int start = getTo() + 1;
        if (start > upto) {
            return null;
        }
        return new Interval(segmentSize, start); //, upto);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Interval other = (Interval) obj;
        if (this.from != other.from) {
            return false;
        }
        if (this.segmentSize != other.segmentSize) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + this.from;
        return hash;
    }
}
