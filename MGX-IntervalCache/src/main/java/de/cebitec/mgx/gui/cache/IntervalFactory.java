/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.cache;

import java.util.Iterator;
import org.apache.commons.math3.util.FastMath;

/**
 *
 * @author sjaenick
 */
public class IntervalFactory {

    private IntervalFactory() {
    }

    public static Iterator<Interval> createSegments(final int from, final int to, final int segmentSize) {

        final int fromInterval = from / segmentSize;

        return new Iterator<Interval>() {

            private Interval cur = new Interval(segmentSize, fromInterval * segmentSize);

            @Override
            public boolean hasNext() {
                return cur != null;
            }

            @Override
            public Interval next() {
                Interval i = cur;
                if (cur.getTo() <= to) {
                    cur = cur.next(to);
                } else {
                    cur = null;
                }
                return i;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Not supported.");
            }
        };
    }

    public static Iterator<Interval> slidingWindow(final int from, final int to, final int segmentSize, final int shift) {

        if (segmentSize <= 0 || shift <= 0) {
            throw new IllegalArgumentException("Invalid parameters");
        }
        final int fromInterval = from / segmentSize;

        return new Iterator<Interval>() {

            private Interval cur = new Interval(segmentSize, fromInterval * segmentSize);

            @Override
            public boolean hasNext() {
                return cur != null;
            }

            @Override
            public Interval next() {
                Interval i = cur;
                if (cur.getTo() + FastMath.max(1, shift) <= to) {
                    // make sure we shift at least by 1
                    cur = new Interval(segmentSize, i.getFrom() + FastMath.max(1, shift));
                } else {
                    cur = null;
                }
                return i;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Not supported.");
            }
        };
    }
}
