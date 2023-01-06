/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping.tracks;

import de.cebitec.mgx.gui.datamodel.MappedSequence;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 *
 * @author sj
 */
public class LayouterTest {

    @Test
    public void testSortOrderID() {
        System.out.println("testSortOrderID");
        LayouterI xx = new Layouter();

        Track t1 = new Track(1);
        MappedSequence ms1 = new MappedSequence(1, 5, 20, 0);
        t1.add(ms1);
        xx.add(t1);

        Track t2 = new Track(2);
        MappedSequence ms2 = new MappedSequence(1, 5, 20, 0);
        t2.add(ms2);
        xx.add(t2);

        assertEquals(t1, xx.first());
        assertEquals(t2, xx.last());
    }

    @Test
    public void testSortOrderMax() {
        System.out.println("testSortOrderMax");
        Layouter xx = new Layouter();

        Track t1 = new Track(1);
        MappedSequence ms1 = new MappedSequence(1, 5, 25, 0);
        t1.add(ms1);
        xx.add(t1);

        Track t2 = new Track(2);
        MappedSequence ms2 = new MappedSequence(1, 5, 20, 0);
        t2.add(ms2);
        xx.add(t2);

        assertEquals(t2, xx.first());
        assertEquals(t1, xx.last());
    }

    @Test
    public void testFoo() {
        System.out.println("testSortOrderMax");
        Layouter xx = new Layouter();

        Track t1 = new Track(1);
        MappedSequence ms1 = new MappedSequence(1, 5, 25, 0);
        t1.add(ms1);
        xx.add(t1);

        Track t2 = new Track(2);
        MappedSequence ms2 = new MappedSequence(1, 5, 26, 0);
        t2.add(ms2);
        xx.add(t2);

    }

}
