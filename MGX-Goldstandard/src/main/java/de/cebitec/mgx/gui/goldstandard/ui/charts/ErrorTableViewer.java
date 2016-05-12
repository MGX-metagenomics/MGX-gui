package de.cebitec.mgx.gui.goldstandard.ui.charts;

import de.cebitec.mgx.api.groups.ImageExporterI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.model.AttributeTypeI;
import java.util.List;
import javax.swing.JComponent;
import org.jdesktop.swingx.JXTable;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author pblumenk
 */
@ServiceProvider(service = EvaluationViewerI.class)
public class ErrorTableViewer extends EvaluationViewerI<DistributionI<Long>>{

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
        return DistributionI.class;
    }

    @Override
    public void show(List<DistributionI<Long>> dists) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
