package de.cebitec.mgx.gui.charts.basic.customizer;

import de.cebitec.mgx.gui.attributevisualization.filter.ExcludeFilter;
import de.cebitec.mgx.gui.swingutils.JCheckBoxList;
import de.cebitec.mgx.gui.attributevisualization.filter.LimitFilter;
import de.cebitec.mgx.gui.attributevisualization.filter.SortOrder;
import de.cebitec.mgx.gui.attributevisualization.filter.ToFractionFilter;
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
import de.cebitec.mgx.gui.swingutils.NonEDT;
import de.cebitec.mgx.gui.util.Reference;
import de.cebitec.mgx.gui.attributevisualization.sorter.SortByNumberOfValues;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataEvent;

/**
 *
 * @author sjaenick
 */
public class BarChartCustomizer extends javax.swing.JPanel implements VisFilterI<Distribution>, PropertyChangeListener {

    private AttributeType at;
    private final Map<AttributeType, SortedSet<Attribute>> parents = new HashMap<>();
    private final JCheckBoxList<Attribute> filterList = new JCheckBoxList<>();
    private final Set<Attribute> blackList = new HashSet<>();
    private Tree<Long> guideTree = null;

    /**
     * Creates new form BasicCustomizer
     */
    public BarChartCustomizer() {
        initComponents();
        AttrTypeModel atModel = (AttrTypeModel) attrTypeFilter.getModel();
        attrTypeFilter.addItemListener(atModel);
        filterList.addPropertyChangeListener(this);
    }

    public void setAttributeType(final AttributeType aType) {
        if (aType.equals(at)) {
            return;
        }
        at = aType;
        guideTree = null;
        blackList.clear();

        AttrTypeModel atModel = (AttrTypeModel) attrTypeFilter.getModel();
        atModel.clear();
        attrTypeFilter.setEnabled(false);
        attrTypeFilter.setSelectedIndex(-1);
        filterList.clear();
        listholder.remove(filterList);

        if (at.getStructure() == AttributeType.STRUCTURE_HIERARCHICAL) {
            parents.clear();
            guideTree = createGuideTree();
            for (Node<Long> node : guideTree.getNodes()) {
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
            SortByNumberOfValues sorter = new SortByNumberOfValues();
            sorter.setMap(parents);
            List<AttributeType> typesOrdered = new ArrayList<>(parents.size());
            typesOrdered.addAll(parents.keySet());
            Collections.sort(typesOrdered, sorter);

            if (typesOrdered.size() > 0) {
                atModel.setData(typesOrdered);
                attrTypeFilter.setEnabled(true);
                attrTypeFilter.setSelectedIndex(0);
                listholder.add(filterList);
            }

        }
    }

    public boolean logY() {
        return useLogY.isSelected();
    }

    public boolean useFractions() {
        return useFractions.isSelected();
    }

    public boolean getSortAscending() {
        return sortAscending.isSelected();
    }

    public double getItemMargin() {
        return 1D * itemMargin.getValue() / 100;
    }

    public double getCategoryMargin() {
        return 1D * categoryMargin.getValue() / 100;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sortOrderGroup = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        sortAscending = new javax.swing.JRadioButton();
        sortDescending = new javax.swing.JRadioButton();
        useFractions = new javax.swing.JCheckBox();
        limit = new javax.swing.JComboBox();
        useLogY = new javax.swing.JCheckBox();
        categoryMargin = new javax.swing.JSlider();
        jLabel2 = new javax.swing.JLabel();
        itemMargin = new javax.swing.JSlider();
        jLabel3 = new javax.swing.JLabel();
        attrTypeFilter = new javax.swing.JComboBox<AttributeType>();
        jScrollPane1 = new javax.swing.JScrollPane();
        listholder = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel1.setText(org.openide.util.NbBundle.getMessage(BarChartCustomizer.class, "BarChartCustomizer.jLabel1.text")); // NOI18N

        sortOrderGroup.add(sortAscending);
        sortAscending.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        sortAscending.setSelected(true);
        sortAscending.setText(org.openide.util.NbBundle.getMessage(BarChartCustomizer.class, "BarChartCustomizer.sortAscending.text")); // NOI18N

        sortOrderGroup.add(sortDescending);
        sortDescending.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        sortDescending.setText(org.openide.util.NbBundle.getMessage(BarChartCustomizer.class, "BarChartCustomizer.sortDescending.text")); // NOI18N

        useFractions.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        useFractions.setText(org.openide.util.NbBundle.getMessage(BarChartCustomizer.class, "BarChartCustomizer.useFractions.text")); // NOI18N

        limit.setModel(new javax.swing.DefaultComboBoxModel(LimitFilter.LIMITS.values()));
        limit.setSelectedItem(LimitFilter.LIMITS.ALL);
        limit.setMinimumSize(new java.awt.Dimension(16, 24));

        useLogY.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        useLogY.setText(org.openide.util.NbBundle.getMessage(BarChartCustomizer.class, "BarChartCustomizer.useLogY.text")); // NOI18N

        categoryMargin.setValue(5);
        categoryMargin.setMinimumSize(new java.awt.Dimension(16, 16));
        categoryMargin.setPreferredSize(new java.awt.Dimension(25, 16));

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        jLabel2.setText(org.openide.util.NbBundle.getMessage(BarChartCustomizer.class, "BarChartCustomizer.jLabel2.text")); // NOI18N

        itemMargin.setValue(5);
        itemMargin.setMinimumSize(new java.awt.Dimension(15, 16));
        itemMargin.setPreferredSize(new java.awt.Dimension(15, 16));

        jLabel3.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        jLabel3.setText(org.openide.util.NbBundle.getMessage(BarChartCustomizer.class, "BarChartCustomizer.jLabel3.text")); // NOI18N

        attrTypeFilter.setModel(new AttrTypeModel());
        attrTypeFilter.setEnabled(false);

        listholder.setLayout(new java.awt.BorderLayout());
        jScrollPane1.setViewportView(listholder);

        jLabel4.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        jLabel4.setText(org.openide.util.NbBundle.getMessage(BarChartCustomizer.class, "BarChartCustomizer.jLabel4.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(limit, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(attrTypeFilter, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(categoryMargin, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(itemMargin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane1)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(sortAscending)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(sortDescending))
                    .addComponent(useFractions)
                    .addComponent(useLogY)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sortAscending)
                    .addComponent(sortDescending))
                .addGap(18, 18, 18)
                .addComponent(useFractions)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(useLogY)
                .addGap(12, 12, 12)
                .addComponent(limit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(jLabel4)
                .addGap(1, 1, 1)
                .addComponent(attrTypeFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(itemMargin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(categoryMargin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<AttributeType> attrTypeFilter;
    private javax.swing.JSlider categoryMargin;
    private javax.swing.JSlider itemMargin;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JComboBox limit;
    private javax.swing.JPanel listholder;
    private javax.swing.JRadioButton sortAscending;
    private javax.swing.JRadioButton sortDescending;
    private javax.swing.ButtonGroup sortOrderGroup;
    private javax.swing.JCheckBox useFractions;
    private javax.swing.JCheckBox useLogY;
    // End of variables declaration//GEN-END:variables

    @Override
    public List<Pair<VisualizationGroup, Distribution>> filter(List<Pair<VisualizationGroup, Distribution>> dists) {
        if (useFractions()) {
            VisFilterI fracFilter = new ToFractionFilter();
            dists = fracFilter.filter(dists);
        }

        if (at.getStructure() == AttributeType.STRUCTURE_HIERARCHICAL) {
            if (blackList.size() > 0) {
                ExcludeFilter ef = new ExcludeFilter(blackList);
                dists = ef.filter(dists);
            }
        }

        LimitFilter lf = new LimitFilter();
        lf.setLimit(LimitFilter.LIMITS.values()[limit.getSelectedIndex()]);
        dists = lf.filter(dists);

        SortOrder sorter = new SortOrder(at, sortAscending.isSelected() ? SortOrder.ASCENDING : SortOrder.DESCENDING);
        dists = sorter.filter(dists);

        return dists;
    }

//    public Set<Attribute> getFilterEntries() {
//        //return filterList == null ? Collections.EMPTY_SET : filterList.getDeselectedEntries();
//        blackList.addAll(filterList.getDeselectedEntries());
//        return blackList;
//    }
    private Tree<Long> createGuideTree() {
        final Reference<Tree<Long>> result = new Reference<>();
        NonEDT.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                List<Pair<VisualizationGroup, Tree<Long>>> trees = VGroupManager.getInstance().getHierarchies();
                List<Tree<Long>> tmp = new ArrayList<>(trees.size());
                for (Pair<VisualizationGroup, Tree<Long>> p : trees) {
                    tmp.add(p.getSecond());
                }
                Tree<Long> merged = TreeFactory.mergeTrees(tmp);
                result.setValue(merged);
            }
        });

        Tree<Long> merged = result.getValue();
        return merged;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(JCheckBoxList.selectionChange)) {
            Attribute attr = (Attribute) evt.getOldValue();
            boolean isSelected = (boolean) evt.getNewValue();
            Node<Long> n = findNode(attr);
            if (isSelected) {
                enableChildren(n);
            } else {
                disableChildren(n);
            }

        }
    }

    private void enableChildren(Node<Long> n) {
        blackList.remove(n.getAttribute());
        if (n.hasChildren()) {
            for (Node<Long> c : n.getChildren()) {
                enableChildren(c);
            }
        }
    }

    private void disableChildren(Node<Long> n) {
        blackList.add(n.getAttribute());
        if (n.hasChildren()) {
            for (Node<Long> c : n.getChildren()) {
                disableChildren(c);
            }
        }
    }

    private Node<Long> findNode(final Attribute a) {
        for (Node<Long> n : guideTree.getNodes()) {
            if (n.getAttribute().equals(a)) {
                return n;
            }
        }
        return null;
    }

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
        public AttributeType getSelectedItem() {
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
                AttributeType item = getSelectedItem();
                if (item != null) {
                    Set<Attribute> get = parents.get(item);
//                    if (filterList == null) {
//                        filterList = new JCheckBoxList<>();
//                        listholder.add(filterList, BorderLayout.CENTER);
//                    } else {
                    filterList.clear();
                    //}

                    for (Attribute pAttr : get) {
                        filterList.addElement(pAttr, !blackList.contains(pAttr));
                    }
                    //filterList.selectAll();
                }
            }
        }

        protected void fireContentsChanged() {
            ListDataEvent e = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, -1, -1);
            fireContentsChanged(e, -1, -1);
        }
    }
}
