/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.controller.assembly;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.exception.MGXLoggedoutException;
import de.cebitec.mgx.api.misc.TaskI;
import de.cebitec.mgx.api.misc.TaskI.TaskType;
import de.cebitec.mgx.api.model.assembly.AssemblyI;
import de.cebitec.mgx.api.model.assembly.access.AssemblyAccessI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.exception.MGXClientLoggedOutException;
import de.cebitec.mgx.client.exception.MGXDTOException;
import de.cebitec.mgx.dto.dto.AssemblyDTO;
import de.cebitec.mgx.gui.controller.AccessBase;
import de.cebitec.mgx.gui.dtoconversion.AssemblyDTOFactory;
import de.cebitec.mgx.gui.util.BaseIterator;
import java.util.Iterator;
import java.util.UUID;

/**
 *
 * @author sj
 */
public class AssemblyAccess extends AccessBase<AssemblyI> implements AssemblyAccessI {

    public AssemblyAccess(MGXMasterI master, MGXDTOMaster dtomaster) throws MGXException {
        super(master, dtomaster);
    }

    @Override
    public AssemblyI fetch(long id) throws MGXException {
        AssemblyDTO dto = null;
        try {
            dto = getDTOmaster().Assembly().fetch(id);
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
        return AssemblyDTOFactory.getInstance().toModel(getMaster(), dto);
    }

    @Override
    public Iterator<AssemblyI> fetchall() throws MGXException {
        Iterator<AssemblyDTO> it;
        try {
            it = getDTOmaster().Assembly().fetchall().getAssemblyList().iterator();
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
        return new BaseIterator<AssemblyDTO, AssemblyI>(it) {
            @Override
            public AssemblyI next() {
                return AssemblyDTOFactory.getInstance().toModel(getMaster(), iter.next());
            }
        };
    }

    @Override
    public void update(AssemblyI obj) throws MGXException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public TaskI<AssemblyI> delete(AssemblyI obj) throws MGXException {
        TaskI<AssemblyI> ret = null;
        try {
            UUID uuid = getDTOmaster().Assembly().delete(obj.getId());
            ret = getMaster().<AssemblyI>Task().get(obj, uuid, TaskType.DELETE);
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
        return ret;
    }
}
