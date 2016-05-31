/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.misc;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.AttributeI;
import java.util.Map;

/**
 *
 * @author sj
 */
public interface DistributionI<T extends Number> extends Map<AttributeI, T> , Visualizable {

    public MGXMasterI getMaster();

    public long getTotalClassifiedElements();
    
    public Class<T> getEntryType();

//    public void setOrder(List<AttributeI> o);
//
//    public void reset();
}
