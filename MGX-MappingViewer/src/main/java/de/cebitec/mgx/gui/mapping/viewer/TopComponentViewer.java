/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping.viewer;

import de.cebitec.mgx.gui.mapping.MappingCtx;
import de.cebitec.mgx.gui.mapping.ViewController;
import de.cebitec.mgx.gui.mapping.panel.FeaturePanel;
import de.cebitec.mgx.gui.mapping.panel.MappingPanel;
import de.cebitec.mgx.gui.mapping.panel.NavigationPanel;
import java.awt.BorderLayout;
import javax.swing.SwingWorker;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;

@TopComponent.Description(
        preferredID = "TopComponentViewer",
        persistenceType = TopComponent.PERSISTENCE_NEVER)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@ActionID(category = "Window", id = "de.cebitec.mgx.gui.mapping.viewer")
@ActionReference(path = "Menu/Window", position = 334)
@Messages({
    "CTL_MappingAction=ReferenceView",
    "CTL_TopComponentViewer=Mapping Window",})
public final class TopComponentViewer extends TopComponent {

    private final MappingCtx ctx;

    public TopComponentViewer(MappingCtx ctx) {
        this.ctx = ctx;
        setName(Bundle.CTL_TopComponentViewer());
    }

    private void createView() {
        removeAll();
        setLayout(new BorderLayout());
//        JPanel panel = new JPanel();
//        panel.setLayout(new BorderLayout());

        final ViewController vc = new ViewController(ctx);

        NavigationPanel np = new NavigationPanel(vc);
        add(np, BorderLayout.NORTH);

        // precache regions
        SwingWorker<Void, Void> sw = new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws Exception {
                vc.getRegions(0, vc.getReference().getLength() - 1);
                return null;
            }
        };
        sw.execute();
        // precache mappings/coverage
        SwingWorker<Void, Void> sw2 = new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws Exception {
                vc.getCoverage(0, vc.getReference().getLength() - 1);
                return null;
            }
        };
        sw2.execute();
        FeaturePanel fp = new FeaturePanel(vc);
        add(fp, BorderLayout.CENTER);
        
        MappingPanel mp = new MappingPanel(vc);
        add(mp, BorderLayout.SOUTH);

//        BasePanelFactory factory = new BasePanelFactory(ctx);
//        ReferenceBasePanel refBasePanel = factory.getGenomeViewerBasePanel();
//        ReadsBasePanel readsBasePanel = factory.getReadViewerBasePanel();
//        panel.add(refBasePanel, BorderLayout.NORTH);
//        panel.add(readsBasePanel, BorderLayout.CENTER);
//        add(panel);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        createView();
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
}
