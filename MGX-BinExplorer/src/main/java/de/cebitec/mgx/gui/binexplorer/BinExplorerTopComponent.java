/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.binexplorer;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.groups.FileType;
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
import de.cebitec.mgx.gui.binexplorer.internal.SeqPropertyPanel;
import de.cebitec.mgx.gui.binexplorer.util.AttributeTableModel;
import de.cebitec.mgx.gui.binexplorer.util.ContigModel;
import de.cebitec.mgx.gui.binexplorer.util.ContigRenderer;
import de.cebitec.mgx.gui.binexplorer.util.ObservationCellRenderer;
import de.cebitec.mgx.gui.charts.basic.util.FastCategoryDataset;
import de.cebitec.mgx.gui.charts.basic.util.SVGChartPanel;
import de.cebitec.mgx.gui.genbankexporter.GBKExporter;
import de.cebitec.mgx.gui.pool.MGXPool;
import de.cebitec.mgx.gui.swingutils.SeqPanel;
import de.cebitec.mgx.gui.swingutils.util.SuffixFilter;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import javax.swing.JFileChooser;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileFilter;
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
import org.openide.util.NbBundle.Messages;
import org.openide.util.NbPreferences;
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
        iconBase = "de/cebitec/mgx/gui/binexplorer/binexplorer.svg",
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
    "CTL_BinExplorerTopComponent=Bin Explorer",
    "HINT_BinExplorerTopComponent=Bin Explorer"
})
public final class BinExplorerTopComponent extends TopComponent implements LookupListener, PropertyChangeListener, ItemListener {

    private final Lookup.Result<BinI> result;
    private final ContigModel contigListModel = new ContigModel();
    private final AttributeTableModel tableModel = new AttributeTableModel();
    private BinI currentBin = null;
    private boolean isActivated = false;
    private final ContigViewController vc = new ContigViewController();
    private GeneI selectedFeature = null;
    private MGXMasterI currentMaster = null;
    private final FastCategoryDataset coverageDataset = new FastCategoryDataset();
    private JFreeChart coverageChart = null;
    private final TLongObjectMap<String> runNames = new TLongObjectHashMap<>();
    private FeaturePanel featurePanel = null;
    private SeqPropertyPanel seqPropPanel = null;

    public BinExplorerTopComponent() {
        initComponents();
        setName(Bundle.CTL_BinExplorerTopComponent());
        setToolTipText(Bundle.HINT_BinExplorerTopComponent());

        result = Utilities.actionsGlobalContext().lookupResult(BinI.class);
        contigList.setModel(contigListModel);
        contigList.setRenderer(new ContigRenderer());
        contigList.addItemListener(this);

        jXTable1.setHighlighters(new Highlighter[]{HighlighterFactory.createAlternateStriping()});
        jXTable1.getColumn(0).setWidth(130);
        jXTable1.getColumn(2).setMaxWidth(60);
        jXTable1.getColumn(3).setMaxWidth(60);
        jXTable1.setDefaultRenderer(Object.class, new ObservationCellRenderer(this));

        setupCoverageChart();

        ActionListener buttonListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                SwingWorker<SequenceI, Void> sw = new SwingWorker<SequenceI, Void>() {

                    @Override
                    protected SequenceI doInBackground() throws Exception {
                        if (selectedFeature != null) {
                            final MGXMasterI master = selectedFeature.getMaster();
                            return master.Gene().getDNASequence(selectedFeature);
                        }
                        return null;
                    }

                    @Override
                    protected void done() {
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
                sw.execute();
            }
        };

        dnaseq.addActionListener(buttonListener);
        aaseq.addActionListener(buttonListener);

        exportGBK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                final ContigI contig = contigListModel.getSelectedItem();
                final String taxAssignment = currentBin.getTaxonomy();
                JFileChooser fchooser = new JFileChooser();
                fchooser.setDialogType(JFileChooser.SAVE_DIALOG);

                // try to restore last directory selection
                String last = NbPreferences.forModule(JFileChooser.class).get("lastDirectory", null);
                if (last != null) {
                    File f = new File(last);
                    if (f.exists() && f.isDirectory() && f.canWrite()) {
                        fchooser.setCurrentDirectory(f);
                    }
                }

                // suggest a file name
                String suffix = ".gbk";
                File suggestedName = new File(fchooser.getCurrentDirectory(), cleanupName(contig.getName()) + suffix);
                int cnt = 1;
                while (suggestedName.exists()) {
                    String newName = new StringBuilder(cleanupName(contig.getName()))
                            .append(" (")
                            .append(cnt++)
                            .append(")")
                            .append(suffix)
                            .toString();
                    suggestedName = new File(fchooser.getCurrentDirectory(), newName);
                }
                fchooser.setSelectedFile(suggestedName);
                FileFilter ff = new SuffixFilter(FileType.EMBLGENBANK);
                fchooser.addChoosableFileFilter(ff);
                fchooser.setFileFilter(ff);

                if (fchooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) {
                    return;
                }

                NbPreferences.forModule(JFileChooser.class).put("lastDirectory", fchooser.getCurrentDirectory().getAbsolutePath());

                final File target = fchooser.getSelectedFile();
                if (target.exists()) {
                    // ask if file should be overwritten, else return
                    String msg = new StringBuilder("A file named ")
                            .append(target.getName())
                            .append(" already exists. Should this ")
                            .append("file be overwritten?")
                            .toString();
                    NotifyDescriptor nd = new NotifyDescriptor(msg,
                            "Overwrite file?",
                            NotifyDescriptor.OK_CANCEL_OPTION,
                            NotifyDescriptor.WARNING_MESSAGE,
                            null, null);
                    Object ret = DialogDisplayer.getDefault().notify(nd);
                    if (!NotifyDescriptor.OK_OPTION.equals(ret)) {
                        return;
                    }
                }

                ProgressHandle handle = ProgressHandle.createHandle("Export to " + target.getName());
                handle.start();
                handle.switchToIndeterminate();

                MGXPool.getInstance().submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            GBKExporter.exportContig(target, contig, taxAssignment);
                        } catch (Exception ex) {
                            Exceptions.printStackTrace(ex);
                        }

                        handle.finish();
                    }
                });

            }
        });
    }

    @Override
    public Image getIcon() {
        Image image = super.getIcon();
        Image scaledInstance = image.getScaledInstance(16, 16, Image.SCALE_SMOOTH);
        return scaledInstance;
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
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        geneLength = new javax.swing.JLabel();
        geneFrame = new javax.swing.JLabel();
        geneStop = new javax.swing.JLabel();
        geneStart = new javax.swing.JLabel();
        geneName = new javax.swing.JLabel();
        seqpropHolder = new javax.swing.JPanel();
        exportGBK = new javax.swing.JButton();

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(BinExplorerTopComponent.class, "BinExplorerTopComponent.jLabel1.text")); // NOI18N

        contigList.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(BinExplorerTopComponent.class, "BinExplorerTopComponent.jLabel2.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(binName, org.openide.util.NbBundle.getMessage(BinExplorerTopComponent.class, "BinExplorerTopComponent.binName.text")); // NOI18N

        contentPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        contentPanel.setLayout(new java.awt.BorderLayout());

        geneCovPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, org.openide.util.NbBundle.getMessage(BinExplorerTopComponent.class, "BinExplorerTopComponent.geneCovPanel.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
        geneCovPanel.setMaximumSize(new java.awt.Dimension(100, 100));
        geneCovPanel.setMinimumSize(new java.awt.Dimension(100, 100));
        geneCovPanel.setPreferredSize(new java.awt.Dimension(500, 40));

        javax.swing.GroupLayout geneCovPanelLayout = new javax.swing.GroupLayout(geneCovPanel);
        geneCovPanel.setLayout(geneCovPanelLayout);
        geneCovPanelLayout.setHorizontalGroup(
            geneCovPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 490, Short.MAX_VALUE)
        );
        geneCovPanelLayout.setVerticalGroup(
            geneCovPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 212, Short.MAX_VALUE)
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

        jLabel3.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(BinExplorerTopComponent.class, "BinExplorerTopComponent.jLabel3.text")); // NOI18N

        jLabel4.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(BinExplorerTopComponent.class, "BinExplorerTopComponent.jLabel4.text")); // NOI18N

        jLabel5.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, org.openide.util.NbBundle.getMessage(BinExplorerTopComponent.class, "BinExplorerTopComponent.jLabel5.text")); // NOI18N

        jLabel6.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel6, org.openide.util.NbBundle.getMessage(BinExplorerTopComponent.class, "BinExplorerTopComponent.jLabel6.text")); // NOI18N

        jLabel7.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel7, org.openide.util.NbBundle.getMessage(BinExplorerTopComponent.class, "BinExplorerTopComponent.jLabel7.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(geneLength, org.openide.util.NbBundle.getMessage(BinExplorerTopComponent.class, "BinExplorerTopComponent.geneLength.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(geneFrame, org.openide.util.NbBundle.getMessage(BinExplorerTopComponent.class, "BinExplorerTopComponent.geneFrame.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(geneStop, org.openide.util.NbBundle.getMessage(BinExplorerTopComponent.class, "BinExplorerTopComponent.geneStop.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(geneStart, org.openide.util.NbBundle.getMessage(BinExplorerTopComponent.class, "BinExplorerTopComponent.geneStart.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(geneName, org.openide.util.NbBundle.getMessage(BinExplorerTopComponent.class, "BinExplorerTopComponent.geneName.text")); // NOI18N

        seqpropHolder.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        seqpropHolder.setLayout(new java.awt.BorderLayout());

        exportGBK.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(exportGBK, org.openide.util.NbBundle.getMessage(BinExplorerTopComponent.class, "BinExplorerTopComponent.exportGBK.text")); // NOI18N
        exportGBK.setEnabled(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(contentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(contigList, javax.swing.GroupLayout.PREFERRED_SIZE, 484, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(exportGBK))
                            .addComponent(jLabel1)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(binName, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(geneCovPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(dnaseq)
                                    .addGroup(layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel3)
                                            .addComponent(jLabel4)
                                            .addComponent(jLabel5)
                                            .addComponent(jLabel6)
                                            .addComponent(jLabel7))))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(aaseq)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(22, 22, 22)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(geneName, javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(geneStart, javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(geneStop, javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(geneFrame, javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(geneLength, javax.swing.GroupLayout.Alignment.LEADING))))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 460, Short.MAX_VALUE)))
                .addContainerGap())
            .addComponent(seqpropHolder, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(contigList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(exportGBK))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(contentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(seqpropHolder, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(geneName)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel4)
                                    .addComponent(geneStart))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel5)
                                    .addComponent(geneStop))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel6)
                                    .addComponent(geneFrame))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel7)
                                    .addComponent(geneLength))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(dnaseq)
                            .addComponent(aaseq))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(geneCovPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 443, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton aaseq;
    private javax.swing.JLabel binName;
    private javax.swing.JPanel contentPanel;
    private javax.swing.JComboBox<ContigI> contigList;
    private javax.swing.JButton dnaseq;
    private javax.swing.JButton exportGBK;
    private javax.swing.JPanel geneCovPanel;
    private javax.swing.JLabel geneFrame;
    private javax.swing.JLabel geneLength;
    private javax.swing.JLabel geneName;
    private javax.swing.JLabel geneStart;
    private javax.swing.JLabel geneStop;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private org.jdesktop.swingx.JXTable jXTable1;
    private javax.swing.JPanel seqpropHolder;
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        vc.addPropertyChangeListener(this);
        result.addLookupListener(this);
        resultChanged(null);
    }

    @Override
    public void componentClosed() {
        result.removeLookupListener(this);
        vc.removePropertyChangeListener(this);
        if (featurePanel != null) {
            featurePanel.dispose();
            featurePanel = null;
        }
        if (seqPropPanel != null) {
            seqPropPanel.dispose();
            seqPropPanel = null;
        }
        if (selectedFeature != null) {
            selectedFeature.deleted();
            selectedFeature = null;
        }
        tableModel.clear();
        contigListModel.dispose();
        //vc.close();
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
                ContigI contig = (ContigI) item;
                dnaseq.setEnabled(false);
                aaseq.setEnabled(false);
                exportGBK.setEnabled(true);
                geneName.setText("");
                geneStart.setText("");
                geneStop.setText("");
                geneFrame.setText("");
                geneLength.setText("");
                selectedFeature = null;
                tableModel.update(null);
                vc.setContig(contig);
                if (featurePanel != null) {
                    contentPanel.remove(featurePanel);
                    featurePanel.dispose();
                }
                featurePanel = new FeaturePanel(vc);
                contentPanel.add(featurePanel, BorderLayout.CENTER);
                contentPanel.doLayout();

                if (seqPropPanel != null) {
                    seqpropHolder.remove(seqPropPanel);
                    seqPropPanel.dispose();
                }
                seqPropPanel = new SeqPropertyPanel(vc);
                seqpropHolder.add(seqPropPanel, BorderLayout.CENTER);
                seqpropHolder.doLayout();

                geneCovPanel.removeAll();

                repaint();
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
            if (featurePanel != null) {
                featurePanel.clear();
            }
            if (seqPropPanel != null) {
                seqPropPanel.clear();
            }
            tableModel.clear();
            jXTable1.repaint();

            contigListModel.dispose();
            contigList.repaint();

            geneCovPanel.removeAll();
            if (currentBin != null) {
                currentBin.removePropertyChangeListener(this);
            }
            currentBin = newBin;
            currentBin.addPropertyChangeListener(this);
            binName.setText(currentBin.getName());
            dnaseq.setEnabled(false);
            aaseq.setEnabled(false);
            exportGBK.setEnabled(false);
            geneName.setText("");
            geneStart.setText("");
            geneStop.setText("");
            geneFrame.setText("");
            geneLength.setText("");
            selectedFeature = null;

            MGXPool.getInstance().submit(new Runnable() {
                @Override
                public void run() {
                    tableModel.update(null);
                    contigListModel.setBin(currentBin);
                    contigListModel.update();
                    contigList.setEnabled(true);
                }
            });

        }

        if (currentBin == null) {
            contigListModel.clear();
        }

    }

    @Override
    public synchronized void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() instanceof BinI && evt.getPropertyName().equals(ModelBaseI.OBJECT_DELETED)) {
            BinI bin = (BinI) evt.getSource();
            if (currentBin.equals(bin)) {
                currentBin.removePropertyChangeListener(this);
                currentBin = null;
                contigListModel.clear();
                contigList.setSelectedItem(null);
                contigList.setEnabled(false);
                binName.setText("");
                dnaseq.setEnabled(false);
                aaseq.setEnabled(false);
                exportGBK.setEnabled(false);
                geneName.setText("");
                geneStart.setText("");
                geneStop.setText("");
                geneFrame.setText("");
                geneLength.setText("");
                tableModel.update(null);
                selectedFeature = null;

                coverageDataset.clear();
                featurePanel.clear();
                seqPropPanel.clear();

                tableModel.clear();
                contigListModel.dispose();

                repaint();
                return;
            }
        }
        if (evt.getSource() instanceof ContigViewController && evt.getPropertyName().equals(ContigViewController.FEATURE_SELECTED)) {
            dnaseq.setEnabled(true);
            aaseq.setEnabled(true);

            if (evt.getNewValue() == selectedFeature) {
                return;
            }

            tableModel.update(null);
            selectedFeature = (GeneI) evt.getNewValue();

            MGXMasterI master = selectedFeature.getMaster();
            if (!master.equals(currentMaster)) {
                currentMaster = master;
                runNames.clear();
            }

            geneName.setText(contigListModel.getSelectedItem().getName() + "_" + selectedFeature.getId());
            geneStart.setText(NumberFormat.getInstance(Locale.US).format(selectedFeature.getStart()));
            geneStop.setText(NumberFormat.getInstance(Locale.US).format(selectedFeature.getStop()));
            if (selectedFeature.getFrame() > 0) {
                geneFrame.setText("+" + String.valueOf(selectedFeature.getFrame()));
            } else {
                geneFrame.setText(String.valueOf(selectedFeature.getFrame()));
            }
            geneLength.setText(NumberFormat.getInstance(Locale.US).format(selectedFeature.getAALength()) + " aa");

            //
            //  update observation display
            //
            MGXPool.getInstance().submit(new Runnable() {
                @Override
                public void run() {
                    tableModel.update(selectedFeature);
                }
            });

            //
            // update coverage chart
            //
            List<GeneCoverageI> all = new ArrayList<>();
            try {
                Iterator<GeneCoverageI> iter = currentMaster.GeneCoverage().ByGene(selectedFeature);
                while (iter != null && iter.hasNext()) {
                    all.add(iter.next());
                }
                Collections.sort(all);

                coverageDataset.clear();
                for (GeneCoverageI geneCov : all) {
                    if (!runNames.containsKey(geneCov.getRunId())) {
                        SeqRunI run = currentMaster.SeqRun().fetch(geneCov.getRunId());
                        runNames.put(geneCov.getRunId(), run.getName());
                        run.deleted();
                    }
                    coverageDataset.addValue(geneCov.getCoverage(), "", runNames.get(geneCov.getRunId()));
                }
            } catch (MGXException ex) {
                Exceptions.printStackTrace(ex);
                geneCovPanel.removeAll();
                return;
            }

            CategoryPlot plot = coverageChart.getCategoryPlot();
            BarRenderer br = (BarRenderer) plot.getRenderer();
            for (int i = 0; i < all.size(); i++) {
                br.setSeriesPaint(i, Color.BLUE);
            }
            SVGChartPanel svgChartPanel = new SVGChartPanel(coverageChart);
            svgChartPanel.setPopupMenu(null);

            geneCovPanel.removeAll();
            geneCovPanel.setLayout(new BorderLayout());
            geneCovPanel.add(svgChartPanel, BorderLayout.CENTER);
        }
    }

    public GeneI getSelectedFeature() {
        return selectedFeature;
    }

    private void setupCoverageChart() {

        ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
        BarRenderer.setDefaultShadowsVisible(false);
        XYBarRenderer.setDefaultShadowsVisible(false);

        coverageChart = ChartFactory.createBarChart("", "", "", coverageDataset, PlotOrientation.HORIZONTAL, false, true, false);

        coverageChart.setBorderPaint(Color.WHITE);
        coverageChart.setBackgroundPaint(Color.WHITE);
        coverageChart.setAntiAlias(true);

        CategoryPlot plot = coverageChart.getCategoryPlot();
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryMargin(0);
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.STANDARD);

        CategoryItemRenderer renderer = plot.getRenderer();
        renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        renderer.setBaseItemLabelPaint(Color.WHITE);
        ItemLabelPosition position = new ItemLabelPosition(ItemLabelAnchor.CENTER,
                TextAnchor.CENTER_LEFT);
        renderer.setBasePositiveItemLabelPosition(position);
        renderer.setBaseItemLabelsVisible(true);

        BarRenderer br = (BarRenderer) plot.getRenderer();

        br.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator("<html>Sequencing run: {1}<br>Mapped reads: {2}</html>", NumberFormat.getInstance(Locale.US)));
        br.setItemMargin(0);
        br.setMaximumBarWidth(.2); // set maximum width to 20% of chart

    }

    private String cleanupName(String name) {
        if (name.contains(File.separator)) {
            name = name.replace(File.separator, "_");
        }
        return name;
    }

    void writeProperties(java.util.Properties p) {
        p.setProperty("version", "1.0");
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        //this.close();
    }
}
