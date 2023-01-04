/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.cebitec.mgx.gui.binexplorer.util;

import de.cebitec.mgx.api.groups.FileType;
import de.cebitec.mgx.api.model.assembly.ContigI;
import de.cebitec.mgx.gui.binexplorer.internal.ContigViewController;
import de.cebitec.mgx.gui.genbankexporter.GBKExporter;
import de.cebitec.mgx.gui.pool.MGXPool;
import de.cebitec.mgx.gui.swingutils.util.SuffixFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.NbPreferences;

/**
 *
 * @author sj
 */
public class GenBankExportAction implements ActionListener {

    private final ContigViewController vc;

    public GenBankExportAction(ContigViewController vc) {
        this.vc = vc;
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        final String taxAssignment = vc.getSelectedBin().getTaxonomy();
        final ContigI contig = vc.getContig();
        JFileChooser fchooser = new JFileChooser();
        fchooser.setDialogType(JFileChooser.SAVE_DIALOG);

        // try to restore last directory selection
        String last = NbPreferences.forModule(JFileChooser.class).get("lastDirectory", null);
        if (last != null) {
            File f = new File(last);
            if (f.exists() && f.isDirectory() && f.canWrite()) {
                fchooser.setCurrentDirectory(f);
            }
        }

        // suggest a file name
        String suffix = ".gbk";
        File suggestedName = new File(fchooser.getCurrentDirectory(), cleanupName(contig.getName()) + suffix);
        int cnt = 1;
        while (suggestedName.exists()) {
            String newName = new StringBuilder(cleanupName(contig.getName()))
                    .append(" (")
                    .append(cnt++)
                    .append(")")
                    .append(suffix)
                    .toString();
            suggestedName = new File(fchooser.getCurrentDirectory(), newName);
        }
        fchooser.setSelectedFile(suggestedName);
        FileFilter ff = new SuffixFilter(FileType.EMBLGENBANK);
        fchooser.addChoosableFileFilter(ff);
        fchooser.setFileFilter(ff);

        if (fchooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        NbPreferences.forModule(JFileChooser.class).put("lastDirectory", fchooser.getCurrentDirectory().getAbsolutePath());

        final File target = fchooser.getSelectedFile();
        if (target.exists()) {
            // ask if file should be overwritten, else return
            String msg = new StringBuilder("A file named ")
                    .append(target.getName())
                    .append(" already exists. Should this ")
                    .append("file be overwritten?")
                    .toString();
            NotifyDescriptor nd = new NotifyDescriptor(msg,
                    "Overwrite file?",
                    NotifyDescriptor.OK_CANCEL_OPTION,
                    NotifyDescriptor.WARNING_MESSAGE,
                    null, null);
            Object ret = DialogDisplayer.getDefault().notify(nd);
            if (!NotifyDescriptor.OK_OPTION.equals(ret)) {
                return;
            }
        }

        ProgressHandle handle = ProgressHandle.createHandle("Export to " + target.getName());
        handle.start();
        handle.switchToIndeterminate();

        MGXPool.getInstance().submit(new Runnable() {
            @Override
            public void run() {
                try {
                    GBKExporter.exportContig(target, contig, taxAssignment);
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                }

                handle.finish();
            }
        });

    }

    private static String cleanupName(String name) {
        if (name.contains(File.separator)) {
            name = name.replace(File.separator, "_");
        }
        return name;
    }
}
