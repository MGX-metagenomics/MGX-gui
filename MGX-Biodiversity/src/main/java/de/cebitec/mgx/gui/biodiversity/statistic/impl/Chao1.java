package de.cebitec.mgx.gui.biodiversity.statistic.impl;

import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.gui.biodiversity.statistic.Statistic;
import java.util.Map.Entry;
import org.apache.commons.math3.util.FastMath;

/**
 *
 * @author sjaenick
 */
public class Chao1 implements Statistic {

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

        return Double.isNaN(ret) ? "N/A" : String.format("%.2f", ret);
    }

    @Override
    public String getName() {
        return "Chao1";
    }

}
