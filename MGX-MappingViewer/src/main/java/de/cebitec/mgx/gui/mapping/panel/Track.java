/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping.panel;

import de.cebitec.mgx.gui.datamodel.MappedSequence;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author sj
 */
public class Track {

    private final double vOffset;
    private final List<MappedSequence> content = new ArrayList<>();
    private final static int padding = 2;

    public Track(double vOffset) {
        this.vOffset = vOffset;
    }

    public void add(MappedSequence ms) {
        content.add(ms);
    }

    public boolean overlaps(MappedSequence ms) {
        for (MappedSequence m : content) {
            if (overlaps(m, ms)) {
                return true;
            }
        }
        return false;
    }

    public double getVOffset() {
        return vOffset;
    }

    public Iterator<MappedSequence> getSequences() {
        return content.iterator();
    }
    
    private final static boolean overlaps(MappedSequence ms1, MappedSequence ms2) {
        return within(ms1.getMin(), ms2.getMin(), ms2.getMax())
                || within(ms1.getMax(), ms2.getMin(), ms2.getMax());
    }
    
    private final static boolean within(int pos, int from, int to) {
        return pos >= from - padding || pos <= to + padding;
    }
}
