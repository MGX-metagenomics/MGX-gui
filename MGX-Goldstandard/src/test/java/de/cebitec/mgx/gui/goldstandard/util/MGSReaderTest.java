package de.cebitec.mgx.gui.goldstandard.util;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.misc.TaskI;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.ToolI;
import de.cebitec.mgx.common.ToolScope;
import de.cebitec.mgx.gui.goldstandard.actions.AddGoldstandard;
import de.cebitec.mgx.testutils.TestMaster;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

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

    @BeforeAll
    public static void setUpClass() throws MGXException {
        master = TestMaster.getPrivate("MGX_EvalModule");
        assumeTrue(master != null);
        Iterator<ToolI> toolIt = master.Tool().fetchall();
        while (toolIt.hasNext()) {
            ToolI t = toolIt.next();
            if (t.getName().equals(AddGoldstandard.TOOL_NAME)) {
                tool = t;
                break;
            }
        }
        if (tool == null) {
            tool = master.Tool().create(ToolScope.READ, AddGoldstandard.TOOL_NAME, AddGoldstandard.TOOL_LONG_DESCRIPTION, AddGoldstandard.TOOL_AUTHOR, AddGoldstandard.TOOL_WEBSITE, AddGoldstandard.TOOL_VERSION, AddGoldstandard.TOOL_WEBSITE);
        }
        seqrun = master.SeqRun().fetchall().next();
    }

    @AfterEach
    public void tearDown() throws MGXException {
        assumeTrue(master != null);
        Iterator<AttributeI> it = master.Attribute().ByJob(job);
        int i = 0;
        while (it.hasNext()) {
            TaskI<AttributeI> delete = master.Attribute().delete(it.next());
            if (delete.getState() != TaskI.State.FINISHED) {
                i++;
            }
        }
        master.Job().delete(job);
        assertEquals(0, i, "Couldn't delete " + i + " attributes.");
    }

    @Test
    public void testMGSReader() throws MGXException, FileNotFoundException, IOException {
        assumeTrue(master != null);
        String mgsPath = getClass().getClassLoader().getResource("example.mgs").getPath();
        job = master.Job().create(tool, new ArrayList<>(1), seqrun);
        MGSReader reader = new MGSReader(mgsPath, master, job);
        List<MGSEntry> results = new LinkedList<>();
        while (reader.hasNext()) {
            results.add(reader.next());
        }
        assertEquals(10, results.size(), "Results size isn't correct");
        for (MGSAttribute attr : results.get(0).getAttributes()) {
            if (attr.getAttribute().getAttributeType().getName().equals("NCBI_SPECIES")) {
                assertEquals("Escherichia coli", attr.getAttribute().getValue(), "Wrong attribute value in entry 0");
                assertEquals(0, attr.getStart(), "Wrong start value in entry 0");
                assertEquals(249, attr.getStop(), "Wrong stop value in entry 0");
            } else if (attr.getAttribute().getAttributeType().getName().equals("GC")) {
                assertEquals(52.8, Double.parseDouble(attr.getAttribute().getValue()), 0.01, "Wrong GC content in entry 0");
            }
        }
        for (MGSAttribute attr : results.get(9).getAttributes()) {
            if (attr.getAttribute().getAttributeType().getName().equals("NCBI_SPECIES")) {
                assertEquals("Escherichia coli", attr.getAttribute().getValue(), "Wrong attribute value in entry 0");
                assertEquals(0, attr.getStart(), "Wrong start value in entry 0");
                assertEquals(249, attr.getStop(), "Wrong stop value in entry 0");
            } else if (attr.getAttribute().getAttributeType().getName().equals("GC")) {
                assertEquals(55.2, Double.parseDouble(attr.getAttribute().getValue()), 0.01, "Wrong GC content in entry 0");
            }
        }
    }

}
