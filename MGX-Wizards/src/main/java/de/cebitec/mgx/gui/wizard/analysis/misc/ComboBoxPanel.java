package de.cebitec.mgx.gui.wizard.analysis.misc;

import de.cebitec.mgx.gui.datamodel.JobParameter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author sjaenick
 */
public class ComboBoxPanel extends ValueHolderI<String> implements ActionListener {

    private String MODE = "CHOICES";
    private Map<?, String> options = null;
    
    /**
     * Creates new form ComboBoxPanel
     */
    public ComboBoxPanel(JobParameter jp) {
        initComponents();
        jComboBox1.removeAllItems();
        for (String o : jp.getChoices().keySet()) {
            jComboBox1.addItem(o);
        }
        jComboBox1.addActionListener(this);
    }

    public ComboBoxPanel(JobParameter jp, Map<?, String> options) {
        initComponents();
        jComboBox1.removeAllItems();
        this.options = options;
        for (Entry<?, String> e : options.entrySet()) {
            jComboBox1.addItem(e.getKey().toString());
        }
        jComboBox1.addActionListener(this);
        MODE = "OPTIONS";
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jComboBox1 = new javax.swing.JComboBox<String>();

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
    private javax.swing.JComboBox<String> jComboBox1;
    // End of variables declaration//GEN-END:variables

    @Override
    public void actionPerformed(ActionEvent e) {
        item = (String) jComboBox1.getSelectedItem();
        firePropertyChange("input", null, item);
    }
    private String item = null;

    @Override
    public String getValue() {
        return MODE.equals("CHOICES") ? item : options.get(item);
    }

    @Override
    public void setValue(String value) {
        jComboBox1.setSelectedItem(value);
    }
}
