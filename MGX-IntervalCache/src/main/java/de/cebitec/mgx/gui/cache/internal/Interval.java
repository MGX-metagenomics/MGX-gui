package de.cebitec.mgx.gui.cache.internal;

import de.cebitec.mgx.gui.cache.Cache;

/**
 *
 * @author sjaenick
 */
public class Interval<T> {

    // lower bound is always segment-aligned, 
    // upper bound isn't (but might be, if not last segment)
    private final int from;
    private final int to;
    private final Cache<T> cache;

//    public Interval(Cache<T> cache, int from) {
//        this(cache, from, Math.min(from + cache.getSegmentSize() - 1, cache.getReference().getLength() - 1));
//    }

    public Interval(Cache<T> cache, int from, int to) {
        this.cache = cache;
        assert from > -1;
        assert from < cache.getReference().getLength();
        assert to < cache.getReference().getLength();
        this.from = from;
        this.to = to;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    public int length() {
        return to - from + 1;
    }

    public Interval next(int upto) {
        int start = getTo() + 1;
        if (start >= upto) {
            return null;
        }
        int newTo = Math.min(getTo() + cache.getSegmentSize(), cache.getReference().getLength() - 1);
        if (upto > newTo) {
            // next full segment
            return new Interval(cache, start, newTo);
        }
        // partial segment
        return new Interval(cache, start, upto);
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
        if (this.to != other.to) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + this.from;
        hash = 83 * hash + this.to;
        return hash;
    }
}
