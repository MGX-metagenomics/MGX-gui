package de.cebitec.mgx.gui.swingutils;

import de.cebitec.mgx.gui.datamodel.Attribute;
import de.cebitec.mgx.gui.datamodel.AttributeType;
import de.cebitec.mgx.gui.datamodel.misc.Pair;
import de.cebitec.mgx.gui.datamodel.tree.Node;
import de.cebitec.mgx.gui.datamodel.tree.Tree;
import de.cebitec.mgx.gui.datamodel.tree.TreeFactory;
import de.cebitec.mgx.gui.groups.VGroupManager;
import de.cebitec.mgx.gui.groups.VisualizationGroup;
import de.cebitec.mgx.gui.util.Reference;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 * @author sjaenick
 */
public class TreeFilter extends javax.swing.JPanel implements ItemListener, PropertyChangeListener {

    /**
     * Creates new form TreeFilter
     */
    public TreeFilter() {
        initComponents();
        attrTypes.setModel(atModel);
        attrTypes.addItemListener(this);
        attrList.addPropertyChangeListener(this);
    }

    private final AttrTypeModel atModel = new AttrTypeModel();
    private Tree<Long> guideTree = null;
    private final Map<AttributeType, SortedSet<Attribute>> parents = new HashMap<>();
    private final Set<Attribute> blackList = new HashSet<>();
    private AttributeType curAttrType = null;

    public void setAttributeType(AttributeType at) {
        if (at == null || at.equals(curAttrType)) {
            return;
        } else {
            curAttrType = at;
            atModel.clear();
            attrList.clear();
            attrTypes.setEnabled(false);
            attrList.setEnabled(false);
        }
        
        parents.clear();
        blackList.clear();
        guideTree = null;
        if (at.getStructure() != AttributeType.STRUCTURE_HIERARCHICAL) {
            atModel.clear();
            attrList.clear();
            attrTypes.setEnabled(false);
            attrList.setEnabled(false);
            return;
        }

        guideTree = createGuideTree();
        if (guideTree == null) {
            return;
        }
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
            attrTypes.setEnabled(true);
            attrTypes.setSelectedIndex(0);
            attrList.setEnabled(true);
        }
    }

    @Override
    public void itemStateChanged(ItemEvent evt) {
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            AttributeType item = atModel.getSelectedItem();
            if (item != null) {
                Set<Attribute> get = parents.get(item);
                attrList.clear();
                for (Attribute pAttr : get) {
                    attrList.addElement(pAttr, !blackList.contains(pAttr));
                }
            }
        }
    }

    private Tree<Long> createGuideTree() {
        final Reference<Tree<Long>> result = new Reference<>();
        NonEDT.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                List<Pair<VisualizationGroup, Tree<Long>>> trees = VGroupManager.getInstance().getHierarchies();
                if (trees == null) { // conflicts remain
                    return;
                }
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

    private Node<Long> findNode(final Attribute a) {
        for (Node<Long> n : guideTree.getNodes()) {
            if (n.getAttribute().equals(a)) {
                return n;
            }
        }
        return null;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        attrTypes = new javax.swing.JComboBox<AttributeType>();
        jScrollPane1 = new javax.swing.JScrollPane();
        attrList = new de.cebitec.mgx.gui.swingutils.JCheckBoxList<Attribute>();

        attrTypes.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N

        jScrollPane1.setViewportView(attrList);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(attrTypes, 0, 112, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(attrTypes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private de.cebitec.mgx.gui.swingutils.JCheckBoxList<Attribute> attrList;
    private javax.swing.JComboBox<AttributeType> attrTypes;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

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

    public Set<Attribute> getBlackList() {
        return blackList;
    }

    private class SortByNumberOfValues<T> implements Comparator<AttributeType> {

        Map<AttributeType, Set<T>> map;

        public void setMap(Map<AttributeType, Set<T>> data) {
            map = data;
        }

        @Override
        public int compare(AttributeType o1, AttributeType o2) {
            return map != null
                    ? Integer.compare(map.get(o1).size(), map.get(o2).size())
                    : 0;
        }
    }
}
