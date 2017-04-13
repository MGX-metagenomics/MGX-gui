package de.cebitec.mgx.gui.goldstandard.ui.charts.gscomparison;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.groups.ImageExporterI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.gui.goldstandard.ui.charts.EvaluationViewerI;
import de.cebitec.mgx.gui.goldstandard.util.JobUtils;
import de.cebitec.mgx.gui.goldstandard.util.PerformanceMetrics;
import de.cebitec.mgx.gui.goldstandard.wizards.selectjobs.SelectSingleJobWithGSWizardDescriptor;
import de.cebitec.mgx.gui.pool.MGXPool;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import java.awt.Dialog;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import javax.swing.JComponent;
import javax.swing.table.TableColumn;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author patrick
 */
@ServiceProvider(service = GSComparisonI.class)
public class GSCPerformanceMetricsViewer extends EvaluationViewerI implements GSComparisonI {

    private SeqRunI currentSeqrun;
    private List<JobI> currentJobs;

    private JXTable table;

    private GSCPerformanceMetricsViewCustomizer cust = null;

    private JobI gsJob;
    private AttributeTypeI attrType;

    @Override
    public JComponent getComponent() {
        if (gsJob == null || attrType == null || currentSeqrun == null || currentJobs == null) {
            return null;
        }
        if (table == null) {
            evaluate();
        }
        return table;
    }

    @Override
    public ImageExporterI getImageExporter() {
        return null;
    }

    @Override
    public final String getName() {
        return "Performance metrics";
    }

    @Override
    public void evaluate() {
        ProgressHandle p = ProgressHandle.createHandle("Calculating metrics...");
        GSCPerformanceMetricsTableModel model;
        try {
            model = calcPerformanceMetrics(currentJobs, gsJob, attrType, currentSeqrun.getNumSequences(), p);
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
            tidyUp();
            return;
        }
        cust.setModel(model);

        table = new JXTable(model);
        table.setFillsViewportHeight(true);
        for (TableColumn tc : table.getColumns()) {
            if (0 != tc.getModelIndex()) {
                tc.setMinWidth(20);
                tc.setPreferredWidth(40);
                tc.setWidth(40);
            }
        }
        table.setHighlighters(new Highlighter[]{HighlighterFactory.createAlternateStriping()});
        p.finish();
    }

    @Override
    public JComponent getCustomizer() {
        if (cust == null) {
            cust = new GSCPerformanceMetricsViewCustomizer();
        }
        return cust;
    }

    @Override
    public void selectJobs(final SeqRunI seqrun) {
        currentSeqrun = seqrun;
        try {
            final SelectSingleJobWithGSWizardDescriptor jobWizard = new SelectSingleJobWithGSWizardDescriptor(seqrun, false, Integer.MAX_VALUE);
            Dialog dialog = DialogDisplayer.getDefault().createDialog(jobWizard);
            dialog.setVisible(true);
            dialog.toFront();
            if (jobWizard.getValue() == WizardDescriptor.FINISH_OPTION) {
                table = null;
                currentJobs = jobWizard.getJobs();
                gsJob = jobWizard.getGoldstandard();
                attrType = jobWizard.getAttributeType();
            }
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
            tidyUp();
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        tidyUp();
    }

    private void tidyUp() {
        cust.dispose();
        cust = null;
        table = null;
        currentJobs = null;
        gsJob = null;
        attrType = null;
    }

    public GSCPerformanceMetricsTableModel calcPerformanceMetrics(List<JobI> jobs, JobI gsJob, final AttributeTypeI attributeType, final long seqrunSequenceCount, ProgressHandle p) throws MGXException {
        int progress = 0;
        p.start(jobs.size() + 2);
        p.progress(progress++);

        DistributionI<Long> gsDist = gsJob.getMaster().Attribute().getDistribution(attributeType, gsJob);

        // seqId to attribute
        final TLongObjectMap<String> goldstandard = new TLongObjectHashMap<>(jobs.size());
        for (AttributeI attr : gsDist.keySet()) {
            Iterator<Long> it = gsDist.getMaster().Sequence().fetchSequenceIDs(attr);
            while (it.hasNext()) {
                goldstandard.put(it.next(), attr.getValue());
            }
        }
        p.progress(progress++);

        PerformanceMetrics[] performanceMetrics = new PerformanceMetrics[jobs.size()];
        final CountDownLatch allDone = new CountDownLatch(jobs.size());
        int i = 0;
        for (final JobI job : jobs) {
            final PerformanceMetrics pm = new PerformanceMetrics();
            performanceMetrics[i++] = pm;

            MGXPool.getInstance().submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        computeMetric(job, pm, attributeType, goldstandard, seqrunSequenceCount);
                    } catch (MGXException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                    allDone.countDown();
                }
            });

            p.progress(progress++);
        }

        // await task completion
        try {
            allDone.await();
        } catch (InterruptedException ex) {
            Exceptions.printStackTrace(ex);
        }

        String[] columns = new String[jobs.size() + 1];
        columns[0] = "";
        for (i = 1; i < jobs.size() + 1; i++) {
            columns[i] = JobUtils.jobToString(jobs.get(i - 1));
        }

        return new GSCPerformanceMetricsTableModel(columns, performanceMetrics);
    }

    private static void computeMetric(JobI job, PerformanceMetrics pm, AttributeTypeI attrType, TLongObjectMap<String> goldstandard, long totalSeqCount) throws MGXException {
        DistributionI<Long> dist = job.getMaster().Attribute().getDistribution(attrType, job);
        int usedGsIds = 0;
        for (Map.Entry<AttributeI, Long> entry : dist.entrySet()) {
            Iterator<Long> it = dist.getMaster().Sequence().fetchSequenceIDs(entry.getKey());
            while (it.hasNext()) {
                Long id = it.next();
                if (goldstandard.containsKey(id)) {
                    String gs = goldstandard.get(id);
                    usedGsIds++;
                    if (gs.equals(entry.getKey().getValue())) {
                        pm.incrementTP();
                    } else {
                        pm.incrementFN();
                    }
                } else {
                    pm.incrementFP();
                }
            }
        }
        pm.add(0, goldstandard.size() - usedGsIds, 0, 0);
        pm.add(0, 0, 0, totalSeqCount - pm.getFN() - pm.getFP() - pm.getTP());
    }
}
