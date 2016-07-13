package de.cebitec.mgx.gui.goldstandard.ui.charts;

import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author pblumenk
 */
@ServiceProvider(service = ComparisonTypeI.class)
public class PipelineComparisonViewer implements ComparisonTypeI, Comparable<ComparisonTypeI>{

    public PipelineComparisonViewer() {
    }
    
    @Override
    public Class getChartInterface() {
        return PipelineComparisonI.class;
    }
    
    
    @Override
    public String getName() {
        return "Compare pipelines";
    }

    @Override
    public int compareTo(ComparisonTypeI o) {
        return this.getName().compareTo(o.getName());
    }
    
    @Override
    public String toString() {
        return getName();
    }

}