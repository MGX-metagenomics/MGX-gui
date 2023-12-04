/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package de.cebitec.mgx.gui.swingutils.util;

import java.awt.Color;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author sj
 */
public class ColorPaletteTest {
    
    /**
     * Test of from64Palette method, of class ColorPalette.
     */
    @Test
    public void testFrom64Palette() {
        System.out.println("from64Palette");
        List<Color> result = ColorPalette.from64Palette(123);
        assertEquals(123, result.size());
    }
}
