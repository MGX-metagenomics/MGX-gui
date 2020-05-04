/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.access;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.access.datatransfer.DownloadBaseI;
import de.cebitec.mgx.api.access.datatransfer.UploadBaseI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.misc.TaskI;
import de.cebitec.mgx.api.model.MGXFileI;
import java.io.File;
import java.io.OutputStream;
import java.util.Iterator;

/**
 *
 * @author sj
 */
public interface FileAccessI {

    public Iterator<MGXFileI> fetchall() throws MGXException;

    public Iterator<MGXFileI> fetchall(MGXFileI parent) throws MGXException;

    public TaskI<MGXFileI> delete(MGXFileI obj) throws MGXException;

    public DownloadBaseI createPluginDumpDownloader(OutputStream writer) throws MGXException;

    public DownloadBaseI createDownloader(String fullPath, OutputStream writer) throws MGXException;

    public UploadBaseI createUploader(File localFile, MGXFileI targetDir, String name) throws MGXException;

    public boolean createDirectory(MGXFileI parentDir, String name) throws MGXException;

}
