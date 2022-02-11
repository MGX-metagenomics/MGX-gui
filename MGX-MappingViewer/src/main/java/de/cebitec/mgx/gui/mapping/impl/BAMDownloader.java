/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping.impl;

import de.cebitec.mgx.api.access.datatransfer.DownloadBaseI;
import de.cebitec.mgx.api.access.datatransfer.TransferBaseI;
import de.cebitec.mgx.gui.taskview.MGXTask;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.util.Locale;

/**
 *
 * @author sj
 */
public class BAMDownloader extends MGXTask {

    private final DownloadBaseI downloader;
    private final File target;
    private final OutputStream writer;

    public BAMDownloader(DownloadBaseI downloader, File target, OutputStream writer, String name) {
        super(name);
        this.downloader = downloader;
        this.target = target;
        this.writer = writer;
    }

    @Override
    public boolean process() {
        downloader.addPropertyChangeListener(this);
        boolean ret = downloader.download();
        downloader.removePropertyChangeListener(this);
        if (!ret) {
            setStatus(downloader.getErrorMessage());
        }
        return ret;
    }

    @Override
    public void finished() {
        try {
            writer.close();
            super.finished();
        } catch (IOException ex) {
            setStatus(ex.getMessage());
            failed(ex.getMessage());
        }
    }

    @Override
    public void failed(String reason) {
        if (target.exists()) {
            target.delete();
        }
        super.failed(reason);
    }

    @Override
    public void propertyChange(PropertyChangeEvent pce) {
        switch (pce.getPropertyName()) {
            case TransferBaseI.NUM_ELEMENTS_TRANSFERRED:
                setStatus(NumberFormat.getInstance(Locale.US).format(pce.getNewValue()) + " bytes received.");
                break;
            case TransferBaseI.TRANSFER_FAILED:
                failed(pce.getNewValue().toString());
                break;
            default:
                super.propertyChange(pce);
                break;
        }
    }
}
