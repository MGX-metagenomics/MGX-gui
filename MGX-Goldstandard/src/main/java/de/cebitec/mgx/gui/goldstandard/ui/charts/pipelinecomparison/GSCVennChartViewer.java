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

    @Override
    public boolean canHandle(AttributeTypeI valueType) {
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void evaluate() {
        if (dists.size() == 2) {
            DistributionI<Long> distA = dists.get(0);
            DistributionI<Long> distB = dists.get(1);

            String jobAName = JobUtils.jobToString(currentJobs.get(0));
            String jobBName = JobUtils.jobToString(currentJobs.get(1));

            Collection<AttributeI> both = CollectionUtils.intersection(distA.keySet(), distB.keySet());
            Collection<AttributeI> onlyA = CollectionUtils.subtract(distA.keySet(), distB.keySet());
            Collection<AttributeI> onlyB = CollectionUtils.subtract(distB.keySet(), distA.keySet());

            try {
                venn = VennChart.get2Venn(onlyA.size(), onlyB.size(), both.size(), jobAName, jobBName);
            } catch (IOException ex) {
                EvalExceptions.printStackTrace(ex);
                venn = null;
            } finally {
//            p.finish();
            }
        } else if (dists.size() == 3) {
            DistributionI<Long> distA = dists.get(0);
            DistributionI<Long> distB = dists.get(1);
            DistributionI<Long> distC = dists.get(2);

            String jobAName = JobUtils.jobToString(currentJobs.get(0));
            String jobBName = JobUtils.jobToString(currentJobs.get(1));
            String jobCName = JobUtils.jobToString(currentJobs.get(2));

            Collection<AttributeI> abc = CollectionUtils.intersection(CollectionUtils.intersection(distA.keySet(), distC.keySet()), distC.keySet());
            Collection<AttributeI> ab = CollectionUtils.subtract(CollectionUtils.intersection(distA.keySet(), distB.keySet()), abc);
            Collection<AttributeI> bc = CollectionUtils.subtract(CollectionUtils.intersection(distB.keySet(), distC.keySet()), abc);
            Collection<AttributeI> ac = CollectionUtils.subtract(CollectionUtils.intersection(distA.keySet(), distC.keySet()), abc);
            Collection<AttributeI> onlyA = CollectionUtils.subtract(CollectionUtils.subtract(distA.keySet(), distB.keySet()), distC.keySet());
            Collection<AttributeI> onlyB = CollectionUtils.subtract(CollectionUtils.subtract(distB.keySet(), distA.keySet()), distC.keySet());
            Collection<AttributeI> onlyC = CollectionUtils.subtract(CollectionUtils.subtract(distC.keySet(), distA.keySet()), distB.keySet());

            try {
                venn = VennChart.get3Venn(onlyA.size(), onlyB.size(), onlyC.size(), ab.size(), ac.size(), bc.size(), abc.size(), jobAName, jobBName, jobCName);
            } catch (IOException ex) {
                EvalExceptions.printStackTrace(ex);
                venn = null;
            } finally {
//            p.finish();
            }
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
    public void selectJobs(SeqRunI seqrun) {
        try {
            SelectJobsWizardDescriptor jobWizard = new SelectJobsWizardDescriptor(seqrun, false, 3, false);
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
                    dists.add(seqrun.getMaster().Attribute().getDistribution(attrType, job));
                }
            }
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
            venn = null;
            dists = null;
            currentJobs = null;
        }
    }

}
