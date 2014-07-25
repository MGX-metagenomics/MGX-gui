/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.access.datatransfer.DownloadBaseI;
import de.cebitec.mgx.api.access.datatransfer.TransferBaseI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.MGXFileI;
import de.cebitec.mgx.gui.util.TestMaster;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
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
    public void testDownloadFile() {
        System.out.println("DownloadFile");
        MGXMasterI m = TestMaster.getRO();

        OutputStream os = null;
        File f = new File("/tmp/testDownload");
        try {
            os = new FileOutputStream(f);
        } catch (FileNotFoundException ex) {
            fail(ex.getMessage());
        }

        String serverFile = ".|test1";

        DownloadBaseI down = null;
        down = m.File().createDownloader(serverFile, os);
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

        try {
            String md5 = getMD5Checksum(f.getAbsolutePath());
            assertEquals("037db883cd8236c30242da3468cf8a19", md5);
        } catch (Exception ex) {
            fail(ex.getMessage());
        }

        // cleanup
        if (f.exists()) {
            f.delete();
        }

        assertEquals(69, pc.getCount());
        assertEquals(TransferBaseI.TRANSFER_COMPLETED, pc.getLastEvent().getPropertyName());
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

    private static class PropCounter implements PropertyChangeListener {

        private int cnt = 0;
        private PropertyChangeEvent last = null;

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            //System.err.println(evt.getPropertyName());
            last = evt;
            cnt++;
        }

        public int getCount() {
            return cnt;
        }

        public PropertyChangeEvent getLastEvent() {
            return last;
        }
    }
}
