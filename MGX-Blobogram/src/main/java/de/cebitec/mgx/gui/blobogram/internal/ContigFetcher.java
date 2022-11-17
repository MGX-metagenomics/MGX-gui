/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.blobogram.internal;

import com.google.common.cache.Cache;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.assembly.BinI;
import de.cebitec.mgx.api.model.assembly.ContigI;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.util.Exceptions;

/**
 *
 * @author sj
 */
public class ContigFetcher implements Runnable {

    private final BinI bin;
    private final XYSeriesCollection dataset;
    private final CountDownLatch done;
    private final ProgressHandle ph;
    //
    private final Cache<BinI, XYSeries> cache;

    public ContigFetcher(BinI bin, XYSeriesCollection dataset, ProgressHandle ph, Cache<BinI, XYSeries> cache, CountDownLatch cdl) {
        this.bin = bin;
        this.dataset = dataset;
        this.ph = ph;
        this.cache = cache;
        this.done = cdl;
    }

    @Override
    public void run() {
        ph.start();
        ph.switchToIndeterminate();

        XYSeries series = new XYSeries(bin.getName());
        series.setNotify(false);

        Iterator<ContigI> contigIter;
        try {
            contigIter = bin.getMaster().Contig().ByBin(bin);
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
            ph.finish();
            cache.invalidate(bin);
            return;
        }

        while (contigIter.hasNext()) {
            ContigI c = contigIter.next();
            XYDataItem item = new ContigItem(bin, c);
            series.add(item, false);
        }
        series.setNotify(true);
        cache.put(bin, series);

        synchronized (dataset) {
            dataset.addSeries(series);
        }

        ph.finish();
        done.countDown();
    }

}
