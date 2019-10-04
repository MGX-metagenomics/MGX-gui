
package de.cebitec.mgx.gui.biodiversity.statistic.impl;

import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.model.AttributeI;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map.Entry;
import org.apache.commons.math3.util.FastMath;
import de.cebitec.mgx.gui.biodiversity.statistic.StatisticI;

/**
 *
 * @author sjaenick
 */
public class Shannon implements StatisticI {

    @Override
    public String measure(DistributionI<Long> data) {
        double ret = 0;
        long numElem = data.getTotalClassifiedElements();
        for (Entry<AttributeI, Long> e : data.entrySet()) {
            double relAbun = e.getValue().doubleValue() / (double)numElem;
            ret += relAbun * FastMath.log(relAbun);
        }
        ret = -1 * ret;
        return Double.isNaN(ret) ? "N/A" : NumberFormat.getInstance(Locale.US).format(ret);
    }

    @Override
    public String getName() {
        return "Shannon index";
    }
}
