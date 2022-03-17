/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.datamodel.assembly;

import de.cebitec.mgx.gui.datamodel.ReferenceRegion;
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
public class GeneTest {
    
    public GeneTest() {
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

   

    /**
     * Test of getFrame method, of class Gene.
     */
    @Test
    public void testGetFrame() {
        System.out.println("getFrame");
        ReferenceRegion g = new ReferenceRegion(null, -1, -1, 1, 42, "noname");
        assertEquals(-3, g.getFrame());
    }

    
}
