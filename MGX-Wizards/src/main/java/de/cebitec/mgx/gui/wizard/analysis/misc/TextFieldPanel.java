package de.cebitec.mgx.gui.wizard.analysis.misc;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author sjaenick
 */
public class TextFieldPanel extends ValueHolderI<String> implements DocumentListener {

    /**
     * Creates new form TextFieldPanel
     */
    public TextFieldPanel() {
        initComponents();
        jTextField1.getDocument().addDocumentListener(this);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTextField1 = new javax.swing.JTextField();

        jTextField1.setText(org.openide.util.NbBundle.getMessage(TextFieldPanel.class, "TextFieldPanel.jTextField1.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables

    @Override
    public void insertUpdate(DocumentEvent e) {
        firePropertyChange("input", null, jTextField1.getText());
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        firePropertyChange("input", null, jTextField1.getText());
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        firePropertyChange("input", null, jTextField1.getText());
    }
    
    @Override
    public String getValue() {
        return jTextField1.getText();
    }

    @Override
    public void setValue(String value) {
        jTextField1.setText(value);
    }
}
