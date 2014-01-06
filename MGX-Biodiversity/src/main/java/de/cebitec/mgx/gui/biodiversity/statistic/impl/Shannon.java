
package de.cebitec.mgx.gui.biodiversity.statistic.impl;

import de.cebitec.mgx.gui.biodiversity.statistic.Statistic;
import de.cebitec.mgx.gui.datamodel.Attribute;
import de.cebitec.mgx.gui.datamodel.misc.Distribution;
import java.util.Map.Entry;

/**
 *
 * @author sjaenick
 */
public class Shannon implements Statistic<Distribution> {

    @Override
    public String measure(Distribution data) {
        double ret = 0;
        long numElem = data.getTotalClassifiedElements();
        for (Entry<Attribute, Number> e : data.entrySet()) {
            double relAbun = e.getValue().doubleValue() / (double)numElem;
            ret += relAbun * Math.log(relAbun);
        }
        return String.format("%.2f", -1 * ret);
    }

    @Override
    public String getName() {
        return "Shannon index";
    }
}
