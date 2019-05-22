/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.controller.assembly;

import de.cebitec.mgx.api.MGXAssemblyMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.ModelBaseI;
import de.cebitec.mgx.api.model.assembly.access.AssemblyAccessI;
import de.cebitec.mgx.api.model.assembly.access.AssemblyJobAccessI;
import de.cebitec.mgx.api.model.assembly.access.BinAccessI;
import de.cebitec.mgx.api.model.assembly.access.ContigAccessI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.gui.controller.MGXMaster;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 *
 * @author sj
 */
public class MGXAssemblyMaster extends MGXAssemblyMasterI implements PropertyChangeListener {

    private final MGXDTOMaster dtomaster;

    public MGXAssemblyMaster(MGXMaster m, MGXDTOMaster dtomaster) {
        super(m);
        m.addPropertyChangeListener(this);
        this.dtomaster = dtomaster;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case ModelBaseI.OBJECT_DELETED:
                getMaster().removePropertyChangeListener(this);
                deleted();
                break;
            case MGXDTOMaster.PROP_LOGGEDIN:
                if (evt.getSource() == getMaster() && evt.getNewValue() instanceof Boolean) {
                    Boolean isLoggedIn = (Boolean) evt.getNewValue();
                    if (!isDeleted() && !isLoggedIn) {
                        getMaster().removePropertyChangeListener(this);
                        deleted();
                    }
                }
                break;
            default:
                System.err.println("MGXAssemblyMaster received event " + evt);
        }
    }

    @Override
    public AssemblyAccessI Assembly() throws MGXException {
        return new AssemblyAccess(getMaster(), dtomaster);
    }

    @Override
    public AssemblyJobAccessI AssemblyJob() throws MGXException {
        return new AssemblyJobAccess(getMaster(), dtomaster);
    }

    @Override
    public BinAccessI Bin() throws MGXException {
        return new BinAccess(getMaster(), dtomaster);
    }

    @Override
    public ContigAccessI Contig() throws MGXException {
        return new ContigAccess(getMaster(), dtomaster);
    }

}
