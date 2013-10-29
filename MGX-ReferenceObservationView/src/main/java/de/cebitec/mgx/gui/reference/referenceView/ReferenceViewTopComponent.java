/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.reference.referenceView;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.Reference;
import de.cebitec.mgx.gui.datamodel.Region;
import de.cebitec.mgx.gui.reference.dataVisualisation.BoundsInfo;
import de.cebitec.mgx.gui.reference.dataVisualisation.BoundsInfoManager;
import de.cebitec.mgx.gui.reference.dataVisualisation.MousePositionListener;
import de.cebitec.mgx.gui.reference.dataVisualisation.basePanel.AdjustmentPanel;
import de.cebitec.mgx.gui.reference.dataVisualisation.basePanel.BasePanel;
import excluded.BasePanelFactory;
import excluded.PersistantReference;
import excluded.ViewController;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;
import javax.swing.SwingWorker;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//org.mgx.referenceview//ReferenceView//EN",
        autostore = false)
@TopComponent.Description(
        preferredID = "ReferenceViewTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE",
        persistenceType = TopComponent.PERSISTENCE_NEVER)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@ActionID(category = "Window", id = "org.mgx.referenceview.ReferenceViewTopComponent")
@ActionReference(path = "Menu/Window", position = 333)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_ReferenceViewAction",
        preferredID = "ReferenceViewTopComponent")
@Messages({
    "CTL_ReferenceViewAction=ReferenceView",
    "CTL_ReferenceViewTopComponent=ReferenceView Window",
    "HINT_ReferenceViewTopComponent=This is a ReferenceView window"
})
public final class ReferenceViewTopComponent extends TopComponent {


    private MGXMaster currentMaster = null;
    private final Lookup.Result<MGXMaster> mgxMasterResult;
    private static final Logger log = Logger.getLogger(ReferenceViewTopComponent.class.getName());
    private Reference reference;
    private String referenceSequence;

    public ReferenceViewTopComponent() {
        mgxMasterResult = Utilities.actionsGlobalContext().lookupResult(MGXMaster.class);
        reference = null;
        setName(Bundle.CTL_ReferenceViewTopComponent());
        setToolTipText(Bundle.HINT_ReferenceViewTopComponent());
        referenceSequence = null;
    }
    

    private void loadReference(final long lId) {

        final SwingWorker referenceSequenceWorker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                int length = currentMaster.Reference().fetch(lId).getLength();
                return currentMaster.Reference().getSequence(lId, 0, length);
            }

            @Override
            protected void done() {

                try {
                    referenceSequence = get();
                } catch (InterruptedException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }
                loadRegions(lId);

                super.done();
            }
        };

        SwingWorker fetchWorker = new SwingWorker<Reference, Void>() {
            @Override
            protected Reference doInBackground() throws Exception {
                return currentMaster.Reference().fetch(lId);
            }

            @Override
            protected void done() {
                try {
                    reference = get();
                } catch (InterruptedException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }
                referenceSequenceWorker.execute();
            }
        };
        fetchWorker.execute();
    }

    private void loadRegions(final long lId) {
        createView(reference);
    }

    private void createView(Reference ref) {

        PersistantReference vampReference = new PersistantReference(10, ref.getName(), "description", referenceSequence, new Timestamp(new Date().getTime()));
        BoundsInfoManager manager = new BoundsInfoManager(vampReference);

        MousePositionListener listener = new MousePositionListener() {
            @Override
            public void setCurrentMousePosition(int logPos) {
            }

            @Override
            public void setMouseOverPaintingRequested(boolean requested) {
            }
        };
        // BasePanel basepanel = new BasePanel(manager, listener);
        ViewController viewController = new ViewController();
        //viewController.addMousePositionListener(basepanel);
        //ReferenceViewer genomeViewer = new ReferenceViewer(manager, basepanel, vampReference, iter);
        // int maxSliderValue = 500;
        //basepanel.setViewer(genomeViewer);
        //basepanel.setHorizontalAdjustmentPanel(createAdjustmentPanel(true, true, maxSliderValue, manager, vampReference));

        BasePanelFactory factory = new BasePanelFactory(manager, viewController, currentMaster);
        setLayout(new BorderLayout());
        BasePanel b = factory.getGenomeViewerBasePanel(vampReference, reference);
        add(b);
    }

    private void loadMGXMaster() {
        for (MGXMaster newMaster : mgxMasterResult.allInstances()) {
            if (currentMaster == null || !newMaster.equals(currentMaster)) {
                currentMaster = newMaster;
                return;
            }
        }
    }

    private AdjustmentPanel createAdjustmentPanel(boolean hasScrollbar, boolean hasSlider, int sliderMax, BoundsInfoManager boundsManager, PersistantReference refGenome) {
        // create control panel
        BoundsInfo bounds = boundsManager.getUpdatedBoundsInfo(new Dimension(10, 10));
        AdjustmentPanel control = new AdjustmentPanel(1, refGenome.getRefLength(),
                bounds.getCurrentLogPos(), bounds.getZoomValue(), sliderMax, hasScrollbar, hasSlider);
        control.addAdjustmentListener(boundsManager);
        boundsManager.addSynchronousNavigator(control);
        return control;
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
        loadMGXMaster();
        loadReference(62);
    }

    @Override
    public void componentClosed() {
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
