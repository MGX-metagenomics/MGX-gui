/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.JobParameterI;
import de.cebitec.mgx.gui.util.TestMaster;
import de.cebitec.mgx.testutils.TestInput;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author sj
 */
public class ToolAccessTest {

    public ToolAccessTest() {
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
     * Test of getAvailableParameters method, of class ToolAccess.
     */
    @Test
    public void testGetAvailableParameters() {
        System.out.println("getAvailableParameters");
        //File plugin = TestInput.copyTestResource(getClass(), "/de/cebitec/mgx/gui/controller/plugindump.xml");
        File pipe = null;
        try {
            pipe = TestInput.copyTestResource(getClass(), "de/cebitec/mgx/gui/controller/qiime_assignTaxonomy.xml");
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
        String xmlData = null;
        try {
            xmlData = readFile(pipe.getAbsolutePath());
        } catch (IOException ex) {
            fail(ex.getMessage());
        }

        MGXMasterI master = TestMaster.getRO();
        Collection<JobParameterI> params = null;
        try {
            params = master.Tool().getAvailableParameters(xmlData);
        } catch (MGXException ex) {
            fail(ex.getMessage());
        }
        assertNotNull(params);
        assertEquals(4, params.size());

        JobParameterI dbParam = null;
        for (JobParameterI jp : params) {
            if (jp.getUserName().equals("database")) {
                dbParam = jp;
                break;
            }
        }
        assertNotNull(dbParam);
        assertNotNull(dbParam.getChoices());
        assertEquals(2, dbParam.getChoices().size());
    }

    private static String readFile(String path) throws IOException {
        StringBuilder content = new StringBuilder();
        FileInputStream fis = new FileInputStream(path);
        try (DataInputStream in = new DataInputStream(fis)) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                content.append(strLine);
            }
        }
        return content.toString();
    }

}
