package de.cebitec.mgx.gui.swingutils;

import de.cebitec.mgx.api.groups.GroupI;
import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.tree.NodeI;
import de.cebitec.mgx.api.model.tree.TreeI;
import de.cebitec.mgx.gui.datafactories.TreeFactory;
import de.cebitec.mgx.gui.visgroups.VGroupManager;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serial;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import javax.swing.SwingWorker;
import org.openide.util.Exceptions;

/**
 *
 * @author sjaenick
 */
public class TreeFilterUI extends javax.swing.JPanel implements ItemListener, PropertyChangeListener {

    @Serial
    private static final long serialVersionUID = 1L;
    
    /**
     * Creates new form TreeFilter
     */
    public TreeFilterUI() {
        initComponents();
        attrTypes.setModel(atModel);
        attrTypes.addItemListener(this);
        attrList.addPropertyChangeListener(this);
    }

    private final AttrTypeModel atModel = new AttrTypeModel();
    private TreeI<Long> guideTree = null;
    private final Map<AttributeTypeI, SortedSet<AttributeI>> parents = new HashMap<>();
    private final Set<AttributeI> blackList = new HashSet<>();
    private AttributeTypeI curAttrType = null;

    @Override
    public void setEnabled(boolean enabled) {
        if (enabled != super.isEnabled()) {
            super.setEnabled(enabled);
            attrTypes.setEnabled(enabled);
            attrList.setEnabled(enabled);
        }
    }

    public void setAttributeType(AttributeTypeI at) {
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
        if (at.getStructure() != AttributeTypeI.STRUCTURE_HIERARCHICAL) {
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
        for (NodeI<Long> node : guideTree.getNodes()) {
            // collect attributes and attributetypes for same layer and parent nodes only
            if (at.equals(node.getAttribute().getAttributeType())) {
                NodeI<Long> cur = node;
                while (!cur.isRoot()) {
                    AttributeI attr = cur.getAttribute();
                    AttributeTypeI type = attr.getAttributeType();

                    if (!parents.containsKey(type)) {
                        parents.put(type, new TreeSet<AttributeI>());
                    }
                    parents.get(type).add(attr);
                    cur = cur.getParent();
                }
            }
        }
        SortByNumberOfValues sorter = new SortByNumberOfValues();
        sorter.setMap(parents);
        List<AttributeTypeI> typesOrdered = new ArrayList<>(parents.size());
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
            AttributeTypeI item = atModel.getSelectedItem();
            if (item != null) {
                Set<AttributeI> get = parents.get(item);
                attrList.clear();
                for (AttributeI pAttr : get) {
                    attrList.addElement(pAttr, !blackList.contains(pAttr));
                }
            }
        }
    }

    private TreeI<Long> createGuideTree() {

        SwingWorker<TreeI<Long>, Void> sw = new SwingWorker<TreeI<Long>, Void>() {

            @Override
            protected TreeI<Long> doInBackground() throws Exception {
                List<Pair<GroupI, TreeI<Long>>> trees = VGroupManager.getInstance().getHierarchies();
                if (trees == null) { // conflicts remain
                    return null;
                }
                List<Future<TreeI<Long>>> tmp = new ArrayList<>(trees.size());
                for (Pair<GroupI, TreeI<Long>> p : trees) {
                    tmp.add(new NoFuture<>(p.getSecond()));
                }
                TreeI<Long> merged = TreeFactory.mergeTrees(tmp);
                return merged;
            }
        };
        sw.execute();

        TreeI<Long> merged = null;
        try {
            merged = sw.get();
        } catch (InterruptedException | ExecutionException ex) {
            Exceptions.printStackTrace(ex);
        }
        return merged;
    }

    private NodeI<Long> findNode(final AttributeI a) {
        for (NodeI<Long> n : guideTree.getNodes()) {
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

        attrTypes = new javax.swing.JComboBox<AttributeTypeI>();
        jScrollPane1 = new javax.swing.JScrollPane();
        attrList = new de.cebitec.mgx.gui.swingutils.JCheckBoxList<AttributeI>();

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
    private de.cebitec.mgx.gui.swingutils.JCheckBoxList<AttributeI> attrList;
    private javax.swing.JComboBox<AttributeTypeI> attrTypes;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(JCheckBoxList.selectionChange)) {
            AttributeI attr = (AttributeI) evt.getOldValue();
            boolean isSelected = (boolean) evt.getNewValue();
            NodeI<Long> n = findNode(attr);
            if (isSelected) {
                enableChildren(n);
            } else {
                disableChildren(n);
            }

        }
    }

    private void enableChildren(NodeI<Long> n) {
        blackList.remove(n.getAttribute());
        if (n.hasChildren()) {
            for (NodeI<Long> c : n.getChildren()) {
                enableChildren(c);
            }
        }
    }

    private void disableChildren(NodeI<Long> n) {
        blackList.add(n.getAttribute());
        if (n.hasChildren()) {
            for (NodeI<Long> c : n.getChildren()) {
                disableChildren(c);
            }
        }
    }

    public Set<AttributeI> getBlackList() {
        return blackList;
    }

    private class SortByNumberOfValues implements Comparator<AttributeTypeI> {

        Map<AttributeTypeI, SortedSet<AttributeI>> map;

        public void setMap(Map<AttributeTypeI, SortedSet<AttributeI>> data) {
            map = data;
        }

        @Override
        public int compare(AttributeTypeI o1, AttributeTypeI o2) {
            return map != null
                    ? Integer.compare(map.get(o1).size(), map.get(o2).size())
                    : 0;
        }
    }
}
