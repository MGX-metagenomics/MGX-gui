package de.cebitec.mgx.common.visualization;

import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.model.AttributeTypeI;

/**
 *
 * @author sj
 */
public abstract class NumericalViewerI extends ViewerI<DistributionI> {

    @Override
    public boolean canHandle(AttributeTypeI valueType) {
        return valueType.getValueType() == AttributeTypeI.VALUE_NUMERIC;
    }

    @Override
    public void setAttributeType(AttributeTypeI aType) {
        super.setAttributeType(aType);
        super.setTitle("Distribution of " + aType.getName());
    }
}
