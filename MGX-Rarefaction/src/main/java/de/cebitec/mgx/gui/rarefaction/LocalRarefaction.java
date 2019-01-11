/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.rarefaction;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.Point;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.gui.pool.MGXPool;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import org.openide.util.Exceptions;

/**
 *
 * @author sj
 */
public class LocalRarefaction {

    private final static int DEFAULT_REPETITIONS = 55;
    private final static int DEFAULT_NUMSTEPS = 50;

    public static Iterator<Point> rarefy(DistributionI<Long> dist) throws MGXException {
        return rarefy(dist, DEFAULT_REPETITIONS);
    }

    public static Iterator<Point> rarefy(DistributionI<Long> dist, int numRepetitions) throws MGXException {
        return rarefy(dist, numRepetitions, DEFAULT_NUMSTEPS);
    }

    public static Iterator<Point> rarefy(DistributionI<Long> dist, int numRepetitions, int numSteps) throws MGXException {
        if (dist.size() > Math.pow(2, Character.SIZE)) {
            throw new MGXException("Distribution too large.");
        }

        if (dist.getTotalClassifiedElements() > Integer.MAX_VALUE) {
            throw new MGXException("Too many classified sequences.");
        }

        int numFeatures = 0;
        // use char as uint16t
        char[] frame = new char[(int) dist.getTotalClassifiedElements()];

        int idx = 0;
        char featNum = 0;

        for (Map.Entry<AttributeI, Long> e : dist.entrySet()) {
            int count = e.getValue().intValue();
            for (int i = 0; i < count; i++) {
                frame[idx++] = featNum;
            }
            featNum++;
            numFeatures++;
        }

        int depths[] = seq(1, frame.length, numSteps);
        double[] divs = rarefy(depths, numRepetitions, frame, numFeatures);
        List<Point> ret = new ArrayList<>(depths.length);
        for (int i = 0; i < depths.length; i++) {
            ret.add(new Point(depths[i], divs[i]));
        }

        return ret.iterator();
    }

    private static double[] rarefy(final int[] depths, final int rep, final char[] frame, final int numFeatures) {

        final MGXPool pool = MGXPool.getInstance();

        int numSamples = depths.length;
        final double[] meanRichnesses = new double[numSamples];

        final CountDownLatch allDone = new CountDownLatch(numSamples);

        // omp parallel for threadNum(10)
        for (int sIdx = 0; sIdx < numSamples; sIdx++) {

            final int i = sIdx;

            pool.submit(new Runnable() {
                @Override
                public void run() {

                    int sampleSize = depths[i];
                    XorShift64Star engine = new XorShift64Star();

                    int offset = 0;

                    char[] arr_tcopy = Arrays.copyOf(frame, frame.length);
                    shuffle(arr_tcopy, engine);

                    int richnessSum = 0;
                    for (int curRep = 0; curRep < rep; curRep++) {

                        if (offset + sampleSize >= frame.length) {
                            // randomize sampling frame
                            shuffle(arr_tcopy, engine);
                            offset = 0;
                        }

                        //count up
                        boolean[] cnts = new boolean[numFeatures]; // default false
                        for (int j = offset; j < offset + sampleSize; j++) {
                            cnts[arr_tcopy[j]] = true;
                        }

                        // popcnt - number of features present in subsample
                        int rich = 0;
                        for (int k = 0; k < numFeatures; k++) {
                            if (cnts[k]) {
                                ++rich;
                            }
                        }
                        //richnesses[curRep] = rich;
                        richnessSum += rich;

                        offset += sampleSize;
                    }
                    meanRichnesses[i] = 1d * richnessSum / rep;

                    allDone.countDown();
                }
            });
        }

        try {
            allDone.await();
        } catch (InterruptedException ex) {
            Exceptions.printStackTrace(ex);
        }
        return meanRichnesses;
    }

    private static void shuffle(char[] array, final XorShift64Star engine) {
        int count = array.length;
        for (int i = count; i > 1; i--) {
            swap(array, i - 1, engine.nextInt(i));
        }
    }

    private static void swap(char[] array, int i, int j) {
        char temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    private static int[] seq(int from, int to, int numSteps) {
        int[] ret = new int[numSteps];
        ret[0] = from;
        ret[numSteps - 1] = to;

        float interval = to - from + 1;
        interval /= numSteps;

        for (int i = 1; i < numSteps - 1; i++) {
            ret[i] = from + Math.round(interval * (i));
        }
        return ret;
    }

//    public static void main(String[] args) {
//        int[] xx = seq(1, 50, 10);
//        XorShift64Star e = new XorShift64Star();
//        for (int i = 0; i < 100; i++) {
//            System.err.println(e.nextInt(1000));
//        }
//        System.out.println(Arrays.toString(xx));
//    }
    private static class XorShift64Star {

        private long state;

        public XorShift64Star() {
            state = new SecureRandom().nextLong();
        }

        protected int next(int bits) {
            return (int) (next() >>> (64 - bits));
        }

        public long next() {
            long x = state;
            x ^= x >>> 12; // a
            x ^= x << 25; // b
            x ^= x >>> 27; // c
            state = x;
            return x * 0x2545F4914F6CDD1DL;
        }

        public int nextInt(int bound) {

            if ((bound & -bound) == bound) // i.e., bound is a power of 2
            {
                return (int) ((bound * (long) next(31)) >> 31);
            }

            int bits, val;
            do {
                bits = next(31);
                val = bits % bound;
            } while (bits - val + (bound - 1) < 0);
            return val;
        }

    }
}
