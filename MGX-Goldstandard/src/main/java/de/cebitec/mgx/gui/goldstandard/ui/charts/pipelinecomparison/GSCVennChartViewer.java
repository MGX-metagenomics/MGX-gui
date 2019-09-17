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
import de.cebitec.mgx.gui.goldstandard.util.EvalExceptions;
import de.cebitec.mgx.gui.goldstandard.util.JobUtils;
import de.cebitec.mgx.gui.goldstandard.wizards.selectjobs.SelectJobsWizardDescriptor;
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
    private List<JobI> currentJobs;
    private List<DistributionI<Long>> dists;
    private GSCVennChartCustomizer cust = null;

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

    @SuppressWarnings("unchecked")
    @Override
    public void evaluate() {
        try {
            switch (dists.size()) {
                case 2:
                    {
                        DistributionI<Long> distA = dists.get(0);
                        DistributionI<Long> distB = dists.get(1);
                        String jobAName = JobUtils.jobToString(currentJobs.get(0));
                        String jobBName = JobUtils.jobToString(currentJobs.get(1));
                        List<Collection<AttributeI>> vennCollections = calcVenn2Collections(distA, distB);
                        int onlyA = vennCollections.get(2).size();
                        int onlyB = vennCollections.get(1).size();
                        int ab = vennCollections.get(3).size();
                        venn = VennChart.get2Venn(onlyA, onlyB, ab, jobAName, jobBName);
                        break;
                    }
                case 3:
                    {
                        DistributionI<Long> distA = dists.get(0);
                        DistributionI<Long> distB = dists.get(1);
                        DistributionI<Long> distC = dists.get(2);
                        String jobAName = JobUtils.jobToString(currentJobs.get(0));
                        String jobBName = JobUtils.jobToString(currentJobs.get(1));
                        String jobCName = JobUtils.jobToString(currentJobs.get(2));
                        List<Collection<AttributeI>> vennCollections = calcVenn3Collections(distA, distB, distC);
                        int onlyA = vennCollections.get(4).size();
                        int onlyB = vennCollections.get(2).size();
                        int onlyC = vennCollections.get(1).size();
                        int ab = vennCollections.get(6).size();
                        int bc = vennCollections.get(3).size();
                        int ac = vennCollections.get(5).size();
                        int abc = vennCollections.get(7).size();
                        venn = VennChart.get3Venn(onlyA, onlyB, onlyC, ab, ac, bc, abc, jobAName, jobBName, jobCName);
                        break;
                    }
                case 4:
                    {
                        DistributionI<Long> distA = dists.get(0);
                        DistributionI<Long> distB = dists.get(1);
                        DistributionI<Long> distC = dists.get(2);
                        DistributionI<Long> distD = dists.get(3);
                        String jobAName = JobUtils.jobToString(currentJobs.get(0));
                        String jobBName = JobUtils.jobToString(currentJobs.get(1));
                        String jobCName = JobUtils.jobToString(currentJobs.get(2));
                        String jobDName = JobUtils.jobToString(currentJobs.get(3));
                        List<Collection<AttributeI>> collections = calcVenn4Collections(distA, distB, distC, distD);
                        List<Long> vennValues = new ArrayList<>(collections.size());
                        for (Collection<AttributeI> col : collections){
                            if (col != null)
                                vennValues.add(Long.valueOf(col.size()));
                            else
                                vennValues.add(null);
                        }       venn = VennChart.get4Venn(vennValues, jobAName, jobBName, jobCName, jobDName);
                        break;
                    }
                default:
                    break;
            }
        } catch (IOException ex) {
            EvalExceptions.printStackTrace(ex);
            tidyUp();
        } finally {
//            p.finish();
        }
    }

    @Override
    public JComponent getCustomizer() {
        if (cust == null) {
            cust = new GSCVennChartCustomizer();
        }
        return cust;
    }

    @Override
    public void dispose() {
        super.dispose();
        cust.dispose();
        cust = null;
        tidyUp();
    }

    private void tidyUp() {
        venn = null;
        currentJobs = null;
        dists = null;
    }

    @Override
    public void selectJobs(SeqRunI seqrun) {
        try {
            SelectJobsWizardDescriptor jobWizard = new SelectJobsWizardDescriptor(seqrun, false, 4, false);
            Dialog dialog = DialogDisplayer.getDefault().createDialog(jobWizard);
            dialog.setVisible(true);
            dialog.toFront();
            boolean cancelled = jobWizard.getValue() != WizardDescriptor.FINISH_OPTION;
            if (!cancelled) {
                venn = null;
                currentJobs = jobWizard.getJobs();
                AttributeTypeI attrType = jobWizard.getAttributeType();
                dists = new ArrayList<>();
                for (JobI job : currentJobs) {
                    dists.add(seqrun.getMaster().Attribute().getDistribution(attrType, job, seqrun));
                }
            }
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
            tidyUp();
        }
    }

    /**
     *
     * @param distA
     * @param distB
     * @return List of collections of attributes in each Venn section. Sorted by
     * binary numbering. AB = 11 == 3, A = 10 == 2, B = 01 == 1.
     */
    public List<Collection<AttributeI>> calcVenn2Collections(DistributionI<Long> distA, DistributionI<Long> distB) {
        Collection<AttributeI> both = CollectionUtils.intersection(distA.keySet(), distB.keySet());
        Collection<AttributeI> onlyA = CollectionUtils.subtract(distA.keySet(), distB.keySet());
        Collection<AttributeI> onlyB = CollectionUtils.subtract(distB.keySet(), distA.keySet());

        List<Collection<AttributeI>> results = new ArrayList<>(4);
        results.add(null);
        results.add(onlyB);
        results.add(onlyA);
        results.add(both);

        return results;
    }

    /**
     *
     * @param distA
     * @param distB
     * @param distC
     * @return List of collections of attributes in each Venn section. Sorted by
     * binary numbering. ABC = 111 == 7, A = 100 == 4, BC = 011 == 3, ....
     */
    public List<Collection<AttributeI>> calcVenn3Collections(DistributionI<Long> distA, DistributionI<Long> distB, DistributionI<Long> distC) {
        Collection<AttributeI> abc = CollectionUtils.intersection(CollectionUtils.intersection(distA.keySet(), distC.keySet()), distC.keySet());
        Collection<AttributeI> ab = CollectionUtils.subtract(CollectionUtils.intersection(distA.keySet(), distB.keySet()), abc);
        Collection<AttributeI> bc = CollectionUtils.subtract(CollectionUtils.intersection(distB.keySet(), distC.keySet()), abc);
        Collection<AttributeI> ac = CollectionUtils.subtract(CollectionUtils.intersection(distA.keySet(), distC.keySet()), abc);
        Collection<AttributeI> onlyA = CollectionUtils.subtract(CollectionUtils.subtract(distA.keySet(), ab), ac);
        Collection<AttributeI> onlyB = CollectionUtils.subtract(CollectionUtils.subtract(distB.keySet(), ab), bc);
        Collection<AttributeI> onlyC = CollectionUtils.subtract(CollectionUtils.subtract(distC.keySet(), ac), bc);

        List<Collection<AttributeI>> results = new ArrayList<>(8);
        results.add(null);
        results.add(onlyC);
        results.add(onlyB);
        results.add(bc);
        results.add(onlyA);
        results.add(ac);
        results.add(ab);
        results.add(abc);

        return results;
    }
    
    /**
     *
     * @param distA
     * @param distB
     * @param distC
     * @param distD
     * @return List of collections of attributes in each Venn section. Sorted by
     * binary numbering. ABCD = 1111 == 15, A = 1000 == 8, BC = 0110 == 6, ....
     */
    public List<Collection<AttributeI>> calcVenn4Collections(DistributionI<Long> distA, DistributionI<Long> distB, DistributionI<Long> distC, DistributionI<Long> distD) {        
        Collection<AttributeI> uCD = CollectionUtils.union(distC.keySet(), distD.keySet());
        Collection<AttributeI> uBD = CollectionUtils.union(distB.keySet(), distD.keySet());
        Collection<AttributeI> uBC = CollectionUtils.union(distB.keySet(), distC.keySet());
        
        Collection<AttributeI> iCD = CollectionUtils.intersection(distC.keySet(), distD.keySet());
        Collection<AttributeI> iBD = CollectionUtils.intersection(distB.keySet(), distD.keySet());
        Collection<AttributeI> iBC = CollectionUtils.intersection(distB.keySet(), distC.keySet());
        Collection<AttributeI> iAB = CollectionUtils.intersection(distA.keySet(), distB.keySet());
        
        Collection<AttributeI> onlyA = CollectionUtils.subtract(distA.keySet(), CollectionUtils.union(distB.keySet(), uCD));
        Collection<AttributeI> onlyB = CollectionUtils.subtract(distB.keySet(), CollectionUtils.union(distA.keySet(), uCD));
        Collection<AttributeI> onlyC = CollectionUtils.subtract(distC.keySet(), CollectionUtils.union(distA.keySet(), uBD));
        Collection<AttributeI> onlyD = CollectionUtils.subtract(distD.keySet(), CollectionUtils.union(distA.keySet(), uBC));
        Collection<AttributeI> ab = CollectionUtils.subtract(iAB, uCD);
        Collection<AttributeI> ac = CollectionUtils.subtract(CollectionUtils.intersection(distA.keySet(), distC.keySet()), uBD);
        Collection<AttributeI> ad = CollectionUtils.subtract(CollectionUtils.intersection(distA.keySet(), distD.keySet()), uBC);
        Collection<AttributeI> bc = CollectionUtils.subtract(iBC, CollectionUtils.union(distA.keySet(), distD.keySet()));
        Collection<AttributeI> bd = CollectionUtils.subtract(iBD, CollectionUtils.union(distA.keySet(), distC.keySet()));
        Collection<AttributeI> cd = CollectionUtils.subtract(iCD, CollectionUtils.union(distA.keySet(), distB.keySet()));
        Collection<AttributeI> abc = CollectionUtils.subtract(CollectionUtils.intersection(distA.keySet(), iBC), distD.keySet());
        Collection<AttributeI> abd = CollectionUtils.subtract(CollectionUtils.intersection(distA.keySet(), iBD), distC.keySet());
        Collection<AttributeI> acd = CollectionUtils.subtract(CollectionUtils.intersection(distA.keySet(), iCD), distB.keySet());
        Collection<AttributeI> bcd = CollectionUtils.subtract(CollectionUtils.intersection(distB.keySet(), iCD), distA.keySet());
        Collection<AttributeI> abcd = CollectionUtils.intersection(iAB, iCD);
        
        List<Collection<AttributeI>> results = new ArrayList<>(16);
        results.add(null);
        results.add(onlyD);
        results.add(onlyC);
        results.add(cd);
        results.add(onlyB);
        results.add(bd);
        results.add(bc);
        results.add(bcd);
        results.add(onlyA);
        results.add(ad);
        results.add(ac);
        results.add(acd);
        results.add(ab);
        results.add(abd);
        results.add(abc);
        results.add(abcd);        

        return results;
    }

}
