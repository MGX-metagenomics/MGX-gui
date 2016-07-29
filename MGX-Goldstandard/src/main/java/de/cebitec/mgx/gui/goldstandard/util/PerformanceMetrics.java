package de.cebitec.mgx.gui.goldstandard.util;

import org.apache.commons.math3.util.FastMath;

/**
 *
 * @author pblumenk
 */
public class PerformanceMetrics {

    private long fp, fn, tp, tn;
    
    public PerformanceMetrics(){
        this(0, 0, 0, 0);
    }

    public PerformanceMetrics(long falsePostive, long falseNegative, long truePositive, long trueNegative) {
        this.fp = falsePostive;
        this.fn = falseNegative;
        this.tp = truePositive;
        this.tn = trueNegative;
    }
    
    public void add(long falsePostive, long falseNegative, long truePositive, long trueNegative){
        this.fp += falsePostive;
        this.fn += falseNegative;
        this.tp += truePositive;
        this.tn += trueNegative;
    }
    
    public void incrementFP(){
        this.fp++;
    }
    
    public void incrementFN(){
        this.fn++;
    }
    
    public void incrementTP(){
        this.tp++;
    }
    
    public void incrementTN(){
        this.tn++;
    }

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
     * @return \(\frac{TP}{TP+FN}\)
     */
    public double getSensitivity(){
        return tp/(tp+fn);
    }
    
     /**
     * Calculating the specificity or true negative rate
     * @return \(\frac{TN}{TN+FP}\)
     */
    public double getSpecifity(){
        return tn/(tn+fp);
    }
    
    /**
     * Calculating the precision or positive predictive value
     * @return \(\frac{TP}{TP+FP}\)
     */
    public double getPrecision(){
        return tp/(tp+fp);
    }
    
    /**
     * Calculating the negative predictive value
     * @return \(\frac{TN}{TN+FN}\)
     */
    public double getNegativePredictiveValue(){
        return tn/(tn+fn);
    }
    
    /**
     * Calculating the fall-out or false positive rate 
     * @return \(\frac{FP}{FP+TN}\)
     */
    public double getFalsePositiveRate(){
        return fp/(fp+tn);
    }
    
    /**
     * Calculating the false negative rate
     * @return \(\frac{FN}{TP+FN}\)
     */
    public double getFalseNegativeRate(){
        return fn/(tp+fn);
    }
    
    /**
     * Calculating the false discovery rate
     * @return \(\frac{FP}{TP+FP}\)
     */
    public double getFalseDiscoveryRate(){
        return fp/(tp+fp);
    }
    
    /**
     * Calculating the accuracy
     * @return \(\frac{TP+TN}{TP+FP+FN+TN}\)
     */
    public double getAccuracy(){
        return (tp+tn)/(tp+fp+fn+tn);
    }
    
    /**
     * Calculating the F1 score
     * @return \(\frac{2TP}{2TP+FP+FN}\)
     */
    public double getF1Score(){
        return 2*tp/(2*tp+fp+fn);
    }
    
    /**
     * Calculating the Matthews correlation coefficient
     * @return \(\frac{TP*TN-FP*FN}{\sqrt{(TP+FP)(TP+FN)(TN+FP)(TN+FN)}}\)
     */
    public double getMatthewsCorrelationCoefficient(){
        long numerator = tp*tn-fp*fn;
        double denominator = FastMath.sqrt((tp+fp)*(fp+fn)*(tn+fp)*(tn+fn));
        return numerator/denominator;
    }
    
    /**
     * Calculating the informedness
     * @return \(Sensitivity+Specificity-1\)
     */
    public double getInformedness(){
        return getSensitivity() + getSpecifity() - 1;
    }
    
    /**
     * Calculating the markedness
     * @return \(Precision+\text{Negative predictive value}-1\)
     */
    public double getMarkedness(){
        return getPrecision() + getNegativePredictiveValue() - 1;
    }
    
}
