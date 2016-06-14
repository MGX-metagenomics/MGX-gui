package de.cebitec.mgx.gui.goldstandard.ui.charts;

import de.cebitec.mgx.api.groups.ImageExporterI;
import de.cebitec.mgx.api.misc.Visualizable;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.SeqRunI;
import java.util.List;
import javax.swing.JComponent;

/**
 *
 * @param <T>
 * @author sjaenick
 */
public abstract class EvaluationViewerI<T> implements Comparable<EvaluationViewerI<T>> { //, VisFilterI<T>  {

    private String chartTitle;

    public EvaluationViewerI() {
    }

    /**
     *
     * @return main component representing the visualization
     */
    public abstract JComponent getComponent();

    /**
     *
     * @return exporter instance able to save the visualization
     */
    public abstract ImageExporterI getImageExporter();

    /**
     *
     * @return display name of the viewer
     */
    public abstract String getName();

    /**
     *
     * @param valueType
     * @return true if this viewer can display the attribute type
     */
    public abstract boolean canHandle(AttributeTypeI valueType);

    /**
     *
     * @return the expected class of data to be displayed
     */
    public abstract Class getInputType();

    /**
     *
     * @param dists distributions to be displayed
     */
    public abstract void show(List<T> dists);

    /**
     *
     * @return customizing component
     */
    public abstract JComponent getCustomizer();

    
    /**
     * 
     */
    public abstract void init(SeqRunI seqrun);
    
    /**
     *
     */
    public void dispose() {
    }

    /**
     *
     * @param title
     */
    public void setTitle(String title) {
        chartTitle = title;
    }

    /**
     *
     * @return
     */
    protected String getTitle() {
        return chartTitle;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public int compareTo(EvaluationViewerI<T> t) {
        return getName().compareTo(t.getName());
    }
}
