package de.cebitec.mgx.gui.statistics.clustering;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.groups.ConflictingJobsException;
import de.cebitec.mgx.api.groups.FileType;
import de.cebitec.mgx.api.groups.ImageExporterI;
import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.common.VGroupManager;
import de.cebitec.mgx.common.visualization.ViewerI;
import de.cebitec.mgx.gui.statistics.clustering.model.DendrogramBuilder;
import de.cebitec.mgx.gui.statistics.clustering.model.ITreeBuilder;
import de.cebitec.mgx.gui.statistics.clustering.view.DendrogramDisplay;
import de.cebitec.mgx.gui.swingutils.DelayedPlot;
import de.cebitec.mgx.newick.NodeI;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import org.openide.util.Exceptions;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author sjaenick
 */
@ServiceProvider(service = ViewerI.class)
public class HClustPlot extends ViewerI<DistributionI> {

    private final static String NODE_NAME_KEY = "nodeName";
    private final static String X_COORD = "x";
    private DelayedPlot cPanel = null;
    private final HClustCustomizer customizer = new HClustCustomizer();
    private DendrogramDisplay display;

    @Override
    public JComponent getComponent() {
        return cPanel;
    }

    @Override
    public String getName() {
        return "Clustering";
    }

    @Override
    public void show(final List<Pair<VisualizationGroupI, DistributionI>> dists) {

        cPanel = new DelayedPlot();

        SwingWorker<NodeI, Void> worker = new SwingWorker<NodeI, Void>() {
            @Override
            protected NodeI doInBackground() throws Exception {
                MGXMasterI m =  dists.get(0).getSecond().getMaster();
                return m.Statistics().Clustering(dists, customizer.getDistanceMethod(), customizer.getAgglomeration());
            }

            @Override
            protected void done() {
                try {
                    DelayedPlot wp = HClustPlot.this.cPanel;
                    NodeI root = get();

                    ITreeBuilder builder = new DendrogramBuilder(NODE_NAME_KEY, X_COORD, root);
                    display = new DendrogramDisplay(builder);

                    JTextArea area = new JTextArea("Not yet implemented.");
                    wp.setTarget(display);
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }
                super.done();
            }
        };
        worker.execute();
    }

    @Override
    public JComponent getCustomizer() {
        return customizer;
    }

    @Override
    public Class getInputType() {
        return DistributionI.class;
    }

    @Override
    public ImageExporterI getImageExporter() {
        return new ImageExporterI() {

            @Override
            public FileType[] getSupportedTypes() {
                return new FileType[]{FileType.PNG, FileType.JPEG};
            }

            @Override
            public boolean export(FileType type, String fName) throws Exception {
                switch (type) {
                    case PNG:
                        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(fName))) {
                            return display.saveImage(os, type.getSuffices()[0].toUpperCase(), 2);
                        }
                    default:
                        return false;
                }

            }
        };
    }

    @Override
    public boolean canHandle(AttributeTypeI valueType) {
        try {
            return valueType.getValueType() == AttributeTypeI.VALUE_DISCRETE
                    && VGroupManager.getInstance().getActiveGroups().size() > 1
                    && VGroupManager.getInstance().getDistributions().size() > 1;
        } catch (ConflictingJobsException ex) {
            return false;
        }
    }

    @Override
    public void setAttributeType(AttributeTypeI aType) {
        super.setAttributeType(aType);
        super.setTitle("Clustering based on " + aType.getName());
    }
}
