package de.cebitec.mgx.gui.attributevisualization.viewer;

import de.cebitec.mgx.gui.datamodel.AttributeType;
import de.cebitec.mgx.gui.datamodel.tree.Tree;

/**
 *
 * @author sjaenick
 */
public abstract class HierarchicalViewerI extends ViewerI<Tree<Long>> {

    @Override
    public boolean canHandle(AttributeType valueType) {
        return valueType.getStructure() == AttributeType.STRUCTURE_HIERARCHICAL;
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
