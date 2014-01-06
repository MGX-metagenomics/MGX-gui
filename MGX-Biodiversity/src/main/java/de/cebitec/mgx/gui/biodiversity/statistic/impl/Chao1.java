package de.cebitec.mgx.gui.biodiversity.statistic.impl;

import de.cebitec.mgx.gui.biodiversity.statistic.Statistic;
import de.cebitec.mgx.gui.datamodel.Attribute;
import de.cebitec.mgx.gui.datamodel.misc.Distribution;
import java.util.Map.Entry;

/**
 *
 * @author sjaenick
 */
public class Chao1 implements Statistic<Distribution> {

    @Override
    public String measure(Distribution data) {
        long numCategories = 0;
        long numSingletons = 0;
        long numDoubletons = 0;
        for (Entry<Attribute, Number> e : data.entrySet()) {
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
        double ret = numCategories + (Math.pow(numSingletons, 2f) / 2 * numDoubletons);

        return String.format("%.2f", ret);
    }

    @Override
    public String getName() {
        return "Chao1";
    }

}
