/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.access.datatransfer.DownloadBaseI;
import de.cebitec.mgx.api.access.datatransfer.TransferBaseI;
import de.cebitec.mgx.api.access.datatransfer.UploadBaseI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.misc.TaskI;
import de.cebitec.mgx.api.misc.TaskI.State;
import de.cebitec.mgx.api.model.MGXFileI;
import de.cebitec.mgx.testutils.PropCounter;
import de.cebitec.mgx.testutils.TestMaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.util.Iterator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 *
 * @author sjaenick
 */
public class FileAccessTest {

    public FileAccessTest() {
    }

    @BeforeAll
    public static void setUpClass() {
        //
        // testUpload sometimes fails due to ceph latency issues; perform
        // a single cleanup run before executing any tests
        //
        MGXMasterI m = TestMaster.getRW();
        MGXFileI root = MGXFileI.getRoot(m);

        try {
            Iterator<MGXFileI> iter = m.File().fetchall(root);
            while (iter.hasNext()) {
                MGXFileI entry = iter.next();
                if ("testUpload".equals(entry.getName())) {
                    TaskI<MGXFileI> task = m.File().delete(entry);
                    while ((task.getState() != TaskI.State.FINISHED) || (task.getState() != TaskI.State.FAILED)) {
                        System.err.println(" --> " + task.getState());
                        Thread.sleep(1000);
                        if ((task.getState() == TaskI.State.FINISHED) || (task.getState() == TaskI.State.FAILED)) {
                            break;
                        } else {
                            m.<MGXFileI>Task().refresh(task);
                        }
                    }
                }
            }

        } catch (MGXException | InterruptedException ex) {
        }
    }

    @Test
    public void testCreateInvalidDir() {
        System.out.println("testCreateInvalidDir");
        MGXMasterI m = TestMaster.getRO();
        MGXFileI root = MGXFileI.getRoot(m);
        try {
            m.File().createDirectory(root, "..");
        } catch (MGXException ex) {
            assertTrue(ex.getMessage().contains("Invalid character"));
            return;
        }
        fail();
    }

    @Test
    public void testDeleteDir() throws MGXException {
        System.out.println("testDeleteDir");
        MGXMasterI m = TestMaster.getRW();
        MGXFileI root = MGXFileI.getRoot(m);
        boolean success = false;
        try {
            success = m.File().createDirectory(root, "delME");

        } catch (MGXException ex) {
            fail(ex.getMessage());
        }
        assertTrue(success);

        MGXFileI delME = null;
        Iterator<MGXFileI> iter = m.File().fetchall(root);
        while (iter.hasNext()) {
            MGXFileI entry = iter.next();
            if ("delME".equals(entry.getName())) {
                delME = entry;
                break;
            }
        }
        assertNotNull(delME);

        TaskI<MGXFileI> delTask = m.File().delete(delME);
        assertNotNull(delTask);

        while (!delTask.done()) {
            System.err.println(delTask.getState());
            m.<MGXFileI>Task().refresh(delTask);
            try {
                Thread.sleep(20);
            } catch (InterruptedException ex) {
                fail(ex.getMessage());
            }
        }
        assertTrue(delTask.done());
        assertEquals(State.FINISHED, delTask.getState());
        assertTrue(delME.isDeleted());
    }

    @Test
    public void testDownloadFile() throws IOException {
        System.out.println("DownloadFile");
        MGXMasterI m = TestMaster.getRO();

        File f = File.createTempFile("down", "xx");
        PropCounter pc = new PropCounter();

        try {
            OutputStream os = null;
            try {
                os = new FileOutputStream(f);
            } catch (FileNotFoundException ex) {
                fail(ex.getMessage());
            }

            String serverFile = ".|test1";

            DownloadBaseI down = null;
            down = m.File().createDownloader(serverFile, os);
            assertNotNull(down);

            down.addPropertyChangeListener(pc);

            boolean success = down.download();
            assertTrue(success);

            os.close();

            if (!success) {
                fail(down.getErrorMessage());
            }

            String md5 = getMD5Checksum(f.getAbsolutePath());
            assertEquals("037db883cd8236c30242da3468cf8a19", md5);

        } catch (Exception ex) {
            fail(ex.getMessage());
        } finally {
            // cleanup
            if (f.exists()) {
                f.delete();
            }
        }

        assertEquals(35, pc.getCount());
        assertEquals(TransferBaseI.TRANSFER_COMPLETED, pc.getLastEvent().getPropertyName());
    }

    @Test
    public void testUploadFile() throws IOException {
        System.out.println("testUploadFile22");
        MGXMasterI m = TestMaster.getRW();
        assertNotNull(m);

        File f = File.createTempFile("down", "xx");
        PropCounter pc = new PropCounter();

        try {
            try (FileWriter fw = new FileWriter(f)) {
                for (int i = 0; i < 90000; i++) {
                    fw.write("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
                }
            }

            MGXFileI root = MGXFileI.getRoot(m);
            UploadBaseI up = m.File().createUploader(f, root, "testUpload");
            assertNotNull(up);
            up.addPropertyChangeListener(pc);

            boolean success = up.upload();
            assertTrue(success, up.getErrorMessage());
            up.removePropertyChangeListener(pc);

            if (!success) {
                fail(up.getErrorMessage());
            }
            long fileSize = f.length();
            assertEquals(fileSize, pc.getLastEvent().getNewValue());
            assertEquals(TransferBaseI.TRANSFER_COMPLETED, pc.getLastEvent().getPropertyName());

            // find uploaded remote file
            MGXFileI delMe = null;
            Iterator<MGXFileI> iter = m.File().fetchall(root);
            while (iter.hasNext()) {
                MGXFileI entry = iter.next();
                if ("testUpload".equals(entry.getName())) {
                    delMe = entry;
                    break;
                }
            }
            assertNotNull(delMe);

            // delete it again
            TaskI<MGXFileI> task = m.File().delete(delMe);
            while ((task.getState() != TaskI.State.FINISHED) || (task.getState() != TaskI.State.FAILED)) {
                System.err.println(" --> " + task.getState());
                Thread.sleep(1000);
                if ((task.getState() == TaskI.State.FINISHED) || (task.getState() == TaskI.State.FAILED)) {
                    break;
                } else {
                    m.<MGXFileI>Task().refresh(task);
                }
            }
        } catch (IOException | MGXException | InterruptedException ex) {
            fail(ex.getMessage());
        } finally {
            // cleanup
            if (f.exists()) {
                f.delete();
            }

            // attempt cleanup
            try {
                Iterator<MGXFileI> iter = m.File().fetchall(MGXFileI.getRoot(m));
                while (iter.hasNext()) {
                    MGXFileI entry = iter.next();
                    if ("testUpload".equals(entry.getName())) {
                        TaskI<MGXFileI> task = m.File().delete(entry);
                        while ((task.getState() != TaskI.State.FINISHED) || (task.getState() != TaskI.State.FAILED)) {
                            System.err.println(" --> " + task.getState());
                            Thread.sleep(1000);
                            if ((task.getState() == TaskI.State.FINISHED) || (task.getState() == TaskI.State.FAILED)) {
                                break;
                            } else {
                                m.<MGXFileI>Task().refresh(task);
                            }
                        }
                    }
                }
            } catch (MGXException | InterruptedException ex) {
            }
        }

    }

    @Test
    public void testFetchall() throws MGXException {
        System.out.println("fetchall");
        MGXMasterI master = TestMaster.getRO();
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

    @Test
    public void testEquality() throws MGXException {
        System.out.println("testEquality");
        MGXMasterI master = TestMaster.getRO();

        MGXFileI file1 = null;
        Iterator<MGXFileI> iter = master.File().fetchall();
        while (iter.hasNext()) {
            MGXFileI f = iter.next();
            if (f.getName().equals("test1")) {
                file1 = f;
            }
        }

        MGXFileI file2 = null;
        iter = master.File().fetchall();
        while (iter.hasNext()) {
            MGXFileI f = iter.next();
            if (f.getName().equals("test1")) {
                file2 = f;
            }
        }

        assertNotNull(file1);
        assertNotNull(file2);
        assertEquals(file1, file2);
    }

    @Test
    public void testListRecursive() throws MGXException {
        System.out.println("testListRecursive");
        MGXMasterI master = TestMaster.getRO();
        System.out.println(master.getServerName());
        int total = 0;
        Iterator<MGXFileI> iter = master.File().fetchall();
        while (iter.hasNext()) {
            MGXFileI f = iter.next();
            total += listDir(f);
        }

        //  .|dir1
        //  .|dir1|bar
        //  .|dir1|foo
        //  .|dir1|foo|bar
        //  .|test1
        //  .|test2
        //  .|test3
        assertEquals(7, total);
    }

    private int listDir(MGXFileI dir) {
        System.err.println(dir.getFullPath());
        int num = 1;
        if (dir.isDirectory()) {
            Iterator<MGXFileI> iter = null;
            try {
                iter = dir.getMaster().File().fetchall(dir);
            } catch (MGXException ex) {
                fail(dir.getFullPath() + " " + ex.getMessage());
            }
            while (iter.hasNext()) {
                num += listDir(iter.next());
            }
            return num;
        }
        return num;
    }

    @Test
    public void testDownloadDump() throws MGXException, IOException {
        System.out.println("DownloadDump");
        MGXMasterI m = TestMaster.getRO();

        OutputStream os = null;
        File f = File.createTempFile("down", "xx");
        try {
            os = new FileOutputStream(f);
        } catch (FileNotFoundException ex) {
            fail(ex.getMessage());
        }

        DownloadBaseI down = null;
        down = m.File().createPluginDumpDownloader(os);
        assertNotNull(down);

        PropCounter pc = new PropCounter();
        down.addPropertyChangeListener(pc);

        boolean success = down.download();
        assertTrue(success);

        try {
            os.close();
        } catch (IOException ex) {
            fail(ex.getMessage());
        }

        if (!success) {
            fail(down.getErrorMessage());
        }

        // check minimum file size
        assertTrue(500000 < f.length());

        // cleanup
        if (f.exists()) {
            f.delete();
        }

        assertTrue(50 < pc.getCount());
        assertEquals(TransferBaseI.TRANSFER_COMPLETED, pc.getLastEvent().getPropertyName());
    }

    private static String getMD5Checksum(String filename) throws Exception {
        byte[] b = createChecksum(filename);
        String result = "";

        for (int i = 0; i < b.length; i++) {
            result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
        }
        return result;
    }

    private static byte[] createChecksum(String filename) throws Exception {
        InputStream fis = new FileInputStream(filename);

        byte[] buffer = new byte[1024];
        MessageDigest complete = MessageDigest.getInstance("MD5");
        int numRead;

        do {
            numRead = fis.read(buffer);
            if (numRead > 0) {
                complete.update(buffer, 0, numRead);
            }
        } while (numRead != -1);

        fis.close();
        return complete.digest();
    }
}
