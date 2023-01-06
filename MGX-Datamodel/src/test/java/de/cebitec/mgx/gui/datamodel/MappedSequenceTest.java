/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.datamodel;

import de.cebitec.mgx.api.model.MappedSequenceI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 *
 * @author sj
 */
public class MappedSequenceTest {

    public MappedSequenceTest() {
    }

    @Test
    public void testSortOrder() {
        System.err.println("sort order");
        MappedSequence ms1 = new MappedSequence(1, 10, 20, 5);
        MappedSequence ms2 = new MappedSequence(1, 15, 25, 5);
        List<MappedSequenceI> tmp = new ArrayList<>();
        tmp.add(ms1);
        tmp.add(ms2);
        Collections.sort(tmp);
        assertEquals(ms1, tmp.get(0));
    }

    @Test
    public void testMinMax() {
        System.err.println("MinMax");
        MappedSequence ms1 = new MappedSequence(1, 10, 20, 5);
        assertEquals(10, ms1.getMin());
        assertEquals(20, ms1.getMax());
    }

    @Test
    public void testHashSet() {
        System.err.println("testHashSet");
        Set<MappedSequenceI> set = new HashSet<>();

        MappedSequence ms1 = new MappedSequence(3436, 566474, 566528, 12);
        set.add(ms1);
        MappedSequence ms2 = new MappedSequence(26467, 566474, 566528, 11);
        set.add(ms2);

        assertEquals(2, set.size());
    }

    @Test
    public void testSortedSet() {
        System.err.println("testSortedSet");
        SortedSet<MappedSequenceI> set = new TreeSet<>();

        MappedSequence ms1 = new MappedSequence(3436, 566474, 566528, 12);
        set.add(ms1);
        MappedSequence ms2 = new MappedSequence(26467, 566474, 566528, 11);
        set.add(ms2);

        assertEquals(2, set.size());
    }

    @Test
    public void testSortedSet2() {
        System.err.println("testSortedSet2");
        SortedSet<MappedSequenceI> set = new TreeSet<>();

        MappedSequence ms1 = new MappedSequence(3436, 566474, 566528, 12);
        set.add(ms1);
        MappedSequence ms2 = new MappedSequence(3436, 566474, 566528, 12);
        set.add(ms2);

        assertEquals(1, set.size());
    }

}
