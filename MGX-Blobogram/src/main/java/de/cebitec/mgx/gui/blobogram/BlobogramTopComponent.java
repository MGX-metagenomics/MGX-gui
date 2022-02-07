/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.blobogram;

import de.cebitec.mgx.gui.blobogram.internal.ContigItem;
import de.cebitec.mgx.gui.blobogram.internal.ContigFetcher;
import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.assembly.AssemblyI;
import de.cebitec.mgx.api.model.assembly.BinI;
import de.cebitec.mgx.gui.charts.basic.util.JFreeChartUtil;
import de.cebitec.mgx.gui.charts.basic.util.SVGChartPanel;
import de.cebitec.mgx.gui.pool.MGXPool;
import de.cebitec.mgx.gui.swingutils.util.ColorPalette;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.windows.TopComponent;
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
        iconBase = "de/cebitec/mgx/gui/blobogram/blobogram.svg",
        persistenceType = TopComponent.PERSISTENCE_NEVER
)
@TopComponent.Registration(mode = "navigator", openAtStartup = false)
@ActionID(category = "Window", id = "de.cebitec.mgx.gui.blobogram.BlobogramTopComponent")
@ActionReference(path = "Menu/Window", position = 339)
@TopComponent.OpenActionRegistration(
        displayName = "Blob Plot",
        preferredID = "BlobogramTopComponent"
)
public final class BlobogramTopComponent extends TopComponent implements LookupListener {

    private final InstanceContent content = new InstanceContent();
    private final Lookup lookup;
    private final Lookup.Result<AssemblyI> resultAssembly;
    private final Lookup.Result<BinI> resultBin;
    //
    private SVGChartPanel currentPanel = null;
    private final XYSeriesCollection dataset = new XYSeriesCollection();

    public BlobogramTopComponent() {
        initComponents();
        setName("Blob Plot");
        super.setToolTipText("Blob Plot");
        lookup = new AbstractLookup(content);
        associateLookup(lookup);
        resultAssembly = Utilities.actionsGlobalContext().lookupResult(AssemblyI.class);
        resultBin = Utilities.actionsGlobalContext().lookupResult(BinI.class);
        update();
    }

    private static BlobogramTopComponent instance = null;

    public static BlobogramTopComponent getDefault() {
        if (instance == null) {
            instance = new BlobogramTopComponent();
        }
        return instance;
    }

    @Override
    public Image getIcon() {
        Image image = super.getIcon();
        Image scaledInstance = image.getScaledInstance(16, 16, Image.SCALE_SMOOTH);
        return scaledInstance;
    }

    private void update() {

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if (currentPanel != null) {
            currentPanel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        }
        MGXMasterI master = Utilities.actionsGlobalContext().lookup(MGXMasterI.class);

        Collection<? extends AssemblyI> assemblies = resultAssembly.allInstances();
        Collection<BinI> bins = new ArrayList<>();

        MGXPool.getInstance().submit(new Runnable() {
            @Override
            public void run() {
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

                if (bins.isEmpty()) {
                    return;
                }

                // make sure all bins belong to the same assembly
                // for this, check assembly id (for different assemblies
                // within one project) and equality of master instances
                // (for assemblies with same ids but in different projects)
                long asmId = -1;
                MGXMasterI m = null;
                boolean allBinsBelongToSameAssembly = true;
                for (BinI b : bins) {
                    if (asmId == -1) {
                        asmId = b.getAssemblyId();
                        m = b.getMaster();
                    } else {
                        if (asmId != b.getAssemblyId() || !m.equals(b.getMaster())) {
                            allBinsBelongToSameAssembly = false;
                        }
                    }
                }
                if (!allBinsBelongToSameAssembly) {
                    NotifyDescriptor nd = new NotifyDescriptor("Selected bins belong to different assemblies.", "Error",
                            NotifyDescriptor.DEFAULT_OPTION, NotifyDescriptor.ERROR_MESSAGE, null, null);
                    DialogDisplayer.getDefault().notify(nd);
                    return;
                }

                dataset.setNotify(false);
                dataset.removeAllSeries();

                CountDownLatch allProcessed = new CountDownLatch(bins.size());

                for (BinI b : bins) {
                    XYSeries series = new XYSeries(b.getName());
                    series.setNotify(false);
                    dataset.addSeries(series);
                    ContigFetcher fetcher = new ContigFetcher(master, b, series, allProcessed);
                    ProgressHandle ph = ProgressHandle.createHandle("Fetching contigs for " + b.getName(), fetcher, null);
                    fetcher.setProgressHandle(ph);

                    MGXPool.getInstance().submit(fetcher);
                }

                try {
                    allProcessed.await();
                } catch (InterruptedException ex) {
                    Exceptions.printStackTrace(ex);
                    return;
                }

                dataset.setNotify(true);

                XYToolTipGenerator tooltipGenerator = new XYToolTipGenerator() {

                    @Override
                    public String generateToolTip(XYDataset xyd, int series, int item) {
                        XYSeriesCollection dataset = (XYSeriesCollection) xyd;
                        XYDataItem dataItem = dataset.getSeries(series).getDataItem(item);
                        ContigItem ci = (ContigItem) dataItem;
                        return ci.getTooltip();
                    }
                };

                JFreeChart chart = ChartFactory.createScatterPlot(null, "GC content", "Coverage", dataset, PlotOrientation.VERTICAL, false, true, false);
                chart.setBorderPaint(Color.WHITE);
                chart.setBackgroundPaint(Color.WHITE);

                XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(false, true) {

                    private final Ellipse2D.Float circle = new Ellipse2D.Float();

                    @Override
                    public Shape getItemShape(int row, int column) {
                        XYSeries series = dataset.getSeries(row);
                        ContigItem item = (ContigItem) series.getDataItem(column);
                        int length_bp = item.getContig().getLength();
                        float size = (float) (Math.log10(length_bp));
                        circle.height = 0.1f + size * size * size * size * size / 160;
                        circle.width = circle.height;
                        return circle;
                    }

                };
                renderer.setBaseToolTipGenerator(tooltipGenerator);

                List<Color> palette = ColorPalette.pick(bins.size());
                for (int i = 0; i < bins.size(); i++) {
                    renderer.setSeriesPaint(i, palette.get(i));
                }

                if (currentPanel != null) {
                    BlobogramTopComponent.this.remove(currentPanel);
                    content.set(Collections.emptyList(), null);
                }

                currentPanel = new SVGChartPanel(chart);
                currentPanel.setDisplayToolTips(true);
                XYPlot plot = (XYPlot) chart.getPlot();
                plot.setBackgroundPaint(Color.WHITE);
                plot.setRenderer(renderer);

                chart.getRenderingHints().put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                BlobogramTopComponent.this.add(currentPanel, BorderLayout.CENTER);
                content.add(JFreeChartUtil.getImageExporter(chart));

                if (currentPanel != null) {
                    currentPanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                }
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

            }
        });

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
}
