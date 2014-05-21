/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping.tracks;

import de.cebitec.mgx.gui.datamodel.MappedSequence;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author sj
 */
public class Track {

    private final List<MappedSequence> content = new ArrayList<>();
    private final static int padding = 1;

    public Track() {
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

    public Iterator<MappedSequence> getSequences() {
        return content.iterator();
    }
    
    public int size() {
        return content.size();
    }

    private static boolean overlaps(MappedSequence ms1, MappedSequence ms2) {
//        if (within(ms1.getMin(), ms2.getMin(), ms2.getMax())) {
//            System.err.println(ms1.getSeqId() + " min ");
//        }
//        if (within(ms1.getMax(), ms2.getMin(), ms2.getMax())) {
//            System.err.println(ms1.getSeqId() + " max ");
//        }

        return within(ms1.getMin(), ms2.getMin(), ms2.getMax())
                || within(ms1.getMax(), ms2.getMin(), ms2.getMax());
    }

    private static boolean within(int pos, int from, int to) {
        return pos >= from - padding && pos <= to + padding;
    }
}
