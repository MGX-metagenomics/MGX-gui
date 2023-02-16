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
import de.cebitec.mgx.api.model.ToolI;
import de.cebitec.mgx.common.ToolScope;
import de.cebitec.mgx.testutils.TestMaster;
import java.util.Iterator;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

/**
 *
 * @author sj
 */
public class ToolAccessTest {

    public ToolAccessTest() {
    }

    @Test
    public void testFetch() {
        System.out.println("testFetch");
        MGXMasterI master = TestMaster.getRO();
        ToolI tool = null;
        try {
            tool = master.Tool().fetch(17);
        } catch (MGXException ex) {
            fail(ex.getMessage());
        }
        assertNotNull(tool);
        assertEquals("COG", tool.getName());
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
        assertEquals(4, cnt, "Unit test project contains 4 tools");
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
                fail(ex.getMessage());
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
                fail(ex.getMessage());
            }
        }
        assertFalse(tool2Created, "Creation of duplicate tool should not be possible");
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
                    fail(ex.getMessage());
                }
                if (tool2 != null) {
                    try {
                        TaskI<ToolI> task = master.Tool().delete(tool2);
                        while (!task.done()) {
                            master.<ToolI>Task().refresh(task);
                        }
                        assertEquals(State.FINISHED, task.getState());
                    } catch (MGXException ex) {
                        fail(ex.getMessage());
                    }
                }
            }
        }

        assertTrue(tool1Created);
        assertTrue(tool2Created, "Failed to create tool with newer version");
    }

//    private static String readFile(String path) throws IOException {
//        StringBuilder content = new StringBuilder();
//        FileInputStream fis = new FileInputStream(path);
//        try (DataInputStream in = new DataInputStream(fis)) {
//            BufferedReader br = new BufferedReader(new InputStreamReader(in));
//            String strLine;
//            while ((strLine = br.readLine()) != null) {
//                content.append(strLine);
//            }
//        }
//        return content.toString();
//    }
}
