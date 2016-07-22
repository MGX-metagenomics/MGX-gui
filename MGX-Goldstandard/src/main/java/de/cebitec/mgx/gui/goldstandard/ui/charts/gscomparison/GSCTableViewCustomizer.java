package de.cebitec.mgx.gui.goldstandard.ui.charts.gscomparison;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.SequenceI;
import de.cebitec.mgx.gui.goldstandard.ui.EvaluationTopComponent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import javax.swing.JFileChooser;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.NbPreferences;

/**
 *
 * @author pblumenk
 */
public class GSCTableViewCustomizer extends javax.swing.JPanel {

    private static final int CHUNKSIZE = 2_500;

    private GSTableViewerPagingModel model;
    private MGXMasterI master;

    /**
     * Creates new form TableViewCustomizer
     */
    public GSCTableViewCustomizer() {
        initComponents();
    }

    public void setModel(GSTableViewerPagingModel m) {
        model = m;
        exportTSV.setEnabled(m != null && m.getColumnCount() > 0 && m.getRowCount() > 0);
    }

    public void setMaster(MGXMasterI master) {
        this.master = master;
    }

    public boolean includeHeaders() {
        return includeHeader.isSelected();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        exportTSV = new javax.swing.JButton();
        includeHeader = new javax.swing.JCheckBox();

        org.openide.awt.Mnemonics.setLocalizedText(exportTSV, "Export TSV");
        exportTSV.setEnabled(false);
        exportTSV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportTSVActionPerformed(evt);
            }
        });

        includeHeader.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(includeHeader, "Include column headers");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(exportTSV)
                    .addComponent(includeHeader))
                .addContainerGap(74, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(includeHeader)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 292, Short.MAX_VALUE)
                .addComponent(exportTSV)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void exportTSVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportTSVActionPerformed
        EvaluationTopComponent.getExecutorService().submit(new Runnable() {
            @Override
            public void run() {
                ProgressHandle p = ProgressHandle.createHandle("export table view");
                final JFileChooser fc = new JFileChooser();

                String last = NbPreferences.forModule(JFileChooser.class).get("lastDirectory", null);
                if (last != null) {
                    File f = new File(last);
                    if (f.exists() && f.isDirectory()) {
                        fc.setCurrentDirectory(f);
                    }
                }

                File f = new File("MGX_export.tsv");
                fc.setSelectedFile(f);
                fc.setVisible(true);

                fc.addPropertyChangeListener(new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        if ("SelectedFileChangedProperty".equals(evt.getPropertyName())) {
                            NbPreferences.forModule(JFileChooser.class).put("lastDirectory", fc.getCurrentDirectory().getAbsolutePath());
                        }
                    }
                });

                if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {

                    f = fc.getSelectedFile();
                    try {
                        if (f.exists()) {
                            throw new IOException(f.getName() + " already exists.");
                        }
                        BufferedWriter w = new BufferedWriter(new FileWriter(f));

                        if (includeHeaders()) {
                            for (int col = 0; col < model.getColumnCount() - 1; col++) {
                                w.write(model.getColumnName(col));
                                w.write("\t");
                            }
                            w.write(model.getColumnName(model.getColumnCount() - 1));
                            w.write(System.lineSeparator());
                            w.write(System.lineSeparator());
                        }

                        // export data
                        long[] keys = model.getData().keys();
                        p.start(keys.length);
                        int progress = 0;
                        for (int offset = 0; offset * CHUNKSIZE < model.getRealRowCount(); offset++) {
                            String[] header = updateHeader(keys, offset);
                            int size = model.getRealRowCount() - (offset * CHUNKSIZE);
                            if (size > CHUNKSIZE) {
                                size = CHUNKSIZE;
                            }
                            for (int row = 0; row < size; row++) {
                                for (int col = 0; col <= model.getColumnCount() - 1; col++) {
                                    Object val;
                                    if (col == 0) {
                                        val = header[row];
                                    } else {
                                        val = model.getRealValueAt(row + (offset * CHUNKSIZE), col);
                                    }
                                    if (val != null) {
                                        w.write(val.toString());
                                    }
                                    if (col <= model.getColumnCount() - 2 && model.getRealValueAt(row, col + 1) != null) {
                                        w.write("\t");
                                    }
                                }
//                    Object val = model.getValueAt(row, model.getColumnCount() - 1);
//                    val = val != null ? val : "";
                                //                  w.write(val.toString());
                                w.write(System.lineSeparator());
                                p.progress(progress++);
                            }
                        }
                        w.flush();
                        w.close();

                        // report success
                        NotifyDescriptor nd = new NotifyDescriptor.Message("Data exported to " + f.getName());
                        DialogDisplayer.getDefault().notify(nd);
                    } catch (IOException ex) {
                        // some error occured, notify user
                        NotifyDescriptor nd = new NotifyDescriptor("Export failed: " + ex.getMessage(), "Error",
                                NotifyDescriptor.DEFAULT_OPTION, NotifyDescriptor.ERROR_MESSAGE, null, null);
                        DialogDisplayer.getDefault().notify(nd);
                    } catch (MGXException ex) {
                        Exceptions.printStackTrace(ex);
                    } finally {
                        p.finish();
                    }
                };
            }
        });
    }//GEN-LAST:event_exportTSVActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton exportTSV;
    private javax.swing.JCheckBox includeHeader;
    // End of variables declaration//GEN-END:variables

    private String[] updateHeader(long[] keys, int offset) throws MGXException {
        long[] ids;
        int size;
        if (model.getRealRowCount() - offset * CHUNKSIZE < CHUNKSIZE) {
            size = model.getRealRowCount() - (offset * CHUNKSIZE);
            ids = Arrays.copyOfRange(keys, offset * CHUNKSIZE, model.getRealRowCount());
        } else {
            size = CHUNKSIZE;
            ids = Arrays.copyOfRange(keys, offset * CHUNKSIZE, offset * CHUNKSIZE + CHUNKSIZE);
        }
        Iterator<SequenceI> it = master.Sequence().fetchByIds(ids);
        String[] header = new String[size];
        int i = 0;
        while (it.hasNext()) {
            header[i++] = it.next().getName();
        }

        return header;
    }
}
