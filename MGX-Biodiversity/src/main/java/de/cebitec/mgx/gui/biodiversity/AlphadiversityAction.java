/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.biodiversity;

import de.cebitec.mgx.api.groups.GroupI;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

@ActionID(
        category = "File",
        id = "de.cebitec.mgx.gui.biodiversity.AlphadiversityAction"
)
@ActionRegistration(
        iconBase = "de/cebitec/mgx/gui/biodiversity/AlphaDiversity.svg",
        displayName = "#CTL_AlphadiversityAction"
)
@ActionReferences({
    //@ActionReference(path = "Menu/File", position = 1780),
    @ActionReference(path = "Toolbars/UndoRedo", position = 630)
})
@Messages("CTL_AlphadiversityAction=Alpha diversity")
public final class AlphadiversityAction implements ActionListener {

    private final GroupI<?> context;

    public AlphadiversityAction(GroupI context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        TopComponent tc = BiodiversityTopComponent.getDefault();

        if (!tc.isOpened()) {
            Mode m = WindowManager.getDefault().findMode("satellite");
            if (m != null) {
                m.dockInto(tc);
            }
            tc.open();
        }
        if (!tc.isVisible()) {
            tc.setVisible(true);
        }
        tc.toFront();
        tc.requestActive();
    }
}
