
package de.cebitec.mgx.gui.actions;

import de.cebitec.mgx.api.groups.SequenceExporterI;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
    category = "File",
id = "de.cebitec.mgx.gui.actions.ExportSequencesAction")
@ActionRegistration(
    iconBase = "de/cebitec/mgx/gui/actions/SaveFasta.svg",
displayName = "#CTL_ExportSequencesAction")
@ActionReferences({
    //@ActionReference(path = "Menu/File", position = 1750),
    @ActionReference(path = "Toolbars/UndoRedo", position = 530)
})
@Messages("CTL_ExportSequencesAction=Export sequences")
public final class ExportSequencesAction implements ActionListener {

    private final List<SequenceExporterI> context;

    public ExportSequencesAction(List<SequenceExporterI> context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        for (SequenceExporterI ex : context) {
            boolean ok = ex.export();
            if (!ok) {
                return;
            }
        }
    }
}
