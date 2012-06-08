package de.cebitec.mgx.gui.attributevisualization.viewer;

import de.cebitec.mgx.gui.datamodel.AttributeType;
import de.cebitec.mgx.gui.datamodel.Distribution;

/**
 *
 * @author sj
 */
public abstract class CategoricalViewerI extends ViewerI<Distribution> {

    @Override
    public boolean canHandle(AttributeType valueType) {
        return valueType.getValueType() == AttributeType.VALUE_DISCRETE;
    }

    @Override
    public void setAttributeType(AttributeType aType) {
        super.setAttributeType(aType);
        super.setTitle("Distribution of "+ aType.getName());
    }
}
