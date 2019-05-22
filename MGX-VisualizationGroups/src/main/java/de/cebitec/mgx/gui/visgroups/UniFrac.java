/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.visgroups;

import de.cebitec.mgx.api.model.tree.NodeI;
import de.cebitec.mgx.api.model.tree.TreeI;
import de.cebitec.mgx.gui.datafactories.TreeFactory;

/**
 *
 * @author sj
 */
public class UniFrac {

    @SuppressWarnings("unchecked")
    public static double unweighted(TreeI<Long> first, TreeI<Long> second) {
        TreeI<Double> f = TreeFactory.convert(first);
        TreeI<Double> s = TreeFactory.convert(second);
        TreeI<double[]> merged = TreeFactory.mergeTrees(f, s);

        double shared = 0;
        double all = 0;

        for (NodeI<double[]> node : merged.getNodes()) {
            double[] content = node.getContent();

            // shared node
            if (content[0] != 0d && content[1] != 0d) {
                shared += node.getDepth();
            }

            all += node.getDepth();
        }
        return shared / all;
    }

    @SuppressWarnings("unchecked")
    public static double weighted(TreeI<Long> first, TreeI<Long> second) {
        TreeI<Double> f = TreeFactory.normalize(first);
        TreeI<Double> s = TreeFactory.normalize(second);
        TreeI<double[]> merged = TreeFactory.mergeTrees(f, s);

        double u = 0;
        double D = 0;
        for (NodeI<double[]> node : merged.getNodes()) {
            double[] content = node.getContent();
            u += Math.abs(content[0] - content[1]) * (1 + node.getDepth());
            D += (content[0] + content[1]) * (1 + node.getDepth());
        }

        return u / D;
    }

    public static double generalized(TreeI<Long> first, TreeI<Long> second) {
        return generalized(first, second, 0.5);
    }

    @SuppressWarnings("unchecked")
    public static double generalized(TreeI<Long> first, TreeI<Long> second, double alpha) {
        TreeI<Double> f = TreeFactory.normalize(first);
        TreeI<Double> sec = TreeFactory.normalize(second);
        TreeI<double[]> merged = TreeFactory.mergeTrees(f, sec);

        double a = 0;
        double b = 0;
        for (NodeI<double[]> node : merged.getNodes()) {
            double[] content = node.getContent();
            double d = content[0] - content[1];
            double s = content[0] + content[1];
            a += Math.abs(d / s) * Math.pow(s, alpha) * node.getDepth();
            b += Math.pow(s, alpha) * node.getDepth();
        }

        return a / b;
    }

}
