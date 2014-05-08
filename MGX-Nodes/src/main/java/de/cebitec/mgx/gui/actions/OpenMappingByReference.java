/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.actions;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.Mapping;
import de.cebitec.mgx.gui.datamodel.Reference;
import java.awt.event.ActionEvent;
import java.util.Iterator;
import org.openide.util.Utilities;

/**
 *
 * @author sj
 */
public class OpenMappingByReference extends OpenMappingBase {

    private final Iterator<Mapping> mappings;
    private final boolean hasData;

    public OpenMappingByReference() {
        super();
        final MGXMaster m = Utilities.actionsGlobalContext().lookup(MGXMaster.class);
        final Reference ref = Utilities.actionsGlobalContext().lookup(Reference.class);
        mappings = m.Mapping().ByReference(ref.getId());
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
