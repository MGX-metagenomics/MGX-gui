package de.cebitec.mgx.gui.biodiversity.statistic.impl;

import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.gui.biodiversity.statistic.Statistic;
import java.util.Map;

/**
 *
 * @author sjaenick
 */
public class Simpson implements Statistic<DistributionI> {

    @Override
    public String measure(DistributionI data) {
        
        if (data.size() == 1) {
            return "0.00";
        }
        
        double value = 0;
        long n = data.getTotalClassifiedElements();
        n = n * (n - 1);
        for (Map.Entry<AttributeI, Number> e : data.entrySet()) {
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
