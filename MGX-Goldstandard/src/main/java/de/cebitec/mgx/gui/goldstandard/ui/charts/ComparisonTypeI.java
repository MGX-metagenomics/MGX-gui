package de.cebitec.mgx.gui.goldstandard.ui.charts;

/**
 *
 * @author pblumenk
 */
public interface ComparisonTypeI extends Comparable<ComparisonTypeI> {
    
    /**
     *
     * @return return interface which all chart classes implement and which is registered at the lookup manager
     */
    public Class<?> getChartInterface();
    
    /**
     *
     * @return display name of the comparison type
     */
    public String getName();
}
