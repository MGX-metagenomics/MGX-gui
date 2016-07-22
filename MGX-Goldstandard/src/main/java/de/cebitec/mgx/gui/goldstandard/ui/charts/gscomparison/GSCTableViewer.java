package de.cebitec.mgx.gui.goldstandard.ui.charts.gscomparison;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.groups.ImageExporterI;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.tree.NodeI;
import de.cebitec.mgx.api.model.tree.TreeI;
import de.cebitec.mgx.gui.goldstandard.ui.charts.EvaluationViewerI;
import de.cebitec.mgx.gui.goldstandard.util.NodeUtils;
import de.cebitec.mgx.gui.goldstandard.wizards.selectjobs.SelectSingleJobWithGSWizardDescriptor;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
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
@ServiceProvider(service = GSComparisonI.class)
public class GSCTableViewer extends EvaluationViewerI<TreeI<Long>> implements GSComparisonI {

    private static final int PAGESIZE = 1_000;

    private SeqRunI currentSeqrun;
    private JobI currentJob;

    private JXTable table;
    private JScrollPane pane;

    private GSCTableViewCustomizer cust = null;

    private TLongObjectMap<String[]> seqToAttribute;

    @Override
    public JComponent getComponent() {
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

        List<NodeI<Long>> gsLeaves = new ArrayList<>(trees.get(0).getLeaves());
        List<NodeI<Long>> sampleLeaves = new ArrayList<>(trees.get(1).getLeaves());

        ProgressHandle p = ProgressHandle.createHandle("create table view");
        p.start(gsLeaves.size() + sampleLeaves.size() + 1);
        int progress = 0;

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
            Exceptions.printStackTrace(Exceptions.attachMessage(ex, "Cannot download sequence ids for NodeI instance"));
        }
//        }

        gsLeaves = null;
        sampleLeaves = null;

        String[] columns = new String[]{
            "SequenceID", "in Goldstandard", "in both", "in Sample",};

        GSTableViewerPagingModel model = new GSTableViewerPagingModel(seqToAttribute, columns, currentSeqrun.getMaster(), PAGESIZE);
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
        table.setSortOrder("SequenceID", SortOrder.ASCENDING);
        pane = GSTableViewerPagingModel.createPagingScrollPaneForTable(table);
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
    public void start(SeqRunI seqrun) {
        currentSeqrun = seqrun;
        try {
            SelectSingleJobWithGSWizardDescriptor jobWizard = new SelectSingleJobWithGSWizardDescriptor(seqrun);
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
