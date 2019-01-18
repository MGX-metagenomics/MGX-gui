
package de.cebitec.mgx.gui.biodiversity.statistic.impl;

import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.gui.biodiversity.statistic.Statistic;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map.Entry;
import org.apache.commons.math3.util.FastMath;

/**
 *
 * @author sjaenick
 */
public class ShannonEvenness implements Statistic {

    @Override
    public String measure(DistributionI<Long> data) {
        double shannonH = 0;
        long numElem = data.getTotalClassifiedElements();
        int S = 0;
        for (Entry<AttributeI, Long> e : data.entrySet()) {
            double relAbun = e.getValue().doubleValue() / (double)numElem;
            shannonH += relAbun * FastMath.log(relAbun);
            S++;
        }
        shannonH = -1 * shannonH;
        double evenness = shannonH / FastMath.log(S);
        return Double.isNaN(evenness) ? "N/A" : NumberFormat.getInstance(Locale.US).format(evenness);
    }

    @Override
    public String getName() {
        return "Shannon evenness";
    }
}
