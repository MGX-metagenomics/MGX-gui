package de.cebitec.mgx.gui.biodiversity.statistic;

import de.cebitec.mgx.api.misc.DistributionI;

/**
 *
 * @author sjaenick
 */
public interface StatisticI {

    /**
     * @return name of the implemented statistic
     */
    public String getName();
    
    /**
     * 
     * @param distribution attribute distribution for selected group
     * @return pre-formatted String with computed value
     */
    public String measure(DistributionI<Long> distribution);

}
