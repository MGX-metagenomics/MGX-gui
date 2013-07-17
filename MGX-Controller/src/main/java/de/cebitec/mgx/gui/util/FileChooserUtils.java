package de.cebitec.mgx.gui.util;

import java.io.File;
import javax.swing.JFileChooser;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbPreferences;

/**
 *
 * @author sjaenick
 */
public class FileChooserUtils {

    public static String selectNewFilename(FileType[] types) {
        String ret = null;
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogType(JFileChooser.SAVE_DIALOG);

        // try to restore last directory selection
        String last = NbPreferences.forModule(JFileChooser.class).get("lastDirectory", null);
        if (last != null) {
            File f = new File(last);
            if (f.exists() && f.isDirectory() && f.canWrite()) {
                chooser.setCurrentDirectory(f);
            }
        }

        chooser.setAcceptAllFileFilterUsed(false);

        for (FileType ft : types) {
            chooser.addChoosableFileFilter(new SuffixFilter(ft));
        }

        if (chooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) {
            ret = null;
        } else {
            final File target = chooser.getSelectedFile();
            ret = target.getAbsolutePath();
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
                Object foo = DialogDisplayer.getDefault().notify(nd);
                if (!NotifyDescriptor.OK_OPTION.equals(foo)) {
                    ret = null;
                }
            }
        }

        // save directory
        NbPreferences.forModule(JFileChooser.class).put("lastDirectory", chooser.getCurrentDirectory().getAbsolutePath());

        return ret;
    }

    public static String selectExistingFilename(FileType[] types) {
        String ret = null;
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogType(JFileChooser.OPEN_DIALOG);

        // try to restore last directory selection
        String last = NbPreferences.forModule(JFileChooser.class).get("lastDirectory", null);
        if (last != null) {
            File f = new File(last);
            if (f.exists() && f.isDirectory() && f.canWrite()) {
                chooser.setCurrentDirectory(f);
            }
        }

        chooser.setAcceptAllFileFilterUsed(false);

        for (FileType ft : types) {
            chooser.addChoosableFileFilter(new SuffixFilter(ft));
        }

        if (chooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) {
            ret = null;
        } else {
            final File target = chooser.getSelectedFile();
            ret = target.getAbsolutePath();
        }

        // save directory
        NbPreferences.forModule(JFileChooser.class).put("lastDirectory", chooser.getCurrentDirectory().getAbsolutePath());

        return ret;
    }
}
