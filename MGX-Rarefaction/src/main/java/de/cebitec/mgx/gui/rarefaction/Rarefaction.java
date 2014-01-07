package de.cebitec.mgx.gui.rarefaction;

import de.cebitec.mgx.gui.datamodel.Attribute;
import de.cebitec.mgx.gui.datamodel.misc.Distribution;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;
import net.sf.javaml.sampling.Sampling;

/**
 *
 * @author sj
 */
public class Rarefaction {

    private final Distribution dist;
    private final Dataset dataset;
    private final long N;
    private final long K;
    private final int numIterations;
    private final static int DEFAULT_NUM_ITERATIONS = 25;
    
    public Rarefaction(Distribution dist) {
        this(dist, DEFAULT_NUM_ITERATIONS);
    }

    public Rarefaction(Distribution dist, int numIter) {
        this.dist = dist;
        this.dataset = toDataset(dist);
        this.numIterations = numIter;
        N = dist.getTotalClassifiedElements();
        K = dist.keySet().size();
    }

    public double rarefy(int size) {
        assert size > 0;
        assert size <= N;
        double d = 0;
        for (long i = 0; i < numIterations; i++) {
            Dataset subsampleDS = Sampling.SubSampling.sample(dataset, size).x();
            d += rarefy(toDistribution(subsampleDS));
        }

        return d / numIterations;
    }

    private double rarefy(final Distribution subsample) {
        long sum = 0;
        final long n = subsample.getTotalClassifiedElements();
        for (Attribute a : dist.keySet()) {
            if (subsample.containsKey(a)) {
                sum += binom(N - subsample.get(a).longValue(), n);
            } else {
                sum += binom(N, n);
            }
        }

        return K - Math.pow(binom(N, n), -1) * sum;
    }

    private static Distribution toDistribution(final Dataset ds) {
        Map<Attribute, Long> data = new HashMap<>();
        long total = 0;
        Iterator<Instance> iter = ds.iterator();
        while (iter.hasNext()) {
            total++;
            Instance next = iter.next();
            Attribute attr = (Attribute) next.classValue();
            if (data.containsKey(attr)) {
                data.put(attr, Long.valueOf(data.get(attr).longValue() + 1));
            } else {
                data.put((Attribute) next.classValue(), Long.valueOf(1));
            }
        }

        return new Distribution(data, total, null);
    }

    private static Dataset toDataset(final Distribution d) {
        Dataset data = new DefaultDataset();

        for (Entry<Attribute, Number> e : d.entrySet()) {
            long cnt = e.getValue().longValue();
            for (long i = 1; i <= cnt; i++) {
                data.add(new DenseInstance(new double[]{1.00}, e.getKey()));
            }
        }
        return data;
    }

    private static long binom(long n, final long k) {
        final long min = (k < n - k ? k : n - k);
        long bin = 1;
        for (long i = 1; i <= min; i++) {
            bin *= n;
            bin /= i;
            n--;
        }
        return bin;
    }

}
