/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.blobogram;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.assembly.AssemblyI;
import de.cebitec.mgx.api.model.assembly.BinI;
import de.cebitec.mgx.api.model.assembly.ContigI;
import de.cebitec.mgx.gui.charts.basic.util.JFreeChartUtil;
import de.cebitec.mgx.gui.charts.basic.util.SVGChartPanel;
import de.cebitec.mgx.gui.pool.MGXPool;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//de.cebitec.mgx.gui.blobogram//Blobogram//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "BlobogramTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE",
        persistenceType = TopComponent.PERSISTENCE_NEVER
)
@TopComponent.Registration(mode = "navigator", openAtStartup = false)
@ActionID(category = "Window", id = "de.cebitec.mgx.gui.blobogram.BlobogramTopComponent")
@ActionReference(path = "Menu/Window" , position = 339 )
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_BlobogramAction",
        preferredID = "BlobogramTopComponent"
)
@Messages({
    "CTL_BlobogramAction=Blobogram",
    "CTL_BlobogramTopComponent=Blobogram Window",
    "HINT_BlobogramTopComponent=This is a Blobogram window"
})
public final class BlobogramTopComponent extends TopComponent implements LookupListener {

    private final InstanceContent content = new InstanceContent();
    private final Lookup lookup;
    private final Lookup.Result<AssemblyI> resultAssembly;
    private final Lookup.Result<BinI> resultBin;
    //
    private SVGChartPanel currentPanel = null;

    public BlobogramTopComponent() {
        initComponents();
        setName(Bundle.CTL_BlobogramTopComponent());
        setToolTipText(Bundle.HINT_BlobogramTopComponent());
        lookup = new AbstractLookup(content);
        associateLookup(lookup);
        resultAssembly = Utilities.actionsGlobalContext().lookupResult(AssemblyI.class);
        resultBin = Utilities.actionsGlobalContext().lookupResult(BinI.class);
        update();
    }

    private void update() {
        MGXMasterI master = Utilities.actionsGlobalContext().lookup(MGXMasterI.class);
        if (master == null) {
            if (currentPanel != null) {
                content.set(Collections.emptyList(), null);
                remove(currentPanel);
                currentPanel = null;
            }
            return;
        }

        Collection<? extends AssemblyI> assemblies = resultAssembly.allInstances();
        Collection<BinI> bins = new ArrayList<>();

        if (!assemblies.isEmpty()) {
            AssemblyI assembly = assemblies.toArray(new AssemblyI[]{})[0];
            Iterator<BinI> asmIter = null;
            try {
                asmIter = master.Bin().ByAssembly(assembly);
            } catch (MGXException ex) {
                Exceptions.printStackTrace(ex);
            }
            while (asmIter != null && asmIter.hasNext()) {
                bins.add(asmIter.next());
            }
        } else if (!resultBin.allInstances().isEmpty()) {
            bins.addAll(resultBin.allInstances());
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries series = new XYSeries("");
        dataset.setNotify(false);
        series.setNotify(false);

        CountDownLatch allProcessed = new CountDownLatch(bins.size());

        for (BinI b : bins) {
            MGXPool.getInstance().submit(new ContigFetcher(master, b, series, allProcessed));
        }

        try {
            allProcessed.await();
        } catch (InterruptedException ex) {
            Exceptions.printStackTrace(ex);
            return;
        }

        dataset.addSeries(series);
        dataset.setNotify(true);
        series.setNotify(true);

        final NumberAxis domainAxis = new NumberAxis("GC");
        domainAxis.setAutoRangeIncludesZero(false);
        final NumberAxis rangeAxis = new LogarithmicAxis("Coverage");
        rangeAxis.setAutoRangeIncludesZero(false);

        XYToolTipGenerator tooltipGenerator = new XYToolTipGenerator() {

            @Override
            public String generateToolTip(XYDataset xyd, int series, int item) {
                XYSeriesCollection dataset = (XYSeriesCollection) xyd;
                XYDataItem dataItem = dataset.getSeries(series).getDataItem(item);
                ContigItem ci = (ContigItem) dataItem;
                return ci.getTooltip();
            }
        };

        JFreeChart chart = ChartFactory.createScatterPlot(null, "GC", "Coverage", dataset, PlotOrientation.VERTICAL, false, true, false);
        chart.setBorderPaint(Color.WHITE);
        chart.setBackgroundPaint(Color.WHITE);

        XYItemRenderer renderer = new XYLineAndShapeRenderer(false, true) {

            private final Ellipse2D.Float circle = new Ellipse2D.Float();

            @Override
            public Shape getItemShape(int row, int column) {
                ContigItem item = (ContigItem) series.getDataItem(column);
                int length_bp = item.getContig().getLength();
                float size = (float) (1 + Math.log(length_bp));
                circle.height = size;
                circle.width = size;
                circle.x = - size/2;
                circle.y = - size/2;
                return circle;
            }

        };
        renderer.setBaseToolTipGenerator(tooltipGenerator);
        //renderer.setSeriesShape(0, shape);

        if (currentPanel != null) {
            this.remove(currentPanel);
            content.set(Collections.emptyList(), null);
        }

        currentPanel = new SVGChartPanel(chart);
        currentPanel.setDisplayToolTips(true);
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRenderer(renderer);

        chart.getRenderingHints().put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        this.add(currentPanel, BorderLayout.CENTER);
        content.add(JFreeChartUtil.getImageExporter(chart));

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        resultAssembly.addLookupListener(this);
        resultBin.addLookupListener(this);
        update();
    }

    @Override
    public void componentClosed() {
        resultAssembly.removeLookupListener(this);
        resultBin.removeLookupListener(this);
    }

    void writeProperties(java.util.Properties p) {
        p.setProperty("version", "1.0");
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
    }

    @Override
    public void resultChanged(LookupEvent le) {
        update();
    }

    private static class ContigFetcher implements Runnable {

        private final MGXMasterI master;
        private final BinI bin;
        private final XYSeries series;
        private final CountDownLatch done;

        public ContigFetcher(MGXMasterI master, BinI bin, XYSeries series, CountDownLatch cdl) {
            this.master = master;
            this.bin = bin;
            this.series = series;
            this.done = cdl;
        }

        @Override
        public void run() {
            String taxonomy = bin.getTaxonomy();
            Iterator<ContigI> contigIter = null;
            try {
                contigIter = master.Contig().ByBin(bin);
            } catch (MGXException ex) {
                Exceptions.printStackTrace(ex);
            }
            while (contigIter != null && contigIter.hasNext()) {
                ContigI c = contigIter.next();
                XYDataItem item = new ContigItem(c, taxonomy);
                synchronized (series) {
                    series.add(item, false);
                }
            }
            done.countDown();
        }

    }

    private static class ContigItem extends XYDataItem {

        private final String taxonomy;
        private final ContigI contig;

        public ContigItem(ContigI ctg, String taxonomy) {
            super(ctg.getGC(), ctg.getCoverage());
            this.contig = ctg;
            this.taxonomy = taxonomy;
        }

        public final ContigI getContig() {
            return contig;
        }

        public final String getTooltip() {
            return taxonomy;
        }
    }
}
