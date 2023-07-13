
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
public class Menhinick implements StatisticI {
    
    // https://www.coastalwiki.org/introduced/Measurements_of_biodiversity

    @Override
    public String measure(DistributionI<Long> data) {
        int S = data.size();
        long N = data.getTotalClassifiedElements();
        double Dmn = S / (FastMath.sqrt(N));
        return Double.isNaN(Dmn) ? "N/A" : NumberFormat.getInstance(Locale.US).format(Dmn);
    }

    @Override
    public String getName() {
        return "Menhinick's diversity index";
    }
}
