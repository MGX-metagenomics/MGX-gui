package de.cebitec.mgx.gui.goldstandard.ui.charts.pipelinecomparison;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.groups.ImageExporterI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.gui.goldstandard.ui.charts.EvaluationViewerI;
import de.cebitec.mgx.gui.goldstandard.ui.charts.VennChart;
import de.cebitec.mgx.gui.goldstandard.ui.charts.gscomparison.GSComparisonI;
import de.cebitec.mgx.gui.goldstandard.util.EvalExceptions;
import de.cebitec.mgx.gui.goldstandard.util.JobUtils;
import de.cebitec.mgx.gui.goldstandard.wizards.selectjobs.SelectJobsWizardDescriptor;
import gnu.trove.map.TLongObjectMap;
import java.awt.Dialog;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
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
@ServiceProvider(service = PipelineComparisonI.class)
public class GSCVennChartViewer extends EvaluationViewerI implements PipelineComparisonI {

    private VennChart venn = null;

    private SeqRunI currentSeqrun;
    private List<JobI> currentJobs;

    List<DistributionI<Long>> dists;

    private GSCVennChartCustomizer cust = null;

    private TLongObjectMap<String> onlyGSID;
    private TLongObjectMap<String> onlySampleID;
    private TLongObjectMap<String> gsAndSampleID;
    
    private String jobAName;
    private String jobBName;

    @Override
    public JComponent getComponent() {
        if (dists == null) {
            return null;
        }
        if (venn == null) {
            evaluate();
        }

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

    @SuppressWarnings("unchecked")
    @Override
    public void evaluate() {
        DistributionI<Long> distA = dists.get(0);
        DistributionI<Long> distB = dists.get(1);
        
        Collection<AttributeI> both = CollectionUtils.intersection(distA.keySet(), distB.keySet());
        Collection<AttributeI> onlyA = CollectionUtils.subtract(distA.keySet(), distB.keySet());
        Collection<AttributeI> onlyB = CollectionUtils.subtract(distB.keySet(), distA.keySet());
        
        
//        List<Integer> sampleIndizes = new LinkedList<>();
//        for (int i = 0; i < sampleLeaves.size(); i++) {
//            sampleIndizes.add(i);
//        }
//
//        ProgressHandle p = ProgressHandle.createHandle("create venn chart");
//        p.start(gsLeaves.size() + sampleLeaves.size());
//        int progress = 0;
//
//        onlyGSID = new TLongObjectHashMap<>((int) currentSeqrun.getNumSequences() / 6);
//        onlySampleID = new TLongObjectHashMap<>((int) currentSeqrun.getNumSequences() / 6);
//        gsAndSampleID = new TLongObjectHashMap<>((int) currentSeqrun.getNumSequences() / 3 * 2);
//        try {
//            for (NodeI<Long> gsNode : gsLeaves) {
//                NodeI<Long> sampleNode = null;
//                for (Integer i : sampleIndizes) {
//                    if (sampleLeaves.get(i).getAttribute().equals(gsNode.getAttribute())) {
//                        sampleNode = sampleLeaves.get(i);
//                        sampleIndizes.remove(i);
//                        break;
//                    }
//                }
//                if (sampleNode == null) {
//                    String attr = gsNode.getAttribute().getValue();
//                    for (long l : NodeUtils.getSeqIDs(gsNode)) {
//                        onlyGSID.put(l, attr);
//                    }
//                    continue;
//                }
//
//                List<Long> gsIDs = NodeUtils.getSeqIDs(gsNode);
//                List<Long> sampleIDs = NodeUtils.getSeqIDs(sampleNode);
//
//                List<Long> intersect = new ArrayList<>(CollectionUtils.intersection(gsIDs, sampleIDs));
//                List<Long> oGS = new ArrayList<>(CollectionUtils.subtract(gsIDs, sampleIDs));
//                List<Long> oSample = new ArrayList<>(CollectionUtils.subtract(sampleIDs, gsIDs));
//
//                if (!intersect.isEmpty()) {
//                    String attr = gsNode.getAttribute().getValue();
//                    for (long l : intersect) {
//                        gsAndSampleID.put(l, attr);
//                    }
//                }
//                if (!oGS.isEmpty()) {
//                    String attr = gsNode.getAttribute().getValue();
//                    for (long l : oGS) {
//                        onlyGSID.put(l, attr);
//                    }
//                }
//                if (!oSample.isEmpty()) {
//                    String attr = sampleNode.getAttribute().getValue();
//                    for (long l : oSample) {
//                        onlySampleID.put(l, attr);
//                    }
//                }
//                p.progress(progress++);
//            }
//
//            if (!sampleIndizes.isEmpty()) {
//                for (Integer i : sampleIndizes) {
//                    NodeI<Long> node = sampleLeaves.get(i);
//                    List<Long> seqIDs = NodeUtils.getSeqIDs(node);
//                    for (long l : seqIDs) {
//                        String attr = node.getAttribute().getValue();
//                        onlyGSID.put(l, attr);
//                    }
//                    p.progress(progress++);
//                }
//            }
//
//        } catch (MGXException ex) {
//            EvalExceptions.printStackTrace(Exceptions.attachMessage(ex, "Cannot download sequence ids for NodeI instance"));
//            venn = null;
//            p.finish();
//            return;
//        }
//
//        int a = onlyGSID.size();
//        int b = onlySampleID.size();
//        int ab = gsAndSampleID.size();

        try {
            venn = VennChart.get2Venn(onlyA.size(), onlyB.size(), both.size(), jobAName, jobBName);
        } catch (IOException ex) {
            EvalExceptions.printStackTrace(ex);
            venn = null;
        } finally {
//            p.finish();
        };
    }

    @Override
    public JComponent getCustomizer() {
        if (cust == null) {
            cust = new GSCVennChartCustomizer();
        }
        return cust;
    }

    @Override
    public void selectJobs(SeqRunI seqrun) {
        currentSeqrun = seqrun;
        try {
            SelectJobsWizardDescriptor jobWizard = new SelectJobsWizardDescriptor(seqrun, false, 2, true);
            Dialog dialog = DialogDisplayer.getDefault().createDialog(jobWizard);
            dialog.setVisible(true);
            dialog.toFront();
            boolean cancelled = jobWizard.getValue() != WizardDescriptor.FINISH_OPTION;
            if (!cancelled) {
                venn = null;
                currentJobs = jobWizard.getJobs();                
                AttributeTypeI attrType = jobWizard.getAttributeType();
                jobAName = JobUtils.jobToString(currentJobs.get(0));
                jobBName = JobUtils.jobToString(currentJobs.get(1));
                dists = new ArrayList<>();
                dists.add(seqrun.getMaster().Attribute().getDistribution(attrType, currentJobs.get(0)));
                dists.add(seqrun.getMaster().Attribute().getDistribution(attrType, currentJobs.get(1)));
            }
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
            venn = null;
            dists = null;
            currentJobs = null;
        }
    }

}
