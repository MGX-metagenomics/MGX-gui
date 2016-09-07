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
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;
import java.awt.Dialog;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
        if (gsJob == null || attrType == null) {
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
    public String getName() {
        return "Performance metrics";
    }

    @Override
    public boolean canHandle(AttributeTypeI valueType) {
        return true;
    }

    @Override
    public void evaluate() {
        ProgressHandle p = ProgressHandle.createHandle("calculating...");
        p.start(currentJobs.size() + 2);
        int progress = 0;
        p.progress(progress++);

        long numSeqs = currentSeqrun.getNumSequences();
        TLongObjectMap<String> goldstandard = new TLongObjectHashMap<>(currentJobs.size());

        try {
            DistributionI<Long> dist = gsJob.getMaster().Attribute().getDistribution(attrType, gsJob);
            for (Map.Entry<AttributeI, Long> entry : dist.entrySet()) {
                Iterator<Long> it = dist.getMaster().Sequence().fetchSequenceIDs(entry.getKey());
                while (it.hasNext()) {
                    goldstandard.put(it.next(), entry.getKey().getValue());
                }
            }
            p.progress(progress++);
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
            table = null;
            return;
        }

        PerformanceMetrics[] performanceMetrics = new PerformanceMetrics[currentJobs.size()];

        try {
            int i = 0;
            for (JobI job : currentJobs) {
                PerformanceMetrics pm = new PerformanceMetrics();
                DistributionI<Long> dist = gsJob.getMaster().Attribute().getDistribution(attrType, job);
                int usedGsIds = 0;
                for (Map.Entry<AttributeI, Long> entry : dist.entrySet()) {
                    Iterator<Long> it = dist.getMaster().Sequence().fetchSequenceIDs(entry.getKey());
                    TLongSet ids = new TLongHashSet(entry.getValue().intValue());
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
                pm.add(0, 0, 0, numSeqs - pm.getFN() - pm.getFP() - pm.getTP());
                performanceMetrics[i++] = pm;
                p.progress(progress++);
            }
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
            table = null;
            return;
        }

        String[] columns = new String[currentJobs.size() + 1];
        columns[0] = "";
        for (int i = 1; i < currentJobs.size() + 1; i++) {
            columns[i] = JobUtils.jobToString(currentJobs.get(i - 1));
        }

        GSCPerformanceMetricsTableModel model = new GSCPerformanceMetricsTableModel(columns, performanceMetrics);
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
        p.progress(progress++);
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
            boolean cancelled = jobWizard.getValue() != WizardDescriptor.FINISH_OPTION;
            if (!cancelled) {
                table = null;
                currentJobs = jobWizard.getJobs();
                gsJob = jobWizard.getGoldstandard();
                attrType = jobWizard.getAttributeType();
            }
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
            cust = null;
            table = null;
            currentJobs = null;
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        cust.dispose();
        cust = null;
        table = null;
        currentJobs = null;
    }

}
