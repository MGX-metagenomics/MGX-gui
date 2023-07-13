
package de.cebitec.mgx.gui.biodiversity.statistic.impl;

import de.cebitec.mgx.api.misc.DistributionI;
import java.text.NumberFormat;
import java.util.Locale;
import org.apache.commons.math3.util.FastMath;
import de.cebitec.mgx.gui.biodiversity.statistic.StatisticI;

/**
 *
 * @author sjaenick
 */
public class Margalef implements StatisticI {
    
    // https://www.coastalwiki.org/introduced/Measurements_of_biodiversity

    @Override
    public String measure(DistributionI<Long> data) {
        int S = data.size();
        long N = data.getTotalClassifiedElements();
        double Dmg = (S-1) / (FastMath.log(N));
        return Double.isNaN(Dmg) ? "N/A" : NumberFormat.getInstance(Locale.US).format(Dmg);
    }

    @Override
    public String getName() {
        return "Margalef's diversity index";
    }
}
