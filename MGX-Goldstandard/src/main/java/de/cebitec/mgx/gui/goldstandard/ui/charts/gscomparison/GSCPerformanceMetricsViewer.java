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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
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

    private final GSCPerformanceMetricsViewCustomizer cust = new GSCPerformanceMetricsViewCustomizer();

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
    public final ImageExporterI getImageExporter() {
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
            model = calcPerformanceMetrics(currentJobs, gsJob, currentSeqrun, attrType, p);
        } catch (MGXException ex) {
            p.finish();
            Exceptions.printStackTrace(ex);
            tidyUp();
            return;
        }
        if (model != null) {
            cust.setModel(model);

            table = new JXTable(model);
            table.setSortable(false);
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
    }

    @Override
    public JComponent getCustomizer() {
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
        if (cust != null) {
            cust.dispose();
        }
        table = null;
        currentJobs = null;
        gsJob = null;
        attrType = null;
    }

    public static GSCPerformanceMetricsTableModel calcPerformanceMetrics(List<JobI> jobs, JobI gsJob, SeqRunI run, final AttributeTypeI attributeType, final ProgressHandle p) throws MGXException {
        int progress = 0;
        p.start(jobs.size() + 2);
        p.progress(progress++);

        DistributionI<Long> gsDist = gsJob.getMaster().Attribute().getDistribution(attributeType, gsJob, run);

        //
        // seqId to attribute value
        // trove isn't thread-safe, so we need a lock to protect the map
        //
        final TLongObjectMap<String> goldstandard = new TLongObjectHashMap<>(jobs.size());
        final ReentrantLock lock = new ReentrantLock();

        final Semaphore rateLimit = new Semaphore(10);
        final CountDownLatch goldStandardLatch = new CountDownLatch(gsDist.size());

        for (AttributeI attr : gsDist.keySet()) {

            MGXPool.getInstance().submit(new Runnable() {
                @Override
                public void run() {
                    p.progress("Processing " + attr.getValue());
                    try {
                        rateLimit.acquireUninterruptibly();
                        Iterator<Long> it = gsDist.getMaster().Sequence().fetchSequenceIDs(attr);
                        lock.lock();
                        while (it.hasNext()) {
                            goldstandard.put(it.next(), attr.getValue());
                        }
                    } catch (MGXException ex) {
                        Exceptions.printStackTrace(ex);
                    } finally {
                        lock.unlock();
                        rateLimit.release();
                        goldStandardLatch.countDown();
                    }

                }

            });
        }

        try {
            goldStandardLatch.await();
        } catch (InterruptedException ex) {
            Exceptions.printStackTrace(ex);
            return null;
        }

        p.progress("Goldstandard data complete.", progress++);

        int idx = 0;
        PerformanceMetrics[] performanceMetrics = new PerformanceMetrics[jobs.size()];

        final AtomicBoolean hasError = new AtomicBoolean(false);
        final CountDownLatch allDone = new CountDownLatch(jobs.size());

        for (final JobI job : jobs) {
            final PerformanceMetrics pm = new PerformanceMetrics(goldstandard);
            performanceMetrics[idx++] = pm;

            MGXPool.getInstance().submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        rateLimit.acquireUninterruptibly();
                        pm.compute(p, job, attributeType, run);
                    } catch (MGXException ex) {
                        p.finish();
                        hasError.set(true);
                        Exceptions.printStackTrace(ex);
                    } finally {
                        rateLimit.release();
                        allDone.countDown();
                    }
                }
            });

            p.progress(progress++);
        }

        // await completion of all tasks
        try {
            allDone.await();
        } catch (InterruptedException ex) {
            p.finish();
            Exceptions.printStackTrace(ex);
        }

        if (hasError.get()) {
            return null;
        }

        // table header
        String[] columns = new String[jobs.size() + 1];
        columns[0] = attributeType.getName(); //"";
        for (idx = 1; idx < jobs.size() + 1; idx++) {
            columns[idx] = JobUtils.jobToString(jobs.get(idx - 1));
        }

        return new GSCPerformanceMetricsTableModel(columns, performanceMetrics);
    }
}
