package de.cebitec.mgx.gui.wizard.seqrun;

import de.cebitec.mgx.api.groups.FileType;
import de.cebitec.mgx.gui.swingutils.util.SuffixFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import org.openide.util.NbPreferences;

public final class SeqRunVisualPanel2 extends JPanel {

    public static final String PROP_SEQFILES = "seqfiles";
    private File file1 = null;
    private File file2 = null;
    private boolean isPaired;

    private final JFileChooser fchooser;

    /**
     * Creates new form SeqRunVisualPanel2
     */
    public SeqRunVisualPanel2() {
        initComponents();
        fchooser = new JFileChooser();
        String last = NbPreferences.forModule(JFileChooser.class).get("lastDirectory", null);
        if (last != null) {
            File f = new File(last);
            if (f.exists() && f.isDirectory()) {
                fchooser.setCurrentDirectory(f);
            }
        }

        jButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                String lastDir = NbPreferences.forModule(JFileChooser.class).get("lastDirectory", null);
                if (lastDir != null) {
                    File f = new File(lastDir);
                    if (f.exists() && f.isDirectory()) {
                        fchooser.setCurrentDirectory(f);
                    }
                }

                if (fchooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                    file1 = fchooser.getSelectedFile();
                    first.setText(file1.getAbsolutePath());
                    firePropertyChange(PROP_SEQFILES, 0, 1);
                }

                NbPreferences.forModule(JFileChooser.class).put("lastDirectory", fchooser.getCurrentDirectory().getAbsolutePath());
            }
        });

        jButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                String lastDir = NbPreferences.forModule(JFileChooser.class).get("lastDirectory", null);
                if (lastDir != null) {
                    File f = new File(lastDir);
                    if (f.exists() && f.isDirectory()) {
                        fchooser.setCurrentDirectory(f);
                    }
                }

                if (fchooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                    file2 = fchooser.getSelectedFile();
                    second.setText(file2.getAbsolutePath());
                    firePropertyChange(PROP_SEQFILES, 0, 1);
                }

                NbPreferences.forModule(JFileChooser.class).put("lastDirectory", fchooser.getCurrentDirectory().getAbsolutePath());
            }
        });

        fchooser.addChoosableFileFilter(new SuffixFilter(FileType.FAS));
        fchooser.addChoosableFileFilter(new SuffixFilter(FileType.FASGZ));
        fchooser.addChoosableFileFilter(new SuffixFilter(FileType.FASTQ));
        fchooser.addChoosableFileFilter(new SuffixFilter(FileType.FASTQGZ));
        fchooser.addChoosableFileFilter(new SuffixFilter(FileType.SFF));
    }

    @Override
    public String getName() {
        return "Select sequence data";
    }
    
    public  void setPaired(boolean paired) {
        isPaired = paired;
        if (!isPaired) {
            second.setText("");
            second.setEnabled(false);
            jButton2.setEnabled(false);
            file2 = null;
        } else {
            second.setText("");
            second.setEnabled(true);
            jButton1.setEnabled(true);
        }
    }

    public File[] getSelectedFiles() {
        return new File[]{file1, file2};
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        first = new javax.swing.JTextField();
        second = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        first.setEditable(false);
        first.setBackground(new java.awt.Color(255, 255, 255));
        first.setText(org.openide.util.NbBundle.getMessage(SeqRunVisualPanel2.class, "SeqRunVisualPanel2.first.text")); // NOI18N

        second.setEditable(false);
        second.setBackground(new java.awt.Color(255, 255, 255));
        second.setText(org.openide.util.NbBundle.getMessage(SeqRunVisualPanel2.class, "SeqRunVisualPanel2.second.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(SeqRunVisualPanel2.class, "SeqRunVisualPanel2.jLabel1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(SeqRunVisualPanel2.class, "SeqRunVisualPanel2.jLabel2.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jButton1, org.openide.util.NbBundle.getMessage(SeqRunVisualPanel2.class, "SeqRunVisualPanel2.jButton1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jButton2, org.openide.util.NbBundle.getMessage(SeqRunVisualPanel2.class, "SeqRunVisualPanel2.jButton2.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(second)
                            .addComponent(first))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jButton2, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addGap(0, 351, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(76, 76, 76)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(first, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addGap(37, 37, 37)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(second, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2))
                .addContainerGap(145, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField first;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextField second;
    // End of variables declaration//GEN-END:variables
}
