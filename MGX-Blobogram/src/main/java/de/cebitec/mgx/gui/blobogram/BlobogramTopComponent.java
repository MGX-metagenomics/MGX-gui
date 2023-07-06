/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.blobogram;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.cebitec.mgx.gui.blobogram.internal.ContigItem;
import de.cebitec.mgx.gui.blobogram.internal.ContigFetcher;
import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.assembly.BinI;
import de.cebitec.mgx.gui.charts.basic.util.JFreeChartUtil;
import de.cebitec.mgx.gui.charts.basic.util.LogAxis;
import de.cebitec.mgx.gui.charts.basic.util.SVGChartPanel;
import de.cebitec.mgx.gui.pool.MGXPool;
import de.cebitec.mgx.gui.swingutils.util.ColorPalette;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.io.Serial;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.TickUnitSource;
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

    @Serial
    private static final long serialVersionUID = 1L;

    private final InstanceContent content = new InstanceContent();
    private final Lookup lookup;
    private final Lookup.Result<BinI> resultBin;
    private boolean isActivated = false;
    //
    private final Semaphore updateLock = new Semaphore(1);
    //
    private static Cache<BinI, XYSeries> cache;
    //
    private SVGChartPanel currentPanel = null;
    private final XYToolTipGenerator tooltipGenerator;
    private final XYLineAndShapeRenderer renderer;
    private final XYSeriesCollection dataset;
    private final LogarithmicAxis rangeAxis;

    public BlobogramTopComponent() {
        initComponents();
        setName("Blob Plot");
        super.setToolTipText("Blob Plot");
        lookup = new AbstractLookup(content);
        associateLookup(lookup);
        resultBin = Utilities.actionsGlobalContext().lookupResult(BinI.class);

        cache = CacheBuilder.newBuilder()
                .expireAfterAccess(5, TimeUnit.MINUTES)
                .build();

        dataset = new XYSeriesCollection();

        tooltipGenerator = new XYToolTipGenerator() {

            @Override
            public String generateToolTip(XYDataset xyd, int series, int item) {
                XYSeriesCollection dataset = (XYSeriesCollection) xyd;
                XYDataItem dataItem = dataset.getSeries(series).getDataItem(item);
                ContigItem ci = (ContigItem) dataItem;
                return ci.getTooltip();
            }
        };

        renderer = new XYLineAndShapeRenderer(false, true) {

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
        renderer.setDefaultToolTipGenerator(tooltipGenerator);

        rangeAxis = new LogarithmicAxis("Coverage");
        rangeAxis.setStrictValuesFlag(false);
        TickUnitSource tus = LogAxis.createLogTickUnits(Locale.US);
        rangeAxis.setStandardTickUnits(tus);
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

    private synchronized void update() {

        // avoid update when component itself is activated, because we're
        // just getting the contents of our own lookup
        if (isActivated) {
            return;
        }

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if (currentPanel != null) {
            this.remove(currentPanel);
            currentPanel = null;
            this.repaint();
        }

        Collection<BinI> bins = new HashSet<>();
        bins.addAll(resultBin.allInstances());

        if (bins.isEmpty()) {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            return;
        }

        //
        // make sure all bins belong to the same assembly
        // for this, check assembly id (for different assemblies
        // within one project) and equality of master instances
        // (for assemblies with same ids but in different projects)
        //
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
            this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            return;
        }

        //
        // we're running on the EDT here, so acquiring the lock is kind of dangerous;
        // however, we need to leave the EDT or otherwise our task progress handles
        // won't become visible at all
        //
        try {
            updateLock.acquire();
        } catch (InterruptedException ex) {
            Exceptions.printStackTrace(ex);
            this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            return;
        }

        MGXPool.getInstance().submit(new Runnable() {
            @Override
            public void run() {

                dataset.removeAllSeries();
                dataset.setNotify(false);

                CountDownLatch allProcessed = new CountDownLatch(bins.size());

                for (BinI b : bins) {
                    XYSeries series = cache.getIfPresent(b);
                    if (series != null) {
                        dataset.addSeries(series);
                        allProcessed.countDown();
                    } else {
                        ProgressHandle ph = ProgressHandle.createHandle("Fetching contigs for " + b.getName(), null, null);
                        ContigFetcher fetcher = new ContigFetcher(b, dataset, ph, cache, allProcessed);
                        MGXPool.getInstance().submit(fetcher);
                    }
                }

                // await completion of all tasks
                try {
                    allProcessed.await();
                } catch (InterruptedException ex) {
                    Exceptions.printStackTrace(ex);
                    dataset.removeAllSeries();
                    updateLock.release();
                    return;
                }

                dataset.setNotify(true);

                JFreeChart chart = ChartFactory.createScatterPlot(null, "GC content", "Coverage", dataset, PlotOrientation.VERTICAL, false, true, false);
                chart.setBorderPaint(Color.WHITE);
                chart.setBackgroundPaint(Color.WHITE);

                List<Color> palette = ColorPalette.pick(bins.size());
                for (int i = 0; i < bins.size(); i++) {
                    renderer.setSeriesPaint(i, palette.get(i));
                }

                currentPanel = new SVGChartPanel(chart);
                currentPanel.setDisplayToolTips(true);

                XYPlot plot = (XYPlot) chart.getPlot();
                plot.setBackgroundPaint(Color.WHITE);
                plot.setRenderer(renderer);

                // coverage axis in log scale
                rangeAxis.setLabelFont(plot.getDomainAxis().getLabelFont());
                plot.setRangeAxis(rangeAxis);

                chart.getRenderingHints().put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // #163; reset zoom on update
                currentPanel.restoreAutoBounds();
                rangeAxis.setAutoRange(true);
                plot.getDomainAxis().setAutoRange(true);

                content.set(Collections.emptyList(), null);
                content.add(JFreeChartUtil.getImageExporter(chart));

                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        BlobogramTopComponent.this.add(currentPanel, BorderLayout.CENTER);
                        BlobogramTopComponent.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                        BlobogramTopComponent.this.revalidate();
                        BlobogramTopComponent.this.repaint();
                        updateLock.release();
                    }
                });

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

        setBackground(java.awt.Color.white);
        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        resultBin.addLookupListener(this);
        update();
    }

    @Override
    public void componentClosed() {
        resultBin.removeLookupListener(this);
    }

    @Override
    protected void componentDeactivated() {
        super.componentDeactivated();
        isActivated = false;
    }

    @Override
    protected void componentActivated() {
        super.componentActivated();
        isActivated = true;
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
