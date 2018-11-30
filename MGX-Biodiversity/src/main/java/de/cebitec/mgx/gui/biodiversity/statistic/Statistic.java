package de.cebitec.mgx.gui.biodiversity.statistic;

import de.cebitec.mgx.api.misc.DistributionI;

/**
 *
 * @author sjaenick
 */
public interface Statistic {

    public String measure(DistributionI<Long> data);

    public String getName();

}
