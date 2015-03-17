/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.datamodel.misc;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.AttributeI;
import java.util.Collection;
import java.util.Map;

/**
 *
 * @author sjaenick
 */
public class NormalizedDistribution extends DistributionBase<Double> {

    public NormalizedDistribution(MGXMasterI master, Map<AttributeI, Double> data, long totalElem) {
        this(master, data, data.keySet(), totalElem);
    }

    public NormalizedDistribution(MGXMasterI master, Map<AttributeI, Double> data, Collection<AttributeI> order, long totalElem) {
        super(master, data, order, totalElem);
    }

    @Override
    public Class<Double> getEntryType() {
        return Double.class;
    }

}
