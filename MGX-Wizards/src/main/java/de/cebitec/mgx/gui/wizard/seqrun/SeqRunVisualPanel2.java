package de.cebitec.mgx.gui.wizard.seqrun;

import de.cebitec.mgx.gui.util.FileType;
import de.cebitec.mgx.gui.util.SuffixFilter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import org.openide.util.NbPreferences;

public final class SeqRunVisualPanel2 extends JPanel {

    public static final String PROP_SEQFILE = "seqfile";
    private File file = null;

    /**
     * Creates new form SeqRunVisualPanel2
     */
    public SeqRunVisualPanel2() {
        initComponents();
        String last = NbPreferences.forModule(JFileChooser.class).get("lastDirectory", null);
        if (last != null) {
            File f = new File(last);
            if (f.exists() && f.isDirectory()) {
                fchooser.setCurrentDirectory(f);
            }
        }

        fchooser.addChoosableFileFilter(new SuffixFilter(FileType.FAS));
        fchooser.addChoosableFileFilter(new SuffixFilter(FileType.FASGZ));
        fchooser.addChoosableFileFilter(new SuffixFilter(FileType.FASTQ));
        fchooser.addChoosableFileFilter(new SuffixFilter(FileType.FASTQGZ));

        fchooser.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("SelectedFileChangedProperty".equals(evt.getPropertyName())) {
                    NbPreferences.forModule(JFileChooser.class).put("lastDirectory", fchooser.getCurrentDirectory().getAbsolutePath().toString());
                    file = fchooser.getSelectedFile();
                    firePropertyChange(PROP_SEQFILE, 0, 1);
                }
            }
        });
    }

    @Override
    public String getName() {
        return "Select sequence data";
    }

    public File getSelectedFile() {
        return file;
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fchooser = new javax.swing.JFileChooser();

        fchooser.setControlButtonsAreShown(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(fchooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(fchooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JFileChooser fchooser;
    // End of variables declaration//GEN-END:variables
}
