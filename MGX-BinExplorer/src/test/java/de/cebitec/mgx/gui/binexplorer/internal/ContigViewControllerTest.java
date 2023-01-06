/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package de.cebitec.mgx.gui.binexplorer.internal;

import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;


/**
 *
 * @author sj
 */
public class ContigViewControllerTest {

    @Test
    public void testEmptyState() {
        System.out.println("testEmptyState");
        ContigViewController instance = new ContigViewController();
        assertNull(instance.getSelectedBin());
        assertNull(instance.getContig());
        assertNull(instance.getSelectedRegion());
    }

    @Test
    public void testBinSelect() {
        System.out.println("testBinSelect");
        ContigViewController instance = new ContigViewController();
        instance.selectBin(null);
        assertNull(instance.getContig());
        assertNull(instance.getSelectedRegion());
    }

    @Test
    public void testContigSelect() {
        System.out.println("testContigSelect");
        ContigViewController instance = new ContigViewController();
        instance.setContig(null);
        assertNull(instance.getSelectedRegion());
    }

}
