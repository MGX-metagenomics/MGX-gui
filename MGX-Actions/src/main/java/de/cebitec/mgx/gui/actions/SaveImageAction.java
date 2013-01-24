
package de.cebitec.mgx.gui.actions;

import de.cebitec.mgx.gui.groups.ImageExporterI;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
    category = "File",
id = "de.cebitec.mgx.gui.actions.SaveImageAction")
@ActionRegistration(
    iconBase = "de/cebitec/mgx/gui/actions/SavePicture.png",
displayName = "#CTL_SaveImageAction")
@ActionReferences({
    @ActionReference(path = "Menu/File", position = 1760),
    @ActionReference(path = "Toolbars/File", position = 610)
})
@Messages("CTL_SaveImageAction=Save image")
public final class SaveImageAction implements ActionListener {

    private final ImageExporterI context;

    public SaveImageAction(ImageExporterI context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        context.export();
    }
}
