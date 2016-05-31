package de.cebitec.mgx.gui.attributevisualization.util;

import de.cebitec.mgx.api.groups.VGroupManagerI;
import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.tree.TreeI;
import de.cebitec.mgx.gui.attributevisualization.ui.ControlPanel;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import org.openide.util.Exceptions;

/**
 *
 * @author sjaenick
 */
public class ResultCollector extends SwingWorker<Pair<List<Pair<VisualizationGroupI, DistributionI<Long>>>, List<Pair<VisualizationGroupI, TreeI<Long>>>>, Void> {

    private final AttributeTypeI aType;
    private final List<Pair<VisualizationGroupI, DistributionI<Long>>> distHolder;
    private final List<Pair<VisualizationGroupI, TreeI<Long>>> hierarchyHolder;
    private final ControlPanel ctl;
    private final VGroupManagerI mgr;

    public ResultCollector(VGroupManagerI mgr, AttributeTypeI aType, List<Pair<VisualizationGroupI, DistributionI<Long>>> distHolder, List<Pair<VisualizationGroupI, TreeI<Long>>> hierarchyHolder, ControlPanel ctl) {
        this.mgr = mgr;
        this.aType = aType;
        this.distHolder = distHolder;
        this.hierarchyHolder = hierarchyHolder;
        this.ctl = ctl;
    }

    @Override
    protected Pair<List<Pair<VisualizationGroupI, DistributionI<Long>>>, List<Pair<VisualizationGroupI, TreeI<Long>>>> doInBackground() throws Exception {

        List<Pair<VisualizationGroupI, DistributionI<Long>>> distributions = mgr.getDistributions();
        assert distributions != null;
        List<Pair<VisualizationGroupI, TreeI<Long>>> hierarchies = null;

        if (aType.getStructure() == AttributeTypeI.STRUCTURE_HIERARCHICAL) {
            hierarchies = mgr.getHierarchies();
            assert hierarchies != null;
        }

        return new Pair<>(distributions, hierarchies);
    }

    @Override
    protected void done() {
        Pair<List<Pair<VisualizationGroupI, DistributionI<Long>>>, List<Pair<VisualizationGroupI, TreeI<Long>>>> p = null;
        try {
            p = get();
        } catch (InterruptedException | ExecutionException ex) {
            Exceptions.printStackTrace(ex);
        }
        distHolder.clear();
        hierarchyHolder.clear();
        if (p != null) {
            distHolder.addAll(p.getFirst());
            if (aType.getStructure() == AttributeTypeI.STRUCTURE_HIERARCHICAL) {
                hierarchyHolder.addAll(p.getSecond());
            }

            // we have the distribution, trigger update of viewer list
            ctl.updateViewerList();
        }
        super.done();
    }
}
