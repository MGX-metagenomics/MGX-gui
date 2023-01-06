/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.datamodel.misc;

import de.cebitec.mgx.gui.datamodel.Attribute;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 *
 * @author sj
 */
public class MatrixTest {

    public MatrixTest() {
    }

    @Test
    public void test() {
        /*
         *            cattr1
         *   rattr1     1
         *   rattr2     5
         * 
         */
        Attribute rowAttr = new Attribute();
        rowAttr.setValue("rattr1");
        Attribute rowAttr2 = new Attribute();
        rowAttr2.setValue("rattr2");
        Attribute colAttr = new Attribute();
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