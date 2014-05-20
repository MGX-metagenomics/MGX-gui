/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.wizard.mapping;

import de.cebitec.mgx.gui.mapping.MappingCtx;
import java.awt.Component;
import java.util.Collection;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public final class MappingWizardVisualPanel1 extends JPanel implements ListSelectionListener {

    /**
     * Creates new form MappingWizardVisualPanel1
     */
    public MappingWizardVisualPanel1() {
        initComponents();
        list.setCellRenderer(new ListCellRenderer<MappingCtx>() {

            private final MappingPanel mp = new MappingPanel();
            private final Border noFocusBorder = new EmptyBorder(15, 1, 1, 1);
            private final Border focusBorder = LineBorder.createGrayLineBorder();

            @Override
            public Component getListCellRendererComponent(JList<? extends MappingCtx> list, MappingCtx value, int index, boolean isSelected, boolean cellHasFocus) {
                mp.setMapping(value);
                mp.setBorder(isSelected ? focusBorder : noFocusBorder);
                return mp;
            }
        });
        list.addListSelectionListener(this);
    }

    public void setData(Collection<MappingCtx> ctxs) {
        list.setListData(ctxs.toArray(new MappingCtx[]{}));
        list.setSelectedIndex(0);
    }

    @Override
    public String getName() {
        return "Select mapping";
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        list = new javax.swing.JList<MappingCtx>();

        list.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(list);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JList<MappingCtx> list;
    // End of variables declaration//GEN-END:variables

    @Override
    public void valueChanged(ListSelectionEvent e) {
        firePropertyChange("MappingCtxSelected", null, list.getSelectedValue());
    }
}