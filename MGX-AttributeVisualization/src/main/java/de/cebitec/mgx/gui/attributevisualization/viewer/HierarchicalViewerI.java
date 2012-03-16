package de.cebitec.mgx.gui.attributevisualization.viewer;

import de.cebitec.mgx.gui.datamodel.AttributeType;

/**
 *
 * @author sjaenick
 */
public abstract class HierarchicalViewerI extends ViewerI {

    @Override
    public boolean canHandle(AttributeType valueType) {
        return valueType.getValueType() == AttributeType.STRUCTURE_HIERARCHICAL;
    }
}
