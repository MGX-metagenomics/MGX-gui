package de.cebitec.mgx.gui.statistics.viewer;

import de.cebitec.mgx.gui.attributevisualization.viewer.CategoricalViewerI;
import de.cebitec.mgx.gui.attributevisualization.viewer.ViewerI;
import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.Attribute;
import de.cebitec.mgx.gui.datamodel.AttributeType;
import de.cebitec.mgx.gui.datamodel.misc.Distribution;
import de.cebitec.mgx.gui.datamodel.misc.Pair;
import de.cebitec.mgx.gui.groups.ImageExporterI;
import de.cebitec.mgx.gui.groups.VisualizationGroup;
import de.cebitec.mgx.gui.statistics.PCA;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.JComponent;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author sj
 */
@ServiceProvider(service = ViewerI.class)
public class PCAPlot extends CategoricalViewerI {

    @Override
    public boolean canHandle(AttributeType valueType) {
        // currently broken, disable viewer
        return false;
    }
    JComponent component;

    @Override
    public JComponent getComponent() {
        return component;
    }

    @Override
    public String getName() {
        return "PCA";
    }

    @Override
    public Class getInputType() {
        return Distribution.class;
    }

    @Override
    public void show(List<Pair<VisualizationGroup, Distribution>> dists) {

        // collect all attributes that somehow appear in the distributions
        SortedSet<Attribute> attributes = new TreeSet<>();
        for (Pair<VisualizationGroup, Distribution> p : dists) {
            Distribution dist = p.getSecond();
            attributes.addAll(dist.keySet());
        }
        MGXMaster master = (MGXMaster) dists.get(0).getSecond().getMaster();
        int numAttributes = attributes.size();

        double[][] x = new double[dists.size()][];
        int curDist = 0;
        for (Pair<VisualizationGroup, Distribution> p : dists) {
            Distribution dist = p.getSecond();
            x[curDist] = new double[numAttributes];
            int curAttr = 0;
            for (Attribute a : attributes) {
                x[curDist][curAttr] = dist.containsKey(a) ? dist.get(a).doubleValue() : 0;
                curAttr++;
            }
            curDist++;
        }

        PCA pca = new PCA(x);
        pca.print();
        component = pca.view();
    }

    @Override
    public JComponent getCustomizer() {
        return null;
    }

    @Override
    public ImageExporterI getImageExporter() {
        return null;
    }
}
