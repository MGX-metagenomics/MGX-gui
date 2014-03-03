/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.wizard.mapping;

import de.cebitec.mgx.gui.datamodel.Mapping;
import de.cebitec.mgx.gui.datamodel.Reference;
import de.cebitec.mgx.gui.datamodel.SeqRun;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Iterator;
import java.util.List;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;

public final class MappingVisualPanel1 extends JPanel {

    final static String SEQRUN_COMBOBOX_COMMAND = "ComboBox";
    final static String PROP_MAPPING = "ComboBox";

    /**
     * Creates new form wizardVisualPanel1
     */
    public MappingVisualPanel1(Iterator<SeqRun> seqruns) {
        initComponents();
        seqrunCombo.setActionCommand(SEQRUN_COMBOBOX_COMMAND);
        mappingList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        while (seqruns.hasNext()) {
            seqrunCombo.addItem(seqruns.next());
        }
        seqrunCombo.setSelectedItem(null);
        mappingList.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                JList l = (JList) e.getSource();
                ListModel m = l.getModel();
                int index = l.locationToIndex(e.getPoint());
                if (index > -1) {
                    l.setToolTipText(m.getElementAt(index).toString());
                }
            }
        });
    }

    public void addActionListenerToCombobox(ActionListener listener) {
        seqrunCombo.addActionListener(listener);
    }

    public void addListSelectionListener(ListSelectionListener listener) {
        mappingList.addListSelectionListener(listener);
    }

    @Override
    public String getName() {
        return "Select a Read Mapping.";
    }

    public SeqRun getSeqRun() {
        return seqrunCombo.getItemAt(seqrunCombo.getSelectedIndex());
    }

    public void setMappings(List<MappingEntry> mappings) {
        this.mappingList.setListData(mappings.toArray());
    }

    public MappingEntry getMappings() {
        if (mappingList.getSelectedIndex() == -1) {
            return null;
        }
        return (MappingEntry) this.mappingList.getModel().getElementAt(mappingList.getSelectedIndex());
    }

    public class MappingEntry {

        private Reference reference;

        private Mapping mapping;

        public MappingEntry(Reference reference, Mapping mapping) {
            this.reference = reference;
            this.mapping = mapping;
        }

        @Override
        public String toString() {
            return "<html> Job ID: " + Long.toString(mapping.getJobID()) + "<br/> Reference:" + reference.getName() + "</html>";
        }
        
        public Reference getReference() {
            return reference;
        }

        public Mapping getMapping() {
            return mapping;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        seqrunCombo = new javax.swing.JComboBox<SeqRun>();
        jscrollpane = new javax.swing.JScrollPane();
        mappingList = new javax.swing.JList();
        referenceLabel = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        seqrunCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                seqrunComboActionPerformed(evt);
            }
        });

        jscrollpane.setViewportView(mappingList);

        org.openide.awt.Mnemonics.setLocalizedText(referenceLabel, org.openide.util.NbBundle.getMessage(MappingVisualPanel1.class, "MappingVisualPanel1.referenceLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(MappingVisualPanel1.class, "MappingVisualPanel1.jLabel1.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(referenceLabel)
                    .addComponent(jLabel1))
                .addGap(81, 81, 81)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jscrollpane, javax.swing.GroupLayout.DEFAULT_SIZE, 383, Short.MAX_VALUE)
                    .addComponent(seqrunCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(93, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(44, 44, 44)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(seqrunCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 86, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(referenceLabel)
                    .addComponent(jscrollpane, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(50, 50, 50))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void seqrunComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_seqrunComboActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_seqrunComboActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jscrollpane;
    private javax.swing.JList mappingList;
    private javax.swing.JLabel referenceLabel;
    private javax.swing.JComboBox<SeqRun> seqrunCombo;
    // End of variables declaration//GEN-END:variables
}