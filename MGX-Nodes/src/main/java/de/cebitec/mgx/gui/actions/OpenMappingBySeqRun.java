/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.actions;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.Mapping;
import de.cebitec.mgx.gui.datamodel.SeqRun;
import java.awt.event.ActionEvent;
import java.util.Iterator;
import org.openide.util.Utilities;

/**
 *
 * @author sj
 */
public class OpenMappingBySeqRun extends OpenMappingBase {

    private final Iterator<Mapping> mappings;
    private final boolean hasData;

    public OpenMappingBySeqRun() {
        super();
        final MGXMaster m = Utilities.actionsGlobalContext().lookup(MGXMaster.class);
        final SeqRun run = Utilities.actionsGlobalContext().lookup(SeqRun.class);
        mappings = m.Mapping().BySeqRun(run.getId());
        hasData = mappings.hasNext();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public boolean isEnabled() {
        return hasData && super.isEnabled();
    }
}
