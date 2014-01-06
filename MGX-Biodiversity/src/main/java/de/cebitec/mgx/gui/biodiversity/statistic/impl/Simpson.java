package de.cebitec.mgx.gui.biodiversity.statistic.impl;

import de.cebitec.mgx.gui.biodiversity.statistic.Statistic;
import de.cebitec.mgx.gui.datamodel.Attribute;
import de.cebitec.mgx.gui.datamodel.misc.Distribution;
import java.util.Map;

/**
 *
 * @author sjaenick
 */
public class Simpson implements Statistic<Distribution> {

    @Override
    public String measure(Distribution data) {
        
        if (data.size() == 1) {
            return "0.00";
        }
        
        double value = 0;
        long n = data.getTotalClassifiedElements();
        n = n * (n - 1);
        for (Map.Entry<Attribute, Number> e : data.entrySet()) {
            double tmp = e.getValue().doubleValue();
            value += (tmp * (tmp - 1)) / n;
        }
        return String.format("%.2f", 1 - value);
    }

    @Override
    public String getName() {
        return "Simpson index";
    }

}
