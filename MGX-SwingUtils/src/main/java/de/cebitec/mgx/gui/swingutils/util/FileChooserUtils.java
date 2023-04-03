package de.cebitec.mgx.gui.swingutils.util;

import de.cebitec.mgx.api.groups.FileType;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.lang.reflect.Field;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.plaf.FileChooserUI;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbPreferences;

/**
 *
 * @author sjaenick
 */
public class FileChooserUtils {

    public static String selectNewFilename(final FileType[] types, final String suggestedPrefix) {
        if (types.length == 0) {
            return null;
        }

        String ret = null;
        final JFileChooser chooser = new JFileChooser();
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

        final File suggestion = generateSuggestion(chooser.getCurrentDirectory(), suggestedPrefix, types[0]);
        chooser.setSelectedFile(suggestion);

        chooser.addPropertyChangeListener(JFileChooser.FILE_FILTER_CHANGED_PROPERTY, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                FileType ftold = ((SuffixFilter) evt.getOldValue()).getType();
                String newSuffix = ((SuffixFilter) evt.getNewValue()).getType().getSuffices()[0];

                /*
                 * getSelectedFile() doesn't work here (returns null); UGLY HACK:
                 * directly access the corresponding text field using reflection
                 */
                try {
                    FileChooserUI ui2 = chooser.getUI();
                    Class<?> c = ui2.getClass();
                    Field f = null;

                    try {
                        f = c.getDeclaredField("filenameTextField");
                    } catch (NoSuchFieldException | SecurityException ex) {
                    }

                    if (f == null) {
                        try {
                            f = c.getDeclaredField("fileNameTextField");
                        } catch (NoSuchFieldException | SecurityException ex) {
                        }
                    }

                    try {
                        if (f == null) {
                            // for FlatLaF, FlatFileChooserUI inherits from MetalFileChooserUI,
                            // which contains the "fileNameTextField"
                            c = c.getSuperclass();
                            f = c.getDeclaredField("fileNameTextField");
                        }
                    } catch (NoSuchFieldException | SecurityException x) {
                    }

                    if (f != null) {
                        f.setAccessible(true);
                        JTextField fileNameField = (JTextField) f.get(ui2);
                        String filename = fileNameField.getText();
                        for (String sfx : ftold.getSuffices()) {
                            if (filename.endsWith(sfx)) {
                                filename = filename.replace(sfx, newSuffix);
                                chooser.setSelectedFile(new File(chooser.getCurrentDirectory(), filename));
                            }
                        }
                    }
                } catch (SecurityException | IllegalArgumentException | IllegalAccessException x) {
                }
            }
        });

        if (chooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) {
            return null;
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

        if (chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
            ret = null;
        } else {
            final File target = chooser.getSelectedFile();
            ret = target.getAbsolutePath();
        }

        // save directory
        NbPreferences.forModule(JFileChooser.class).put("lastDirectory", chooser.getCurrentDirectory().getAbsolutePath());

        return ret;
    }

    private static File generateSuggestion(File dir, String tmpl, FileType ft) {
        File f = new File(dir, tmpl + "." + ft.getSuffices()[0]);
        int i = 0;
        while (f.exists()) {
            i++;
            f = new File(dir, tmpl + "(" + i + ")." + ft.getSuffices()[0]);
        }
        return f;
    }
}
