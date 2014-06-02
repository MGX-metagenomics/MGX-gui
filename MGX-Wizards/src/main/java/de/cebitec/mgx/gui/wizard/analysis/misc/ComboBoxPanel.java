package de.cebitec.mgx.gui.wizard.analysis.misc;

import de.cebitec.mgx.api.model.JobParameterI;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

/**
 *
 * @author sjaenick
 */
public class ComboBoxPanel<T> extends ValueHolderI<T> implements ActionListener {

    /**
     * Creates new form ComboBoxPanel
     */
//    public ComboBoxPanel(JobParameter jp) {
//        this(jp, jp.getChoices().keySet());
//        initComponents();
//        jComboBox1.removeAllItems();
//        for (String o : jp.getChoices().keySet()) {
//            jComboBox1.addItem(o);
//        }
//        jComboBox1.addActionListener(this);
//    }

    public ComboBoxPanel(JobParameterI jp, Collection<T> allowedValues) {
        initComponents();
        jComboBox1.removeAllItems();
        for (T s : allowedValues) {
            if (item == null) {
                // preselect first item
                item = s;
            }
            jComboBox1.addItem(s);
        }
        jComboBox1.addActionListener(this);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jComboBox1 = new javax.swing.JComboBox<T>();

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<T> jComboBox1;
    // End of variables declaration//GEN-END:variables

    @Override
    public void actionPerformed(ActionEvent e) {
        item = (T) jComboBox1.getSelectedItem();
        firePropertyChange("input", null, item);
    }
    private T item = null;

    @Override
    public T getValue() {
        return item;
    }

    @Override
    public void setValue(T value) {
        jComboBox1.setSelectedItem(value);
    }
}
