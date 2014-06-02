/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.datamodel.misc;

import de.cebitec.mgx.gui.datamodel.Attribute;
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
public class MatrixTest {

    public MatrixTest() {
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
    public void test() {
        /*
         *            cattr1
         *   rattr1     1
         *   rattr2     5
         * 
         */
        Attribute rowAttr = new Attribute(null);
        rowAttr.setValue("rattr1");
        Attribute rowAttr2 = new Attribute(null);
        rowAttr2.setValue("rattr2");
        Attribute colAttr = new Attribute(null);
        colAttr.setValue("cattr1");
        Matrix<Attribute, Attribute> instance = new Matrix<>();
        instance.addData(rowAttr, colAttr, 1);
        instance.addData(rowAttr2, colAttr, 5);
        assertEquals(instance.getRowHeaders().size(), 2);
        assertEquals(instance.getColumnHeaders().size(), 1);
        assertArrayEquals(new int[]{2, 1}, instance.getSize());
        //
        long rowSum = instance.getRowSum(rowAttr);
        assertEquals(1, rowSum);
        //
        long columnSum = instance.getColumnSum(colAttr);
        assertEquals(6, columnSum);
    }
}