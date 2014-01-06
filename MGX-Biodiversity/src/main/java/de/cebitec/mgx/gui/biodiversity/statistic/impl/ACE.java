package de.cebitec.mgx.gui.biodiversity.statistic.impl;

import de.cebitec.mgx.gui.biodiversity.statistic.Statistic;
import de.cebitec.mgx.gui.datamodel.Attribute;
import de.cebitec.mgx.gui.datamodel.misc.Distribution;
import java.util.Map;

/**
 *
 * @author sjaenick
 */
public class ACE implements Statistic<Distribution> {

    @Override
    public String measure(Distribution data) {
        double ret = 0;
        double Sabundant = 0;
        double Srare = 0;
        for (Map.Entry<Attribute, Number> e : data.entrySet()) {

            long val = e.getValue().longValue();

            if (val > 10) {
                Sabundant++;
            } else {
                Srare++;
            }
        }
        double F1 = getF(data, 1);
        double Nrare = getNrare(data);
        double Cace = 1 - (F1 / Nrare);
        double gamma = getGamma(data);

        ret = Sabundant + (Srare / Cace) + (F1 / Cace) * Math.pow(gamma, 2);
        return String.format("%.2f", ret);
    }

    @Override
    public String getName() {
        return "ACE";
    }

    private double getGamma(Distribution data) {
        double ret = 0;
        
        // zaehler
        for (int i = 1; i <= 10; i++) {
            ret += i * (i - 1) * getF(data, i);
        }
        ret = ret * getSrare(data);
        
        // nenner
        double F1 = getF(data, 1);
        double Nrare = getNrare(data);
        double Cace = 1 - (F1 / Nrare);
        
        ret = (ret / (Cace * Nrare * (Nrare -1))) -1;
        
        return Math.max(ret, 0);
    }

    private double getSrare(Distribution data) {
        double ret = 0;
        for (Map.Entry<Attribute, Number> e : data.entrySet()) {
            long val = e.getValue().longValue();
            if (val <= 10) {
                ret++;
            }
        }
        return ret;
    }

    private double getNrare(Distribution data) {
        double ret = 0;
        for (int i = 1; i <= 10; i++) {
            ret += getF(data, i);
        }
        return ret;
    }

    private double getF(Distribution data, int num) {
        double ret = 0;
        for (Map.Entry<Attribute, Number> e : data.entrySet()) {
            if (e.getValue().intValue() == num) {
                ret++;
            }
        }
        return ret;
    }
}
