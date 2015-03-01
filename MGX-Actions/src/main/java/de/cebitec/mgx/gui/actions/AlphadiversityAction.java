/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.actions;

import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.gui.biodiversity.BiodiversityTopComponent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.Mode;
import org.openide.windows.WindowManager;

@ActionID(
        category = "File",
        id = "de.cebitec.mgx.gui.actions.AlphadiversityAction"
)
@ActionRegistration(
        iconBase = "de/cebitec/mgx/gui/actions/AlphaDiversity.png",
        displayName = "#CTL_AlphadiversityAction"
)
@ActionReferences({
    @ActionReference(path = "Menu/File", position = 1780),
    @ActionReference(path = "Toolbars/UndoRedo", position = 630)
})
@Messages("CTL_AlphadiversityAction=Alpha diversity")
public final class AlphadiversityAction implements ActionListener {

    private final VisualizationGroupI context;

    public AlphadiversityAction(VisualizationGroupI context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        BiodiversityTopComponent biodiv = new BiodiversityTopComponent();
        biodiv.setVisible(true);

        Mode m = WindowManager.getDefault().findMode("satellite");
        if (m != null) {
            m.dockInto(biodiv);
        } else {
            System.err.println("satellite mode not found");
        }
        biodiv.open();
    }
}
