/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.blobogram.internal;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.assembly.BinI;
import de.cebitec.mgx.api.model.assembly.ContigI;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.util.Cancellable;
import org.openide.util.Exceptions;

/**
 *
 * @author sj
 */
public class ContigFetcher implements Runnable, Cancellable {
    
    private final MGXMasterI master;
    private final BinI bin;
    private final XYSeries series;
    private final CountDownLatch done;
    private ProgressHandle ph;
    private volatile boolean cancelled = false;

    public ContigFetcher(MGXMasterI master, BinI bin, XYSeries series, CountDownLatch cdl) {
        this.master = master;
        this.bin = bin;
        this.series = series;
        this.done = cdl;
    }

    public void setProgressHandle(ProgressHandle ph) {
        this.ph = ph;
    }

    @Override
    public void run() {
        ph.start();
        ph.switchToIndeterminate();
        Iterator<ContigI> contigIter = null;
        try {
            contigIter = master.Contig().ByBin(bin);
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
        }
        while (!cancelled && contigIter != null && contigIter.hasNext()) {
            ContigI c = contigIter.next();
            XYDataItem item = new ContigItem(bin, c);
            synchronized (series) {
                series.add(item, false);
            }
        }
        series.setNotify(true);
        if (cancelled) {
            series.clear();
        }
        done.countDown();
        ph.finish();
    }

    @Override
    public boolean cancel() {
        cancelled = true;
        return true;
    }

    public boolean isCancelled() {
        return cancelled;
    }
    
}
