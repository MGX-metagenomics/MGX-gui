package de.cebitec.mgx.gui.attributevisualization;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(dtd = "-//de.cebitec.mgx.gui.attributevisualization//AttributeVisualization//EN",
autostore = false)
@TopComponent.Description(preferredID = "AttributeVisualizationTopComponent",
//iconBase="SET/PATH/TO/ICON/HERE", 
persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@ActionID(category = "Window", id = "de.cebitec.mgx.gui.attributevisualization.AttributeVisualizationTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(displayName = "#CTL_AttributeVisualizationAction",
preferredID = "AttributeVisualizationTopComponent")
public final class AttributeVisualizationTopComponent extends TopComponent implements PropertyChangeListener {

    public AttributeVisualizationTopComponent() {
        initComponents();
        setName(NbBundle.getMessage(AttributeVisualizationTopComponent.class, "CTL_AttributeVisualizationTopComponent"));
        setToolTipText(NbBundle.getMessage(AttributeVisualizationTopComponent.class, "HINT_AttributeVisualizationTopComponent"));
        groupingPanel1.addPropertyChangeListener(this);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        groupingPanel1 = new de.cebitec.mgx.gui.attributevisualization.GroupingPanel();

        setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.BorderLayout());
        jPanel1.add(groupingPanel1, java.awt.BorderLayout.PAGE_END);

        add(jPanel1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private de.cebitec.mgx.gui.attributevisualization.GroupingPanel groupingPanel1;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
    }

    @Override
    public void componentClosed() {
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName() != null) {
            System.err.println("TC received event "+evt.getPropertyName() + " " + evt.getOldValue() + " " + evt.getNewValue());
        }
    }
}
