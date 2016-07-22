package de.cebitec.mgx.gui.goldstandard.ui;

import de.cebitec.mgx.api.groups.ImageExporterI;
import de.cebitec.mgx.gui.goldstandard.ui.charts.EvaluationViewerI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.util.Lookup;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//de.cebitec.mgx.gui.goldstandard.ui//Evaluation//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "EvaluationTopComponent",
        iconBase = "de/cebitec/mgx/gui/goldstandard/ui/stopwatch.png",
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@ActionID(category = "Window", id = "de.cebitec.mgx.gui.goldstandard.ui.EvaluationTopComponent")
@ActionReferences({
    @ActionReference(path = "Menu/Window", position = 342),
    @ActionReference(path = "Toolbars/UndoRedo", position = 560)
})
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_EvaluationAction",
        preferredID = "EvaluationTopComponent"
)
@Messages({
    "CTL_EvaluationAction=EvaluationTopComponent",
    "CTL_EvaluationTopComponent=EvaluationTopComponent Window",
    "HINT_EvaluationTopComponent=This is a EvaluationTopComponent window"
})
public final class EvaluationTopComponent extends TopComponent {

    private final EvaluationControlPanel controlPanel1 = new EvaluationControlPanel();
    private final Lookup lookup;
    private final InstanceContent content = new InstanceContent();
    
    private static ExecutorService executor = null;

    public EvaluationTopComponent() {
        initComponents();
        setName(Bundle.CTL_EvaluationTopComponent());
        setToolTipText(Bundle.HINT_EvaluationTopComponent());

        int width = jSplitPane1.getSize().width;
        jSplitPane1.setDividerLocation(width - 50);
        lookup = new AbstractLookup(content);
        associateLookup(lookup);
        controlPanel1.setTopComponent(this);
        jSplitPane1.setRightComponent(controlPanel1);
        controlPanel1.updateViewerList();
        //
        chartpane.getVerticalScrollBar().setUnitIncrement(16);
    }
    
    public static ExecutorService getExecutorService(){
        if (executor == null) {
            int threads = Math.min(Runtime.getRuntime().availableProcessors(), 20);
            executor = Executors.newFixedThreadPool(threads);
        }

        return executor;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        chartpane = new javax.swing.JScrollPane();

        jSplitPane1.setDividerLocation(800);
        jSplitPane1.setPreferredSize(new java.awt.Dimension(850, 700));

        chartpane.setMinimumSize(new java.awt.Dimension(600, 400));
        jSplitPane1.setLeftComponent(chartpane);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 844, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 492, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane chartpane;
    private javax.swing.JSplitPane jSplitPane1;
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
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

    public void setVisualization(EvaluationViewerI v) {
        if (v.getComponent() != null) {
            chartpane.setViewportView(v.getComponent());
            ImageExporterI exporter = v.getImageExporter();
            if (exporter != null && exporter.getSupportedTypes().length > 0) {
                content.add(exporter);
            }
        }
    }
}
