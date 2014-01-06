package de.cebitec.mgx.gui.biodiversity.statistic;

/**
 *
 * @author sjaenick
 */
public interface Statistic<T> {

    public String measure(T data);

    public String getName();

}
