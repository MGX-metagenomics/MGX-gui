package de.cebitec.mgx.gui.viewer.api;

import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.tree.TreeI;

/**
 *
 * @author sjaenick
 */
public abstract class HierarchicalViewerI extends AbstractViewer<TreeI<Long>> {

    @Override
    public boolean canHandle(AttributeTypeI valueType) {
        return valueType.getStructure() == AttributeTypeI.STRUCTURE_HIERARCHICAL;
    }

    @Override
    public Class<?> getInputType() {
        return TreeI.class;
    }
//    @Override
//    public List<Pair<VisualizationGroup, Distribution>> filter(List<Pair<VisualizationGroup, Distribution>> dists) {
//        for (Pair<VisualizationGroup, Distribution> pair : dists) {
//            Distribution dist = pair.getSecond();
//            TreeFactory.createTree(dist.getMap());
//        }
//        return null;
//    }
//    
}
