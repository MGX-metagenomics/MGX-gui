package de.cebitec.mgx.gui.goldstandard.ui.charts;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.groups.ImageExporterI;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.tree.NodeI;
import de.cebitec.mgx.api.model.tree.TreeI;
import de.cebitec.mgx.gui.goldstandard.util.NodeUtils;
import de.cebitec.mgx.gui.goldstandard.wizards.selectjobs.SelectSingleJobWizardDescriptor;
import java.awt.Dialog;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.JComponent;
import org.jdesktop.swingx.JXTable;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.lookup.ServiceProvider;
import org.apache.commons.collections4.CollectionUtils;

/**
 *
 * @author pblumenk
 */
@ServiceProvider(service = EvaluationViewerI.class)
public class GSComparisonViewer extends EvaluationViewerI<TreeI<Long>> {

    public enum ComparisonViews {
        TableView, VennChart
    };

    private JXTable table;
    private VennChart venn;
    private ComparisonViews currentView;
    private GSComparisonViewCustomizer cust = null;

    private Map<AttributeI, Collection<Long>> onlyGS;
    private Map<AttributeI, Collection<Long>> onlySample;
    private Map<AttributeI, Collection<Long>> gsAndSample;

    @Override
    public JComponent getComponent() {
        switch (currentView) {
            case TableView:
                return table;
            case VennChart:
                return venn;
            default:
                Exceptions.printStackTrace(new MGXException("Unknown ComparisonView in GSComparisonView"));
                return null;
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
        if (trees.size() != 2) {
            return;
        }

        TreeI<Long> gsTree = trees.get(0);
        TreeI<Long> sampleTree = trees.get(1);

        List<NodeI<Long>> gsLeaves = new ArrayList<>(gsTree.getLeaves());
        List<NodeI<Long>> sampleLeaves = new ArrayList<>(sampleTree.getLeaves());
        List<Integer> sampleIndizes = new LinkedList<>();
        for (int i = 0; i < sampleLeaves.size(); i++) {
            sampleIndizes.add(i);
        }

        onlyGS = new HashMap<>();
        onlySample = new HashMap<>();
        gsAndSample = new HashMap<>();
        try {
            for (NodeI<Long> gsNode : gsLeaves) {
                NodeI<Long> sampleNode = null;
                for (Integer i : sampleIndizes) {
                    if (sampleLeaves.get(i).getAttribute().equals(gsNode.getAttribute())) {
                        sampleNode = sampleLeaves.get(i);
                        sampleIndizes.remove(i);
                        break;
                    }
                }

                if (sampleNode == null) {
                    onlyGS.put(gsNode.getAttribute(), NodeUtils.getSeqIDs(gsNode));
                    continue;
                }

                List<Long> gsIDs = NodeUtils.getSeqIDs(gsNode);
                List<Long> sampleIDs = NodeUtils.getSeqIDs(sampleNode);

                Collection<Long> intersect = CollectionUtils.intersection(gsIDs, sampleIDs);
                Collection<Long> oGS = CollectionUtils.subtract(gsIDs, sampleIDs);
                Collection<Long> oSample = CollectionUtils.subtract(sampleIDs, gsIDs);

                if (!intersect.isEmpty()) {
                    gsAndSample.put(gsNode.getAttribute(), intersect);
                }
                if (!oGS.isEmpty()) {
                    onlyGS.put(gsNode.getAttribute(), oGS);
                }
                if (!oSample.isEmpty()) {
                    onlySample.put(sampleNode.getAttribute(), oSample);
                }
            }
            
            if (!sampleIndizes.isEmpty()){
                for (Integer i : sampleIndizes){
                    NodeI<Long> node = sampleLeaves.get(i);                    
                    List<Long> seqIDs = NodeUtils.getSeqIDs(node);
                    onlySample.put(node.getAttribute(), seqIDs);
                }
            }

            currentView = cust.getCurrentComparisonView();

            if (currentView == ComparisonViews.VennChart) {
                int a = 0, b = 0, ab = 0;
                for (Collection<Long> i : onlyGS.values())
                    a += i.size();
                for (Collection<Long> i : onlySample.values())
                    b += i.size();
                for (Collection<Long> i : gsAndSample.values())
                    ab += i.size();
                venn = VennChart.get2Venn(a, b, ab);
            } else if (currentView == ComparisonViews.TableView) {

            }
        } catch (MGXException ex) {
            Exceptions.printStackTrace(Exceptions.attachMessage(ex, "Cannot download sequence ids for NodeI instance"));
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
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
