/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.access.datatransfer.UploadBaseI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.misc.TaskI;
import de.cebitec.mgx.api.model.MGXReferenceI;
import de.cebitec.mgx.testutils.TestMaster;
import java.io.File;
import java.util.Iterator;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import org.junit.jupiter.api.Test;
import org.openide.util.Exceptions;

/**
 *
 * @author sjaenick
 */
public class ReferenceAccessTest {

    @Test
    public void testCreateUploader() throws MGXException {
        System.out.println("createUploader");
        MGXMasterI master = TestMaster.getPrivate("MGX_Mammoth");
        assumeTrue(master != null);

        File f = new File("/vol/biodb/ncbi_genomes/Bacteria/Variovorax_paradoxus_B4_uid218005/NC_022234.gbk");
        assumeTrue(f.exists());

        UploadBaseI up = master.Reference().createUploader(f);
        boolean success = up.upload();

        if (!success) {
            String err = up.getErrorMessage();
            assertNotNull(err);
            assertNotEquals("", err);
            fail(err);
        }
        assertTrue(success);

        MGXReferenceI ref = null;
        Iterator<MGXReferenceI> iter = master.Reference().fetchall();
        while (iter.hasNext()) {
            ref = iter.next();
            if (ref.getName().equals("Variovorax paradoxus B4 chromosome 2")) {
                break;
            }
        }
        assertNotNull(ref);
        TaskI<MGXReferenceI> task = master.Reference().delete(ref);
        while ((task.getState() != TaskI.State.FINISHED) || (task.getState() != TaskI.State.FAILED)) {
            System.err.println(" --> " + task.getState());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Exceptions.printStackTrace(ex);
            }
            if ((task.getState() == TaskI.State.FINISHED) || (task.getState() == TaskI.State.FAILED)) {
                break;
            } else {
                master.<MGXReferenceI>Task().refresh(task);
            }
        }
    }

}
