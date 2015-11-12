package de.cebitec.mgx.gui.attributevisualization.ui;

import de.cebitec.mgx.api.groups.ImageExporterI;
import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.common.visualization.ViewerI;
import de.cebitec.mgx.gui.attributevisualization.exportwizard.SeqExporter;
import de.cebitec.mgx.api.groups.SequenceExporterI;
import java.util.Collections;
import java.util.List;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(dtd = "-//de.cebitec.mgx.gui.attributevisualization.ui//AttributeVisualization//EN",
        autostore = false)
@TopComponent.Description(preferredID = "AttributeVisualizationTopComponent",
        iconBase = "de/cebitec/mgx/gui/attributevisualization/ui/ShowVisualization.png",
        persistenceType = TopComponent.PERSISTENCE_NEVER)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@ActionID(category = "Window", id = "de.cebitec.mgx.gui.attributevisualization.ui.AttributeVisualizationTopComponent")
@ActionReferences({
    @ActionReference(path = "Menu/Window", position = 333),
    @ActionReference(path = "Toolbars/UndoRedo", position = 510)
})
@TopComponent.OpenActionRegistration(displayName = "#CTL_AttributeVisualizationAction",
        preferredID = "AttributeVisualizationTopComponent")
@NbBundle.Messages({
    "CTL_AttributeVisualizationAction=Visualize results",
    "CTL_AttributeVisualizationTopComponent=Visualization",
    "HINT_AttributeVisualizationTopComponent=Visualization window"
})
public final class AttributeVisualizationTopComponent extends TopComponent {

    private final InstanceContent content = new InstanceContent();
    private final Lookup lookup;
    private final ControlPanel controlPanel1 = new ControlPanel();
    //private final VGroupManagerI vmgr = VGroupManager.getInstance();

    public AttributeVisualizationTopComponent() {
        initComponents();
        setName(NbBundle.getMessage(AttributeVisualizationTopComponent.class, "CTL_AttributeVisualizationTopComponent"));
        setToolTipText(NbBundle.getMessage(AttributeVisualizationTopComponent.class, "HINT_AttributeVisualizationTopComponent"));
        // forward group change events to the control panel
//        groupingPanel1.addPropertyChangeListener(controlPanel1);
        // create initial group
//        groupingPanel1.addGroup();

        int width = jSplitPane1.getSize().width;
        jSplitPane1.setDividerLocation(width - 100);
        lookup = new AbstractLookup(content);
        associateLookup(lookup);
        controlPanel1.setTopComponent(this);
        jSplitPane1.setRightComponent(controlPanel1);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        chartpane = new javax.swing.JScrollPane();

        setMinimumSize(new java.awt.Dimension(100, 50));
        setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.BorderLayout());

        jSplitPane1.setDividerSize(5);
        jSplitPane1.setResizeWeight(1.0);

        chartpane.setMinimumSize(new java.awt.Dimension(800, 500));
        chartpane.setPreferredSize(new java.awt.Dimension(500, 500));
        jSplitPane1.setLeftComponent(chartpane);

        jPanel1.add(jSplitPane1, java.awt.BorderLayout.CENTER);

        add(jPanel1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane chartpane;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSplitPane jSplitPane1;
    // End of variables declaration//GEN-END:variables

    @Override
    public void componentOpened() {
        openVGroupTopComponent();
    }

    @Override
    public void componentClosed() {
        controlPanel1.dispose();
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
    }

    public void setVisualization(ViewerI v) {
        chartpane.setViewportView(v.getComponent());
        ImageExporterI exporter = v.getImageExporter();
        if (exporter != null && exporter.getSupportedTypes().length > 0) {
            content.add(exporter);
        }
    }

    private void openVGroupTopComponent() {
        // TODO: prevent opening this twice
        VisualizationGroupTopComponent pe = Lookup.getDefault().lookup(VisualizationGroupTopComponent.class);
        pe.setVisible(true);

        Mode m = WindowManager.getDefault().findMode("output");
        if (m != null) {
            m.dockInto(pe);
        } else {
            System.err.println("output mode not found");
        }
        pe.open();

        //pe.requestActive();
    }

    void updateLookup(List<Pair<VisualizationGroupI, DistributionI<Long>>> currentDistributions) {
        content.set(Collections.emptyList(), null); // clear content
        for (Pair<VisualizationGroupI, DistributionI<Long>> p : currentDistributions) {
            SequenceExporterI exp = new SeqExporter(p.getFirst(), p.getSecond());
            content.add(exp);
        }
    }
}
