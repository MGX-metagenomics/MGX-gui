/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.report.ui;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.ModelBaseI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.qc.QCResultI;
import de.cebitec.mgx.api.model.tree.TreeI;
import de.cebitec.mgx.common.JobState;
import de.cebitec.mgx.gui.datafactories.DistributionFactory;
import de.cebitec.mgx.gui.qcmon.QCChartGenerator;
import de.cebitec.mgx.gui.vizfilter.LimitFilter;
import de.cebitec.mgx.gui.vizfilter.LimitFilter.LIMITS;
import de.cebitec.mgx.gui.vizfilter.SortOrder;
import de.cebitec.mgx.gui.vizfilter.SortOrder.Order;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serial;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieChartBuilder;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.style.PieStyler.LabelType;
import org.knowm.xchart.style.Styler.ChartTheme;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;

/**
 * ‚
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//de.cebitec.mgx.gui.mgx_report//ReportSummary//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "ReportSummaryTopComponent",
        iconBase = "de/cebitec/mgx/gui/reportcom/report.svg",
        persistenceType = TopComponent.PERSISTENCE_NEVER)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@ActionID(category = "Window", id = "de.cebitec.mgx.gui.mgx_report.ReportSummaryTopComponent")
@ActionReferences({
    @ActionReference(path = "Menu/Window", position = 550)
})
public final class ReportSummaryTopComponent extends TopComponent implements LookupListener, PropertyChangeListener {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Lookup.Result<SeqRunI> resultSeqRun;
    private SeqRunI currentSeqRun = null;

    private int tabIdx = -1;
    //
    private final static Color TOOLTIP_COLOR = new Color(120, 85, 137);
    private final static Font CHART_FONT = new Font("Avenir Next Condensed", Font.BOLD, 12);
    //
    private final static List<String> funcLabels
            = Arrays.asList("A RNA processing and modification",
                    "B Chromatin structure and dynamics",
                    "C Energy production and conversion",
                    "D Cell cycle control, cell division, chromosome partitioning",
                    "E Amino acid transport and metabolism",
                    "F Nucleotide transport and metabolism",
                    "G Carbohydrate transport and metabolism",
                    "H Coenzyme transport and metabolism",
                    "I Lipid transport and metabolism", "J Translation, ribosomal structure and biogenesis",
                    "K Transcription",
                    "L Replication, recombination and repair",
                    "M Cell wall/membrane/envelope biogenesis",
                    "N Cell motility",
                    "O Posttranslational modification, protein turnover, chaperones",
                    "P Inorganic ion transport and metabolism",
                    "Q Secondary metabolites biosynthesis, transport and catabolism",
                    "R General function prediction only",
                    "S Function unknown",
                    "T Signal transduction mechanisms",
                    "U Intracellular trafficking, secretion, and vesicular transport",
                    "V Defense mechanisms",
                    "Y Nuclear structure",
                    "Z Cytoskeleton");
    private final static List<String> vtmp = Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "Y", "Z");

    final private Color[] colorPalette;

    public ReportSummaryTopComponent() {
        this.colorPalette = generatePaletteCBFriendlyLight();
        initComponents();
        super.setName("Data Report");
        super.setToolTipText("Data Report");

        resultSeqRun = Utilities.actionsGlobalContext().lookupResult(SeqRunI.class);
        update();
        tabbedpane.addChangeListener((ChangeEvent e) -> {
            int newIdx = tabbedpane.getSelectedIndex();
            if (newIdx != tabIdx) {
                tabbedpane.getSelectedComponent();
                tabIdx = newIdx;
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

        jTextField1 = new javax.swing.JTextField();
        jFrame1 = new javax.swing.JFrame();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabbedpane = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        z = new javax.swing.JLabel();
        nameseq = new javax.swing.JTextField();
        z1 = new javax.swing.JLabel();
        seqid = new javax.swing.JTextField();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        z2 = new javax.swing.JLabel();
        seqcount = new javax.swing.JTextField();
        z3 = new javax.swing.JLabel();
        seqmeth = new javax.swing.JTextField();
        z4 = new javax.swing.JLabel();
        seqtech = new javax.swing.JTextField();
        z5 = new javax.swing.JLabel();
        paired = new javax.swing.JTextField();
        qccontroll = new javax.swing.JTabbedPane();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jobLabel = new javax.swing.JLabel();
        phylumpanel = new JPanel();
        classpanel = new JPanel();
        kingdompanel = new JPanel();
        orderpanel = new JPanel();
        familypanel = new JPanel();
        genuspanel = new JPanel();
        organismpanel = new JPanel();
        jPanel3 = new javax.swing.JPanel();
        jobLabel1 = new javax.swing.JLabel();
        cogpanel = new JPanel();
        funcpanel = new JPanel();

        jTextField1.setText(org.openide.util.NbBundle.getMessage(ReportSummaryTopComponent.class, "ReportSummaryTopComponent.jTextField1.text")); // NOI18N

        javax.swing.GroupLayout jFrame1Layout = new javax.swing.GroupLayout(jFrame1.getContentPane());
        jFrame1.getContentPane().setLayout(jFrame1Layout);
        jFrame1Layout.setHorizontalGroup(
            jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jFrame1Layout.setVerticalGroup(
            jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(ReportSummaryTopComponent.class, "ReportSummaryTopComponent.jLabel2.text")); // NOI18N

        tabbedpane.setPreferredSize(new java.awt.Dimension(1400, 2300));

        jPanel1.setLayout(new FlowLayout());
        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setToolTipText(org.openide.util.NbBundle.getMessage(ReportSummaryTopComponent.class, "ReportSummaryTopComponent.jPanel1.toolTipText")); // NOI18N
        jPanel1.setPreferredSize(new java.awt.Dimension(1980, 1200));

        z.setFont(new java.awt.Font("Avenir Next Condensed", 1, 18)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(z, org.openide.util.NbBundle.getMessage(ReportSummaryTopComponent.class, "ReportSummaryTopComponent.z.text")); // NOI18N
        z.setToolTipText(org.openide.util.NbBundle.getMessage(ReportSummaryTopComponent.class, "ReportSummaryTopComponent.z.toolTipText")); // NOI18N

        nameseq.setEditable(false);
        nameseq.setFont(new java.awt.Font("Avenir Next Condensed", 0, 18)); // NOI18N
        nameseq.setText(org.openide.util.NbBundle.getMessage(ReportSummaryTopComponent.class, "ReportSummaryTopComponent.nameseq.text")); // NOI18N
        nameseq.setToolTipText(org.openide.util.NbBundle.getMessage(ReportSummaryTopComponent.class, "ReportSummaryTopComponent.nameseq.toolTipText")); // NOI18N
        nameseq.setBorder(null);

        z1.setFont(new java.awt.Font("Avenir Next Condensed", 1, 18)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(z1, org.openide.util.NbBundle.getMessage(ReportSummaryTopComponent.class, "ReportSummaryTopComponent.z1.text")); // NOI18N

        seqid.setFont(new java.awt.Font("Avenir Next Condensed", 0, 18)); // NOI18N
        seqid.setText(org.openide.util.NbBundle.getMessage(ReportSummaryTopComponent.class, "ReportSummaryTopComponent.seqid.text")); // NOI18N
        seqid.setToolTipText(org.openide.util.NbBundle.getMessage(ReportSummaryTopComponent.class, "ReportSummaryTopComponent.seqid.toolTipText")); // NOI18N
        seqid.setBorder(null);

        z2.setFont(new java.awt.Font("Avenir Next Condensed", 1, 18)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(z2, org.openide.util.NbBundle.getMessage(ReportSummaryTopComponent.class, "ReportSummaryTopComponent.z2.text")); // NOI18N

        seqcount.setEditable(false);
        seqcount.setFont(new java.awt.Font("Avenir Next Condensed", 0, 18)); // NOI18N
        seqcount.setText(org.openide.util.NbBundle.getMessage(ReportSummaryTopComponent.class, "ReportSummaryTopComponent.seqcount.text")); // NOI18N
        seqcount.setToolTipText(org.openide.util.NbBundle.getMessage(ReportSummaryTopComponent.class, "ReportSummaryTopComponent.seqcount.toolTipText")); // NOI18N
        seqcount.setBorder(null);

        z3.setFont(new java.awt.Font("Avenir Next Condensed", 1, 18)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(z3, org.openide.util.NbBundle.getMessage(ReportSummaryTopComponent.class, "ReportSummaryTopComponent.z3.text")); // NOI18N

        seqmeth.setEditable(false);
        seqmeth.setFont(new java.awt.Font("Avenir Next Condensed", 0, 18)); // NOI18N
        seqmeth.setText(org.openide.util.NbBundle.getMessage(ReportSummaryTopComponent.class, "ReportSummaryTopComponent.seqmeth.text")); // NOI18N
        seqmeth.setToolTipText(org.openide.util.NbBundle.getMessage(ReportSummaryTopComponent.class, "ReportSummaryTopComponent.seqmeth.toolTipText")); // NOI18N
        seqmeth.setBorder(null);

        z4.setFont(new java.awt.Font("Avenir Next Condensed", 1, 18)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(z4, org.openide.util.NbBundle.getMessage(ReportSummaryTopComponent.class, "ReportSummaryTopComponent.z4.text")); // NOI18N

        seqtech.setEditable(false);
        seqtech.setFont(new java.awt.Font("Avenir Next Condensed", 0, 18)); // NOI18N
        seqtech.setText(org.openide.util.NbBundle.getMessage(ReportSummaryTopComponent.class, "ReportSummaryTopComponent.seqtech.text")); // NOI18N
        seqtech.setToolTipText(org.openide.util.NbBundle.getMessage(ReportSummaryTopComponent.class, "ReportSummaryTopComponent.seqtech.toolTipText")); // NOI18N
        seqtech.setBorder(null);

        z5.setFont(new java.awt.Font("Avenir Next Condensed", 1, 18)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(z5, org.openide.util.NbBundle.getMessage(ReportSummaryTopComponent.class, "ReportSummaryTopComponent.z5.text")); // NOI18N

        paired.setEditable(false);
        paired.setFont(new java.awt.Font("Avenir Next Condensed", 0, 18)); // NOI18N
        paired.setText(org.openide.util.NbBundle.getMessage(ReportSummaryTopComponent.class, "ReportSummaryTopComponent.paired.text")); // NOI18N
        paired.setToolTipText(org.openide.util.NbBundle.getMessage(ReportSummaryTopComponent.class, "ReportSummaryTopComponent.paired.toolTipText")); // NOI18N
        paired.setBorder(null);

        jLabel1.setFont(new java.awt.Font("Avenir Next Condensed", 1, 24)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(ReportSummaryTopComponent.class, "ReportSummaryTopComponent.jLabel1.text")); // NOI18N

        jLabel3.setFont(new java.awt.Font("Avenir Next Condensed", 1, 24)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(ReportSummaryTopComponent.class, "ReportSummaryTopComponent.jLabel3.text")); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(246, 246, 246)
                                .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(qccontroll)
                                .addGap(94, 94, 94))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(452, 452, 452)
                                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(711, 711, 711))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(z, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(z1, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(z2, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(z5, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(z4, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(z3, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(117, 117, 117)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(seqmeth)
                                    .addComponent(seqtech)
                                    .addComponent(seqcount)
                                    .addComponent(nameseq)
                                    .addComponent(seqid)
                                    .addComponent(paired))
                                .addGap(528, 528, 528))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(474, 474, 474)
                        .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(800, 800, 800)))
                .addGap(218, 218, 218))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {z, z1, z2, z3, z4, z5});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE)
                        .addGap(33, 33, 33)
                        .addComponent(nameseq))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(94, 94, 94)
                        .addComponent(z, javax.swing.GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(seqid)
                    .addComponent(z1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(seqcount)
                    .addComponent(z2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(seqmeth)
                    .addComponent(z3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(6, 6, 6)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(seqtech)
                    .addComponent(z4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(paired)
                    .addComponent(z5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(48, 48, 48)
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(47, 47, 47)
                .addComponent(qccontroll, javax.swing.GroupLayout.DEFAULT_SIZE, 392, Short.MAX_VALUE)
                .addGap(732, 732, 732)
                .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(732, 732, 732))
        );

        tabbedpane.addTab(org.openide.util.NbBundle.getMessage(ReportSummaryTopComponent.class, "ReportSummaryTopComponent.jPanel1.TabConstraints.tabTitle"), jPanel1); // NOI18N
        jPanel1.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(ReportSummaryTopComponent.class, "ReportSummaryTopComponent.jPanel1.AccessibleContext.accessibleDescription")); // NOI18N

        jPanel2.setLayout(new FlowLayout(FlowLayout.LEFT));
        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setAutoscrolls(true);
        jPanel2.setRequestFocusEnabled(false);

        jobLabel.setBackground(new java.awt.Color(255, 255, 255));
        jobLabel.setFont(new java.awt.Font("Avenir Next Condensed", 1, 18)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jobLabel, org.openide.util.NbBundle.getMessage(ReportSummaryTopComponent.class, "ReportSummaryTopComponent.jobLabel.text")); // NOI18N

        phylumpanel.setPreferredSize(new java.awt.Dimension(700, 500));

        javax.swing.GroupLayout phylumpanelLayout = new javax.swing.GroupLayout(phylumpanel);
        phylumpanel.setLayout(phylumpanelLayout);
        phylumpanelLayout.setHorizontalGroup(
            phylumpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        phylumpanelLayout.setVerticalGroup(
            phylumpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        classpanel.setPreferredSize(new java.awt.Dimension(700, 500));

        javax.swing.GroupLayout classpanelLayout = new javax.swing.GroupLayout(classpanel);
        classpanel.setLayout(classpanelLayout);
        classpanelLayout.setHorizontalGroup(
            classpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        classpanelLayout.setVerticalGroup(
            classpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 481, Short.MAX_VALUE)
        );

        kingdompanel.setPreferredSize(new java.awt.Dimension(700, 500));
        kingdompanel.setRequestFocusEnabled(false);

        orderpanel.setPreferredSize(new java.awt.Dimension(700, 500));

        javax.swing.GroupLayout orderpanelLayout = new javax.swing.GroupLayout(orderpanel);
        orderpanel.setLayout(orderpanelLayout);
        orderpanelLayout.setHorizontalGroup(
            orderpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        orderpanelLayout.setVerticalGroup(
            orderpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        familypanel.setPreferredSize(new java.awt.Dimension(700, 500));

        genuspanel.setPreferredSize(new java.awt.Dimension(700, 500));

        organismpanel.setPreferredSize(new java.awt.Dimension(700, 500));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(classpanel, javax.swing.GroupLayout.DEFAULT_SIZE, 732, Short.MAX_VALUE)
                            .addComponent(kingdompanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(familypanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(26, 26, 26)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(phylumpanel, javax.swing.GroupLayout.DEFAULT_SIZE, 732, Short.MAX_VALUE)
                            .addComponent(orderpanel, javax.swing.GroupLayout.DEFAULT_SIZE, 732, Short.MAX_VALUE)
                            .addComponent(genuspanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(574, 574, 574)
                        .addComponent(jobLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(541, 541, 541))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(402, 402, 402)
                        .addComponent(organismpanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(360, 360, 360)))
                .addGap(28, 28, 28))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addComponent(jobLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 52, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(kingdompanel, javax.swing.GroupLayout.DEFAULT_SIZE, 492, Short.MAX_VALUE)
                    .addComponent(phylumpanel, javax.swing.GroupLayout.DEFAULT_SIZE, 492, Short.MAX_VALUE))
                .addGap(29, 29, 29)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(classpanel, javax.swing.GroupLayout.DEFAULT_SIZE, 481, Short.MAX_VALUE)
                    .addComponent(orderpanel, javax.swing.GroupLayout.DEFAULT_SIZE, 481, Short.MAX_VALUE))
                .addGap(26, 26, 26)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(familypanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(genuspanel, javax.swing.GroupLayout.DEFAULT_SIZE, 492, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(organismpanel, javax.swing.GroupLayout.DEFAULT_SIZE, 492, Short.MAX_VALUE)
                .addGap(132, 132, 132))
        );

        jobLabel.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(ReportSummaryTopComponent.class, "ReportSummaryTopComponent.jobLabel.AccessibleContext.accessibleName")); // NOI18N

        tabbedpane.addTab(org.openide.util.NbBundle.getMessage(ReportSummaryTopComponent.class, "ReportSummaryTopComponent.jPanel2.TabConstraints.tabTitle"), jPanel2); // NOI18N

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setToolTipText(org.openide.util.NbBundle.getMessage(ReportSummaryTopComponent.class, "ReportSummaryTopComponent.jPanel3.toolTipText")); // NOI18N

        jobLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jobLabel1.setFont(new java.awt.Font("Avenir Next Condensed", 1, 18)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jobLabel1, org.openide.util.NbBundle.getMessage(ReportSummaryTopComponent.class, "ReportSummaryTopComponent.jobLabel1.text")); // NOI18N
        jobLabel1.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        cogpanel.setPreferredSize(new java.awt.Dimension(1300, 800));
        cogpanel.setRequestFocusEnabled(false);

        funcpanel.setPreferredSize(new java.awt.Dimension(1300, 1100));
        funcpanel.setRequestFocusEnabled(false);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(514, 514, 514)
                .addComponent(jobLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(629, 629, 629))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(47, 47, 47)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(funcpanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cogpanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(143, 143, 143))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addComponent(jobLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cogpanel, javax.swing.GroupLayout.PREFERRED_SIZE, 811, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(funcpanel, javax.swing.GroupLayout.PREFERRED_SIZE, 1122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(264, 264, 264))
        );

        tabbedpane.addTab(org.openide.util.NbBundle.getMessage(ReportSummaryTopComponent.class, "ReportSummaryTopComponent.jPanel3.TabConstraints.tabTitle"), jPanel3); // NOI18N

        jScrollPane1.setViewportView(tabbedpane);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1496, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel classpanel;
    private javax.swing.JPanel cogpanel;
    private javax.swing.JPanel familypanel;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JPanel funcpanel;
    private javax.swing.JPanel genuspanel;
    private javax.swing.JFrame jFrame1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JLabel jobLabel;
    private javax.swing.JLabel jobLabel1;
    private javax.swing.JPanel kingdompanel;
    private javax.swing.JTextField nameseq;
    private javax.swing.JPanel orderpanel;
    private javax.swing.JPanel organismpanel;
    private javax.swing.JTextField paired;
    private javax.swing.JPanel phylumpanel;
    private javax.swing.JTabbedPane qccontroll;
    private javax.swing.JTextField seqcount;
    private javax.swing.JTextField seqid;
    private javax.swing.JTextField seqmeth;
    private javax.swing.JTextField seqtech;
    private javax.swing.JTabbedPane tabbedpane;
    private javax.swing.JLabel z;
    private javax.swing.JLabel z1;
    private javax.swing.JLabel z2;
    private javax.swing.JLabel z3;
    private javax.swing.JLabel z4;
    private javax.swing.JLabel z5;
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        resultSeqRun.addLookupListener(this);
        update();
    }

    @Override
    public void componentClosed() {
        resultSeqRun.removeLookupListener(this);
    }

    void writeProperties(java.util.Properties p) {
        p.setProperty("version", "1.0");

    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
    }

    private void update() {
        SeqRunI prevRun = currentSeqRun;

        for (SeqRunI run : resultSeqRun.allInstances()) {
            if (currentSeqRun == null || !run.equals(currentSeqRun)) {
                currentSeqRun = run;
                break;
            }
        }

        if (currentSeqRun == null) {
            jPanel1.removeAll();
            return;
        }

        if (currentSeqRun.equals(prevRun)) {
            return;
        }

        if (prevRun != null) {
            prevRun.removePropertyChangeListener(this);
        }
        currentSeqRun.addPropertyChangeListener(this);

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        // Overview
        nameseq.setText(currentSeqRun.getName());
        seqid.setText(String.format("%s", currentSeqRun.getId()));
        seqcount.setText(NumberFormat.getNumberInstance(Locale.US).format(currentSeqRun.getNumSequences()));
        seqmeth.setText(currentSeqRun.getSequencingMethod().getName());
        seqtech.setText(currentSeqRun.getSequencingTechnology().getName());
        paired.setText(currentSeqRun.isPaired() ? "yes" : "no");

        //QC Print
        try {
            qccontroll.removeAll();
            for (QCResultI t : currentSeqRun.getMaster().SeqRun().getQC(currentSeqRun)) {
                qccontroll.addTab(t.getName(), QCChartGenerator.createChart(t));
            }
        } catch (MGXException e) {
            Exceptions.printStackTrace(e);
        }

        // taxonomy
        try {
            Map<String, DistributionI<Long>> taxonomie = getTaxonomy();
            createPieCharts(taxonomie);
        } catch (MGXException e) {
            Exceptions.printStackTrace(e);
        }

        // COG functional profile
        try {
            Map<String, DistributionI<Long>> functional = getFunctional();
            createBarCharts(functional);
        } catch (MGXException e) {
            Exceptions.printStackTrace(e);
        }

        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    private Map<String, DistributionI<Long>> getTaxonomy() throws MGXException {

        final MGXMasterI master = currentSeqRun.getMaster();

        for (JobI job : master.Job().BySeqRun(currentSeqRun)) {

            if (job.getStatus() == JobState.FINISHED) {
                String toolName = master.Tool().ByJob(job).getName();

                if (toolName.equals("MGX taxonomic classification") || toolName.equals("Kraken 2")) {

                    Iterator<AttributeTypeI> attributeit = master.AttributeType().byJob(job);

                    if (attributeit.hasNext()) {
                        AttributeTypeI attrType = attributeit.next();
                        TreeI<Long> tree = master.Attribute().getHierarchy(attrType, job, currentSeqRun);
                        return DistributionFactory.splitTree(tree);
                    }
                }
            }

        }

        return null;
    }

    private void createPieCharts(Map<String, DistributionI<Long>> taxData) {
        if (taxData != null) {
            createPiechart(kingdompanel, "Kingdom", taxData.get("NCBI_SUPERKINGDOM"));
            createPiechart(phylumpanel, "Phylum", taxData.get("NCBI_PHYLUM"));
            createPiechart(classpanel, "Class", taxData.get("NCBI_CLASS"));
            createPiechart(orderpanel, "Order", taxData.get("NCBI_ORDER"));
            createPiechart(familypanel, "Family", taxData.get("NCBI_FAMILY"));
            createPiechart(genuspanel, "Genus", taxData.get("NCBI_GENUS"));
            createPiechart(organismpanel, "Species", taxData.get("NCBI_SPECIES"));
        } else {
            // no data, clear all panels
            createPiechart(kingdompanel, null, null);
            createPiechart(phylumpanel, null, null);
            createPiechart(classpanel, null, null);
            createPiechart(orderpanel, null, null);
            createPiechart(familypanel, null, null);
            createPiechart(genuspanel, null, null);
            createPiechart(organismpanel, null, null);
        }
    }

    private void createPiechart(JPanel container, String chartTitle, DistributionI<Long> dist) {

        container.removeAll();

        if (dist != null) {

            PieChart pieChart = new PieChartBuilder().width(700).height(500).theme(ChartTheme.GGPlot2).build();
            pieChart.setTitle(chartTitle);

            pieChart.getStyler().setLabelType(LabelType.Value);
            pieChart.getStyler().setLabelsFontColor(Color.blue);
            pieChart.getStyler().setForceAllLabelsVisible(true);
            pieChart.getStyler().setLabelsDistance(1.1);
            pieChart.getStyler().setPlotContentSize(.8);
            pieChart.getStyler().setSeriesColors(colorPalette);
            pieChart.getStyler().setToolTipsEnabled(true);
            pieChart.getStyler().setToolTipHighlightColor(TOOLTIP_COLOR);
            pieChart.getStyler().setSumFont(CHART_FONT);
            pieChart.getStyler().setChartTitleFont(CHART_FONT);
            pieChart.getStyler().setLegendFont(CHART_FONT);
            pieChart.getStyler().setSumVisible(true);
            pieChart.getStyler().setAnnotationTextPanelFontColor(Color.BLACK);

            XChartPanel<PieChart> chartPanel = new XChartPanel<>(pieChart);

            SortOrder<Long> sort = new SortOrder<>(Order.DESCENDING);
            LimitFilter<Long> limit = new LimitFilter<>(LIMITS.TOP10);
            DistributionI<Long> topten = limit.filterDist(sort.filterDist(dist));
            topten.forEach((k, v) -> pieChart.addSeries(k.getValue(), v));

            container.setLayout(new FlowLayout(FlowLayout.LEFT));
            container.add(chartPanel);
        } else {
            JLabel noData = new JLabel("No data.");
            noData.setFont(CHART_FONT);
            noData.setHorizontalAlignment(JLabel.CENTER);
            noData.setVerticalAlignment(JLabel.CENTER);

            container.setLayout(new BorderLayout());
            container.add(noData, BorderLayout.CENTER);
        }

        container.revalidate();
        container.repaint();
    }

    private void createBarCharts(Map<String, DistributionI<Long>> cogdata) {

        cogpanel.removeAll();
        funcpanel.removeAll();

        if (cogdata != null) {

            DistributionI<Long> cog = cogdata.get("COG");
            if (cog != null && !cog.isEmpty()) {

                CategoryChart cogchart = new CategoryChartBuilder().width(1300).height(800).theme(ChartTheme.GGPlot2).build();
                cogchart.setTitle("Top 10 COG Groups");

                cogchart.getStyler().setPlotContentSize(.8);
                cogchart.getStyler().setYAxisDecimalPattern("##.###");
                cogchart.getStyler().setSeriesColors(this.colorPalette);
                cogchart.getStyler().setToolTipsEnabled(true);
                cogchart.getStyler().setToolTipHighlightColor(TOOLTIP_COLOR);
                cogchart.getStyler().setLegendFont(CHART_FONT);
                cogchart.getStyler().setChartTitleFont(CHART_FONT);
                cogchart.getStyler().setPlotGridLinesVisible(false);
                cogchart.getStyler().setOverlapped(false);
                cogchart.getStyler().setStacked(true);
                cogchart.getStyler().setXAxisLabelRotation(45);
                cogchart.getStyler().setAvailableSpaceFill(0.7);

                XChartPanel<CategoryChart> xcp = new XChartPanel<>(cogchart);

                SortOrder<Long> sort = new SortOrder<>(Order.DESCENDING);
                LimitFilter<Long> limit = new LimitFilter<>(LIMITS.TOP10);
                DistributionI<Long> cogtop = limit.filterDist(sort.filterDist(cog));

                List<AttributeI> cogkeys = new ArrayList<>(cogtop.keySet());
                List<Long> cogval = new ArrayList<>(cogtop.values());
                List<String> keys = new ArrayList<>();
                cogtop.keySet().stream().map((i) -> i.getValue().split(" ")[0]).forEachOrdered(keys::add);

                for (int i = 0; i < 10; i++) {
                    List<Double> val = generateSeries(cogval.get(i), i, keys.size());
                    cogchart.addSeries(cogkeys.get(i).getValue(), keys, val);
                }

                cogpanel.setLayout(new FlowLayout(FlowLayout.LEFT));
                cogpanel.add(xcp);

            } else {
                JLabel noData = new JLabel("No data.");
                noData.setFont(CHART_FONT);
                noData.setHorizontalAlignment(JLabel.CENTER);
                noData.setVerticalAlignment(JLabel.CENTER);

                cogpanel.setLayout(new BorderLayout());
                cogpanel.add(noData, BorderLayout.CENTER);
            }

            DistributionI<Long> COG_funccat = cogdata.get("COG_funccat");
            if (COG_funccat != null && !COG_funccat.isEmpty()) {

                CategoryChart funcchart = new CategoryChartBuilder().width(1300).height(1100).theme(ChartTheme.GGPlot2).build();
                funcchart.setTitle("COG functional categories");

                Color[] colorcog = generateCOGFuncCatPalette();
                funcchart.getStyler().setYAxisDecimalPattern("##.###");
                funcchart.getStyler().setSeriesColors(colorcog);
                funcchart.getStyler().setToolTipsEnabled(true);
                funcchart.getStyler().setToolTipHighlightColor(TOOLTIP_COLOR);
                funcchart.getStyler().setLegendFont(CHART_FONT);
                funcchart.getStyler().setChartTitleFont(CHART_FONT);
                funcchart.getStyler().setPlotGridLinesVisible(false);
                funcchart.getStyler().setOverlapped(false);
                funcchart.getStyler().setStacked(true);
                funcchart.getStyler().setAvailableSpaceFill(0.7);

                XChartPanel<CategoryChart> xcp = new XChartPanel<>(funcchart);

                List<String> funckeys = new ArrayList<>();
                List<Long> funcval = new ArrayList<>();
                for (Entry<AttributeI, Long> e : COG_funccat.entrySet()) {
                    funckeys.add(e.getKey().getValue());
                    funcval.add(e.getValue());
                }

                for (int i = 0; i < 24; i++) {
                    String currentLabel = funcLabels.get(i);
                    if (funckeys.contains(currentLabel)) {
                        int idx = funckeys.indexOf(currentLabel);
                        List<Double> t = generateSeries(funcval.get(idx), i, 24);
                        funcchart.addSeries(funcLabels.get(i), vtmp, t);
                    } else {
                        List<Double> t = generateSeries(0, i, 24);
                        funcchart.addSeries(funcLabels.get(i), vtmp, t);
                    }
                }

                funcpanel.setLayout(new FlowLayout(FlowLayout.LEFT));
                funcpanel.add(xcp);
            } else {
                JLabel noData = new JLabel("No data.");
                noData.setFont(CHART_FONT);
                noData.setHorizontalAlignment(JLabel.CENTER);
                noData.setVerticalAlignment(JLabel.CENTER);

                funcpanel.setLayout(new BorderLayout());
                funcpanel.add(noData, BorderLayout.CENTER);
            }
        } else {
            // no data
            JLabel noData = new JLabel("No data.");
            noData.setFont(CHART_FONT);
            noData.setHorizontalAlignment(JLabel.CENTER);
            noData.setVerticalAlignment(JLabel.CENTER);

            cogpanel.setLayout(new BorderLayout());
            cogpanel.add(noData, BorderLayout.CENTER);

            JLabel noData2 = new JLabel("No data.");
            noData2.setFont(CHART_FONT);
            noData2.setHorizontalAlignment(JLabel.CENTER);
            noData2.setVerticalAlignment(JLabel.CENTER);

            funcpanel.setLayout(new BorderLayout());
            funcpanel.add(noData2, BorderLayout.CENTER);
        }

        cogpanel.revalidate();
        cogpanel.repaint();

        funcpanel.revalidate();
        funcpanel.repaint();
    }

    private Map<String, DistributionI<Long>> getFunctional() throws MGXException {

        final MGXMasterI master = currentSeqRun.getMaster();

        for (JobI job : master.Job().BySeqRun(currentSeqRun)) {
            if (job.getStatus() == JobState.FINISHED && master.Tool().ByJob(job).getName().equals("COG")) {
                Map<String, DistributionI<Long>> cog = new HashMap<>(2);
                Iterator<AttributeTypeI> attributeit = master.AttributeType().byJob(job);
                while (attributeit.hasNext()) {
                    AttributeTypeI attrType = attributeit.next();
                    DistributionI<Long> dist = master.Attribute().getDistribution(attrType, job, currentSeqRun);
                    cog.put(attrType.getName(), dist);
                }
                return cog;
            }
        }

        // no data
        return null;
    }

    private static List<Double> generateSeries(long data, int idx, int value) {
        Double[] tmp = new Double[value];
        Arrays.fill(tmp, 0D);
        tmp[idx] = Double.valueOf(data);
        return Arrays.asList(tmp);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() instanceof SeqRunI && currentSeqRun != null && currentSeqRun.equals(evt.getSource())) {
            if (ModelBaseI.OBJECT_DELETED.equals(evt.getPropertyName())) {
                SeqRunI src = (SeqRunI) evt.getSource();
                src.removePropertyChangeListener(this);
                currentSeqRun = null;
                tabbedpane.removeAll();
            }
        }
    }

    @Override
    public void resultChanged(LookupEvent le) {
        update();
    }

    private static Color[] generatePaletteCBFriendly() {
        Color[] palette = new Color[11];
        palette[0] = new Color(166, 206, 227);
        palette[1] = new Color(178, 223, 138);
        palette[2] = new Color(31, 120, 180);//white Label
        palette[3] = new Color(51, 160, 44); //white Label
        palette[4] = new Color(251, 154, 153);
        palette[5] = new Color(227, 26, 28);//white Label
        palette[6] = new Color(253, 191, 111);
        palette[7] = new Color(255, 127, 0);
        palette[8] = new Color(202, 178, 214);
        palette[9] = new Color(106, 61, 154); //white Label
        palette[10] = new Color(255, 255, 153);
        return palette;
    }

    private static Color[] generatePaletteCBFriendlyLight() {
        Color[] palette = new Color[11];
        palette[0] = new Color(166, 206, 227);
        palette[1] = new Color(178, 223, 138);
        palette[2] = new Color(240, 228, 66);
        palette[3] = new Color(68, 170, 153);
        palette[4] = new Color(251, 154, 153);
        palette[5] = new Color(255, 109, 182);
        palette[6] = new Color(253, 191, 111);
        palette[7] = new Color(255, 127, 0);
        palette[8] = new Color(202, 178, 214);
        palette[9] = new Color(100, 143, 255);
        palette[10] = new Color(255, 255, 153);
        return palette;
    }

    private static Color[] generatePaletteGradient(int size) {
        Color[] piecolors = new Color[size];
        int red = 108;
        int green = 158;
        int blue = 177;
        for (int i = 0; i < size; i++) {
            final Color color = new Color(red, green, blue);
            piecolors[i] = color;
            red = red + 14;
            green = green - 10;
            if (i % 2 == 0) {
                blue = blue - 5;
            } else {
                blue = blue - 6;
            }
            if (i == 10) {
                piecolors[10] = new Color(177, 108, 123);
            }

        }
        return piecolors;
    }

    private static Color[] generateCOGFuncCatPalette() {
        Color[] cogcolor = new Color[24];
        cogcolor[0] = new Color(194, 175, 88); //A
        cogcolor[1] = new Color(255, 198, 0); //B
        cogcolor[2] = new Color(153, 0, 255); //C
        cogcolor[3] = new Color(153, 255, 0); //D
        cogcolor[4] = new Color(255, 0, 255); //E
        cogcolor[5] = new Color(153, 51, 77); //F
        cogcolor[6] = new Color(128, 86, 66); //G
        cogcolor[7] = new Color(114, 125, 204); //H
        cogcolor[8] = new Color(92, 90, 27); //I
        cogcolor[9] = new Color(255, 0, 0); // J
        cogcolor[10] = new Color(255, 153, 0); // K
        cogcolor[11] = new Color(255, 255, 0); //L
        cogcolor[12] = new Color(158, 201, 40); //M
        cogcolor[13] = new Color(0, 102, 51); //N
        cogcolor[14] = new Color(0, 255, 255); //O
        cogcolor[15] = new Color(0, 153, 255); //P
        cogcolor[16] = new Color(255, 204, 153); //Q
        cogcolor[17] = new Color(255, 153, 153); // R
        cogcolor[18] = new Color(214, 170, 223); //S
        cogcolor[19] = new Color(0, 0, 255); //T
        cogcolor[20] = new Color(51, 204, 153); //U
        cogcolor[21] = new Color(255, 0, 138); //V
        cogcolor[22] = new Color(73, 49, 38); // Y
        cogcolor[23] = new Color(102, 0, 153); // Z
        return cogcolor;
    }
}
