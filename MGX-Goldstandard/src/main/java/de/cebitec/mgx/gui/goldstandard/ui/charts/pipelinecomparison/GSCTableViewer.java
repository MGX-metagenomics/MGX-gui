package de.cebitec.mgx.gui.goldstandard.ui.charts.pipelinecomparison;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.groups.ImageExporterI;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.tree.NodeI;
import de.cebitec.mgx.api.model.tree.TreeI;
import de.cebitec.mgx.gui.goldstandard.ui.charts.EvaluationViewerI;
import de.cebitec.mgx.gui.goldstandard.util.EvalExceptions;
import de.cebitec.mgx.gui.goldstandard.util.JobUtils;
import de.cebitec.mgx.gui.goldstandard.util.NodeUtils;
import de.cebitec.mgx.gui.goldstandard.wizards.selectjobs.SelectJobsWizardDescriptor;
import gnu.trove.list.TLongList;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import gnu.trove.procedure.TLongProcedure;
import java.awt.Dialog;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.SortOrder;
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
@ServiceProvider(service = PipelineComparisonI.class)
public class GSCTableViewer extends EvaluationViewerI implements PipelineComparisonI {

    private static final int PAGESIZE = 1_000;

    private SeqRunI currentSeqrun;
    private List<JobI> currentJobs;

    private JXTable table;
    private JScrollPane pane = null;

    private GSCTableViewCustomizer cust = null;

    private TLongObjectMap<String[]> seqToAttribute;
    private List<TreeI<Long>> treeList;
    private String jobAName;
    private String jobBName;

    @Override
    public JComponent getComponent() {
        if (treeList == null || currentSeqrun == null) {
            return null;
        }
        if (pane == null) {
            evaluate();
        }

        return pane;
    }

    @Override
    public ImageExporterI getImageExporter() {
        return null;
    }

    @Override
    public String getName() {
        return "Table view";
    }

    @Override
    public void evaluate() {
        if (treeList.size() != 2) {
            return;
        }

        List<NodeI<Long>> jobALeaves = new ArrayList<>(treeList.get(0).getLeaves());
        List<NodeI<Long>> jobBLeaves = new ArrayList<>(treeList.get(1).getLeaves());

        ProgressHandle p = ProgressHandle.createHandle("create table view");
        p.start(jobALeaves.size() + jobBLeaves.size() + 1);
        int progress = 0;

        seqToAttribute = new TLongObjectHashMap<>((int) currentSeqrun.getNumSequences());

        try {
            for (NodeI<Long> node : jobALeaves) {
                TLongList ids = NodeUtils.getSeqIDs(currentSeqrun.getMaster(), node);
                final String attr = node.getAttribute().getValue();
                ids.forEach(new TLongProcedure() {
                    @Override
                    public boolean execute(long id) {
                        seqToAttribute.put(id, new String[]{attr, ""});
                        return true;
                    }
                    
                });

                p.progress(progress++);
            }

            for (NodeI<Long> node : jobBLeaves) {
                TLongList ids = NodeUtils.getSeqIDs(currentSeqrun.getMaster(), node);
                final String attr = node.getAttribute().getValue();
                ids.forEach(new TLongProcedure() {
                    @Override
                    public boolean execute(long id) {
                        if (seqToAttribute.containsKey(id)) {
                            seqToAttribute.get(id)[1] = attr;
                        } else {
                            seqToAttribute.put(id, new String[]{"", attr});
                        }
                        return true;
                    }
                });
               
                p.progress(progress++);
            }

        } catch (MGXException ex) {
            EvalExceptions.printStackTrace(Exceptions.attachMessage(ex, "Cannot download sequence ids for NodeI instance"));
            p.finish();
            currentJobs = null;
            pane = null;
            table = null;
            return;
        }

//        jobALeaves = null;
//        jobBLeaves = null;
        String[] columns = new String[]{
            "Sequence", "in " + jobAName, "in both", "in " + jobBName};

        GSCTableViewerPagingModel model = new GSCTableViewerPagingModel(seqToAttribute, columns, currentSeqrun.getMaster(), PAGESIZE);
        cust.setModel(model); // for tsv export
        cust.setMaster(currentSeqrun.getMaster());
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
        table.setSortOrder("Sequence", SortOrder.ASCENDING);
        table.setSortable(false);
        pane = GSCTableViewerPagingModel.createPagingScrollPaneForTable(table);
        p.progress(progress++);
        p.finish();
    }

    @Override
    public JComponent getCustomizer() {
        if (cust == null) {
            cust = new GSCTableViewCustomizer();
        }

        return cust;
    }

    @Override
    public void selectJobs(final SeqRunI seqrun) {
        currentSeqrun = seqrun;
        try {
            final SelectJobsWizardDescriptor jobWizard = new SelectJobsWizardDescriptor(seqrun, true, 2, true);
            Dialog dialog = DialogDisplayer.getDefault().createDialog(jobWizard);
            dialog.setVisible(true);
            dialog.toFront();
            boolean cancelled = jobWizard.getValue() != WizardDescriptor.FINISH_OPTION;
            if (!cancelled) {
                pane = null;
                currentJobs = jobWizard.getJobs();
                AttributeTypeI attrType = jobWizard.getAttributeType();
                jobAName = JobUtils.jobToString(currentJobs.get(0));
                jobBName = JobUtils.jobToString(currentJobs.get(1));
                treeList = new ArrayList<>();
                treeList.add(seqrun.getMaster().Attribute().getHierarchy(attrType, currentJobs.get(0), seqrun));
                treeList.add(seqrun.getMaster().Attribute().getHierarchy(attrType, currentJobs.get(1), seqrun));
            } else {
                currentSeqrun = null;
            }
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        cust.dispose();
        cust = null;
        table = null;
        pane = null;
        seqToAttribute = null;
        treeList = null;
    }

}
