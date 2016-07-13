package de.cebitec.mgx.gui.goldstandard.ui.charts;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.groups.ImageExporterI;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.tree.NodeI;
import de.cebitec.mgx.api.model.tree.TreeI;
import de.cebitec.mgx.gui.goldstandard.util.NodeUtils;
import de.cebitec.mgx.gui.goldstandard.wizards.selectjobs.SelectSingleJobWizardDescriptor;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import java.awt.Dialog;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JComponent;
import org.apache.commons.collections4.CollectionUtils;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author pblumenk
 */
@ServiceProvider(service = GSComparisonI.class)
public class GSComparisonVennChart extends EvaluationViewerI<TreeI<Long>> implements GSComparisonI {

    private VennChart venn;

    private SeqRunI currentSeqrun;
    private JobI currentJob;
    
    private GSComparisonVennChartCustomizer cust = null;

    private TLongObjectMap<String> onlyGSID;
    private TLongObjectMap<String> onlySampleID;
    private TLongObjectMap<String> gsAndSampleID;

    @Override
    public JComponent getComponent() {
        return venn;

    }

    @Override
    public ImageExporterI getImageExporter() {
        return VennChart.getImageExporter(venn);
    }

    @Override
    public String getName() {
        return "Venn chart";
    }

    @Override
    public boolean canHandle(AttributeTypeI valueType) {
        return true;
    }

    @Override
    public Class getInputType() {
        return TreeI.class;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void show(List<TreeI<Long>> trees) {
        if (trees.size() != 2) {
            return;
        }

        List<NodeI<Long>> gsLeaves = new ArrayList<>(trees.get(0).getLeaves());
        List<NodeI<Long>> sampleLeaves = new ArrayList<>(trees.get(1).getLeaves());
        List<Integer> sampleIndizes = new LinkedList<>();
        for (int i = 0; i < sampleLeaves.size(); i++) {
            sampleIndizes.add(i);
        }

        onlyGSID = new TLongObjectHashMap<>((int) currentSeqrun.getNumSequences() / 6);
        onlySampleID = new TLongObjectHashMap<>((int) currentSeqrun.getNumSequences() / 6);
        gsAndSampleID = new TLongObjectHashMap<>((int) currentSeqrun.getNumSequences() / 3 * 2);
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
                    String attr = gsNode.getAttribute().getValue();
                    for (long l : NodeUtils.getSeqIDs(gsNode)) {
                        onlyGSID.put(l, attr);
                    }
                    continue;
                }

                List<Long> gsIDs = NodeUtils.getSeqIDs(gsNode);
                List<Long> sampleIDs = NodeUtils.getSeqIDs(sampleNode);

                List<Long> intersect = new ArrayList<>(CollectionUtils.intersection(gsIDs, sampleIDs));
                List<Long> oGS = new ArrayList<>(CollectionUtils.subtract(gsIDs, sampleIDs));
                List<Long> oSample = new ArrayList<>(CollectionUtils.subtract(sampleIDs, gsIDs));

                if (!intersect.isEmpty()) {
                    String attr = gsNode.getAttribute().getValue();
                    for (long l : intersect) {
                        gsAndSampleID.put(l, attr);
                    }
                }
                if (!oGS.isEmpty()) {
                    String attr = gsNode.getAttribute().getValue();
                    for (long l : oGS) {
                        onlyGSID.put(l, attr);
                    }
                }
                if (!oSample.isEmpty()) {
                    String attr = sampleNode.getAttribute().getValue();
                    for (long l : oSample) {
                        onlySampleID.put(l, attr);
                    }
                }
            }

            if (!sampleIndizes.isEmpty()) {
                for (Integer i : sampleIndizes) {
                    NodeI<Long> node = sampleLeaves.get(i);
                    List<Long> seqIDs = NodeUtils.getSeqIDs(node);
                    for (long l : seqIDs) {
                        String attr = node.getAttribute().getValue();
                        onlyGSID.put(l, attr);
                    }
                }
            }

        } catch (MGXException ex) {
            Exceptions.printStackTrace(Exceptions.attachMessage(ex, "Cannot download sequence ids for NodeI instance"));
        }

        int a = onlyGSID.size();
        int b = onlySampleID.size();
        int ab = gsAndSampleID.size();

        try {
            venn = VennChart.get2Venn(a, b, ab);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    
    @Override
    public JComponent getCustomizer() {
        if (cust == null) {
            cust = new GSComparisonVennChartCustomizer();
        }
        return cust;
    }

    @Override
    public void start(SeqRunI seqrun) {
        currentSeqrun = seqrun;
//        if (lastSeqrun != currentSeqrun) {
//            cache = new HashMap<>();
//        }
        try {
            SelectSingleJobWizardDescriptor jobWizard = new SelectSingleJobWizardDescriptor(seqrun);
            Dialog dialog = DialogDisplayer.getDefault().createDialog(jobWizard);
            dialog.setVisible(true);
            dialog.toFront();
            boolean cancelled = jobWizard.getValue() != WizardDescriptor.FINISH_OPTION;
            if (!cancelled) {
                currentJob = jobWizard.getJob();
                JobI gsJob = jobWizard.getGoldstandard();
                AttributeTypeI attrType = jobWizard.getAttributeType();
                List<TreeI<Long>> treeList = new ArrayList<>();
                treeList.add(seqrun.getMaster().Attribute().getHierarchy(attrType, gsJob));
                treeList.add(seqrun.getMaster().Attribute().getHierarchy(attrType, currentJob));
                show(treeList);
            }
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    
}