/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.misc.TaskI;
import de.cebitec.mgx.api.misc.TaskI.State;
import de.cebitec.mgx.api.model.JobParameterI;
import de.cebitec.mgx.api.model.ToolI;
import de.cebitec.mgx.common.ToolScope;
import de.cebitec.mgx.gui.util.TestInput;
import de.cebitec.mgx.gui.util.TestMaster;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openide.util.Exceptions;

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

    @Test
    public void testFetch() {
        System.out.println("testFetch");
        MGXMasterI master = TestMaster.getRO();
        ToolI tool = null;
        try {
            tool = master.Tool().fetch(1);
        } catch (MGXException ex) {
            fail(ex.getMessage());
        }
        assertNotNull(tool);
        assertEquals("GC content", tool.getName());
    }

    @Test
    public void testFetchall() {
        System.out.println("testFetchall");
        MGXMasterI master = TestMaster.getRO();
        Iterator<ToolI> iter = null;
        try {
            iter = master.Tool().fetchall();
        } catch (MGXException ex) {
            fail(ex.getMessage());
        }
        assertNotNull(iter);
        int cnt = 0;
        while (iter.hasNext()) {
            ToolI tool = iter.next();
            assertNotNull(tool);
            cnt++;
        }
        assertEquals("Unit test project contains 18 tools", 18, cnt);
    }

    @Test
    public void testCreate() {
        System.out.println("testCreate");
        MGXMasterI master = TestMaster.getRW();
        ToolI tool = null;
        try {
            tool = master.Tool().create(ToolScope.READ, "Tool Name", "Tool description", "Tool author", "http://", 1.0f, "<xml>");
        } catch (MGXException ex) {
            fail(ex.getMessage());
        } finally {
            boolean toolCreated = tool != null;
            if (tool != null) {
                try {
                    TaskI<ToolI> task = master.Tool().delete(tool);
                    while (!task.done()) {
                        master.<ToolI>Task().refresh(task);
                    }
                    assertEquals(State.FINISHED, task.getState());
                } catch (MGXException ex) {
                    fail(ex.getMessage());
                }
            }
            assertTrue(toolCreated);
            assertTrue(tool.isDeleted());
        }

    }

    @Test
    public void testDuplicateCreate() {
        System.out.println("testDuplicateCreate");
        MGXMasterI master = TestMaster.getRW();
        ToolI tool1 = null;
        try {
            tool1 = master.Tool().create(ToolScope.READ, "Tool Name", "Tool description", "Tool author", "http://", 1.0f, "<xml>");
        } catch (MGXException ex) {
            fail(ex.getMessage());
        }
        boolean tool1Created = tool1 != null;

        ToolI tool2 = null;
        try {
            tool2 = master.Tool().create(ToolScope.READ, "Tool Name", "Tool description", "Tool author", "http://", 1.0f, "<xml>");
        } catch (MGXException ex) {
            if (ex.getMessage() != null && ex.getMessage().contains("duplicate")) {
                // expected exception
                System.err.println(ex.getMessage());
            } else {
                fail(ex.getMessage());
            }
        }
        boolean tool2Created = tool2 != null;

        if (tool1 != null) {
            try {
                TaskI<ToolI> task = master.Tool().delete(tool1);
                while (!task.done()) {
                    master.<ToolI>Task().refresh(task);
                }
                assertEquals(State.FINISHED, task.getState());
            } catch (MGXException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        assertTrue(tool1Created);
        assertTrue(tool1.isDeleted());

        if (tool2 != null) {
            try {
                TaskI<ToolI> task = master.Tool().delete(tool2);
                while (!task.done()) {
                    master.<ToolI>Task().refresh(task);
                }
                assertEquals(State.FINISHED, task.getState());
            } catch (MGXException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        assertFalse("Creation of duplicate tool should not be possible", tool2Created);
    }

    @Test
    public void testDifferentVersionsCreate() {
        System.out.println("testDifferentVersionsCreate");
        MGXMasterI master = TestMaster.getRW();
        ToolI tool1 = null;
        ToolI tool2 = null;
        boolean tool1Created, tool2Created;

        try {
            try {
                tool1 = master.Tool().create(ToolScope.READ, "Tool Name", "Tool description", "Tool author", "http://", 1.0f, "<xml>");
            } catch (MGXException ex) {
                fail(ex.getMessage());
            }
            tool1Created = tool1 != null;

            try {
                tool2 = master.Tool().create(ToolScope.READ, "Tool Name", "Tool description", "Tool author", "http://", 1.1f, "<xml>");
            } catch (MGXException ex) {
                fail(ex.getMessage());
            }
            tool2Created = tool2 != null;
        } finally {
            if (tool1 != null) {
                try {
                    TaskI<ToolI> task = master.Tool().delete(tool1);
                    while (!task.done()) {
                        master.<ToolI>Task().refresh(task);
                    }
                    assertEquals(State.FINISHED, task.getState());
                } catch (MGXException ex) {
                    Exceptions.printStackTrace(ex);
                }
                if (tool2 != null) {
                    try {
                        TaskI<ToolI> task = master.Tool().delete(tool2);
                        while (!task.done()) {
                            master.<ToolI>Task().refresh(task);
                        }
                        assertEquals(State.FINISHED, task.getState());
                    } catch (MGXException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
        }

        assertTrue(tool1Created);
        assertTrue("Failed to create tool with newer version", tool2Created);
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
        } catch (IOException ex) {
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
        Map<String, String> choices = dbParam.getChoices();
        assertTrue(choices.containsKey("SILVA"));
        assertTrue(choices.containsKey("Greengenes"));
        assertEquals(2, choices.size());
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
