/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.goldstandard.util;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.JobParameterI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.ToolI;
import de.cebitec.mgx.gui.goldstandard.actions.AddGoldstandard;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author pblumenk
 */
public class MGSReaderTest {

    private static MGXMasterI master;
    private static ToolI tool;
    private static SeqRunI seqrun;

    public MGSReaderTest() {
    }

    @BeforeClass
    public static void setUpClass() throws MGXException {
        master = TestMaster.getRW();
        Iterator<ToolI> toolIt = master.Tool().fetchall();
        while (toolIt.hasNext()) {
            ToolI t = toolIt.next();
            if (t.getName().equals(AddGoldstandard.TOOL_NAME)) {
                tool = t;
                break;
            }
        }
        if (tool == null) {
            tool = master.Tool().create(AddGoldstandard.TOOL_NAME, AddGoldstandard.TOOL_LONG_DESCRIPTION, AddGoldstandard.TOOL_AUTHOR, AddGoldstandard.TOOL_WEBSITE, AddGoldstandard.TOOL_VERSION, AddGoldstandard.TOOL_WEBSITE);
        }
        seqrun = master.SeqRun().fetchall().next();
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
    public void testMGSReader() throws MGXException, FileNotFoundException, IOException{
        String mgsPath = this.getClass().getResource("example.mgs").getPath();
        JobI job = master.Job().create(tool, seqrun, new ArrayList<JobParameterI>(1));
        MGSReader reader = new MGSReader(mgsPath, master, job);
        List<MGSEntry> results = new LinkedList<>();
        while (reader.hasNext()){
            results.add(reader.next());
        }
        assertEquals("All results are avialable", 10, results.size());
    }

}
