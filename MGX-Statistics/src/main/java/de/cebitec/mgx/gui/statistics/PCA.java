package de.cebitec.mgx.gui.statistics;

import Jama.EigenvalueDecomposition;
import javax.swing.JComponent;
import org.math.array.DoubleArray;
import org.math.array.LinearAlgebra;
import org.math.array.StatisticSample;
import org.math.plot.Plot2DPanel;

/**
 * Copyright : BSD License
 *
 * @author Yann RICHET
 */
public class PCA {

    double[][] X; // initial datas : lines = events and columns = variables
    double[] meanX, stdevX;
    double[][] Z; // X centered reduced
    double[][] cov; // Z covariance matrix
    double[][] U; // projection matrix
    double[] info; // information matrix

    public PCA(double[][] _X) {
        X = _X;
        System.err.println("1");
        stdevX = StatisticSample.stddeviation(X);
        System.err.println("2");
        meanX = StatisticSample.mean(X);
        System.err.println("3");

        Z = center_reduce(X);
        System.err.println("4");

        cov = StatisticSample.covariance(Z);
        System.err.println("5");

        EigenvalueDecomposition e = LinearAlgebra.eigen(cov);
        System.err.println("6");
        U = StatisticSample.transpose(e.getV().getArray());
        System.err.println("7");
        //U = transpose(e.getV());
        info = e.getRealEigenvalues(); // covariance matrix is symetric, so only real eigenvalues...
        System.err.println("8");
    }

    // normalization of x relatively to X mean and standard deviation
    public final double[][] center_reduce(double[][] x) {
        double[][] y = new double[x.length][x[0].length];
        for (int i = 0; i < y.length; i++) {
            for (int j = 0; j < y[i].length; j++) {
                y[i][j] = (x[i][j] - meanX[j]) / stdevX[j];
            }
        }
        return y;
    }

    // de-normalization of y relatively to X mean and standard deviation
    public double[] inv_center_reduce(double[] y) {
        return inv_center_reduce(new double[][]{y})[0];
    }

    // de-normalization of y relatively to X mean and standard deviation
    public double[][] inv_center_reduce(double[][] y) {
        double[][] x = new double[y.length][y[0].length];
        for (int i = 0; i < x.length; i++) {
            for (int j = 0; j < x[i].length; j++) {
                x[i][j] = (y[i][j] * stdevX[j]) + meanX[j];
            }
        }
        return x;
    }

    public JComponent view() {
        // Plot
        Plot2DPanel plot = new Plot2DPanel();

        // initial Datas plot
        plot.addScatterPlot("datas", X);

        // line plot of principal directions
        plot.addLinePlot(Math.rint(info[0] * 100 / DoubleArray.sum(info)) + " %", meanX, inv_center_reduce(U[0]));
        plot.addLinePlot(Math.rint(info[1] * 100 / DoubleArray.sum(info)) + " %", meanX, inv_center_reduce(U[1]));
        return plot;
        // display in JFrame
        //return new FrameView(plot).
    }

    public void print() {
        // Command line display of results
        //System.out.println("projection vectors\n" + DoubleArray.tostring(LinearAlgebra.transpose(U)));
        //System.out.println("information per projection vector\n" + DoubleArray.tostring(info));
    }

    public static void main(String[] args) {
        double[][] xinit = DoubleArray.random(10, 3, 0, 10);

        System.err.println("len is " + xinit.length);

        // artificial initialization of relations
        double[][] x = new double[xinit.length][];
        for (int i = 0; i < x.length; i++) {
            x[i] = new double[]{xinit[i][0] + xinit[i][1], xinit[i][1]};
        }

        PCA pca = new PCA(x);
        pca.print();
        pca.view();
    }
}
