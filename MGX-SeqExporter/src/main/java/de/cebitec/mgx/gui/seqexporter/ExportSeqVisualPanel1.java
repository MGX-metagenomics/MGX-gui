package de.cebitec.mgx.gui.seqexporter;

import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.gui.swingutils.JCheckBoxList;
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
import java.util.concurrent.ExecutionException;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.openide.util.Exceptions;

public final class ExportSeqVisualPanel1<T extends Number> extends JPanel implements PropertyChangeListener {

    @Serial
    private static final long serialVersionUID = 1L;

    private final JCheckBoxList<AttributeI> checkboxList = new JCheckBoxList<>();
    private boolean all_selected = true;
    private final Map<AttributeI, Boolean> selection = new HashMap<>();

    /**
     * Creates new form ExportSeqVisualPanel1
     */
    public ExportSeqVisualPanel1() {
        initComponents();
        scrollpane.setViewportView(checkboxList);
        checkboxList.addPropertyChangeListener(this);

        searchfield.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                filterModel();

            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterModel();

            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterModel();

            }

        });
    }

    public void setDistribution(DistributionI<T> d) {
        List<AttributeI> elements = new ArrayList<>();
        elements.addAll(d.keySet());
        Collections.sort(elements);

        checkboxList.clear();
        selection.clear();

        for (AttributeI attr : elements) {
            checkboxList.addElement(attr);
            selection.put(attr, true);
        }
        jButton1ActionPerformed(null); //WHY?
    }

    public Set<AttributeI> getSelectedAttributes() {
        Set<AttributeI> ret = new HashSet<>();
        for (Map.Entry<AttributeI, Boolean> me : selection.entrySet()) {
            if (me.getValue()) {
                ret.add(me.getKey());
            }
        }
        return ret;
    }

    @Override
    public String getName() {
        return "Select attributes";
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        scrollpane = new javax.swing.JScrollPane();
        jButton1 = new javax.swing.JButton();
        searchfield = new javax.swing.JTextField();

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(ExportSeqVisualPanel1.class, "ExportSeqVisualPanel1.jLabel1.text")); // NOI18N

        jButton1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jButton1, org.openide.util.NbBundle.getMessage(ExportSeqVisualPanel1.class, "ExportSeqVisualPanel1.jButton1.text")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        searchfield.setForeground(new java.awt.Color(102, 102, 102));
        searchfield.setText(org.openide.util.NbBundle.getMessage(ExportSeqVisualPanel1.class, "ExportSeqVisualPanel1.searchfield.text")); // NOI18N
        searchfield.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                searchfieldMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                    .addComponent(scrollpane)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addGap(48, 48, 48)
                        .addComponent(searchfield, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollpane, javax.swing.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(searchfield, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if (all_selected) {
            checkboxList.deselectAll();
            all_selected = false;
            jButton1.setText("Select all");
        } else {
            checkboxList.selectAll();
            all_selected = true;
            jButton1.setText("Deselect all");
        }
        validateInput();
        repaint();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void searchfieldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_searchfieldMouseClicked
        if (searchfield.getText().equals("Search")) {
            searchfield.setText("");
            searchfield.setForeground(new java.awt.Color(0, 0, 0));
        }
        //validateInput();
        repaint();
    }//GEN-LAST:event_searchfieldMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane scrollpane;
    private javax.swing.JTextField searchfield;
    // End of variables declaration//GEN-END:variables

    public void filterModel() {
        String filterText = searchfield.getText();

        SwingWorker<List<Map.Entry<AttributeI, Boolean>>, Void> sw = new SwingWorker<List<Map.Entry<AttributeI, Boolean>>, Void>() {
            @Override
            protected List<Map.Entry<AttributeI, Boolean>> doInBackground() throws Exception {
                List<Map.Entry<AttributeI, Boolean>> tmp = new ArrayList<>();
                for (Map.Entry<AttributeI, Boolean> me : selection.entrySet()) {
                    AttributeI attr = me.getKey();
                    if (attr.getValue().contains(filterText)) {
                        tmp.add(me);
                    }
                }

                Collections.sort(tmp, new Comparator<Map.Entry<AttributeI, Boolean>>() {
                    @Override
                    public int compare(Map.Entry<AttributeI, Boolean> o1, Map.Entry<AttributeI, Boolean> o2) {
                        return o1.getKey().compareTo(o2.getKey());
                    }

                });

                return tmp;
            }

            @Override
            protected void done() {
                List<Map.Entry<AttributeI, Boolean>> get;
                try {
                    get = get();
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                    return;
                }
                checkboxList.clear();
                for (Map.Entry<AttributeI, Boolean> me : get) {
                    checkboxList.addElement(me.getKey(), me.getValue());
                }

                super.done();
            }
        };
        sw.execute();
    }

    private void validateInput() {
        firePropertyChange("REVALIDATE", 0, 1);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(JCheckBoxList.selectionChange)) {
            AttributeI attr = (AttributeI) evt.getOldValue();
            Boolean selected = (Boolean) evt.getNewValue();
            selection.put(attr, selected);
            firePropertyChange("REVALIDATE", 0, 1);
        }
    }
}
