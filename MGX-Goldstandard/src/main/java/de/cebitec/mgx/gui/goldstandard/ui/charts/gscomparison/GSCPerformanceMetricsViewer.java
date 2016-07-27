package de.cebitec.mgx.gui.goldstandard.ui.charts.gscomparison;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.groups.ImageExporterI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.tree.NodeI;
import de.cebitec.mgx.api.model.tree.TreeI;
import de.cebitec.mgx.gui.goldstandard.ui.charts.EvaluationViewerI;
import de.cebitec.mgx.gui.goldstandard.util.EvalExceptions;
import de.cebitec.mgx.gui.goldstandard.util.NodeUtils;
import de.cebitec.mgx.gui.goldstandard.util.PerformanceMetrics;
import de.cebitec.mgx.gui.goldstandard.wizards.selectjobs.SelectSingleJobWithGSWizardDescriptor;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import java.awt.Dialog;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.SortOrder;
import javax.swing.table.DefaultTableModel;
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

    private TLongObjectMap<String[]> seqToAttribute;
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
        List<PerformanceMetrics> performanceMetrics = new ArrayList<>(currentJobs.size());
        ProgressHandle p = ProgressHandle.createHandle("calculating...");
        p.start(currentJobs.size());
        int progress = 0;
        
        try {
            DistributionI dist = gsJob.getMaster().Attribute().getDistribution(attrType, gsJob);
            
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
        }
        Set<AttributeI> goldstandard = new HashSet<>();
        
        for (JobI job : currentJobs){
            
        }
        
        List<NodeI<Long>> gsLeaves = new ArrayList<>(treeList.get(0).getLeaves());
        List<NodeI<Long>> sampleLeaves = new ArrayList<>(treeList.get(1).getLeaves());        

        seqToAttribute = new TLongObjectHashMap<>((int) currentSeqrun.getNumSequences());

        try {
            for (NodeI<Long> node : gsLeaves) {
                List<Long> ids = NodeUtils.getSeqIDs(node);
                String attr = node.getAttribute().getValue();
                for (long id : ids) {
                    seqToAttribute.put(id, new String[]{attr, ""});
                }
                p.progress(progress++);
            }

            for (NodeI<Long> node : sampleLeaves) {
                List<Long> ids = NodeUtils.getSeqIDs(node);
                String attr = node.getAttribute().getValue();
                for (long id : ids) {
                    if (seqToAttribute.containsKey(id)) {
                        seqToAttribute.get(id)[1] = attr;
                    } else {
                        seqToAttribute.put(id, new String[]{"", attr});
                    }
                }
                p.progress(progress++);
            }

        } catch (MGXException ex) {
            EvalExceptions.printStackTrace(Exceptions.attachMessage(ex, "Cannot download sequence ids for NodeI instance"));
            p.finish();
            currentJobs = null;
            table = null;
        }
//        }

        gsLeaves = null;
        sampleLeaves = null;

        String[] columns = new String[]{
            "SequenceID", "in Goldstandard", "in both", "in Sample",};

        DefaultTableModel model = new DefaultTableModel(columns, 0) {

            @Override
            public Class<?> getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return String.class;
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                        return Long.class;
                    default:
                        return Double.class;
                }
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

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
        table.setSortOrder("SequenceID", SortOrder.ASCENDING);
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
            seqToAttribute = null;
            treeList = null;
            currentJobs = null;
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        cust.dispose();
        cust = null;
        table = null;
        seqToAttribute = null;
        treeList = null;
        currentJobs = null;
    }

}
