package de.cebitec.mgx.gui.goldstandard.ui.charts.pipelinecomparison;

import de.cebitec.mgx.gui.goldstandard.ui.charts.ComparisonTypeI;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author pblumenk
 */
@ServiceProvider(service = ComparisonTypeI.class)
public class PipelineComparison implements ComparisonTypeI {

    @Override
    public final Class<?> getChartInterface() {
        return PipelineComparisonI.class;
    }

    @Override
    public final String getName() {
        return "Compare pipelines";
    }

    @Override
    public final int compareTo(ComparisonTypeI o) {
        return this.getName().compareTo(o.getName());
    }

    @Override
    public final String toString() {
        return getName();
    }

}
