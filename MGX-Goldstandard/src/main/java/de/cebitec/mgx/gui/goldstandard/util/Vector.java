package de.cebitec.mgx.gui.goldstandard.util;

import java.util.Arrays;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.util.FastMath;

/**
 *
 * @author patrick
 */
public class Vector {

    private int pointer;
    private final double[] data;

    public Vector(double[] array) {
        data = array.clone();
    }

    public Vector(int size) {
        pointer = 0;
        data = new double[size];
        Arrays.fill(data, 0);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Arrays.hashCode(this.data);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Vector other = (Vector) obj;
        return Arrays.equals(this.data, other.data);
    }

    public double[] asArray() {
        return data.clone();
    }

    public void add(double value) {
        if (pointer < data.length) {
            data[pointer++] = value;
        }
    }

    public Vector normalize() {
        Vector normalized = new Vector(this.dimensions());
        double length = length();
        for (int i = 0; i < data.length; i++) {
            normalized.add(data[i] / length);
        }
        return normalized;
    }

    public double length() {
        double sum = 0;
        for (double d : data) {
            sum += FastMath.pow(d, 2);
        }
        return FastMath.sqrt(sum);
    }

    public int dimensions() {
        return this.data.length;
    }

    public double euclideanDistance(Vector other) {
        return MathArrays.distance(this.data, other.data);
    }

    public double manhattanDistance(Vector other) {
        return MathArrays.distance1(this.data, other.data);
    }

    public double chebyshevDistance(Vector other) {
        return MathArrays.distanceInf(this.data, other.data);
    }

    public double pDistance(Vector other, double p) {
        if (data.length != other.data.length) {
            throw new DimensionMismatchException(other.data.length, data.length);
        }
        double sum = 0;
        for (int i = 0; i < data.length; i++) {
            double diff = data[i] - other.data[i];
            sum += FastMath.pow(diff, p);
        }
        return FastMath.pow(sum, 1 / p);
    }

}
