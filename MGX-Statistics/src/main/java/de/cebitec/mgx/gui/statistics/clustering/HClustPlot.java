package de.cebitec.mgx.gui.statistics.clustering;

import de.cebitec.mgx.gui.attributevisualization.ui.DelayedPlot;
import de.cebitec.mgx.gui.attributevisualization.viewer.ViewerI;
import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.AttributeType;
import de.cebitec.mgx.gui.datamodel.misc.Distribution;
import de.cebitec.mgx.gui.datamodel.misc.Pair;
import de.cebitec.mgx.gui.groups.ConflictingJobsException;
import de.cebitec.mgx.gui.groups.ImageExporterI;
import de.cebitec.mgx.gui.groups.VGroupManager;
import de.cebitec.mgx.gui.groups.VisualizationGroup;
import de.cebitec.mgx.newick.NodeI;
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
public class HClustPlot extends ViewerI<Distribution> {

    private DelayedPlot cPanel = null;
    private final HClustCustomizer customizer = new HClustCustomizer();

    @Override
    public JComponent getComponent() {
        return cPanel;
    }

    @Override
    public String getName() {
        return "Clustering";
    }

    @Override
    public void show(final List<Pair<VisualizationGroup, Distribution>> dists) {

        cPanel = new DelayedPlot();

        SwingWorker<NodeI, Void> worker = new SwingWorker<NodeI, Void>() {

            @Override
            protected NodeI doInBackground() throws Exception {
                MGXMaster m = (MGXMaster) dists.get(0).getSecond().getMaster();
                return m.Statistics().Clustering(dists, customizer.getDistanceMethod(), customizer.getAgglomeration());
            }

            @Override
            protected void done() {
                try {
                    DelayedPlot wp = HClustPlot.this.cPanel;
                    NodeI root = get();
                    JTextArea area = new JTextArea("Not yet implemented.");
                    wp.setTarget(area);
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
        return Distribution.class;
    }

    @Override
    public ImageExporterI getImageExporter() {
        return null;
    }

    @Override
    public boolean canHandle(AttributeType valueType) {
        try {
            return valueType.getValueType() == AttributeType.VALUE_DISCRETE
                    && VGroupManager.getInstance().getActiveGroups().size() > 1
                    && VGroupManager.getInstance().getDistributions().size() > 1;
        } catch (ConflictingJobsException ex) {
            return false;
        }
    }

    @Override
    public void setAttributeType(AttributeType aType) {
        super.setAttributeType(aType);
        super.setTitle("Clustering based on " + aType.getName());
    }
}
