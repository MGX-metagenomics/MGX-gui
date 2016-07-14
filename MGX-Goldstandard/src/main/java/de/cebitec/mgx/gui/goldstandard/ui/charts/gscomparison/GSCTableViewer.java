package de.cebitec.mgx.gui.goldstandard.ui.charts.gscomparison;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.groups.ImageExporterI;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.SequenceI;
import de.cebitec.mgx.api.model.tree.NodeI;
import de.cebitec.mgx.api.model.tree.TreeI;
import de.cebitec.mgx.gui.goldstandard.ui.charts.EvaluationViewerI;
import de.cebitec.mgx.gui.goldstandard.util.NodeUtils;
import de.cebitec.mgx.gui.goldstandard.wizards.selectjobs.SelectSingleJobWizardDescriptor;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import java.awt.Dialog;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.SortOrder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import org.apache.commons.collections4.CollectionUtils;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author patrick
 */
@ServiceProvider(service = GSComparisonI.class)
public class GSCTableViewer extends EvaluationViewerI<TreeI<Long>> implements GSComparisonI {

    private static final int HEADER_CHUNK_SIZE = 10_000;

    private SeqRunI lastSeqrun;
    private SeqRunI currentSeqrun;
    private JobI currentJob;

    private JXTable table;

    private GSCViewCustomizer cust = null;

    private TLongObjectMap<AttributeInfos> onlyGSID;
    private TLongObjectMap<AttributeInfos> onlySampleID;
    private TLongObjectMap<AttributeInfos> gsAndSampleID;

    @Override
    public JComponent getComponent() {
        return table;
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
                        onlyGSID.put(l, new AttributeInfos(attr));
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
                        gsAndSampleID.put(l, new AttributeInfos(attr));
                    }
                }
                if (!oGS.isEmpty()) {
                    String attr = gsNode.getAttribute().getValue();
                    for (long l : oGS) {
                        onlyGSID.put(l, new AttributeInfos(attr));
                    }
                }
                if (!oSample.isEmpty()) {
                    String attr = sampleNode.getAttribute().getValue();
                    for (long l : oSample) {
                        onlySampleID.put(l, new AttributeInfos(attr));
                    }
                }
            }

            if (!sampleIndizes.isEmpty()) {
                for (Integer i : sampleIndizes) {
                    NodeI<Long> node = sampleLeaves.get(i);
                    List<Long> seqIDs = NodeUtils.getSeqIDs(node);
                    for (long l : seqIDs) {
                        String attr = node.getAttribute().getValue();
                        onlyGSID.put(l, new AttributeInfos(attr));
                    }
                }
            }

        } catch (MGXException ex) {
            Exceptions.printStackTrace(Exceptions.attachMessage(ex, "Cannot download sequence ids for NodeI instance"));
        }
//        }

        gsLeaves = null;
        sampleLeaves = null;
        sampleIndizes = null;

//            TLongObjectMap<String> headers;
//            try {
//                headers = getSequenceHeaders(gsAndSample.values(), onlyGS.values(), onlySample.values());
//            } catch (MGXException ex) {
//                Exceptions.printStackTrace(Exceptions.attachMessage(ex, "Cannot download sequences by ID"));
//                return;
//            }
        //onlySample not needed because gsAndSample + onlyGS must contain all sequence ids
//            int size = 0;
//            for (List<Long> ll : gsAndSample.values()) {
//                size += ll.size();
//            }
//            for (List<Long> ll : onlyGS.values()) {
//                size += ll.size();
//            }
//            long[] ids = new long[size];
        int size = onlyGSID.size() + gsAndSampleID.size();
        long[] ids = new long[size];

//            int i = 0;
//            for (List<Long> ll : gsAndSample.values()) {
//                for (Long l : ll) {
//                    ids[i++] = l;
//                }
//            }
//            for (List<Long> ll : onlyGS.values()) {
//                for (Long l : ll) {
//                    ids[i++] = l;
//                }
//            }
        System.arraycopy(onlyGSID.keys(), 0, ids, 0, onlyGSID.size());
        System.arraycopy(gsAndSampleID.keys(), 0, ids, onlyGSID.size(), gsAndSampleID.size());

        Arrays.sort(ids);

//            TLongLongMap idMap = new TLongLongHashMap(size);
//            int i = 0;
//            for (long id : ids) {
//                idMap.put(id, i++);
//            }
        int i = 0;
        for (long id : ids) {
            if (gsAndSampleID.containsKey(id)) {
                gsAndSampleID.get(id).seqIdRank = i++;
            } else {
                onlyGSID.get(id).seqIdRank = i++;
            }
        }

        String[] columns = new String[]{
            "SequenceID", "in Goldstandard", "in both", "in Sample",};
        DefaultTableModel model = new DefaultTableModel(columns, size) {

            @Override
            public Class<?> getColumnClass(int column) {
                return column == 0 ? Long.class : String.class;

            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

//            for (AttributeI attribute : gsAndSample.keySet()) {
//                for (Long id : gsAndSample.get(attribute)) {
//                    Object[] rowData = new Object[columns.length];
////                    rowData[0] = headers.get(id);
//                    rowData[0] = idMap.get(id);
//                    rowData[1] = "";
//                    rowData[2] = attribute.getValue();
//                    rowData[3] = "";
//                    model.addRow(rowData);
//                }
//            }
//            Map<Long, Object[]> merged = new HashMap<>();
//            for (AttributeI attribute : onlyGS.keySet()) {
//                for (Long id : onlyGS.get(attribute)) {
////                    merged.put(id, new Object[]{headers.get(id), attribute.getValue(), "", ""});
//                    merged.put(id, new Object[]{idMap.get(id), attribute.getValue(), "", ""});
//                }
//            }
//            for (AttributeI attribute : onlySample.keySet()) {
//                for (Long id : onlySample.get(attribute)) {
//                    if (merged.containsKey(id)) {
//                        Object[] row = merged.get(id);
//                        row[3] = attribute.getValue();
//                        merged.put(id, row);
//                    } else {
////                        merged.put(id, new Object[]{headers.get(id), "", "", attribute.getValue()});
//                        merged.put(id, new Object[]{idMap.get(id), "", "", attribute.getValue()});
//                    }
//                }
//            }
//            for (Long id : merged.keySet()) {
//                model.addRow(merged.get(id));
//            }
//        cust.setModel(model); // for tsv export

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
    }

    @Override
    public JComponent getCustomizer() {
        if (cust == null) {
            cust = new GSCViewCustomizer();
        }
//        cust.setAttributeType(getAttributeType());
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

    @SafeVarargs
    private final TLongObjectMap<String> getSequenceHeaders(Collection<List<Long>>... values) throws MGXException {
        int size = 0;
        for (Collection<List<Long>> coll : values) {
            for (List<Long> ll : coll) {
                {
                    size += ll.size();
                }
            }
        }
        TLongObjectMap<String> headers = new TLongObjectHashMap<>(size);
        for (Collection<List<Long>> idCollection : values) {
            for (List<Long> idLists : idCollection) {
                for (Long id : idLists) {
                    headers.put(id, null);
                }
            }
        }

        for (int j = 0; j < headers.size(); j += HEADER_CHUNK_SIZE) {
            long[] ids;
            if (j + HEADER_CHUNK_SIZE > headers.size()) {
                ids = Arrays.copyOfRange(headers.keys(), j, headers.size());
            } else {
                ids = Arrays.copyOfRange(headers.keys(), j, j + HEADER_CHUNK_SIZE);
            }

            long start = System.currentTimeMillis();
            Iterator<SequenceI> seqs = currentSeqrun.getMaster().Sequence().fetchByIds(ids);
            long stop = System.currentTimeMillis();
            System.out.println("Fetch: " + (stop - start) + " " + ids.length);

            long convertTime = 0;
            start = System.currentTimeMillis();
            while (seqs.hasNext()) {
                long startConvert = System.currentTimeMillis();
                SequenceI seq = seqs.next();
                convertTime += (System.currentTimeMillis() - startConvert);
                headers.put(seq.getId(), seq.getName());
            }
            System.out.println("conversion time " + convertTime);
            stop = System.currentTimeMillis();
            System.out.println("While: " + (stop - start));
        }
        return headers;

    }

    private static class AttributeInfos {

        public String name;
        public int seqIdRank;

        public AttributeInfos(String name, int seqIdRank) {
            this.name = name;
            this.seqIdRank = seqIdRank;
        }

        public AttributeInfos(String name) {
            this(name, -1);
        }
    }

}
