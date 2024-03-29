package de.cebitec.mgx.gui.wizard.extract;

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

public final class DNAExtractVisualPanel1 extends JPanel implements DocumentListener {

    @Serial
    private static final long serialVersionUID = 1L;
    
    public static final String PROP_NAME = "name";
    public static final String PROP_METHOD = "method";
    public static final String PROP_PROTOCOL = "protocol";
    public static final String PROP_FIVEPRIMER = "5' primer";
    public static final String PROP_THREEPRIMER = "3' primer";
    public static final String PROP_GENE = "targetgene";
    public static final String PROP_FRAGMENT = "targetfragment";
    //
    private final static String[] methods = {"metagenome", "metatranscriptome", "amplicon"};
    //
    private final AmpliconDetailPanel adp = new AmpliconDetailPanel();
    private final MetatranscriptomeDetailPanel mtdp = new MetatranscriptomeDetailPanel();

    /**
     * Creates new form DNAExtractVisualPanel1
     */
    public DNAExtractVisualPanel1() {
        initComponents();
        initDetailPanels();
        nameField.getDocument().addDocumentListener(this);
        method.setModel(new javax.swing.DefaultComboBoxModel<>(methods));
        method.setEditable(false);
        method.addActionListener(new MethodListener());
        protocol.getDocument().addDocumentListener(this);

        placeholder.add(new JPanel(), "metagenome");
        placeholder.add(adp, "amplicon");
        placeholder.add(mtdp, "metatranscriptome");

        // amplicon-specific attributes
        adp.getFiveprimer().getDocument().addDocumentListener(this);
        adp.getThreeprimer().getDocument().addDocumentListener(this);
        adp.getTargetgene().getDocument().addDocumentListener(this);
        adp.getTargetfragment().getDocument().addDocumentListener(this);
    }

    private void initDetailPanels() {
        List<String> treatments = new ArrayList<>();
        treatments.add("none");
        treatments.add("Ribo Zero");
        treatments.add("other");
        mtdp.setDepletionOptions(treatments);
    }

    @Override
    public String getName() {
        return "Extraction protocol";
    }

    public String getExtractName() {
        return nameField.getText();
    }

    public void setExtractName(String n) {
        nameField.setText(n);
    }

    public String getFiveprimer() {
        return adp.getFiveprimer().getText();
    }

    public void setFiveprimer(String fiveprimer) {
        adp.getFiveprimer().setText(fiveprimer);
    }

    public String getMethod() {
        return (String) method.getSelectedItem();
    }

    public void setMethod(String method) {
        this.method.setSelectedItem(method);
    }

    public String getProtocol() {
        return protocol.getText();
    }

    public void setProtocol(String protocol) {
        this.protocol.setText(protocol);
    }

    public String getTargetfragment() {
        return adp.getTargetfragment().getText();
    }

    public void setTargetfragment(String targetfragment) {
        adp.getTargetfragment().setText(targetfragment);
    }

    public String getTargetgene() {
        return adp.getTargetgene().getText();
    }

    public void setTargetgene(String targetgene) {
        adp.getTargetgene().setText(targetgene);
    }

    public String getThreeprimer() {
        return adp.getThreeprimer().getText();
    }

    public void setThreeprimer(String threeprimer) {
        adp.getThreeprimer().setText(threeprimer);
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        handleUpdate(e);
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        handleUpdate(e);
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        handleUpdate(e);
    }

    private void handleUpdate(DocumentEvent e) {
        Document d = e.getDocument();
        if (protocol.getDocument() == d) {
            firePropertyChange(PROP_PROTOCOL, 0, 1);
        } else if (nameField.getDocument() == d) {
            firePropertyChange(PROP_NAME, 0, 1);
        } else if (adp.getFiveprimer().getDocument() == d) {
            firePropertyChange(PROP_FIVEPRIMER, 0, 1);
        } else if (adp.getThreeprimer().getDocument() == d) {
            firePropertyChange(PROP_THREEPRIMER, 0, 1);
        } else if (adp.getTargetgene().getDocument() == d) {
            firePropertyChange(PROP_GENE, 0, 1);
        } else if (adp.getTargetfragment().getDocument() == d) {
            firePropertyChange(PROP_FRAGMENT, 0, 1);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        protocol = new javax.swing.JTextField();
        method = new javax.swing.JComboBox<String>();
        placeholder = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        nameField = new javax.swing.JTextField();

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(DNAExtractVisualPanel1.class, "DNAExtractVisualPanel1.jLabel1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(DNAExtractVisualPanel1.class, "DNAExtractVisualPanel1.jLabel2.text")); // NOI18N

        protocol.setText(org.openide.util.NbBundle.getMessage(DNAExtractVisualPanel1.class, "DNAExtractVisualPanel1.protocol.text")); // NOI18N

        placeholder.setLayout(new java.awt.CardLayout());

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(DNAExtractVisualPanel1.class, "DNAExtractVisualPanel1.jLabel3.text")); // NOI18N

        nameField.setText(org.openide.util.NbBundle.getMessage(DNAExtractVisualPanel1.class, "DNAExtractVisualPanel1.nameField.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(placeholder, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(method, 0, 248, Short.MAX_VALUE)
                    .addComponent(protocol, javax.swing.GroupLayout.DEFAULT_SIZE, 248, Short.MAX_VALUE)
                    .addComponent(nameField))
                .addGap(26, 26, 26))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(nameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(method, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(protocol, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(18, 18, 18)
                .addComponent(placeholder, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JComboBox<String> method;
    private javax.swing.JTextField nameField;
    private javax.swing.JPanel placeholder;
    private javax.swing.JTextField protocol;
    // End of variables declaration//GEN-END:variables

    private final class MethodListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JComboBox<?> cb = (JComboBox) e.getSource();
            String methodName = (String) cb.getSelectedItem();
            CardLayout cl = (CardLayout) (placeholder.getLayout());
            cl.show(placeholder, methodName);
        }
    }
}
