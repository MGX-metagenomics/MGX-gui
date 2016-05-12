package de.cebitec.mgx.gui.goldstandard.util;

import de.cebitec.mgx.api.misc.Triple;
import de.cebitec.mgx.api.model.AttributeI;
import java.util.Collection;
import java.util.HashSet;

/**
 *
 * @author pblumenk
 */
public class MGSEntry {
    private final String header;
    
    private final Collection<Triple<AttributeI, Integer, Integer>> attribute;

    public MGSEntry(String header) {
        this.header = header;
        this.attribute = new HashSet<>();
    }
    
    public MGSEntry add(AttributeI at, int start, int stop){
        attribute.add(new Triple<>(at, start, stop));        
        return this;
    }
    
    public Collection<Triple<AttributeI, Integer, Integer>> getAttributes(){
        return attribute;
    }

    public String getHeader() {
        return header;
    }
    
    
}
