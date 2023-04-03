package de.cebitec.mgx.gui.wizard.seqrun;

import de.cebitec.mgx.api.model.TermI;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.Serial;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

public final class SeqRunVisualPanel1 extends JPanel implements DocumentListener {

    @Serial
    private static final long serialVersionUID = 1L;
    
    public static final String PROP_NAME = "name";
    public static final String PROP_SUBMITTED = "submitted_to_INSDC";
    public static final String PROP_ACCESSION = "accession";
    public static final String PROP_PLATFORM = "seqPlatform";
    public static final String PROP_METHOD = "seqMethod";
    public static final String PROP_RUNTOOLS = "runDefaultTools";
    public static final String PROP_ISPAIRED = "isPaired";
    
    /**
     * Creates new form SeqRunVisualPanel1
     */
    public SeqRunVisualPanel1() {
        initComponents();
        name.getDocument().addDocumentListener(this);
        accession.getDocument().addDocumentListener(this);
        submitted.addItemListener(new CheckBoxListener());

        method.addActionListener(new MethodListener());
        platform.addActionListener(new PlatformListener());
    }

    public void setMethods(TermI[] terms) {
        method.setModel(new javax.swing.DefaultComboBoxModel<>(terms));
        method.setEditable(false);
    }

    public void setPlatforms(TermI[] terms) {
        platform.setModel(new javax.swing.DefaultComboBoxModel<>(terms));
        platform.setEditable(false);
    }

    @Override
    public String getName() {
        return "Sequencing run";
    }

    public String getRunName() {
        return name.getText().trim();
    }

    public void setRunName(String n) {
        name.setText(n);
    }

    public boolean getSubmittedState() {
        return submitted.isSelected();
    }

    public void setSubmittedState(boolean state) {
        submitted.setSelected(state);
    }

    public String getAccession() {
        return accession.getText().trim();
    }

    public void setAccession(String acc) {
        accession.setText(acc);
    }

    public TermI getPlatform() {
        return (TermI) platform.getSelectedItem();
    }

    public void setPlatform(TermI p) {
        platform.setSelectedItem(p);
        platform.getModel().setSelectedItem(p);
    }

    public TermI getMethod() {
        return (TermI) method.getSelectedItem();
    }

    public void setMethod(TermI m) {
        method.setSelectedItem(m);
        method.getModel().setSelectedItem(m);
    }

    public boolean runDefaultTools() {
        return runDefaultTools.isSelected();
    }

    public void runDefaultTools(boolean run) {
        runDefaultTools.setSelected(run);
    }
    
    public void disableRunDefaultTools(boolean disable) {
        if (disable) {
            runDefaultTools.setSelected(false);
            runDefaultTools.setEnabled(false);
        } else {
            runDefaultTools.setEnabled(true);
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
        platform = new javax.swing.JComboBox<>();
        method = new javax.swing.JComboBox<>();
        submitted = new javax.swing.JCheckBox();
        accession = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        name = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        runDefaultTools = new javax.swing.JCheckBox();

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(SeqRunVisualPanel1.class, "SeqRunVisualPanel1.jLabel1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(SeqRunVisualPanel1.class, "SeqRunVisualPanel1.jLabel2.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(submitted, org.openide.util.NbBundle.getMessage(SeqRunVisualPanel1.class, "SeqRunVisualPanel1.submitted.text")); // NOI18N

        accession.setEditable(false);
        accession.setText(org.openide.util.NbBundle.getMessage(SeqRunVisualPanel1.class, "SeqRunVisualPanel1.accession.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(SeqRunVisualPanel1.class, "SeqRunVisualPanel1.jLabel3.text")); // NOI18N

        name.setText(org.openide.util.NbBundle.getMessage(SeqRunVisualPanel1.class, "SeqRunVisualPanel1.name.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(SeqRunVisualPanel1.class, "SeqRunVisualPanel1.jLabel4.text")); // NOI18N

        runDefaultTools.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(runDefaultTools, org.openide.util.NbBundle.getMessage(SeqRunVisualPanel1.class, "SeqRunVisualPanel1.runDefaultTools.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(18, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(runDefaultTools)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(accession, javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(submitted)
                        .addComponent(method, 0, 221, Short.MAX_VALUE)
                        .addComponent(platform, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(name)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(name, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(platform, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(method, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(18, 18, 18)
                .addComponent(submitted)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(accession, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 45, Short.MAX_VALUE)
                .addComponent(runDefaultTools)
                .addGap(26, 26, 26))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField accession;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JComboBox<TermI> method;
    private javax.swing.JTextField name;
    private javax.swing.JComboBox<TermI> platform;
    private javax.swing.JCheckBox runDefaultTools;
    private javax.swing.JCheckBox submitted;
    // End of variables declaration//GEN-END:variables

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
        if (accession.getDocument() == d) {
            firePropertyChange(PROP_ACCESSION, 0, 1);
        }
        if (name.getDocument() == d) {
            firePropertyChange(PROP_NAME, 0, 1);
        }
    }

    private final class CheckBoxListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent ie) {
            if (ie.getStateChange() == ItemEvent.DESELECTED) {
                accession.setText("");
                accession.setEditable(false);
            } else {
                accession.setEditable(true);
            }
            firePropertyChange(PROP_SUBMITTED, 0, 1);
        }
    }

    private final class MethodListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            firePropertyChange(PROP_METHOD, 0, 1);
        }
    }

    private final class PlatformListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            firePropertyChange(PROP_PLATFORM, 0, 1);
        }
    }
}
