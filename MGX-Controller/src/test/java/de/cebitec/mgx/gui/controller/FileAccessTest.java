/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.api.model.MGXFileI;
import de.cebitec.mgx.client.datatransfer.FileUploader;
import de.cebitec.mgx.gui.datamodel.MGXFile;
import de.cebitec.mgx.gui.datamodel.misc.Task;
import de.cebitec.mgx.gui.util.TestMaster;
import java.io.File;
import java.util.Iterator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sjaenick
 */
public class FileAccessTest {

    private MGXMaster master;

    public FileAccessTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        master = TestMaster.getRO();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testFetchall() {
        System.out.println("fetchall");
        Iterator<MGXFileI> iter = master.File().fetchall();
        assertNotNull(iter);
        int numFiles = 0;
        int numDirs = 0;
        while (iter.hasNext()) {
            MGXFileI f = iter.next();
            //System.err.println(f.getFullPath() + " --> " + f.getName());
            if (f.isDirectory()) {
                numDirs++;
            } else {
                numFiles++;
            }
        }
        assertEquals(1, numDirs);
        assertEquals(3, numFiles);
    }

}
