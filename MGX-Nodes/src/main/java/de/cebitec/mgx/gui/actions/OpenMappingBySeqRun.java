/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.actions;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.MappingI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.gui.swingutils.NonEDT;
import java.awt.event.ActionEvent;
import java.util.Iterator;
import org.openide.util.Utilities;

/**
 *
 * @author sj
 */
public class OpenMappingBySeqRun extends OpenMappingBase {

    private Iterator<MappingI> mappings;
    private boolean hasData = false;

    public OpenMappingBySeqRun() {
        super();
        final MGXMasterI m = Utilities.actionsGlobalContext().lookup(MGXMasterI.class);
        final SeqRunI run = Utilities.actionsGlobalContext().lookup(SeqRunI.class);
        
        if (m == null || run == null) {
            return;
        }
        NonEDT.invokeAndWait(new Runnable() {

            @Override
            public void run() {
                mappings = m.Mapping().BySeqRun(run.getId());
                hasData = mappings.hasNext();
            }
        });

    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public boolean isEnabled() {
        return hasData && super.isEnabled();
    }
}
