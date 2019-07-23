package de.cebitec.mgx.gui.wizard.analysis.misc;

import de.cebitec.mgx.api.groups.FileType;
import de.cebitec.mgx.gui.swingutils.util.FileChooserUtils;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import org.openide.util.Exceptions;

/**
 *
 * @author sjaenick
 */
public class NewToolPanel extends javax.swing.JPanel implements DocumentListener {

    public static final String TOOL_DEFINED = "toolDefined";

    /**
     * Creates new form NewToolPanel
     */
    public NewToolPanel() {
        initComponents();
        toolName.getDocument().addDocumentListener(this);
        author.getDocument().addDocumentListener(this);
        description.getDocument().addDocumentListener(this);
        version.getDocument().addDocumentListener(this);
        uriField.getDocument().addDocumentListener(this);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        toolName = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        author = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        description = new javax.swing.JTextArea();
        jLabel4 = new javax.swing.JLabel();
        version = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        xml = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        uriField = new javax.swing.JTextField();

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(NewToolPanel.class, "NewToolPanel.jLabel1.text")); // NOI18N

        toolName.setText(org.openide.util.NbBundle.getMessage(NewToolPanel.class, "NewToolPanel.toolName.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(NewToolPanel.class, "NewToolPanel.jLabel2.text")); // NOI18N

        author.setText(org.openide.util.NbBundle.getMessage(NewToolPanel.class, "NewToolPanel.author.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(NewToolPanel.class, "NewToolPanel.jLabel3.text")); // NOI18N

        description.setColumns(20);
        description.setRows(5);
        jScrollPane1.setViewportView(description);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(NewToolPanel.class, "NewToolPanel.jLabel4.text")); // NOI18N

        version.setText(org.openide.util.NbBundle.getMessage(NewToolPanel.class, "NewToolPanel.version.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, org.openide.util.NbBundle.getMessage(NewToolPanel.class, "NewToolPanel.jLabel5.text")); // NOI18N

        xml.setEditable(false);
        xml.setText(org.openide.util.NbBundle.getMessage(NewToolPanel.class, "NewToolPanel.xml.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jButton1, org.openide.util.NbBundle.getMessage(NewToolPanel.class, "NewToolPanel.jButton1.text")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel6, org.openide.util.NbBundle.getMessage(NewToolPanel.class, "NewToolPanel.jLabel6.text")); // NOI18N

        uriField.setText(org.openide.util.NbBundle.getMessage(NewToolPanel.class, "NewToolPanel.uriField.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(toolName)
                            .addComponent(author)))
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(version, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(xml, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton1)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(uriField)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(toolName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(author, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(version, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(uriField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(17, 17, 17)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(xml, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        String fname = FileChooserUtils.selectExistingFilename(new FileType[]{FileType.XML});
        xml.setText(fname);
        updated();
    }//GEN-LAST:event_jButton1ActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField author;
    private javax.swing.JTextArea description;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField toolName;
    private javax.swing.JTextField uriField;
    private javax.swing.JTextField version;
    private javax.swing.JTextField xml;
    // End of variables declaration//GEN-END:variables

    @Override
    public void insertUpdate(DocumentEvent e) {
        updated();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        updated();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        updated();
    }

    private void updated() {
        if (Empty(toolName) || Empty(author) || Empty(description) || Empty(xml)) {
            return;
        }

        String t = getToolName();
        if (t != null) {
            firePropertyChange(TOOL_DEFINED, null, t);
        }
    }

    public String getVersion() {
        return version.getText().trim();
    }

    private boolean Empty(JTextComponent f) {
        return "".equals(f.getText().trim());
    }

    private static String readFile(String path) throws IOException {
        StringBuilder content = new StringBuilder();

        FileInputStream fis = new FileInputStream(path);
        try (DataInputStream in = new DataInputStream(fis)) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                content.append(strLine);
            }
        }

        return content.toString();
    }

    public String getToolName() {
        if (Empty(toolName) || Empty(author) || Empty(description) || Empty(xml)) {
            return null;
        }

        return toolName.getText().trim();
    }

    public String getToolDescription() {
        if (Empty(toolName) || Empty(author) || Empty(description) || Empty(xml)) {
            return null;
        }
        return description.getText().trim();
    }

    public String getToolAuthor() {
        if (Empty(toolName) || Empty(author) || Empty(description) || Empty(xml)) {
            return null;
        }
        return author.getText().trim();
    }

    public String getToolWebsite() {
        if (Empty(toolName) || Empty(author) || Empty(description) || Empty(xml)) {
            return null;
        }
        return uriField.getText().trim();
    }

    public String getToolDefinition() {
        if (Empty(toolName) || Empty(author) || Empty(description) || Empty(xml)) {
            return null;
        }
        String xmlData;
        try {
            xmlData = readFile(xml.getText());
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            return null;
        }
        return xmlData;
    }
}
