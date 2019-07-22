package de.cebitec.mgx.gui.goldstandard.util;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.misc.TaskI;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.ToolI;
import de.cebitec.mgx.gui.goldstandard.actions.AddGoldstandard;
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
import org.junit.Assume;

/**
 *
 * @author pblumenk
 */
public class MGSReaderTest {

    private static MGXMasterI master;
    private static ToolI tool;
    private static SeqRunI seqrun;
    private JobI job;

    public MGSReaderTest() {
    }

    @BeforeClass
    public static void setUpClass() throws MGXException {
        master = TestMaster.getPrivate("MGX_EvalModule");
        Assume.assumeNotNull(master);
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
    public void tearDown() throws MGXException {
        Assume.assumeNotNull(master);
        Iterator<AttributeI> it = master.Attribute().ByJob(job);
        int i = 0;
        while (it.hasNext()){
            TaskI<AttributeI> delete = master.Attribute().delete(it.next());
            if (delete.getState() != TaskI.State.FINISHED){
                i++;
            }
        }
        master.Job().delete(job);
        assertEquals("Couldn't delete " + i + " attributes.", 0, i);
    }

    @Test
    public void testMGSReader() throws MGXException, FileNotFoundException, IOException{
        Assume.assumeNotNull(master);
        String mgsPath = getClass().getClassLoader().getResource("example.mgs").getPath();
        job = master.Job().create(tool, new ArrayList<>(1), seqrun);
        MGSReader reader = new MGSReader(mgsPath, master, job);
        List<MGSEntry> results = new LinkedList<>();
        while (reader.hasNext()){
            results.add(reader.next());
        }
        assertEquals("Results size isn't correct", 10, results.size());
        for (MGSAttribute attr : results.get(0).getAttributes()){
            if (attr.getAttribute().getAttributeType().getName().equals("NCBI_SPECIES")){
                assertEquals("Wrong attribute value in entry 0", "Escherichia coli", attr.getAttribute().getValue());
                assertEquals("Wrong start value in entry 0", 0, attr.getStart());
                assertEquals("Wrong stop value in entry 0", 249, attr.getStop());                
            } else if (attr.getAttribute().getAttributeType().getName().equals("GC")){
                assertEquals("Wrong GC content in entry 0", 52.8, Double.parseDouble(attr.getAttribute().getValue()), 0.01);
            }
        }
        for (MGSAttribute attr : results.get(9).getAttributes()){
            if (attr.getAttribute().getAttributeType().getName().equals("NCBI_SPECIES")){
                assertEquals("Wrong attribute value in entry 0", "Escherichia coli", attr.getAttribute().getValue());
                assertEquals("Wrong start value in entry 0", 0, attr.getStart());
                assertEquals("Wrong stop value in entry 0", 249, attr.getStop());                
            } else if (attr.getAttribute().getAttributeType().getName().equals("GC")){
                assertEquals("Wrong GC content in entry 0", 55.2, Double.parseDouble(attr.getAttribute().getValue()), 0.01);
            }
        }
    }

}
