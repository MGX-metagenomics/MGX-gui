package de.cebitec.mgx.gui.goldstandard.ui.charts;

import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author pblumenk
 */
@ServiceProvider(service = ComparisonTypeI.class)
public class PipelineComparisonViewer implements ComparisonTypeI{

    @Override
    public Class getChartInterface() {
        return PipelineComparisonI.class;
    }
    
    
    @Override
    public String getName() {
        return "Compare pipelines";
    }

}
