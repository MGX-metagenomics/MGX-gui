package de.cebitec.mgx.gui.attributevisualization.viewer;

import de.cebitec.mgx.gui.datamodel.AttributeType;

/**
 *
 * @author sj
 */
public abstract class CategoricalViewerI extends ViewerI {

    @Override
    public boolean canHandle(AttributeType valueType) {
        return valueType.getValueType() == AttributeType.VALUE_DISCRETE;
    }
    
}
