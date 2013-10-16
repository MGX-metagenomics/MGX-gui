package de.cebitec.mgx.gui.cache.internal;

import de.cebitec.mgx.gui.cache.Cache;

/**
 *
 * @author sjaenick
 */
public class Interval<T> {

    private final int from;
    private final Cache<T> cache;

    public Interval(Cache<T> cache, int from) {
        this.cache = cache;
        this.from = from;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return from + cache.getSegmentSize();
    }
    
    public Interval next() {
        return new Interval(cache, from + cache.getSegmentSize() + 1);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + this.from;
        //hash = 89 * hash + this.to;
        return hash;
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
//        if (this.to != other.to) {
//            return false;
//        }
        return true;
    }
}
