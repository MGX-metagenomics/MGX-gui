package de.cebitec.mgx.gui.attributevisualization.util;

import de.cebitec.mgx.gui.attributevisualization.VGroupManager;
import de.cebitec.mgx.gui.attributevisualization.ui.ControlPanel;
import de.cebitec.mgx.gui.datamodel.AttributeType;
import de.cebitec.mgx.gui.datamodel.misc.Distribution;
import de.cebitec.mgx.gui.datamodel.misc.Pair;
import de.cebitec.mgx.gui.datamodel.tree.Tree;
import de.cebitec.mgx.gui.groups.VisualizationGroup;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import org.openide.util.Exceptions;

/**
 *
 * @author sjaenick
 */
public class ResultCollector extends SwingWorker<Pair<List<Pair<VisualizationGroup, Distribution>>, List<Pair<VisualizationGroup, Tree<Long>>>>, Void> {

    private AttributeType aType;
    private List<Pair<VisualizationGroup, Distribution>> distHolder;
    private List<Pair<VisualizationGroup, Tree<Long>>> hierarchyHolder;
    private ControlPanel ctl;

    public ResultCollector(AttributeType aType, List<Pair<VisualizationGroup, Distribution>> distHolder, List<Pair<VisualizationGroup, Tree<Long>>> hierarchyHolder, ControlPanel ctl) {
        this.aType = aType;
        this.distHolder = distHolder;
        this.hierarchyHolder = hierarchyHolder;
        this.ctl = ctl;
    }

    @Override
    protected Pair<List<Pair<VisualizationGroup, Distribution>>, List<Pair<VisualizationGroup, Tree<Long>>>> doInBackground() throws Exception {
        
        List<Pair<VisualizationGroup, Distribution>> distributions = VGroupManager.getInstance().getDistributions();
        List<Pair<VisualizationGroup, Tree<Long>>> hierarchies = null;
        
        if (aType.getStructure() == AttributeType.STRUCTURE_HIERARCHICAL) {
            hierarchies = VGroupManager.getInstance().getHierarchies();
        }

        return new Pair(distributions, hierarchies);
    }

    @Override
    protected void done() {
        Pair<List<Pair<VisualizationGroup, Distribution>>, List<Pair<VisualizationGroup, Tree<Long>>>> p = null;
        try {
             p = get();
        } catch (InterruptedException | ExecutionException ex) {
            Exceptions.printStackTrace(ex);
        }
        assert p != null;
        distHolder.clear();
        hierarchyHolder.clear();
        distHolder.addAll(p.getFirst());
        
        if (p.getSecond() != null)
            hierarchyHolder.addAll(p.getSecond());

        if (aType.getStructure() == AttributeType.STRUCTURE_HIERARCHICAL) {
            assert hierarchyHolder != null;
        }
        // we have the distribution, trigger update of viewer list
        ctl.updateViewerList();
        super.done();
    }
}