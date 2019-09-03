/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.binexplorer;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.ModelBaseI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.SequenceI;
import de.cebitec.mgx.api.model.assembly.BinI;
import de.cebitec.mgx.api.model.assembly.ContigI;
import de.cebitec.mgx.api.model.assembly.GeneCoverageI;
import de.cebitec.mgx.api.model.assembly.GeneI;
import de.cebitec.mgx.dnautils.DNAUtils;
import de.cebitec.mgx.gui.binexplorer.internal.ContigViewController;
import de.cebitec.mgx.gui.binexplorer.internal.FeaturePanel;
import de.cebitec.mgx.gui.binexplorer.util.AttributeTableModel;
import de.cebitec.mgx.gui.binexplorer.util.ContigModel;
import de.cebitec.mgx.gui.binexplorer.util.ContigRenderer;
import de.cebitec.mgx.gui.charts.basic.util.FastCategoryDataset;
import de.cebitec.mgx.gui.charts.basic.util.SVGChartPanel;
import de.cebitec.mgx.gui.pool.MGXPool;
import de.cebitec.mgx.gui.swingutils.SeqPanel;
import de.cebitec.mgx.gui.swingutils.util.ColorPalette;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import javax.swing.table.TableModel;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.ui.TextAnchor;
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
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//de.cebitec.mgx.gui.binexplorer//BinExplorer//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "BinExplorerTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE",
        persistenceType = TopComponent.PERSISTENCE_NEVER
)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@ActionID(category = "Window", id = "de.cebitec.mgx.gui.binexplorer.BinExplorerTopComponent")
@ActionReference(path = "Menu/Window", position = 340)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_BinExplorerAction",
        preferredID = "BinExplorerTopComponent"
)
@Messages({
    "CTL_BinExplorerAction=Bin Explorer",
    "CTL_BinExplorerTopComponent=Bin Explorer Window",
    "HINT_BinExplorerTopComponent=Bin Explorer"
})
public final class BinExplorerTopComponent extends TopComponent implements LookupListener, PropertyChangeListener, ItemListener {

    private final Lookup.Result<BinI> result;
    private final ContigModel contigModel = new ContigModel();
    private final AttributeTableModel tableModel = new AttributeTableModel();
    private BinI currentBin = null;
    private boolean isActivated = false;
    private ContigViewController vc = null;
    private GeneI selectedFeature = null;

    public BinExplorerTopComponent() {
        initComponents();
        setName(Bundle.CTL_BinExplorerTopComponent());
        setToolTipText(Bundle.HINT_BinExplorerTopComponent());
        result = Utilities.actionsGlobalContext().lookupResult(BinI.class);
        contigList.setModel(contigModel);
        contigList.setRenderer(new ContigRenderer());
        contigList.addItemListener(this);

        jXTable1.setHighlighters(new Highlighter[]{HighlighterFactory.createAlternateStriping()});

        ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
        BarRenderer.setDefaultShadowsVisible(false);
        XYBarRenderer.setDefaultShadowsVisible(false);

        ActionListener buttonListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                SwingWorker<SequenceI, Void> sw = new SwingWorker<SequenceI, Void>() {

                    @Override
                    protected SequenceI doInBackground() throws Exception {
                        final MGXMasterI master = selectedFeature.getMaster();
                        return master.Gene().getDNASequence(selectedFeature);
                    }

                    @Override
                    protected void done() {
                        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                        try {
                            SequenceI seq = get();
                            if (seq != null) {
                                String[] opts = new String[]{"Close"};
                                String title = e.getSource() == dnaseq
                                        ? "DNA sequence for " + seq.getName()
                                        : "Amino acid sequence for " + seq.getName();
                                NotifyDescriptor d = new NotifyDescriptor(
                                        "Text",
                                        title,
                                        NotifyDescriptor.DEFAULT_OPTION,
                                        NotifyDescriptor.PLAIN_MESSAGE,
                                        opts,
                                        opts[0]
                                );
                                SeqPanel sp = new SeqPanel();
                                if (e.getSource() == aaseq) {
                                    String translation = DNAUtils.translate(seq.getSequence());
                                    seq.setSequence(translation);
                                }
                                sp.show(seq);
                                d.setMessage(sp);
                                DialogDisplayer.getDefault().notify(d);
                            }
                        } catch (InterruptedException | ExecutionException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                        super.done();
                    }

                };
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                sw.execute();
            }
        };

        dnaseq.addActionListener(buttonListener);
        aaseq.addActionListener(buttonListener);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        contigList = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        binName = new javax.swing.JLabel();
        contentPanel = new javax.swing.JPanel();
        geneCovPanel = new javax.swing.JPanel();
        dnaseq = new javax.swing.JButton();
        aaseq = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jXTable1 = new org.jdesktop.swingx.JXTable();

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(BinExplorerTopComponent.class, "BinExplorerTopComponent.jLabel1.text")); // NOI18N

        contigList.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(BinExplorerTopComponent.class, "BinExplorerTopComponent.jLabel2.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(binName, org.openide.util.NbBundle.getMessage(BinExplorerTopComponent.class, "BinExplorerTopComponent.binName.text")); // NOI18N

        contentPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        contentPanel.setLayout(new java.awt.BorderLayout());

        geneCovPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout geneCovPanelLayout = new javax.swing.GroupLayout(geneCovPanel);
        geneCovPanel.setLayout(geneCovPanelLayout);
        geneCovPanelLayout.setHorizontalGroup(
            geneCovPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 382, Short.MAX_VALUE)
        );
        geneCovPanelLayout.setVerticalGroup(
            geneCovPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        dnaseq.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(dnaseq, org.openide.util.NbBundle.getMessage(BinExplorerTopComponent.class, "BinExplorerTopComponent.dnaseq.text")); // NOI18N
        dnaseq.setEnabled(false);

        aaseq.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(aaseq, org.openide.util.NbBundle.getMessage(BinExplorerTopComponent.class, "BinExplorerTopComponent.aaseq.text")); // NOI18N
        aaseq.setEnabled(false);

        jXTable1.setModel(tableModel);
        jXTable1.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jXTable1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(contentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(dnaseq)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(aaseq))
                            .addComponent(geneCovPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane1))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(contigList, javax.swing.GroupLayout.PREFERRED_SIZE, 484, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(binName, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 501, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(binName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(contigList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(contentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(dnaseq)
                            .addComponent(aaseq))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(geneCovPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 370, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton aaseq;
    private javax.swing.JLabel binName;
    private javax.swing.JPanel contentPanel;
    private javax.swing.JComboBox<ContigI> contigList;
    private javax.swing.JButton dnaseq;
    private javax.swing.JPanel geneCovPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private org.jdesktop.swingx.JXTable jXTable1;
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        result.addLookupListener(this);
        resultChanged(null);
    }

    @Override
    public void componentClosed() {
        result.removeLookupListener(this);
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

    @Override
    public void itemStateChanged(ItemEvent event) {
        if (event.getStateChange() == ItemEvent.SELECTED) {
            Object item = event.getItem();
            if (item instanceof ContigI) {
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                ContigI contig = (ContigI) item;
                if (vc != null) {
                    vc.removePropertyChangeListener(this);
                    vc.close();
                }
                dnaseq.setEnabled(false);
                aaseq.setEnabled(false);
                selectedFeature = null;
                geneCovPanel.removeAll();
                tableModel.update(null);
                vc = new ContigViewController(contig);
                vc.addPropertyChangeListener(this);
                FeaturePanel fp = new FeaturePanel(vc);
                contentPanel.removeAll();
                contentPanel.setLayout(new BorderLayout());
                contentPanel.add(fp, BorderLayout.CENTER);
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        }
    }

    @Override
    public void resultChanged(LookupEvent le) {
        // avoid update when component is activated
        if (isActivated && currentBin != null) {
            return;
        }

        BinI newBin = null;
        for (BinI bin : result.allInstances()) {
            newBin = bin;
        }
        if (newBin != null && !newBin.equals(currentBin)) {
            if (currentBin != null) {
                currentBin.removePropertyChangeListener(this);
            }
            currentBin = newBin;
            currentBin.addPropertyChangeListener(this);
            binName.setText(currentBin.getName());
            dnaseq.setEnabled(false);
            aaseq.setEnabled(false);
            selectedFeature = null;
            tableModel.update(null);
            contigModel.setBin(currentBin);
            contigModel.update();
        }

        if (currentBin == null) {
            contigModel.clear();
        }
    }

    @Override
    public synchronized void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() instanceof BinI && evt.getPropertyName().equals(ModelBaseI.OBJECT_DELETED)) {
            BinI bin = (BinI) evt.getSource();
            if (currentBin.equals(bin)) {
                currentBin.removePropertyChangeListener(this);
                currentBin = null;
                contigModel.clear();
                binName.setText("");
                dnaseq.setEnabled(false);
                aaseq.setEnabled(false);
                tableModel.update(null);
                selectedFeature = null;
                repaint();
                return;
            }
        }
        if (evt.getSource() instanceof ContigViewController && evt.getPropertyName().equals(ContigViewController.FEATURE_SELECTED)) {
            dnaseq.setEnabled(true);
            aaseq.setEnabled(true);

            selectedFeature = (GeneI) evt.getNewValue();
            final MGXMasterI master = selectedFeature.getMaster();

            MGXPool.getInstance().submit(new Runnable() {
                @Override
                public void run() {
                    tableModel.update(selectedFeature);
                }
            });

            MGXPool.getInstance().submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        List<GeneCoverageI> all = new ArrayList<>();
                        Iterator<GeneCoverageI> iter = master.GeneCoverage().ByGene(selectedFeature);
                        while (iter != null && iter.hasNext()) {
                            all.add(iter.next());
                        }
                        Collections.sort(all);

                        Map<Long, String> runNames = new HashMap<>();

                        FastCategoryDataset dataset = new FastCategoryDataset();
                        dataset.setNotify(false);
                        for (GeneCoverageI gc : all) {
                            if (!runNames.containsKey(gc.getRunId())) {
                                SeqRunI run = master.SeqRun().fetch(gc.getRunId());
                                runNames.put(gc.getRunId(), run.getName());
                                run.deleted();
                            }
                            dataset.addValue(gc.getCoverage(), "", runNames.get(gc.getRunId()));
                        }
                        dataset.setNotify(true);

                        JFreeChart chart = ChartFactory.createBarChart("", "Sequencing run", "Read count", dataset, PlotOrientation.HORIZONTAL, false, true, false);

                        chart.setBorderPaint(Color.WHITE);
                        chart.setBackgroundPaint(Color.WHITE);
                        chart.setAntiAlias(true);

                        CategoryPlot plot = chart.getCategoryPlot();
                        CategoryAxis domainAxis = plot.getDomainAxis();
                        domainAxis.setCategoryMargin(0);
                        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.STANDARD);

                        CategoryItemRenderer renderer = plot.getRenderer();
                        renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
                        renderer.setBaseItemLabelsVisible(true);
                        ItemLabelPosition position = new ItemLabelPosition(ItemLabelAnchor.INSIDE10,
                                TextAnchor.TOP_CENTER);
                        renderer.setBasePositiveItemLabelPosition(position);

                        BarRenderer br = (BarRenderer) plot.getRenderer();

                        br.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator("<html>Sequencing run: {1}<br>Mapped reads: {2}</html>", NumberFormat.getInstance(Locale.US)));
                        br.setItemMargin(0);
                        br.setMaximumBarWidth(.2); // set maximum width to 20% of chart

                        // colors
                        List<Color> colors = ColorPalette.pick(all.size());
                        for (int i = 0; i < all.size(); i++) {
                            renderer.setSeriesPaint(i, colors.get(i));
                        }
                        SVGChartPanel svgChartPanel = new SVGChartPanel(chart);
                        geneCovPanel.removeAll();
                        geneCovPanel.setLayout(new BorderLayout());
                        geneCovPanel.add(svgChartPanel, BorderLayout.CENTER);
                        geneCovPanel.repaint();

                    } catch (MGXException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            });
        }
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }
}
