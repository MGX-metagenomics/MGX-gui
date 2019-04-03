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
public class Chao1 implements StatisticI {

    @Override
    public String measure(DistributionI<Long> data) {
        long numCategories = 0;
        long numSingletons = 0;
        long numDoubletons = 0;
        for (Entry<AttributeI, Long> e : data.entrySet()) {
            numCategories++;
            switch (e.getValue().intValue()) {
                case 1:
                    numSingletons++;
                    break;
                case 2:
                    numDoubletons++;
                    break;
                default:
                    break;

            }
        }
        // classic formula
        double ret = numCategories + (FastMath.pow(numSingletons, 2f) / 2 * numDoubletons);

        return Double.isNaN(ret) ? "N/A" : NumberFormat.getInstance(Locale.US).format(ret);
    }

    @Override
    public String getName() {
        return "Chao1";
    }

}
