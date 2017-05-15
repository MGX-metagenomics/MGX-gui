package de.cebitec.mgx.gui.goldstandard.util;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.JobI;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.math3.util.FastMath;

/**
 *
 * @author pblumenk
 */
public class PerformanceMetrics {

    private long fp, fn, tp, tn;
    private final TLongObjectMap<String> goldstandard;

    public PerformanceMetrics(TLongObjectMap<String> goldstandard) {
        this.fp = 0;
        this.fn = 0;
        this.tp = 0;
        this.tn = 0;
        this.goldstandard = goldstandard;
    }

    public void compute(JobI job, AttributeTypeI attrType) throws MGXException {
        DistributionI<Long> dist = job.getMaster().Attribute().getDistribution(attrType, job);
        final TLongObjectMap<String> jobAttr = new TLongObjectHashMap<>(); //seqid to attr value
        for (Map.Entry<AttributeI, Long> entry : dist.entrySet()) {
            String jobAssignment = entry.getKey().getValue();
            Iterator<Long> it = dist.getMaster().Sequence().fetchSequenceIDs(entry.getKey());
            while (it.hasNext()) {
                Long seqId = it.next();
                jobAttr.put(seqId, jobAssignment);
            }
        }

        // collect all seq ids
        TLongSet allIds = new TLongHashSet();
        allIds.addAll(goldstandard.keySet());
        allIds.addAll(jobAttr.keySet());
        
        final AtomicLong shouldntBeClassified = new AtomicLong(0);

        allIds.forEach(new TLongProcedure() {
            @Override
            public boolean execute(long seqId) {
                // we cannot remove from the gold standard since it's
                // used for all selected jobs
                String gsAssignment = goldstandard.get(seqId);
                String jobAssignment = jobAttr.remove(seqId);

                if (gsAssignment != null) {
                    if (jobAssignment != null) {
                        if (gsAssignment.equals(jobAssignment)) {
                            // correct assignment
                            tp++;
                        } else {
                            // wrong assignment
                            fp++;
                        }
                    } else {
                        // not assigned by job, but by GS
                        fn++;
                    }
                } else {
                    // assigned by job, but not by goldstandard
                    fp++;
                    shouldntBeClassified.incrementAndGet();
                }

                return true;
            }
        });
        
        tn = job.getSeqrun().getNumSequences() - goldstandard.size() - shouldntBeClassified.longValue();
    }

//    public void add(long falsePositive, long falseNegative, long truePositive, long trueNegative) {
//        this.fp += falsePositive;
//        this.fn += falseNegative;
//        this.tp += truePositive;
//        this.tn += trueNegative;
//    }
    public long getFP() {
        return fp;
    }

    public long getFN() {
        return fn;
    }

    public long getTP() {
        return tp;
    }

    public long getTN() {
        return tn;
    }

    /**
     * Calculating the sensitivity or true positive rate
     *
     * @return \(\frac{TP}{TP+FN}\)
     */
    public double getSensitivity() {
        return (double) tp / (tp + fn);
    }

    /**
     * Calculating the specificity or true negative rate
     *
     * @return \(\frac{TN}{TN+FP}\)
     */
    public double getSpecificity() {
        return (double) tn / (tn + fp);
    }

    /**
     * Calculating the precision or positive predictive value
     *
     * @return \(\frac{TP}{TP+FP}\)
     */
    public double getPrecision() {
        return (double) tp / (tp + fp);
    }

    /**
     * Calculating the negative predictive value
     *
     * @return \(\frac{TN}{TN+FN}\)
     */
    public double getNegativePredictiveValue() {
        return (double) tn / (tn + fn);
    }

    /**
     * Calculating the fall-out or false positive rate
     *
     * @return \(\frac{FP}{FP+TN}\)
     */
    public double getFalsePositiveRate() {
        return (double) fp / (fp + tn);
    }

    /**
     * Calculating the false negative rate
     *
     * @return \(\frac{FN}{TP+FN}\)
     */
    public double getFalseNegativeRate() {
        return (double) fn / (tp + fn);
    }

    /**
     * Calculating the false discovery rate
     *
     * @return \(\frac{FP}{TP+FP}\)
     */
    public double getFalseDiscoveryRate() {
        return (double) fp / (tp + fp);
    }

    /**
     * Calculating the accuracy
     *
     * @return \(\frac{TP+TN}{TP+FP+FN+TN}\)
     */
    public double getAccuracy() {
        return (double) (tp + tn) / (tp + fp + fn + tn);
    }

    /**
     * Calculating the F1 score
     *
     * @return \(\frac{2TP}{2TP+FP+FN}\)
     */
    public double getF1Score() {
        return 2.0 * tp / (2 * tp + fp + fn);
    }

    /**
     * Calculating the Matthews correlation coefficient Buggy!
     *
     * @return \(\frac{TP*TN-FP*FN}{\sqrt{(TP+FP)(TP+FN)(TN+FP)(TN+FN)}}\)
     */
    public double getMatthewsCorrelationCoefficient() {
        long numerator = tp * tn - fp * fn;
        double denominator = FastMath.sqrt((tp + fp) * (fp + fn) * (tn + fp) * (tn + fn));
        return numerator / denominator;
    }

    /**
     * Calculating the informedness
     *
     * @return \(Sensitivity+Specificity-1\)
     */
    public double getInformedness() {
        return getSensitivity() + getSpecificity() - 1;
    }

    /**
     * Calculating the markedness
     *
     * @return \(Precision+\text{Negative predictive value}-1\)
     */
    public double getMarkedness() {
        return getPrecision() + getNegativePredictiveValue() - 1;
    }

}
