/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.binexplorer;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.assembly.AssembledRegionI;
import de.cebitec.mgx.api.model.assembly.BinI;
import de.cebitec.mgx.api.model.assembly.BinSearchResultI;
import de.cebitec.mgx.api.model.assembly.ContigI;
import de.cebitec.mgx.api.model.assembly.GeneCoverageI;
import de.cebitec.mgx.common.RegionType;
import de.cebitec.mgx.gui.binexplorer.internal.ContigViewController;
import de.cebitec.mgx.gui.binexplorer.internal.FeaturePanel;
import de.cebitec.mgx.gui.binexplorer.internal.SeqPropertyPanel;
import de.cebitec.mgx.gui.binexplorer.util.AttributeTableModel;
import de.cebitec.mgx.gui.swingutils.ContigListModel;
import de.cebitec.mgx.gui.binexplorer.util.ContigRenderer;
import de.cebitec.mgx.gui.binexplorer.util.GenBankExportAction;
import de.cebitec.mgx.gui.binexplorer.util.ObservationCellRenderer;
import de.cebitec.mgx.gui.binexplorer.util.SequenceDisplayAction;
import de.cebitec.mgx.gui.charts.basic.util.FastCategoryDataset;
import de.cebitec.mgx.gui.charts.basic.util.SVGChartPanel;
import de.cebitec.mgx.gui.pool.MGXPool;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serial;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
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
import org.jfree.chart.ui.TextAnchor;
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

    @Serial
    private static final long serialVersionUID = 1L;

    private final Lookup.Result<BinI> binResult;
    private final Lookup.Result<BinSearchResultI> binSearchResult;
    //
    private final ContigViewController vc = new ContigViewController();
    private final static NumberFormat nf = NumberFormat.getInstance(Locale.US);
    //
    private final ContigListModel contigListModel = new ContigListModel();
    private final AttributeTableModel attrTableModel = new AttributeTableModel(vc);
    //
    private boolean isActivated = false;
    //
    private final FastCategoryDataset<String> coverageDataset = new FastCategoryDataset<>();
    private final JFreeChart coverageChart;
    private final SVGChartPanel svgChartPanel;
    private final TLongObjectMap<String> runNames = new TLongObjectHashMap<>();
    private final FeaturePanel featurePanel;
    private final SeqPropertyPanel seqPropPanel;
    //
    private final InstanceContent content = new InstanceContent();
    //

    public BinExplorerTopComponent() {
        initComponents();
        setName(Bundle.CTL_BinExplorerTopComponent());
        setToolTipText(Bundle.HINT_BinExplorerTopComponent());

        binResult = Utilities.actionsGlobalContext().lookupResult(BinI.class);
        binSearchResult = Utilities.actionsGlobalContext().lookupResult(BinSearchResultI.class);
        binSearchResult.addLookupListener(new BinSearchHandler());

        Lookup lookup = new AbstractLookup(content);
        associateLookup(lookup);

        contigList.setModel(contigListModel);
        contigList.setRenderer(new ContigRenderer(nf));
        contigList.addItemListener(this);

        featurePanel = new FeaturePanel(vc, nf);
        contentPanel.add(featurePanel, BorderLayout.CENTER);

        seqPropPanel = new SeqPropertyPanel(vc);
        seqpropHolder.add(seqPropPanel, BorderLayout.CENTER);

        jXTable1.setHighlighters(new Highlighter[]{HighlighterFactory.createAlternateStriping()});
        jXTable1.getColumn(0).setMaxWidth(115); // attribute tpe
        jXTable1.getColumn(1).setWidth(140); // attribute
        jXTable1.getColumn(2).setMaxWidth(60); // start
        jXTable1.getColumn(3).setMaxWidth(60); // stop
        jXTable1.setDefaultRenderer(Object.class, new ObservationCellRenderer(vc));

        ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
        BarRenderer.setDefaultShadowsVisible(false);
        XYBarRenderer.setDefaultShadowsVisible(false);
        coverageChart = ChartFactory.createBarChart("", "", "", coverageDataset, PlotOrientation.HORIZONTAL, false, true, false);
        setupCoverageChart();

        svgChartPanel = new SVGChartPanel(coverageChart);
        svgChartPanel.setPopupMenu(null);
        geneCovPanel.add(svgChartPanel, BorderLayout.CENTER);

        dnaseq.addActionListener(new SequenceDisplayAction(vc, false));
        aaseq.addActionListener(new SequenceDisplayAction(vc, true));
        exportGBK.addActionListener(new GenBankExportAction(vc));

        geneName.setText("");
        geneStart.setText("");
        geneStop.setText("");
        geneFrame.setText("");
        geneLength.setText("");
        dnaseq.setEnabled(false);
        aaseq.setEnabled(false);
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
        jLabel8 = new javax.swing.JLabel();
        completeness = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        contamination = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        taxonomy = new javax.swing.JLabel();

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(BinExplorerTopComponent.class, "BinExplorerTopComponent.jLabel1.text")); // NOI18N

        contigList.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(BinExplorerTopComponent.class, "BinExplorerTopComponent.jLabel2.text")); // NOI18N
        jLabel2.setToolTipText(org.openide.util.NbBundle.getMessage(BinExplorerTopComponent.class, "BinExplorerTopComponent.jLabel2.toolTipText")); // NOI18N

        binName.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(binName, org.openide.util.NbBundle.getMessage(BinExplorerTopComponent.class, "BinExplorerTopComponent.binName.text")); // NOI18N

        contentPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        contentPanel.setLayout(new java.awt.BorderLayout());

        geneCovPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, org.openide.util.NbBundle.getMessage(BinExplorerTopComponent.class, "BinExplorerTopComponent.geneCovPanel.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
        geneCovPanel.setMaximumSize(new java.awt.Dimension(100, 100));
        geneCovPanel.setMinimumSize(new java.awt.Dimension(100, 100));
        geneCovPanel.setPreferredSize(new java.awt.Dimension(100, 40));
        geneCovPanel.setLayout(new java.awt.BorderLayout());

        dnaseq.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(dnaseq, org.openide.util.NbBundle.getMessage(BinExplorerTopComponent.class, "BinExplorerTopComponent.dnaseq.text")); // NOI18N
        dnaseq.setToolTipText(org.openide.util.NbBundle.getMessage(BinExplorerTopComponent.class, "BinExplorerTopComponent.dnaseq.toolTipText")); // NOI18N
        dnaseq.setEnabled(false);

        aaseq.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(aaseq, org.openide.util.NbBundle.getMessage(BinExplorerTopComponent.class, "BinExplorerTopComponent.aaseq.text")); // NOI18N
        aaseq.setToolTipText(org.openide.util.NbBundle.getMessage(BinExplorerTopComponent.class, "BinExplorerTopComponent.aaseq.toolTipText")); // NOI18N
        aaseq.setEnabled(false);

        jXTable1.setModel(attrTableModel);
        jXTable1.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jXTable1);

        jLabel3.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(BinExplorerTopComponent.class, "BinExplorerTopComponent.jLabel3.text")); // NOI18N

        jLabel4.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(BinExplorerTopComponent.class, "BinExplorerTopComponent.jLabel4.text")); // NOI18N

        jLabel5.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, org.openide.util.NbBundle.getMessage(BinExplorerTopComponent.class, "BinExplorerTopComponent.jLabel5.text")); // NOI18N

        jLabel6.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel6, org.openide.util.NbBundle.getMessage(BinExplorerTopComponent.class, "BinExplorerTopComponent.jLabel6.text")); // NOI18N

        jLabel7.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
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
        exportGBK.setToolTipText(org.openide.util.NbBundle.getMessage(BinExplorerTopComponent.class, "BinExplorerTopComponent.exportGBK.toolTipText")); // NOI18N
        exportGBK.setEnabled(false);

        jLabel8.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel8, org.openide.util.NbBundle.getMessage(BinExplorerTopComponent.class, "BinExplorerTopComponent.jLabel8.text")); // NOI18N
        jLabel8.setToolTipText(org.openide.util.NbBundle.getMessage(BinExplorerTopComponent.class, "BinExplorerTopComponent.jLabel8.toolTipText")); // NOI18N

        completeness.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(completeness, org.openide.util.NbBundle.getMessage(BinExplorerTopComponent.class, "BinExplorerTopComponent.completeness.text")); // NOI18N

        jLabel9.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel9, org.openide.util.NbBundle.getMessage(BinExplorerTopComponent.class, "BinExplorerTopComponent.jLabel9.text")); // NOI18N
        jLabel9.setToolTipText(org.openide.util.NbBundle.getMessage(BinExplorerTopComponent.class, "BinExplorerTopComponent.jLabel9.toolTipText")); // NOI18N

        contamination.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(contamination, org.openide.util.NbBundle.getMessage(BinExplorerTopComponent.class, "BinExplorerTopComponent.contamination.text")); // NOI18N

        jLabel10.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel10, org.openide.util.NbBundle.getMessage(BinExplorerTopComponent.class, "BinExplorerTopComponent.jLabel10.text")); // NOI18N
        jLabel10.setToolTipText(org.openide.util.NbBundle.getMessage(BinExplorerTopComponent.class, "BinExplorerTopComponent.jLabel10.toolTipText")); // NOI18N

        taxonomy.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(taxonomy, org.openide.util.NbBundle.getMessage(BinExplorerTopComponent.class, "BinExplorerTopComponent.taxonomy.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(contentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(binName, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(taxonomy, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(completeness, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(contamination, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(contigList, javax.swing.GroupLayout.PREFERRED_SIZE, 484, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(exportGBK))
                            .addComponent(jLabel1))
                        .addGap(0, 921, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(geneCovPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel6)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(geneFrame))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel5)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(geneStop))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel4)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(geneStart))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addComponent(jLabel3)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(geneName))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addComponent(dnaseq)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(aaseq)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel7)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(geneLength)))
                                .addGap(18, 18, 18)))
                        .addComponent(jScrollPane1)))
                .addContainerGap())
            .addComponent(seqpropHolder, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(binName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(taxonomy, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel8)
                    .addComponent(completeness, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(contamination, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(geneName))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(geneStart)
                            .addComponent(jLabel4))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(geneStop, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel5))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(geneFrame)
                            .addComponent(jLabel6))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(geneLength))
                        .addGap(15, 15, 15)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(dnaseq)
                            .addComponent(aaseq))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(geneCovPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 502, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton aaseq;
    private javax.swing.JLabel binName;
    private javax.swing.JLabel completeness;
    private javax.swing.JLabel contamination;
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
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private org.jdesktop.swingx.JXTable jXTable1;
    private javax.swing.JPanel seqpropHolder;
    private javax.swing.JLabel taxonomy;
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        vc.addPropertyChangeListener(this);
        binResult.addLookupListener(this);
        resultChanged(null);
    }

    @Override
    public void componentClosed() {
        binResult.removeLookupListener(this);
        vc.removePropertyChangeListener(this);
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
                vc.setContig(contig);
                repaint();
                updateLookup();

            }
        }
    }

    @Override
    public void resultChanged(LookupEvent le) {
        // avoid update when component is activated
        if (isActivated && vc.getSelectedBin() != null) {
            return;
        }

        BinI newBin = null;
        for (BinI bin : binResult.allInstances()) {
            newBin = bin;
        }
        if (newBin != null && !newBin.equals(vc.getSelectedBin())) {
            vc.selectBin(newBin);
        }
    }

    @Override
    public synchronized void propertyChange(PropertyChangeEvent evt) {

        if (evt.getPropertyName().equals(ContigViewController.BIN_SELECTED)) {
            BinI bin = vc.getSelectedBin();
            contigListModel.clear();
            contigListModel.setBin(bin);
            contigListModel.update();

            if (bin != null) {
                binName.setText(bin.getName());
                taxonomy.setText(bin.getTaxonomy());
                completeness.setText(bin.getCompleteness() + "%");
                contamination.setText(bin.getContamination() + "%");
            } else {
                binName.setText("-");
                taxonomy.setText("");
                completeness.setText("N/A");
                contamination.setText("N/A");
            }
        }

        if (evt.getPropertyName().equals(ContigViewController.CONTIG_CHANGE)) {
            ContigI contig = vc.getContig();
            exportGBK.setEnabled(contig != null);
        }

        if (evt.getPropertyName().equals(ContigViewController.FEATURE_SELECTED) || evt.getPropertyName().equals(ContigViewController.NAVIGATE_TO_REGION)) {

            AssembledRegionI region = vc.getSelectedRegion();

            final MGXMasterI master = region != null ? region.getMaster() : null;
            if (master != null && !master.equals(vc.getSelectedBin().getMaster())) {
                runNames.clear();
            }

            if (region != null) {

                ContigI contig = vc.getContig();
                geneName.setText(contig.getName() + "_" + region.getId());
                geneStart.setText(nf.format(region.getStart()));
                geneStop.setText(nf.format(region.getStop()));
                if (region.getFrame() > 0) {
                    geneFrame.setText("+" + String.valueOf(region.getFrame()));
                } else {
                    geneFrame.setText(String.valueOf(region.getFrame()));
                }

                if (region.getType() == RegionType.CDS) {
                    geneLength.setText(nf.format(region.getLength() / 3) + " aa");
                } else {
                    geneLength.setText(nf.format(region.getLength()) + " nt");
                }

                dnaseq.setEnabled(true);

                // only enable amino acid sequence display for CDS features
                aaseq.setEnabled(region.getType() == RegionType.CDS);

            } else {
                geneName.setText("");
                geneStart.setText("");
                geneStop.setText("");
                geneFrame.setText("");
                geneLength.setText("");
                dnaseq.setEnabled(false);
                aaseq.setEnabled(false);
            }

            //
            // update coverage chart
            //
            MGXPool.getInstance().submit(new Runnable() {
                @Override
                public void run() {

                    try {
                        List<GeneCoverageI> geneCov = new ArrayList<>();

                        if (region != null) {
                            Iterator<GeneCoverageI> iter = region.getMaster().GeneCoverage().ByGene(region);
                            while (iter != null && iter.hasNext()) {
                                geneCov.add(iter.next());
                            }
                            Collections.sort(geneCov);
                        }

                        Color purple = new Color(156, 17, 130);
                        CategoryPlot plot = coverageChart.getCategoryPlot();
                        CategoryItemRenderer br = plot.getRenderer();
                        for (int i = 0; i < geneCov.size(); i++) {
                            br.setSeriesPaint(i, purple);
                        }

                        coverageDataset.clear();
                        for (GeneCoverageI gc : geneCov) {
                            if (!runNames.containsKey(gc.getRunId())) {
                                SeqRunI run = master.SeqRun().fetch(gc.getRunId());
                                runNames.put(gc.getRunId(), run.getName());
                                run.deleted();
                            }
                            coverageDataset.addValue(gc.getCoverage(), "", runNames.get(gc.getRunId()));
                        }
                    } catch (MGXException ex) {
                        Exceptions.printStackTrace(ex);
                        coverageDataset.clear();
                        return;
                    }

                    EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            geneCovPanel.revalidate();
                            geneCovPanel.repaint();
                        }

                    });

                }
            });

            updateLookup();
        }
    }

    private void setupCoverageChart() {

        coverageChart.setBorderPaint(Color.WHITE);
        coverageChart.setBackgroundPaint(Color.WHITE);
        coverageChart.setAntiAlias(true);

        CategoryPlot plot = coverageChart.getCategoryPlot();
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryMargin(0);
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.STANDARD);

        CategoryItemRenderer renderer = plot.getRenderer();
        renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        renderer.setDefaultItemLabelPaint(Color.WHITE);
        ItemLabelPosition position = new ItemLabelPosition(ItemLabelAnchor.CENTER,
                TextAnchor.CENTER_LEFT);
        renderer.setDefaultPositiveItemLabelPosition(position);
        renderer.setDefaultItemLabelsVisible(true);

        BarRenderer br = (BarRenderer) plot.getRenderer();

        br.setDefaultToolTipGenerator(new StandardCategoryToolTipGenerator("<html>Sequencing run: {1}<br>Mapped reads: {2}</html>", nf));
        br.setItemMargin(0);
        br.setMaximumBarWidth(.2); // set maximum width to 20% of chart

    }

    private void updateLookup() {
        content.set(Collections.emptyList(), null);

        BinI bin = vc.getSelectedBin();
        if (bin != null) {
            content.add(bin.getMaster());
            content.add(bin);
        }

        ContigI contig = vc.getContig();
        if (contig != null) {
            content.add(contig);
        }

        AssembledRegionI region = vc.getSelectedRegion();
        if (region != null) {
            content.add(region);
        }
    }

    void writeProperties(java.util.Properties p) {
        p.setProperty("version", "1.0");
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        //this.close();
    }

    private class BinSearchHandler implements LookupListener {

        public BinSearchHandler() {
        }

        @Override
        public void resultChanged(LookupEvent le) {
            BinSearchResultI sr = null;
            for (BinSearchResultI bsr : binSearchResult.allInstances()) {
                sr = bsr;
            }
            if (sr != null) {
                long contigId = sr.getContigId();

                int contigIdx = contigListModel.findIndexByID(contigId);
                if (contigIdx == -1) {
                    System.err.println("BinSearchHandler unable to find contig with ID " + contigId + " in list model with size " + contigListModel.getSize());
                    return;
                }
                contigList.setSelectedIndex(contigIdx);
                ContigI contig = contigListModel.getElementAt(contigIdx);
                vc.setContig(contig);

                vc.navigateToRegion(sr.getRegionId());
            }
        }

    }
}
