package de.cebitec.mgx.gui.goldstandard.util;

import de.cebitec.mgx.api.model.AttributeI;
import java.util.Collection;
import java.util.HashSet;

/**
 *
 * @author pblumenk
 */
public class MGSEntry {

    private final String header;
    private final Collection<MGSAttribute> attribute;

    public MGSEntry(String header) {
        this.header = header;
        this.attribute = new HashSet<>();
    }

    public MGSEntry add(AttributeI at, int start, int stop) {
        attribute.add(new MGSAttribute(at, start, stop));
        return this;
    }

    public Collection<MGSAttribute> getAttributes() {
        return attribute;
    }

    public String getHeader() {
        return header;
    }

}
