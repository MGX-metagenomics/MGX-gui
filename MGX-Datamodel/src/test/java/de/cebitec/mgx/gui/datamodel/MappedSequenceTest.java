/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.datamodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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
        MappedSequence ms1 = new MappedSequence(1, 10, 20, 5);
        MappedSequence ms2 = new MappedSequence(1, 15, 25, 5);
        List<MappedSequence> tmp = new ArrayList<>();
        tmp.add(ms1);
        tmp.add(ms2);
        Collections.sort(tmp);
        Assert.assertEquals(ms1, tmp.get(0));
    }

}
