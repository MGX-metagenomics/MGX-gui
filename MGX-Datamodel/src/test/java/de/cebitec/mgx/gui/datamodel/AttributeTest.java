/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.datamodel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import org.junit.jupiter.api.Test;

/**
 *
 * @author sj
 */
public class AttributeTest {

    @Test
    public void testEquals() {
        System.out.println("testEquals");
        Attribute a1 = new Attribute();
        a1.setValue("FOO");
        Attribute a2 = new Attribute();
        a2.setValue("FOO");
        assertEquals(a1, a2);
    }

    @Test
    public void testNotEquals() {
        System.out.println("testNotEquals");
        Attribute a1 = new Attribute();
        a1.setValue("FOO");
        Attribute a2 = new Attribute();
        a2.setValue("BAR");
        assertNotEquals(a1, a2);
    }

}
