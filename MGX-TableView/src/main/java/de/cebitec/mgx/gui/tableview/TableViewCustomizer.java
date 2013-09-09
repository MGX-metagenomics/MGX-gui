package de.cebitec.mgx.gui.tableview;

import de.cebitec.mgx.gui.attributevisualization.filter.ExcludeFilter;
import de.cebitec.mgx.gui.attributevisualization.filter.VisFilterI;
import de.cebitec.mgx.gui.datamodel.Attribute;
import de.cebitec.mgx.gui.datamodel.AttributeType;
import de.cebitec.mgx.gui.datamodel.misc.Distribution;
import de.cebitec.mgx.gui.datamodel.misc.Pair;
import de.cebitec.mgx.gui.datamodel.tree.Node;
import de.cebitec.mgx.gui.datamodel.tree.Tree;
import de.cebitec.mgx.gui.datamodel.tree.TreeFactory;
import de.cebitec.mgx.gui.groups.VGroupManager;
import de.cebitec.mgx.gui.groups.VisualizationGroup;
import de.cebitec.mgx.gui.swingutils.JCheckBoxList;
import de.cebitec.mgx.gui.swingutils.NonEDT;
import de.cebitec.mgx.gui.util.Reference;
import de.cebitec.mgx.gui.attributevisualization.sorter.SortByNumberOfValues;
import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.SwingWorker;
import javax.swing.event.ListDataEvent;
import javax.swing.table.AbstractTableModel;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.NbPreferences;

/**
 *
 * @author sjaenick
 */
public class TableViewCustomizer extends javax.swing.JPanel implements VisFilterI<Distribution> {

    /**
     * Creates new form TableViewCustomizer
     */
    public TableViewCustomizer() {
        initComponents();
        AttrTypeModel atModel = (AttrTypeModel) attrTypeFilter.getModel();
        attrTypeFilter.addItemListener(atModel);
    }
    private AttributeType at = null;
    private AbstractTableModel model = null;
    private JCheckBoxList<Attribute> filterList = null;
    private final Map<AttributeType, Set<Attribute>> parents = new HashMap<>();

    public void setModel(AbstractTableModel m) {
        model = m;
        export.setEnabled(model != null);
    }

    public boolean includeHeaders() {
        return includeHeaders.isSelected();
    }

    public void setAttributeType(final AttributeType aType) {
        if (aType.equals(at)) {
            return;
        }
        at = aType;

        AttrTypeModel atModel = (AttrTypeModel) attrTypeFilter.getModel();
        atModel.clear();
        attrTypeFilter.setEnabled(false);
        attrTypeFilter.setSelectedIndex(-1);
        if (filterList != null) {
            filterList.clear();
            listholder.remove(filterList);
            filterList = null;
        }

        if (at.getStructure() == AttributeType.STRUCTURE_HIERARCHICAL) {


            SwingWorker<List<AttributeType>, Void> sw = new SwingWorker<List<AttributeType>, Void>() {
                @Override
                protected List<AttributeType> doInBackground() throws Exception {
                    parents.clear();
                    for (Pair<VisualizationGroup, Tree<Long>> p : VGroupManager.getInstance().getHierarchies()) {
                        Tree<Long> tree = p.getSecond();
                        for (Node<Long> node : tree.getNodes()) {

                            // collect attributes and attributetypes for same layer and parent nodes only
                            if (at.equals(node.getAttribute().getAttributeType())) {
                                Node<Long> cur = node;
                                while (!cur.isRoot()) {
                                    Attribute attr = cur.getAttribute();
                                    AttributeType type = attr.getAttributeType();

                                    if (!parents.containsKey(type)) {
                                        parents.put(type, new TreeSet<Attribute>());
                                    }
                                    parents.get(type).add(attr);
                                    cur = cur.getParent();
                                }
                            }
                        }
                    }
                    SortByNumberOfValues<Attribute> sorter = new SortByNumberOfValues<>();
                    sorter.setMap(parents);
                    List<AttributeType> typesOrdered = new ArrayList<>(parents.size());
                    typesOrdered.addAll(parents.keySet());
                    Collections.sort(typesOrdered, sorter);
                    return typesOrdered;
                }
            };

            sw.execute();
            try {
                List<AttributeType> typesOrdered = sw.get();

                if (typesOrdered.size() > 0) {
                    atModel.setData(typesOrdered);
                    attrTypeFilter.setEnabled(true);
                    attrTypeFilter.setSelectedIndex(0);
                }
            } catch (InterruptedException | ExecutionException ex) {
                Exceptions.printStackTrace(ex);
            }

        }
    }

    @Override
    public List<Pair<VisualizationGroup, Distribution>> filter(List<Pair<VisualizationGroup, Distribution>> dists) {

        Set<Attribute> filterEntries = getFilterEntries();

        if (filterEntries.isEmpty()) {
            return dists;
        }

        final Reference<Tree<Long>> result = new Reference<>();
        NonEDT.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                List<Pair<VisualizationGroup, Tree<Long>>> trees = VGroupManager.getInstance().getHierarchies();
                List<Tree<Long>> tmp = new LinkedList<>();
                for (Pair<VisualizationGroup, Tree<Long>> p : trees) {
                    tmp.add(p.getSecond());
                }
                Tree<Long> merged = TreeFactory.mergeTrees(tmp);
                result.setValue(merged);
            }
        });

        Tree<Long> merged = result.getValue();
        Set<Attribute> blackList = createBlackList(merged, filterEntries);

        ExcludeFilter ef = new ExcludeFilter(blackList);
        dists = ef.filter(dists);

        return dists;
    }

    public Set<Attribute> createBlackList(Tree<Long> tree, Set<Attribute> filterEntries) {
        Set<Attribute> blackList = new HashSet<>();

        for (Node<Long> node : tree.getNodes()) {
            if (at.equals(node.getAttribute().getAttributeType())) {
                for (Node<Long> pathNode : node.getPath()) {
                    if (filterEntries.contains(pathNode.getAttribute())) {
                        blackList.add(node.getAttribute());
                        break;
                    }
                }
            }
        }
        return blackList;
    }

    public Set<Attribute> getFilterEntries() {
        return filterList == null ? Collections.EMPTY_SET : filterList.getDeselectedEntries();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        export = new javax.swing.JButton();
        includeHeaders = new javax.swing.JCheckBox();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        listholder = new javax.swing.JPanel();
        attrTypeFilter = new javax.swing.JComboBox<AttributeType>();

        org.openide.awt.Mnemonics.setLocalizedText(export, org.openide.util.NbBundle.getMessage(TableViewCustomizer.class, "TableViewCustomizer.export.text")); // NOI18N
        export.setEnabled(false);
        export.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportActionPerformed(evt);
            }
        });

        includeHeaders.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        includeHeaders.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(includeHeaders, org.openide.util.NbBundle.getMessage(TableViewCustomizer.class, "TableViewCustomizer.includeHeaders.text")); // NOI18N

        jLabel4.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(TableViewCustomizer.class, "TableViewCustomizer.jLabel4.text")); // NOI18N

        listholder.setLayout(new java.awt.BorderLayout());
        jScrollPane1.setViewportView(listholder);

        attrTypeFilter.setModel(new AttrTypeModel());
        attrTypeFilter.setEnabled(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(export)
                            .addComponent(includeHeaders)
                            .addComponent(jLabel4))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(attrTypeFilter, 0, 153, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(includeHeaders)
                .addGap(29, 29, 29)
                .addComponent(jLabel4)
                .addGap(4, 4, 4)
                .addComponent(attrTypeFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 142, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(export)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void exportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportActionPerformed
        assert model != null;
        final JFileChooser fc = new JFileChooser();

        String last = NbPreferences.forModule(JFileChooser.class).get("lastDirectory", null);
        if (last != null) {
            File f = new File(last);
            if (f.exists() && f.isDirectory()) {
                fc.setCurrentDirectory(f);
            }
        }

        File f = new File("MGX_export.tsv");
        fc.setSelectedFile(f);
        fc.setVisible(true);

        fc.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("SelectedFileChangedProperty".equals(evt.getPropertyName())) {
                    NbPreferences.forModule(JFileChooser.class).put("lastDirectory", fc.getCurrentDirectory().getAbsolutePath().toString());
                }
            }
        });


        if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {

            f = fc.getSelectedFile();
            try {
                if (f.exists()) {
                    throw new IOException(f.getName() + " already exists.");
                }
                BufferedWriter w = new BufferedWriter(new FileWriter(f));

                if (includeHeaders()) {
                    for (int col = 0; col < model.getColumnCount() - 1; col++) {
                        w.write(model.getColumnName(col));
                        w.write("\t");
                    }
                    w.write(model.getColumnName(model.getColumnCount() - 1));
                    w.write(System.lineSeparator());
                    w.write(System.lineSeparator());
                }


                // export data
                for (int row = 0; row < model.getRowCount(); row++) {
                    for (int col = 0; col <= model.getColumnCount() - 1; col++) {
                        Object val = model.getValueAt(row, col);
                        if (val != null) {
                            w.write(val.toString());
                        }
                        if (col <= model.getColumnCount() - 2 && model.getValueAt(row, col + 1) != null) {
                            w.write("\t");
                        }
                    }
//                    Object val = model.getValueAt(row, model.getColumnCount() - 1);
//                    val = val != null ? val : "";
                    //                  w.write(val.toString());
                    w.write(System.lineSeparator());
                }
                w.flush();
                w.close();

                // report success
                NotifyDescriptor nd = new NotifyDescriptor.Message("Data exported to " + f.getName());
                DialogDisplayer.getDefault().notify(nd);
            } catch (IOException ex) {
                // some error occured, notify user
                NotifyDescriptor nd = new NotifyDescriptor("Export failed: " + ex.getMessage(), "Error",
                        NotifyDescriptor.DEFAULT_OPTION, NotifyDescriptor.ERROR_MESSAGE, null, null);
                DialogDisplayer.getDefault().notify(nd);
            }
        }
    }//GEN-LAST:event_exportActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<AttributeType> attrTypeFilter;
    private javax.swing.JButton export;
    private javax.swing.JCheckBox includeHeaders;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel listholder;
    // End of variables declaration//GEN-END:variables

    private class AttrTypeModel extends AbstractListModel implements ComboBoxModel, ItemListener {

        private final List<AttributeType> data = new ArrayList<>();
        private int selectionIdx = -1;

        public void setData(final Collection<AttributeType> newData) {
            data.clear();
            data.addAll(newData);
            selectionIdx = -1;
            if (getSize() > 0) {
                setSelectedItem(data.get(0));
            }
            fireContentsChanged();
        }

        public void clear() {
            data.clear();
            selectionIdx = -1;
            fireContentsChanged();
        }

        @Override
        public void setSelectedItem(Object anItem) {
            if (data.contains(anItem)) {
                selectionIdx = data.indexOf(anItem);
                itemStateChanged(new ItemEvent(attrTypeFilter,
                        ItemEvent.ITEM_STATE_CHANGED,
                        anItem,
                        ItemEvent.SELECTED));
            }

        }

        @Override
        public Object getSelectedItem() {
            return selectionIdx != -1 ? data.get(selectionIdx) : null;
        }

        @Override
        public int getSize() {
            return data.size();
        }

        @Override
        public AttributeType getElementAt(int index) {
            return data.get(index);
        }

        @Override
        public void itemStateChanged(ItemEvent evt) {
            if (evt.getStateChange() == ItemEvent.SELECTED) {
                Object item = getSelectedItem();
                if (item != null) {
                    Set<Attribute> get = parents.get(item);
                    if (filterList == null) {
                        filterList = new JCheckBoxList<>();
                        listholder.add(filterList, BorderLayout.CENTER);
                    } else {
                        filterList.clear();
                    }

                    for (Attribute pAttr : get) {
                        filterList.addElement(pAttr);
                    }
                    filterList.selectAll();
                }
            }
        }

        protected void fireContentsChanged() {
            ListDataEvent e = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, -1, -1);
            fireContentsChanged(e, -1, -1);
        }
    }
}
