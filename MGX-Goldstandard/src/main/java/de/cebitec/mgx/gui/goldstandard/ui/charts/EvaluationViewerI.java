package de.cebitec.mgx.gui.goldstandard.ui.charts;

import de.cebitec.mgx.api.groups.ImageExporterI;
import de.cebitec.mgx.api.model.SeqRunI;
import javax.swing.JComponent;

/**
 *
 * @author sjaenick
 */
public abstract class EvaluationViewerI implements Comparable<EvaluationViewerI> { //, VisFilterI<T>  {

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
     * calculates everything needed for painting the panel
     */
    public abstract void evaluate();

    /**
     *
     * @return customizing component
     */
    public abstract JComponent getCustomizer();

    
    /**
     * 
     */
    public abstract void selectJobs(SeqRunI seqrun);
    
    /**
     *
     */
    public void dispose() {
    }

    @Override
    public final String toString() {
        return getName();
    }

    @Override
    public final int compareTo(EvaluationViewerI t) {
        return getName().compareTo(t.getName());
    }
}
