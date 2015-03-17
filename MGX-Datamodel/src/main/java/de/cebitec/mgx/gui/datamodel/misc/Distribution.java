/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.datamodel.misc;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.AttributeI;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author sjaenick
 */
public class Distribution extends DistributionBase<Long> {
    
    
    public Distribution(MGXMasterI master, Map<AttributeI, Long> data) {
        this(master, data, count(data));
    }
    
    public Distribution(MGXMasterI master, Map<AttributeI, Long> data, long total) {
        this(master, data, data.keySet(), total);
    }
    
    public Distribution(MGXMasterI master, Map<AttributeI, Long> data, Collection<AttributeI> order, long total) {
        super(master, data, order, total);
    }
  
    @Override
    public Class<Long> getEntryType() {
        return Long.class;
    }
    
}
