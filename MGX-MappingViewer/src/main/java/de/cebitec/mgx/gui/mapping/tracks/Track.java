/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping.tracks;

import de.cebitec.mgx.gui.datamodel.MappedSequence;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author sj
 */
public class Track {

    private final List<MappedSequence> content = new LinkedList<>();
    private final int padding = 25; // bp, should be px

    Track() {
    }

    public boolean tryAdd(MappedSequence ms) {
        for (MappedSequence m : content) {
            if (overlaps(m, ms)) {
                return false;
            }
        }
        content.add(ms);
        return true;
    }
    
    public void add(MappedSequence ms) {
        content.add(ms);
    }

    public boolean canAdd(MappedSequence ms) {
        for (MappedSequence m : content) {
            if (overlaps(m, ms)) {
                return false;
            }
        }
        return true;
    }

    public Iterator<MappedSequence> getSequences() {
        return content.iterator();
    }

    public int size() {
        return content.size();
    }

    private boolean overlaps(MappedSequence ms1, MappedSequence ms2) {
        int ms2min = ms2.getMin() - padding;
        int ms2max = ms2.getMax() + padding;
        return within(ms1.getMin(), ms2min, ms2max)
                || within(ms1.getMax(), ms2min, ms2max);
    }

    private static boolean within(int pos, int from, int to) {
        return pos >= from && pos <= to;
    }
}
