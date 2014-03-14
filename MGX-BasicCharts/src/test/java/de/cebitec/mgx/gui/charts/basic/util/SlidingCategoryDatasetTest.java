/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.charts.basic.util;

import org.jfree.data.category.DefaultCategoryDataset;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sjaenick
 */
public class SlidingCategoryDatasetTest {

    private final SlidingCategoryDataset ds;

    public SlidingCategoryDatasetTest() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String[] series = new String[]{"ser1", "ser2"};
        for (String s : series) {
            for (int i = 1; i <= 100; i++) {
                dataset.addValue(i, s, "A" + i);
            }
        }
        ds = new SlidingCategoryDataset(dataset);
    }

    @Test
    public void testGetWindowSize() {
        System.out.println("getWindowSize");
        assertEquals(SlidingCategoryDataset.DEFAULT_WINSIZE, ds.getWindowSize());
    }

    @Test
    public void testGetColumnCount() {
        System.out.println("testGetColumnCount");
        assertEquals(25, ds.getColumnCount());
    }

    @Test
    public void testGetTotalColumnCount() {
        System.out.println("testGetTotalColumnCount");
        assertEquals(100, ds.getTotalColumnCount());
    }

    @Test
    public void testOffSet() {
        System.out.println("testOffSet");
        ds.setOffset(0);
        Comparable cKey = ds.getColumnKey(0);
        assertEquals("A1", cKey);

        ds.setOffset(1);
        cKey = ds.getColumnKey(0);
        assertEquals("A2", cKey);

        ds.setOffset(75);
        cKey = ds.getColumnKey(0);
        assertEquals("A76", cKey);

        cKey = ds.getColumnKey(24);
        assertEquals("A100", cKey);

        boolean ok = false;
        try {
            ds.setOffset(76);
        } catch (ArrayIndexOutOfBoundsException a) {
            ok = true;
        } finally {
            ds.setOffset(0);
        }
        assertTrue(ok);

    }

    @Test
    public void testMaxOffSet() {
        int max = ds.getTotalColumnCount() - ds.getColumnCount();
        assertEquals(75, max);
    }

}
