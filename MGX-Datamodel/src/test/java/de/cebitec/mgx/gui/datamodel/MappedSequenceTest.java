/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.datamodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sj
 */
public class MappedSequenceTest {

    public MappedSequenceTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testSortOrder() {
        System.err.println("sort order");
        MappedSequence ms1 = new MappedSequence(null, 1, 10, 20, 5);
        MappedSequence ms2 = new MappedSequence(null, 1, 15, 25, 5);
        List<MappedSequence> tmp = new ArrayList<>();
        tmp.add(ms1);
        tmp.add(ms2);
        Collections.sort(tmp);
        assertEquals(ms1, tmp.get(0));
    }

    @Test
    public void testMinMax() {
        System.err.println("MinMax");
        MappedSequence ms1 = new MappedSequence(null, 1, 10, 20, 5);
        assertEquals(10, ms1.getMin());
        assertEquals(20, ms1.getMax());
    }

    @Test
    public void testHashSet() {
        System.err.println("testHashSet");
        Set<MappedSequence> set = new HashSet<>();

        MappedSequence ms1 = new MappedSequence(null, 3436, 566474, 566528, 12);
        set.add(ms1);
        MappedSequence ms2 = new MappedSequence(null, 26467, 566474, 566528, 11);
        set.add(ms2);

        assertEquals(2, set.size());
    }

    @Test
    public void testSortedSet() {
        System.err.println("testSortedSet");
        SortedSet<MappedSequence> set = new TreeSet<>();

        MappedSequence ms1 = new MappedSequence(null, 3436, 566474, 566528, 12);
        set.add(ms1);
        MappedSequence ms2 = new MappedSequence(null, 26467, 566474, 566528, 11);
        set.add(ms2);

        assertEquals(2, set.size());
    }

    @Test
    public void testSortedSet2() {
        System.err.println("testSortedSet2");
        SortedSet<MappedSequence> set = new TreeSet<>();

        MappedSequence ms1 = new MappedSequence(null, 3436, 566474, 566528, 12);
        set.add(ms1);
        MappedSequence ms2 = new MappedSequence(null, 3436, 566474, 566528, 12);
        set.add(ms2);

        assertEquals(1, set.size());
    }

}
