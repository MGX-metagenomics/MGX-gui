package de.cebitec.mgx.gui.goldstandard.ui.charts;

import de.cebitec.mgx.api.groups.ImageExporterI;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.tree.TreeI;
import de.cebitec.mgx.gui.datamodel.tree.Tree;
import java.util.List;
import javax.swing.JComponent;
import org.jdesktop.swingx.JXTable;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author pblumenk
 */
@ServiceProvider(service = EvaluationViewerI.class)
public class ErrorTableViewer extends EvaluationViewerI<TreeI<Long>>{

    private JXTable table;
    private ErrorTableViewCustomizer cust = null;
    
    @Override
    public JComponent getComponent() {
        return table;
    }

    @Override
    public ImageExporterI getImageExporter() {
        return null;    //no image to export here
    }

    @Override
    public String getName() {
        return "Classification error table";
    }

    @Override
    public boolean canHandle(AttributeTypeI valueType) {
        return true;
    }

    @Override
    public Class getInputType() {
        return TreeI.class;
    }

    @Override
    public void show(List<TreeI<Long>> trees) {
        if (trees.size() != 2)
            return;
        
        TreeI<Long> gsTree = trees.get(0);
        TreeI<Long> sampleTree = trees.get(1);
        return;
    }

    @Override
    public JComponent getCustomizer() {
        if (cust == null) {
            cust = new ErrorTableViewCustomizer();
        }
//        cust.setAttributeType(getAttributeType());
        return cust;
    }
    
}
