package de.cebitec.mgx.gui.biodiversity.statistic.impl;

import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.model.AttributeI;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;
import de.cebitec.mgx.gui.biodiversity.statistic.StatisticI;

/**
 *
 * @author sjaenick
 */
public class Simpson implements StatisticI {

    @Override
    public String measure(DistributionI<Long> data) {

        if (data.size() == 1) {
            return "0.00";
        }

        double value = 0;
        long n = data.getTotalClassifiedElements();
        n = n * (n - 1);
        for (Map.Entry<AttributeI, Long> e : data.entrySet()) {
            double tmp = e.getValue().doubleValue();
            value += (tmp * (tmp - 1)) / n;
        }
        double ret = 1 - value;
        return Double.isNaN(ret) ? "N/A" : NumberFormat.getInstance(Locale.US).format(ret);
    }

    @Override
    public String getName() {
        return "Simpson index";
    }

}
