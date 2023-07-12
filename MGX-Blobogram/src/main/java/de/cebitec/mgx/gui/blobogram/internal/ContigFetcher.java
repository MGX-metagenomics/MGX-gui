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
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private static final Logger LOG = Logger.getLogger(ContigFetcher.class.getName());
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
        ph.switchToDeterminate(bin.getNumContigs());

        XYSeries series = new XYSeries(bin.getName());
        series.setNotify(false);

        int numElements = 0;
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
            numElements++;

            if (numElements % 50 == 0) {
                ph.progress(numElements);
            }
        }

        // check if data is complete
        if (numElements != bin.getNumContigs()) {
            LOG.log(Level.SEVERE, "Expected to receive {0} contigs, only got {1}", new Object[]{bin.getNumContigs(), numElements});
            series.clear();
            series.setNotify(true);
            ph.finish();
            done.countDown();
            return;
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
