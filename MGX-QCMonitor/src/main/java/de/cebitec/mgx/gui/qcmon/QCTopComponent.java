/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.qcmon;

import de.cebitec.mgx.api.groups.ImageExporterI;
import de.cebitec.mgx.api.model.ModelBaseI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.qc.QCResultI;
import de.cebitec.mgx.gui.charts.basic.util.JFreeChartUtil;
import de.cebitec.mgx.gui.charts.basic.util.SVGChartPanel;
import java.awt.Component;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
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
        dtd = "-//de.cebitec.mgx.qcmon//QC//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "QCTopComponent",
        iconBase = "de/cebitec/mgx/gui/qcmon/QC.svg",
        persistenceType = TopComponent.PERSISTENCE_NEVER
)
@TopComponent.Registration(mode = "satellite", openAtStartup = false)
@ActionID(category = "Window", id = "de.cebitec.mgx.qcmon.QCTopComponent")
//@ActionReference(path = "Menu/Window", position = 533)
@TopComponent.OpenActionRegistration(
        displayName = "Quality Control",
        preferredID = "QCTopComponent"
)
public final class QCTopComponent extends TopComponent implements LookupListener, PropertyChangeListener {
    
    private final Lookup.Result<SeqRunI> resultSeqRun;
    private SeqRunI currentSeqRun = null;
    
    private final InstanceContent content = new InstanceContent();
    private final Lookup lookup;
    private int tabIdx = -1;
    private static final Logger LOG = Logger.getLogger(QCTopComponent.class.getName());
    
    private final static String[] QC_ORDER = {
        "Nucleotide distribution",
        "Read length",
        "GC",
        "Forward read quality",
        "Reverse read quality"
    };
    
    private QCTopComponent() {
        initComponents();
        super.setName("Quality Control");
        super.setToolTipText("Quality Control");
        lookup = new AbstractLookup(content);
        associateLookup(lookup);
        resultSeqRun = Utilities.actionsGlobalContext().lookupResult(SeqRunI.class);
        update();
        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int newIdx = tabbedPane.getSelectedIndex();
                if (newIdx != tabIdx) {
                    content.set(Collections.emptyList(), null); // clear lookup
                    Component comp = tabbedPane.getSelectedComponent();
                    if (comp != null && comp instanceof SVGChartPanel) {
                        SVGChartPanel cp = (SVGChartPanel) comp;
                        ImageExporterI exporter = JFreeChartUtil.getImageExporter(cp.getChart());
                        content.add(exporter);
                    }
                    tabIdx = newIdx;
                }
            }
        });
    }
    
    private static QCTopComponent instance = null;
    
    public static QCTopComponent getDefault() {
        if (instance == null) {
            instance = new QCTopComponent();
        }
        return instance;
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

        tabbedPane = new javax.swing.JTabbedPane();

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane tabbedPane;
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
    
    @Override
    public void resultChanged(LookupEvent le) {
        update();
    }
    
    private void update() {
        SeqRunI prevRun = currentSeqRun;
        
        for (SeqRunI run : resultSeqRun.allInstances()) {
            if (currentSeqRun == null || !run.equals(currentSeqRun)) {
                currentSeqRun = run;
                break;
            }
        }
        
        if (currentSeqRun == null || currentSeqRun.equals(prevRun)) {
            return; // no update needed
        }
        
        if (prevRun != null) {
            prevRun.removePropertyChangeListener(this);
        }
        currentSeqRun.addPropertyChangeListener(this);
        
        SwingWorker<List<QCResultI>, Void> sw = new SwingWorker<List<QCResultI>, Void>() {
            
            @Override
            protected List<QCResultI> doInBackground() throws Exception {
                return currentSeqRun.getMaster().SeqRun().getQC(currentSeqRun);
            }
            
            @Override
            protected void done() {
                List<QCResultI> qc = null;
                try {
                    qc = get();
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }
                if (qc == null || qc.isEmpty()) {
                    tabbedPane.removeAll();
                } else {
                    int idx = tabbedPane.getSelectedIndex();
                    tabbedPane.removeAll();
                    int cnt = 0;
                    
                    Map<String, QCResultI> data = new HashMap<>();
                    for (QCResultI qcr : qc) {
                        data.put(qcr.getName(), qcr);
                    }

                    // generate QC charts in order
                    for (String qcName : QC_ORDER) {
                        if (data.containsKey(qcName)) {
                            QCResultI qcr = data.remove(qcName);
                            SVGChartPanel chart = QCChartGenerator.createChart(qcr);
                            tabbedPane.add(qcr.getName(), chart);
                            tabbedPane.setToolTipTextAt(cnt++, qcr.getDescription());
                        }
                    }

                    // append remaining QC metrics
                    for (QCResultI qcr : data.values()) {
                        LOG.log(Level.INFO, "QC result for \"{0}\" not handled in QC_ORDER", qcr.getName());
                        SVGChartPanel chart = QCChartGenerator.createChart(qcr);
                        tabbedPane.add(qcr.getName(), chart);
                        tabbedPane.setToolTipTextAt(cnt++, qcr.getDescription());
                    }

                    // restore tab selection
                    if (idx != -1 && tabbedPane.getTabCount() > idx) {
                        tabbedPane.setSelectedIndex(idx);
                    }
                }
                super.done();
            }
        };
        sw.execute();
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() instanceof SeqRunI && currentSeqRun != null && currentSeqRun.equals(evt.getSource())) {
            if (ModelBaseI.OBJECT_DELETED.equals(evt.getPropertyName())) {
                SeqRunI src = (SeqRunI) evt.getSource();
                src.removePropertyChangeListener(this);
                currentSeqRun = null;
                tabbedPane.removeAll();
            }
        }
    }
}
