package de.cebitec.mgx.gui.actions;

import de.cebitec.mgx.api.groups.ImageExporterI;
import de.cebitec.mgx.gui.swingutils.util.FileChooserUtils;
import de.cebitec.mgx.api.groups.FileType;
import de.cebitec.mgx.api.groups.ImageExporterI.Result;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "File",
        id = "de.cebitec.mgx.gui.actions.SaveImageAction")
@ActionRegistration(
        iconBase = "de/cebitec/mgx/gui/actions/SavePicture.svg",
        displayName = "#CTL_SaveImageAction")
@ActionReferences({
    //@ActionReference(path = "Menu/File", position = 1760),
    @ActionReference(path = "Toolbars/UndoRedo", position = 520)
})
@Messages("CTL_SaveImageAction=Save image")
public final class SaveImageAction implements ActionListener {

    private final ImageExporterI context;

    public SaveImageAction(ImageExporterI context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        FileType[] types = context.getSupportedTypes();
        String fname = FileChooserUtils.selectNewFilename(types, "MGX_Image");
        if (fname == null || fname.trim().isEmpty()) {
            return;
        }

        FileType ft = findType(fname, types);
        if (ft != null) {
            Result success;
            try {
                success = context.export(ft, fname);
            } catch (Exception ex) {
                NotifyDescriptor nd = new NotifyDescriptor.Message("Error: " + ex.getMessage(), NotifyDescriptor.ERROR_MESSAGE);
                DialogDisplayer.getDefault().notify(nd);
                return;
            }
            if (success == Result.SUCCESS) {
                NotifyDescriptor nd = new NotifyDescriptor.Message("Chart saved to " + fname, NotifyDescriptor.INFORMATION_MESSAGE);
                DialogDisplayer.getDefault().notify(nd);
            } else if (success == Result.ERROR) {
                NotifyDescriptor nd = new NotifyDescriptor.Message("Could not save chart.", NotifyDescriptor.ERROR_MESSAGE);
                DialogDisplayer.getDefault().notify(nd);
            }
        } else {
            NotifyDescriptor nd = new NotifyDescriptor.Message("Unrecognized file suffix " + fname, NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(nd);
        }
    }

    private FileType findType(String fname, FileType[] options) {
        int dotLoc = fname.lastIndexOf(".");
        if (dotLoc == -1) {
            return null;
        }
        String suffix = fname.substring(dotLoc + 1);
        for (FileType ft : options) {
            for (String sfx : ft.getSuffices()) {
                if (suffix.equals(sfx)) {
                    return ft;
                }
            }
        }
        return null;
    }
}
