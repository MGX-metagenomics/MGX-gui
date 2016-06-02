package de.cebitec.mgx.gui.goldstandard.ui.charts;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.groups.ImageExporterI;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.tree.NodeI;
import de.cebitec.mgx.api.model.tree.TreeI;
import de.cebitec.mgx.gui.goldstandard.wizards.selectjobs.SelectSingleJobWizardDescriptor;
import java.awt.Dialog;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import org.jdesktop.swingx.JXTable;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author pblumenk
 */
@ServiceProvider(service = EvaluationViewerI.class)
public class GSComparisonViewer extends EvaluationViewerI<TreeI<Long>>{

    public enum ComparisonViews {
        TableView, VennChart
    };
    
    private JXTable table;
    private VennChart venn;
    private ComparisonViews currentView;
    private GSComparisonViewCustomizer cust = null;
    
    @Override
    public JComponent getComponent() {
        switch (currentView){
            case TableView:
                return table;
            case VennChart:
                return venn;
        }        
    }

    @Override
    public ImageExporterI getImageExporter() {
        return null;    //no image to export here
    }

    @Override
    public String getName() {
        return "Compare to Goldstandard";
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
        
        
        currentView = cust.getCurrentComparisonView();
        
        
        return;
    }

    @Override
    public JComponent getCustomizer() {
        if (cust == null) {
            cust = new GSComparisonViewCustomizer();
        }
//        cust.setAttributeType(getAttributeType());
        return cust;
    }

    @Override
    public void init(SeqRunI seqrun) {
        try {
            SelectSingleJobWizardDescriptor jobWizard = new SelectSingleJobWizardDescriptor(seqrun);
            Dialog dialog = DialogDisplayer.getDefault().createDialog(jobWizard);
            dialog.setVisible(true);
            dialog.toFront();
            boolean cancelled = jobWizard.getValue() != WizardDescriptor.FINISH_OPTION;
            if (!cancelled) {
                JobI sampleJob = jobWizard.getJob();
                JobI gsJob = jobWizard.getGoldstandard();
                AttributeTypeI attrType = jobWizard.getAttributeType();
                List<TreeI<Long>> treeList = new ArrayList<>();
                treeList.add(seqrun.getMaster().Attribute().getHierarchy(attrType, gsJob));
                treeList.add(seqrun.getMaster().Attribute().getHierarchy(attrType, sampleJob));
                show(treeList);
            }
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
        }
        
    }
    
}
